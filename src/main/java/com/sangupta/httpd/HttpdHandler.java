/**
 *
 * httpd - simple HTTP server for development
 * Copyright (c) 2014-2015, Sandeep Gupta
 * 
 * http://sangupta.com/projects/httpd
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 		http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package com.sangupta.httpd;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.sangupta.jerry.constants.HttpStatusCode;
import com.sangupta.jerry.util.AssertUtils;
import com.sangupta.jerry.util.StringUtils;

/**
 * Handle all requests from the client
 * 
 * @author sangupta
 *
 */
public class HttpdHandler extends AbstractHandler {
	
	/**
	 * Format to read date in from request
	 */
	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	
	/**
	 * Allowed file name pattern
	 */
	public static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
	
	/**
	 * Document root from where to serve files
	 */
	protected final File documentRoot;
	
	/**
	 * The configuration instance to use
	 */
	protected final HttpdConfig httpdConfig;
	
	/**
	 * Construct an instance of handler for the given document root
	 * 
	 * @param documentRoot
	 */
	public HttpdHandler(HttpdConfig httpdConfig) {
		this.httpdConfig = httpdConfig;
		this.documentRoot = new File(this.httpdConfig.path);
	}

	/**
	 * Handle incoming requests
	 * 
	 */
	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		final long requestTime = System.currentTimeMillis();
		
		// add our custom headers - this happens first otherwise
		// Jetty adds its own headers
		response.addHeader("Server", "httpd");
		
		// handle the incoming request
		try {
			handleIncomingRequest(request, response);
		} finally {
			if(!this.httpdConfig.noLogs) {
				// log this request
				logRequest(request, response, requestTime);
			}
		}
		
		baseRequest.setHandled(true);
	}

	/**
	 * Log the request details to screen
	 * 
	 * @param request
	 * @param response
	 * @param requestTime 
	 */
	private void logRequest(HttpServletRequest request, HttpServletResponse response, long requestTime) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yyyy hh:mm:ss");
		
		StringBuilder builder = new StringBuilder(1024);
		builder.append(request.getRemoteAddr());
		builder.append(" - [");
		builder.append(format.format(new Date(requestTime)));
		builder.append("] \"");
		builder.append(request.getMethod());
		builder.append(' ');
		builder.append(request.getRequestURI());
		builder.append(' ');
		builder.append(request.getProtocol());
		builder.append(" - ");
		builder.append(response.getStatus());
		builder.append(" - ");
		
		String length = response.getHeader("Content-Length");
		if(length == null) {
			length = "0";
		}
		
		builder.append(length);
		
		System.out.println(builder.toString());
	}

	/**
	 * Handle incoming request for directory listing or file serving
	 * 
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void handleIncomingRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String uri = request.getRequestURI();
		
		// logging
		if(uri.endsWith("/")) {
			showDirectoryListing(response, new File(documentRoot, uri));
			return;
		}
		
		if(uri.startsWith("/")) {
			uri = uri.substring(1);
		}
		
		sendFileContents(request, response, uri);
	}

	/**
	 * Send file contents back to client
	 * 
	 * @param request
	 * @param response
	 * @param uri
	 * @throws IOException
	 */
	private void sendFileContents(HttpServletRequest request, HttpServletResponse response, String uri) throws IOException {
		File file = new File(documentRoot, uri);
		if (!file.exists() || file.isHidden()) {
			response.sendError(HttpStatusCode.NOT_FOUND);
			return;
		}
		
		if(file.isDirectory()) {
			response.sendRedirect("/" + uri + "/");
			return;
		}
		
		if (!file.isFile()) {
			response.sendError(HttpStatusCode.FORBIDDEN);
			return;
		}
		
		// check for if-modified-since header name
		String ifModifiedSince = request.getHeader("If-Modified-Since");
		if (ifModifiedSince != null && !ifModifiedSince.isEmpty()) {
			Date ifModifiedSinceDate = parseDateHeader(ifModifiedSince);
			
			if(ifModifiedSinceDate != null) {
				// Only compare up to the second because the datetime format we send to the client
				// does not have milliseconds
				long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
				long fileLastModifiedSeconds = file.lastModified() / 1000;
				
				if (ifModifiedSinceDateSeconds == fileLastModifiedSeconds) {
					response.setStatus(HttpStatusCode.NOT_MODIFIED);
					return;
				}
			}
		}
		
		// add mime-header - based on the file extension
		response.setContentType(MimeUtils.getMimeTypeForFileExtension(FilenameUtils.getExtension(file.getName())));
		response.setDateHeader("Last-Modified", file.lastModified());
		
		// check for no cache
		if(this.httpdConfig.noCache) {
			response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		}
		
		// check for no-sniff
		if(this.httpdConfig.noSniff) {
			response.addHeader("X-Content-Type-Options", "nosniff");
		}
		
		// send back file contents
		response.setContentLength((int) file.length());
		IOUtils.copyLarge(FileUtils.openInputStream(file), response.getOutputStream());
	}

	/**
	 * Send directory listing back to client
	 * 
	 * @param response
	 * @param dir
	 * @throws IOException
	 */
	private void showDirectoryListing(HttpServletResponse response, File dir) throws IOException {
		StringBuilder buf = new StringBuilder();
		String dirPath = dir.getPath();

		buf.append("<!DOCTYPE html>\r\n");
		buf.append("<html><head><title>");
		buf.append("Listing of: ");
		buf.append(dirPath);
		buf.append("</title></head><body>\r\n");

		buf.append("<h3>Listing of: ");
		if(dirPath.startsWith(".")) {
			dirPath = dirPath.substring(0);
		}
		buf.append(dirPath);
		buf.append("</h3>\r\n");

		buf.append("<ul>");
		buf.append("<li><a href=\"../\">..</a></li>\r\n");

		for (File f : dir.listFiles()) {
			if (f.isHidden() || !f.canRead()) {
				continue;
			}

			String name = f.getName();
			if (!ALLOWED_FILE_NAME.matcher(name).matches()) {
				continue;
			}

			buf.append("<li><a href=\"");
			buf.append(name);
			buf.append("\">");
			buf.append(name);
			buf.append("</a></li>\r\n");
		}

		buf.append("</ul></body></html>\r\n");
		
		byte[] bytes = buf.toString().getBytes(StringUtils.CHARSET_UTF8);
		
		response.setHeader("Content-Type", "text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentLength(bytes.length);
		response.getOutputStream().write(bytes);
	}

	/**
	 * Parse the date header from client
	 * 
	 * @param dateString
	 * @return
	 */
	private static Date parseDateHeader(String dateString) {
		if(AssertUtils.isEmpty(dateString)) {
			return null;
		}
		
		SimpleDateFormat dateFormatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US);
		try {
			return dateFormatter.parse(dateString);
		} catch (ParseException e) {
			return null;
		}
	}

}
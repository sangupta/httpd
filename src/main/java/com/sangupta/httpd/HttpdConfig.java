package com.sangupta.httpd;

import java.io.File;

import javax.inject.Inject;

import com.sangupta.jerry.util.AssertUtils;

import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;

@Command(name = "httpd", description = "A simple vanilla HTTP server for local development")
public class HttpdConfig {
	
	@Inject
	public HelpOption helpOption;

	@Option(name = { "-p", "--port" }, description = "The port to run on. Default is 8180.")
	public int port = 8180;
	
	@Option(name = { "-nc", "--noCache" }, description = "Return all files with a no-cache header for browsers. Default is false.")
	public boolean noCache = false;
	
	@Option(name = { "--path" }, description = "The directory to run the server on. Default is current directory.")
	public String path;
	
	@Option(name = { "-ns", "--noSniff" }, description = "Add a `X-Content-Type-Options: nosniff` header to all responses")
	public boolean noSniff;
	
	@Option(name = { "-nl", "--noLogs" }, description = "Do not show any logs when serving request")
	public boolean noLogs;
	
	@Option(name = { "-ndl", "--noDirList" }, description = "Do not show directory listing")
	public boolean noDirList;
	
	@Option(name = { "-ni", "--noIndex" }, description = "Do not show index page by default")
	public boolean noIndex;
	
	@Option(name = { "-ne", "--noEtag" }, description = "Do not generate the weak ETAGs")
	public boolean noEtag;

	public boolean validate() {
		if(this.port <= 1024 || this.port > 65535) {
			System.out.println("Port to run on must be between 1025-65535 (both inclusive)");
			return false;
		}
		
		if(AssertUtils.isEmpty(this.path)) {
			this.path = ".";
		} else {
			// check it is a valid directory
			File dir = new File(path);
			if(!dir.exists()) {
				System.out.println("Path specified does not exist.");
				return false;
			}
			
			if(!dir.isDirectory()) {
				System.out.println("Path specified does not represent a valid directory");
				return false;
			}
			
			if(!dir.canRead()) {
				System.out.println("Cannot read from the path specified");
				return false;
			}
		}
		
		return true;
	}
	
}

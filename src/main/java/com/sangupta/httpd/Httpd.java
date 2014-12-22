/**
 *
 * httpd - simple HTTP server for development
 * Copyright (c) 2014, Sandeep Gupta
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

import org.eclipse.jetty.server.Server;

/**
 * Runs the actual HTTP server
 * 
 * @author sangupta
 *
 */
public class Httpd {
	
	/**
	 * The port on which the server is to run
	 * 
	 */
	private final int port;

	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public Httpd(int port) {
		if(port < 1024 || port > 65535) {
			throw new IllegalArgumentException("Port number to run from should be within 1025-65535 (inclusive)");
		}
		
		this.port = port;
	}

	public void start() {
		final long startTime = System.currentTimeMillis();
		
		Server server = new Server(this.port);
		server.setHandler(new HttpdHandler(new File(".")));
		
		try {
			server.start();
		} catch (Exception e) {
			System.out.println("Unable to start server.");
			e.printStackTrace();
			return;
		}
		
		final long endTime = System.currentTimeMillis();
		try {
			System.out.println("Server started in " + (endTime - startTime) + " milliseconds on port " + this.port);
			server.join();
		} catch (InterruptedException e) {
			// server needs to be stopped
		}
		
		System.out.println("Bye!");
	}

}

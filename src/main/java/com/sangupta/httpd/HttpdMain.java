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

import com.sangupta.jerry.util.AssertUtils;

import io.airlift.airline.SingleCommand;


/**
 * The command line app to fire the HTTPD server.
 * 
 */
public class HttpdMain {

	/**
	 * The main method - to run from command line
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		HttpdConfig httpdConfig;
		if(AssertUtils.isEmpty(args)) {
			// this is a special case - run the server on default values
			httpdConfig = new HttpdConfig();
		} else {
			httpdConfig = SingleCommand.singleCommand(HttpdConfig.class).parse(args);

			if(httpdConfig.helpOption.showHelpIfRequested()) {
				// show help and return
				return;
			}
		}
		
		// create the instance
		final Httpd httpd = new Httpd(httpdConfig);
		
		// add shutdown hook
		Runtime.getRuntime().addShutdownHook(new Thread() {
			
			@Override
			public void run() {
				httpd.stop();
			}
			
		});
		
		httpd.start();
	}
	
}

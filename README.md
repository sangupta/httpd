httpd
=====

`httpd` is a simple HTTP server for development that serves the directory from which 
it is run over HTTP. Directory listing and proper MIME types are supported.

The reason I wrote `httpd` was simple: When using windows machines I do not have to
install `python` or `nodejs` to run a simple server. For me, `Java` is always there
on the machines. Also, in some of our `Linux` environments we do not have the ability 
to download and run `nodejs` - in such environments running a HTTP server via command 
line to just transfer files, makes it very easy. 

Been using it for a few months now and works for me. Some of the things I use this for:

* transferring huge files (in GBs) between machines
* testing my `github.com` blog
* testing prototypes that are static HTML/CSS/JS files
* and some more...  

Changelog
---------

**Current Development**

* Added configuration params: `port`, `path`, `noLogs`, `nosniff`, `noCache`
* Added default behavior to serve `index.html` or `index.htm` if present in the directory
* Added option to disable directory listing

**Version 1.0.0**

* Supports Directory Listing
* Supports correct MIME type
* Supports `If-Modified-Since` header
* Supports hidden-files by sending `NOT FOUND` status

Usage
-----

```text
NAME
        httpd - A simple vanilla HTTP server for local development

SYNOPSIS
        httpd [(-h | --help)] [(-nc | --noCache)] [(-ndl | --noDirList)]
                [(-ni | --noIndex)] [(-nl | --noLogs)] [--nosniff]
                [(-p <port> | --port <port>)] [--path <path>]

OPTIONS
        -h, --help
            Display help information

        -nc, --noCache
            Return all files with a no-cache header for browsers. Default is
            false.

        -ndl, --noDirList
            Do not show directory listing

        -ni, --noIndex
            Do not show index page by default

        -nl, --noLogs
            Do not show any logs when serving request

        --nosniff
            Add a `X-Content-Type-Options: nosniff` header to all responses

        -p <port>, --port <port>
            The port to run on. Default is 8180.

        --path <path>
            The directory to run the server on. Default is current directory.
```

Downloads
---------

The binary can be downloaded directly from:

**1.0.0**

```
URL: http://static.sangupta.com/binaries/httpd-1.0.0.jar
MD5: 7feeb06d6505313d381d3d3c4c53ba08
SHA1: 20fa6d9a0e7dea2a329f14bdebe305fda829a40d
```

Hacking
-------

If you would like to hack or use the server in an embedded mode, include the following Maven
dependency in your project.

```xml
<dependency>
    <groupId>com.sangupta</groupId>
    <artifactId>httpd</artifactId>
    <version>1.0.0</version>
</dependency>
```

Versioning
----------

For transparency and insight into our release cycle, and for striving to maintain backward compatibility, 
`httpd` will be maintained under the Semantic Versioning guidelines as much as possible.

Releases will be numbered with the follow format:

`<major>.<minor>.<patch>`

And constructed with the following guidelines:

* Breaking backward compatibility bumps the major
* New additions without breaking backward compatibility bumps the minor
* Bug fixes and misc changes bump the patch

For more information on SemVer, please visit http://semver.org/.

License
-------
	
```
httpd - simple HTTP server for development
Copyright (c) 2014-2015, Sandeep Gupta

http://sangupta.com/projects/httpd

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

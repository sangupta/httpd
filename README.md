httpd
=====

`httpd` is a simple HTTP server for development that serves the directory from which 
it is run over HTTP. Directory listing and proper MIME types are supported.


Changelog
---------

**Current Development**

* Supports Directory Listing
* Supports correct MIME type
* Supports `If-Modified-Since` header
* Supports hidden-files by sending `NOT FOUND` status


Downloads
---------

The library can be downloaded from Maven Central using:

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
Copyright (c) 2014, Sandeep Gupta

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

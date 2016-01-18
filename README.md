# nuxeo-sheridan


# Build and Install

Assuming [maven](http://maven.apache.org/) (3.2.5) is installed on your system, after downloading the whole repository, execute the following:


* Installation with unit-test (recommended):
  * See the JavaDoc at `nuxeo-sheridan/nuxeo-sheridan-plugin/src/test/java/org/nuxeo/sheridan/test/TestS3TempSignedUrl.java`, and add an `aws-test.conf` file containing the required information (AWS key ID, AWS secret key, S3 bucket, file to test and its size)
  * Then, in the teminal, run

  ```
  cd /path/to/nuxeo-sheridan
  mvn clean install
  ```


* Installation with no unit test:

  ```
  cd /path/to/nuxeo-sheridan
  mvn clean install -DskipTests=true
  ```


The NuxeoPackage is in `nuxeo-sheridan-mp/target`, named `nuxeo-sheridan-mp-{version}.zip`. It can be [installed from the Admin Center](https://doc.nuxeo.com/x/moFH) (see the "Offline Installation" topic), or from the commandline using `nuxeoctl mp-install`.



## License
(C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.

All rights reserved. This program and the accompanying materials
are made available under the terms of the GNU Lesser General Public License
(LGPL) version 2.1 which accompanies this distribution, and is available at
http://www.gnu.org/licenses/lgpl-2.1.html

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

Contributors:
Thibaud Arguillere (https://github.com/ThibArg)

## About Nuxeo

Nuxeo provides a modular, extensible Java-based [open source software platform for enterprise content management](http://www.nuxeo.com) and packaged applications for Document Management, Digital Asset Management and Case Management. Designed by developers for developers, the Nuxeo platform offers a modern architecture, a powerful plug-in model and extensive packaging capabilities for building content applications.

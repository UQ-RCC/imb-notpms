# NotPMS PPMS Tracker

The PPMS Tracker used internally at IMB.

## Installation

* Compile the fatjar, `gradle fatjar`
* Run `notpms/build/libs/notpms-full.jar`
  - Built with Java 11, but should work with Java 8

## Configuration
Put in `/etc/notpms.ini` or `%ProgramData%\notpms.ini`.
```ini
[ppms]
code=$INSTRUMENT_CODE
id=$INSTRUMENT_ID
pf=$PLATFORM
pum_apikey=$PUM_API_KEY
url=https://ppms.server/ppms

logoff=15
max_gap=15

# Alert thresholds
alert = 240
alert = 60
alert = 30
alert = 15
```

## License

This project is licensed under the [Apache License, Version 2.0](https://opensource.org/licenses/Apache-2.0):

Copyright &copy; 2019 [The University of Queensland](http://uq.edu.au/)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

* * *

This project uses [`Apache HttpComponents`](https://hc.apache.org/index.html) library which is licensed under the terms outlined at http://www.apache.org/licenses/.

* * *

This project uses the [`ini4j`](http://ini4j.sourceforge.net) library which is licensed under the terms outlined at http://ini4j.sourceforge.net/license.html.

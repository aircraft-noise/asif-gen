# asif-gen

DSL for generating AEDT ASIF files.

Current status is "proof of concept", handles one very specific case.
I'm already unhappy with some aspects of the DSL, it will almost certainly change in the future...

## Usage

You will need to install Java, and the Leiningen tool.

	https://leiningen.org

I recommend doing all this on a Mac/Unix machine, or if you are on Windows, install/use WSL.
It may be possible to run all this within Windows itself, see the Leiningen install instructions...

To generate an ASIF-XML file from a TCR JSON file:

    lein generate-asif ./data/aedt/FA_Noise_Examples.180401.json test.xml

## License

Copyright © 2018 DCJ
All rights reserved
Will select some open-source license in the future...

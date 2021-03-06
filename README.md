# asif-gen

A tool for generating [AEDT](https://aedt.faa.gov) [ASIF](https://aedt.faa.gov/Documents/AEDT2d_ASIFReferenceGuide.pdf) files.

Two input files are required:

1. A template describing the AEDT study
2. A JSON data file of aircraft flights

Optionally, one or more filters over the aircraft flights data can be specified.

## BUGS and Limitations

asif-gen currently assumes the time offset between Pacific time and UTC is 7 hours, which is only correct during daylight savings time.

Flights originating or departing from the heliports SUMED and VMCED are removed from the generated output file.

## Usage

bin/asif-gen --help
Generates AEDT ASIF XML from the provided study template and flights data.

```
Usage: asif-gen [options]

Options:
      --study file                   Study filename, supported formats/extensions: yaml, json, and edn
      --flights file                 Flights filename (TCR-JSON)
      --filter name             []   Name of filter to invoke on flights, this option can provided multiple times...
      --flight-id icao.segment  nil  Extract only the flight with the given ICAO/segment, overrides any filters
      --output file                  Filename for generated ASIF, if not given, and flight-id is, output file is icao-segment.xml
  -h, --help
```

## Study Template File

asif-gen provides a Domain Specific Language (DSL) for specifying [AEDT/ASIF](https://aedt.faa.gov/Documents/AEDT2d_ASIFReferenceGuide.pdf) input files.

Three equivalent/isomorphic file formats are supported, follow the links below for an example in each of the formats:

* [YAML (.yaml)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.yaml)
* [EDN (.edn)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.edn)
* [JSON (.json)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.json)

Each of these formats generally map 1-1 to the equivalent ASIF XML, in a less verbose and easier to read/author format.

EDN is the native format of asif-gen, the alternate YAML and JSON representations are converted to EDN when read.

The DSL supports a number of "magic" values which are replaced with functions that process the input flights data, and return/insert the resulting generated DSL.

Currently these magic values include:

* !generate-tos-track-nodes! - Generates track nodes for each position of each flight in the flights data file
* !get-airports! - Generates a list of all airports referenced in the flights data file
* !get-earliest-start! - Is replaced by the earliest start time found in the flights data file
* !study-name! - Replaced by a string composed of the flights filename, the output filename, and the study filename

These are the initial functions that were needed for our initial AEDT testing, almost certainly many more such functions will be implemented as required.

## Filters

Filters are an experimental new feature that enable the user to select subsets of the flights data.

The current filters supported are:

* both - both arrivals and departures
* arrivals - arrivals only
* departures - departures only
* KSFO - only flights to/from KSFO
* KOAK - only flights to/from KOAK
* KSJC - only flights to/from KSJC

If no filter is specified on the command line, the "both" filter is applied by default.  If any filter is specified, no default is applied and all desired filters must be individually specified.

Multiple filters specifications are supported, are applied left to right to the input data, and there is an implicit "logical AND" of the specified filters.
For example:

```
asif-gen --filter arrivals --filter KSFO
```

Would first filter the input data for arrivals, then for KSFO, resulting only flights arriving to KSFO being included in the subsequent ASIF generation.

Bundling filtering functionality into asif-gen is almost certainly the wrong thing to do long term, but is included now for reasons of expediency.

## JSON Flights Data File

Currently the only supported format is "TCR", which we are using for reasons of expediency until a better format is designed and documented.  [An example is included here.](https://github.com/aircraft-noise/asif-gen/blob/develop/data/flights/flights-20180401.json). Be forewarned that as soon as an alternate flights data file format is defined, the TCR format will be quickly deprecated.

## Running asif-gen

You will need to install Java (JDK prefered, JRE OK)

Clone this repo from GitHub.

The currently compiled (for Java) executable should be in bin/asif-gen

An example invocation:

```
bin/asif-gen --filter arrivals --study data/examples/tracknode-study.yaml --flights data/flights/flights-20180401.json --output arrivals.xml
```

If you want to run via Leiningen:

```
lein run --  --filter arrivals --study data/examples/tracknode-study.yaml --flights data/flights/flights-20180401.json --output arrivals.xml
```

Windows users must run ```asif-gen``` via Leiningen, and will need to install Java and [Leiningen](https://leiningen.org) as follows:

1. Install Java
2. Install the [lein.bat](https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein.bat) file onto your path
3. Run ```lein self-install```
4. Run ```lein version``` to confirm your Leiningen and Java versions
5. From the base of the asif-gen repo (directory) that you have downloaded to your machine, confirm you can run ```lein run -- --help``` which should print out the usage instructions
6. asif-gen should then be runable via Leiningen


## Development

You will need to [download/install the Leiningen tool](https://leiningen.org)

In accordance with the [gitflow methodology](https://github.com/nvie/gitflow), create a feature branch for your modifications.

Obviously you can develop and test in the Clojure REPL, but when you want to create a standalone command line executable, run:

```
lein bin
```

## License

Copyright © 2019 Clark Communications Corporation
All rights reserved

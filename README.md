# asif-gen

A tool for generating AEDT ASIF files.

Two input files are required:

1. A template describing the AEDT study
2. A JSON data file of aircraft flights

Optionally, one or more filters over the aircraft flights data can be specified.

## Usage

bin/asif-gen --help
Generates AEDT ASIF XML from the provided study template and flights data.

```
Usage: asif-gen [options]

Options:
      --study file        Study filename, supported formats/extensions: .yaml, .json, and .edn
      --flights file      Flights filename (TCR-JSON)
      --filter name   []  Name of filter to invoke on flights, this option can provided multiple times...
      --output file       Filename for generated ASIF
  -h, --help
```

## Study Template File

asif-gen provides a Domain Specific Language (DSL) for specifying AEDT/ASIF input files.

Three equivalent/isomorphic file formats are supported, follow the links below for one example in each of the three formats:

* [YAML (.yaml)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.yaml)
* [EDN (.edn)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.edn)
* [JSON (.json)](https://github.com/aircraft-noise/asif-gen/blob/develop/data/examples/tracknode-study.json)

Each of these formats generally map 1-1 to the equivalent ASIF XML, in a less verbose and easier to read/author format.

EDN is the native format of asif-gen, the alternate YAML and JSON representations are converted to EDN when read.

The DSL supports a number of "magic" values which are replaced with functions that process the input flights data, and return/insert the resulting generated DSL.

Currently these magic values include:

* !generate-tos-track-nodes! - Generates track nodes for each position of each flight in the flights data file
* !get-airports! - Generates a list of all airports referenced in the flights data file

This are the first two functions that were needed for our initial AEDT testing, almost certainly many more such functions will be implemented as needed.

## Filters

Filters are an experimental new feature that enable the user to select subsets of the flights data.

The current filters supported are:

* both - both arrivals and departures
* arrivals - arrivals only
* departures - departures only
* KSFO - only flights to/from KSFO
* KOAK - only flights to/from KOAK
* KSJC - only flights to/from KSJC

Multiple filters specifications are enabled, the are applied left to right to the input data, and there is an implicit "logical AND" of the specified filters.
For example:

```
asif-gen --filter arrivals --filter KSFO
```

Would first filter the input data for arrivals, then for KSFO, resulting only flights arriving to KSFO being included in the subsequent ASIF generation.

Bundling filtering functionality into asif-gen is almost certainly the wrong thing to do, long term, but is included now for reasons of expediency.

## Running asif-gen

You will need to install Java (JDK prefered, JRE OK)

Clone this repo from repo.

The currently compiled (for Java) executable should be in bin/asif-gen

An example invocation:

```
bin/asif-gen --filter arrivals --study data/examples/tracknode-study.yaml --flights data/flights/flights-20180401.json --output arrivals.xml
```

If you want to run via Leiningen:

```
lein run --  --filter arrivals --study data/examples/tracknode-study.yaml --flights data/flights/flights-20180401.json --output arrivals.xml
```

## Development

You will need to [download/install the Leiningen tool](https://leiningen.org)

In accordance with the [gitflow methodology](https://github.com/nvie/gitflow), create a feature branch for your modifications.

Obviously you can develop and test in the Clojure REPL, but when you want to create a standalone command line executable, run:

```
lein bin
```

## License

Copyright © 2018 DCJ
All rights reserved
Will select some open-source license in the future...

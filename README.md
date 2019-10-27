# jGnuCash2Qif

An experimental Java version of my .Net/C# https://github.com/Jason-Carter/GnuCash2Qif program to help me relearn the language. This is currently feature synchronised with the C# version, so please refer to the README.md file on that project for further details.

## Build Prerequisites

Java 8 is required at a minimum to build this project against.

## Build Instructions

```
$ git clone https://github.com/Jason-Carter/jGnuCash2Qif.git
$ cd jGnuCash2Qif
```

TODO: I've built this in IntelliJ, but haven't got a command line alternative for running a build yet.

## Usage

I've only run this through the IntelliJ IDE with the following Program arguments (set in the Run / Edit Configurations screen):

```
-d "C:/accounts.sql.gnucash.sqlite" -o "C:/output.qif"
```

TODO: package this up to be used easily.

## Options

 * `-d` Datafile for GnuCash: Must be an Sqlite datafile (does not work with the XML files)
 * `-o` Output File which will be in QIF format
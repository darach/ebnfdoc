# **ebnfdoc** [![Build Status](https://travis-ci.org/darach/ebnfdoc.png)](https://travis-ci.org/darach/ebnfdoc)

A simple project to generate documentation in markdown format from EBNF source files.

To build a distribution and test:

```
$ ant
```

Example usage:

```
$ java -jar ebnfdoc.jar -d docs test/ebnf.ebnf
```

Generates by default into ./docs directory ( change with -d cmd line option).

Sample [input](test/ebnf.ebnf) and [output](docs/ebnf.ebnf.md).

Enjoy!


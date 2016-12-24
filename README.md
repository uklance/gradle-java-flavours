# gradle-java-flavours [![Build Status](https://travis-ci.org/uklance/gradle-java-flavours.svg?branch=master)](https://travis-ci.org/uklance/gradle-java-flavours) [![Coverage Status](https://coveralls.io/repos/github/uklance/gradle-java-flavours/badge.svg?branch=master)](https://coveralls.io/github/uklance/gradle-java-flavours?branch=master)

A Gradle plugin to add Android style flavours to a Java project

## Usage:

```groovy
apply plugin: 'com.lazan.javaflavours'
javaFlavours {
	flavours = ['free', 'paid']
}
```

## Directories:

- `src/main/java` - Common java sources
- `src/main/resources` - Common resources
- `src/test/java` - Common tests
- `src/test/resources` - Common test resources
- `src/<flavour>/java` - Flavour specific java sources
- `src/<flavour>/resources` - Flavour specific resources
- `src/<flavour>Test/java` - Flavour specific tests
- `src/<flavour>Test/resources` - Flavour specific test resources

## Configurations:

- `<flavour>Compile`
- `<flavour>CompileOnly`
- `<flavour>CompileClasspath`
- `<flavour>Runtime`

## Test Configurations

- `<flavour>TestCompile`
- `<flavour>TestCompileOnly`
- `<flavour>TestCompileClasspath`
- `<flavour>TestRuntime`

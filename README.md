# gradle-java-flavours [![Build Status](https://travis-ci.org/uklance/gradle-java-flavours.svg?branch=master)](https://travis-ci.org/uklance/gradle-java-flavours) [![Coverage Status](https://coveralls.io/repos/github/uklance/gradle-java-flavours/badge.svg?branch=master)](https://coveralls.io/github/uklance/gradle-java-flavours?branch=master)

A Gradle plugin to add Android style flavours to a Java project

## Usage:

```groovy
plugins {
  id "com.lazan.javaflavours" version "1.2"
}
javaFlavours {
    flavour 'free'
    flavour 'paid'
    
    testJavaPathResolver = { String flavour -> "src/${flavour}-test/java" }
    testResourcesPathResolver = { String flavour -> "src/${flavour}-test/resources" }
}
dependencies {
    compile         'aaa:aaa:1.0'
    freeCompile     'bbb:bbb:2.0'
    freeTestCompile 'ccc:ccc:3.0'
    paidRuntime     'ddd:ddd:4.0'
}
```

You find detailed installation instructions at https://plugins.gradle.org/plugin/com.lazan.javaflavours.

## Directories:

- `src/main/java` - Common java sources
- `src/main/resources` - Common resources
- `src/test/java` - Common tests
- `src/test/resources` - Common test resources
- `src/<flavour>/java` - Flavour specific java sources (can be configured)
- `src/<flavour>/resources` - Flavour specific resources (can be configured)
- `src/<flavour>Test/java` - Flavour specific tests (can be configured)
- `src/<flavour>Test/resources` - Flavour specific test resources (can be configured)

## Tasks

- `compile<flavour>Java`
- `compile<flavour>TestJava`
- `<flavour>Classes`
- `<flavour>Jar`
- `<flavour>Test`
- `<flavour>TestClasses`
- `process<flavour>Resources`
- `process<flavour>TestResources`

## Configurations:

- `<flavour>Compile`
- `<flavour>CompileOnly`
- `<flavour>CompileClasspath`
- `<flavour>Runtime`
- `<flavour>TestCompile`
- `<flavour>TestCompileOnly`
- `<flavour>TestCompileClasspath`
- `<flavour>TestRuntime`

# Introduction
It’s a Java Instrumentation project focused on creating a Java agent to help analyze the usage of the Log4j library.
It leverages the [Byte Buddy](https://bytebuddy.net/) Java library for code generation and modification of Java classes.

#### Reasoning behind selecting Byte Buddy 

- A light-weight library
- Offers excellent performance
- Stable
- Established track record. Used by many popular framework such as Mockito, Hibernate, Jackson, Selenium etc.
- Doesn't require an understanding of Java byte code


### Project Structure
It’s a Maven project named Log4jInstrument that consists of two modules.

- **agent** (Responsible for creating an agent, which is essentially a specially crafted JAR file.)
- **application** (a tiny application where the agent can be tested.)


### Installation

#### Creating jar
Run the following command to generate the JAR files for both modules.

`mvn clean package`

Generates the JARs in target/ directory of respective module

- _agent/target/**konvu-log4j-agent-final-1.0-SNAPSHOT.jar**_
- _application/target/**application-1.0-SNAPSHOT-jar-with-dependencies.ja**r_


#### Agent utilization
 
Agent jar can be used in two ways.

1. STATIC : will statically load the agent using -javaagent parameter at JVM startup
2. DYNAMIC : will dynamically load the agent into the running JVM using JAVA Attach API

For STATIC use, start the JVM as following

`java -javaagent:konvu-log4j-agent-final-1.0-SNAPSHOT.jar -jar application-final-1.0-SNAPSHOT.jar`

#### Agent functionality

1. Intercepts **Log4j** logger methods (info, warn, debug, error, trace) and collects following data
   - the message and arguments passed to logging method
   - Log level such as INFO, DEBUG etc.
   - class, method name and line number of caller of logging method was invoked
2. Intercepts all **toString** method and prepend following details
   - class, method name and line number of caller
   - Class on which toString method was invoked

### Performance impact

It introduces an overhead of **3000 nanoseconds** per invocation of the logging method. This overhead is calculated based on the application using a single implementation of the `_org.slf4j.Logger_` class. However, since multiple implementations of the Logger class can exist within a single application, the overhead may increase proportionally with the number of Logger implementations.

### References used

- [Byte Buddy Tutorials](https://bytebuddy.net/#/tutorial)
- Other online materials
- ChatGPT

## Overview 

This repo defines a JUnit5 extension to santisise tests that are potentially flaky. This is achieved by intercepting test execution, and 
programmatically enforcing assumptions that may lead to flakiness. The respective tests will be flagged as ignored when executed. 


## SanitiseNetworkDependenciesExtension

The extension is `SanitiseNetworkDependenciesExtension`.

This extension intercepts exceptions that indicate that tests try to access the network. Flakiness occurs as tests that would otherwise succeed 
result in failure or error when the network is unavailable. 

The following exceptions are intercepted:
1. `java.net.NoRouteToHostException`
2. `java.net.SocketException`
3. `java.net.UnknownHostException`

Those exceptions are also caught in fixtures (`@BeforeEach` and `@AfterEach`) consistent with the standard behaviour of junit, i.e.
errors in fixtures lead to errors in tests. Network exceptions occurring in static fixtures (`@BeforeAll` and `@AfterAll`)
are not intercepted -- the standard junit behaviour is that this will lead to tests being ignored. 
The project contains test cases to document this behaviour.

To use the extension, add the following annotation to test classes:

```java
import nz.ac.wgtn.saflate.network.junit5.SanitiseNetworkDependenciesExtension;

@ExtendWith(SanitiseNetworkDependenciesExtension.class)
```



### Usage

This artifact is (not yet) in the central Maven repo. To use it, install the project locally by running `mvn install`, then use the following dependency in your project:

```xml
<dependency>
    <groupId>nz.ac.wgtn.ecs.saflate</groupId>
    <artifactId>saflate-network-junit5</artifactId>
    <version>0.0.9</version>
    <scope>test</scope>
</dependency>
```

(doublecheck for the correct version number)

### Concurrent Test Execution

By default, the extension is based on the assumption that tests are executed sequentially. JUnit5 contains experimental support for concurrent execution, to use
this, tests must be annotated with `@Execution(ExecutionMode.CONCURRENT)` . To support this, network exceptions can be tracked per-thread. This can be enabled 
by setting the system property `saflate.supportconcurrent-test-execution = true`. 

Note that this can create false negatives if network connections are used in background threads.


### Limitations

1. Due to issues with class loaders (exception classes in the standard library cannot see classes defined in libraries), the instrumented classes
communicate that the exceptions have been instantiated via a system property. 

2. The event monitored in that instances are being created, not actually be thrown. This could create FPs, resulting in more tests being ignored.
We consider this scenario as being highly unlikely.

## Auto-Deployment

The extensions can be applied to all tests by adding the following runtime parameter: `-Djunit.jupiter.extensions.autodetection.enabled=true`. No further configuration is required, see also https://junit.org/junit5/docs/current/user-guide/#extensions-registration-automatic.  

## Extending Saflate with Custom Exceptions

The list of extensions to be sanitised can be extended by implementing and providing the following service (using serviceloaders):

`nz.ac.wgtn.saflate.network.junit5.NetworkExceptionProvider`. 

## Change log

### Version 1.0.0 

initial release

### Version 1.1.0

- fixed JUnit dependency issue causing builds to fail with junit 5.8 

## Related Work

https://github.com/unruly/junit-rules has two rules `QuarantineRule` and `ReliabilityRule` with a similar intent.
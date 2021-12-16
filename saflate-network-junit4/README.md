## Overview 

This repo defines junit4 rules to santisise tests that are potentially flaky. This is achieved by intercepting test execution, and 
programmatically enforcing assumptions that may lead to flakiness. The respective tests will be flagged as ignored when executed. 

## SanitiseNetworkDependencies

The rule is `SanitiseNetworkDependenciesRule`.

This rules intercepts exceptions that indicate that tests try to access the network. Flakiness occurs as tests that would otherwise succeed 
result in failure or error when the network is unavailable. 

### Usage

To use the rule, add the following field to test classes:

```java
@Rule public SanitiseNetworkDependenciesRule rule = new SanitiseNetworkDependenciesRule();
```

This artifact is (not yet) in the central Maven repo. To use it, install the project locally by running `mvn install`, then use the following dependency in your project:

```xml
<dependency>
    <groupId>nz.ac.wgtn.ecs.saflate</groupId>
    <artifactId>saflate-network-junit4</artifactId>
    <version>0.0.1</version>
    <scope>test</scope>
</dependency>
```

(doublecheck for the correct version number)

### Limitations

1. Due to issues with class loaders (exception classes in the standard library cannot see classes defined in libraries), the instrumented classes
communicate that the exceptions have been instantiated via a system property.
2. The event monitored in that instances are being created, not actually be thrown. This could create FPs, resulting in more tests being ignored.
We consider this scenario as being highly unlikely.
3. If network exceptions are thrown in native methods, and tests fail (i.e. the test does niot result in an error with the exception visible in the stacktrace), then this could result in a false negative.

### Extending Saflate with Custom Exceptions

The list of extensions to be sanitised can be extended by implementing and providing the following service (using serviceloaders):

`nz.ac.wgtn.saflate.network.junit4.NetworkExceptionProvider`.

## Related Work

https://github.com/unruly/junit-rules has two rules `QuarantineRule` and `ReliabilityRule` with a similar intent.
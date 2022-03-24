## README 

This is a proof-of-concept to port saflate to [spock](https://spockframework.org/) using its extension mechanism. It is not ready for production. 

To use it, add it as dependency (install local), then add the following annotation to the tests that may fail or error if the network is unavailable: 
`nz.ac.wgtn.saflate.network.spock.@IgnoreOnNetworkError`.

example: the following test is being skipped. 

```groovy
import spock.lang.Specification
class ExampleSpec extends Specification {

    @IgnoreOnNetworkError
    def "foo"() {
        throw new UnknownHostException("boo ! ")
        expect: true
    }
}
```


package nz.ac.wgtn.saflate.network.spock

import spock.lang.Specification

class ExampleSpec extends Specification {

    @IgnoreOnNetworkError
    def "should be a simple assertion"() {
        throw new UnknownHostException("boo ! ")
        expect: true
    }
}

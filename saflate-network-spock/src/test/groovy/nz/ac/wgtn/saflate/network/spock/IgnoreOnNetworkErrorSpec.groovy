package nz.ac.wgtn.saflate.network.spock

import spock.lang.Specification
import spock.util.EmbeddedSpecRunner

/**
 * Check that the semantics of tests not dealing with network exceptions is not altered.
 *
 * The mechanism to check for failed tests by looking for errors looks unsatisfactory, but for failing tests the
 * reference to result is not passed to the then block.
 *
 * @author jens dietrich
 */
class IgnoreOnNetworkErrorSpec extends Specification {

    EmbeddedSpecRunner runner = new EmbeddedSpecRunner()

    def setup() {
        runner.addClassImport(EmbeddedSpecRunner.SummarizedEngineExecutionResults)
    }

    def cleanup() {
    }

    def "unannotated succeeding test without network issue should succeed"() {
        when:
            def result = runner.runWithImports """
                class FooSuccessUnannotated extends spock.lang.Specification {
                    def "should succeed"() {
                        expect: true
                    }
                }
            """
        then:
            result.testsStartedCount==1
            result.testsAbortedCount==0
            result.testsFailedCount==0
            result.testsSucceededCount==1
            result.testsSkippedCount==0
    }

    def "annotated succeeding test should still succeed"() {
        when:
        def result = runner.runWithImports """
            class FooSuccessAnnotated extends spock.lang.Specification {
                @nz.ac.wgtn.saflate.network.spock.IgnoreOnNetworkError
                def "should succeed"() {
                    expect: true
                }
            }
        """

        then:
            result.testsStartedCount==1
            result.testsAbortedCount==0
            result.testsFailedCount==0
            result.testsSucceededCount==1
            result.testsSkippedCount==0
    }

    def "unannotated failing test without network issue should fail"() {
        when:
            def result = runner.run """
                class FooOtherFailureUnannotated extends spock.lang.Specification {
                    def "should fail"() {
                        expect: false
                    }
                }
            """

        then:
            thrown Error
    }

    def "annotated failing test without network issue should still fail"() {
        when:
        def result = runner.run """
                class FooOtherFailureAnnotated extends spock.lang.Specification {
                    @nz.ac.wgtn.saflate.network.spock.IgnoreOnNetworkError
                    def "should fail"() {
                        expect: false
                    }
                }
            """

        then:
            thrown Error
    }

    def "unannotated test failing with network exception should fail"() {
        when:
        def result = runner.run """
                class FooFailureDueToNetworkExceptionUnannotated extends spock.lang.Specification {
                    def "failing test"() {
                        throw new UnknownHostException()
                        expect: true
                    }
                }
            """

        then:
            thrown UnknownHostException
    }

    def "annotated test failing with network exception should be skipped"() {
        when:
        def result = runner.run """
                class FooFailureDueToNetworkExceptionAnnotated extends spock.lang.Specification {
                    @nz.ac.wgtn.saflate.network.spock.IgnoreOnNetworkError
                    def "failing test"() {
                        throw new UnknownHostException()
                        expect: true
                    }
                }
            """

        then:
            result.testsStartedCount==1
            result.testsAbortedCount==1
            result.testsFailedCount==0
            result.testsSucceededCount==0
    }

}

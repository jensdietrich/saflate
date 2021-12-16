package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class AbstractTestTestsWithNetworkDependencies {

    private Set<String> testsWithFailedAssumptions = null;
    private Set<String> testsThatFailed = null;

    private TestExecutionListener listener = new TestExecutionListener() {

        @Override
        public void executionSkipped(TestIdentifier testIdentifier, String reason) {
            if (testIdentifier.getType()== TestDescriptor.Type.TEST) {
                testsWithFailedAssumptions.add(testIdentifier.getDisplayName());
            }
        }

        @Override
        public void executionStarted(TestIdentifier testIdentifier) {
            TestExecutionListener.super.executionStarted(testIdentifier);
        }

        @Override
        public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
            if (testIdentifier.getType()== TestDescriptor.Type.TEST) {
                if (testExecutionResult.getStatus() == TestExecutionResult.Status.FAILED) {
                    testsThatFailed.add(testIdentifier.getDisplayName());
                }
                else if (testExecutionResult.getStatus() == TestExecutionResult.Status.ABORTED) {
                    testsWithFailedAssumptions.add(testIdentifier.getDisplayName());
                }
            }
        }
    };

    @BeforeEach
    public void setup() {
        this.testsWithFailedAssumptions = new HashSet<>();
        this.testsThatFailed = new HashSet<>();
    }

    @AfterEach
    public void tearDown() {
        this.testsWithFailedAssumptions = null;
        this.testsThatFailed = null;
    }

    private void executeTests(Class testClass) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
            .selectors(selectClass(testClass))
            .build();
        Launcher launcher = LauncherFactory.create();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);
    }

    // utility since Set::of is only available in Java 9 +
    private Set setOf(String... elements) {
        return Stream.of(elements).collect(Collectors.toSet());
    }

    @Test
    public void testTestsWithRule() {
        executeTests(TestsWithNetworkDependencyAndSanitiseNetworkDependenciesExtension.class);

        assertEquals(
            setOf(
                "testIgnore()", "testSkipped()","testWithSocketException()",
                "testUnknownHostException()","testNoRouteToHostException()"
            ),
            testsWithFailedAssumptions
        );
        assertEquals(
            setOf(
                "testFails()", "testOtherException()", "testUnexpectedOtherException()"
            ),
            testsThatFailed
        );
    }

    @Test
    public void testTestsWithoutRule() {
        executeTests(TestsWithNetworkDependency.class);

        assertEquals(
            setOf("testIgnore()", "testSkipped()"),
            testsWithFailedAssumptions
        );
        assertEquals(
            setOf(
                "testFails()", "testOtherException()", "testUnexpectedOtherException()",
                "testWithSocketException()", "testUnknownHostException()", "testNoRouteToHostException()"
            ),
            testsThatFailed
        );
    }

    @Test
    public void testBeforeEachWithoutRule () {
        executeTests(TestsWithNetworkDependencyInBeforeEach.class);

        assertEquals(
            setOf("test()"),
            testsThatFailed
        );
        assertEquals(
            setOf(),
            testsWithFailedAssumptions
        );
    }

    @Test
    public void testBeforeEachWithRule () {
        executeTests(TestsWithNetworkDependencyInBeforeEachAndSanitiseNetworkDependenciesExtension.class);

        assertEquals(
            setOf(),
            testsThatFailed
        );
        assertEquals(
            setOf("test()"),
            testsWithFailedAssumptions
        );
    }

    @Test
    public void testAfterEachWithoutRule () {
        executeTests(TestsWithNetworkDependencyInAfterEach.class);

        assertEquals(
                setOf("test()"),
                testsThatFailed
        );
        assertEquals(
                setOf(),
                testsWithFailedAssumptions
        );
    }

    @Test
    public void testAfterEachWithRule () {
        executeTests(TestsWithNetworkDependencyInAfterEachAndSanitiseNetworkDependenciesExtension.class);

        assertEquals(
                setOf(),
                testsThatFailed
        );
        assertEquals(
                setOf("test()"),
                testsWithFailedAssumptions
        );
    }

}


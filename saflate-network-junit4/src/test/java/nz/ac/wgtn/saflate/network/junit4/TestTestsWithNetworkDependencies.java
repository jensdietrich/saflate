package nz.ac.wgtn.saflate.network.junit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class TestTestsWithNetworkDependencies {

    private Set<String> testsWithFailedAssumptions = null;
    private Set<String> testsThatFailed = null;

    private RunListener listener = new RunListener() {
        @Override
        public void testAssumptionFailure(Failure failure) {
            super.testAssumptionFailure(failure);
            testsWithFailedAssumptions.add(failure.getDescription().getMethodName());
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            super.testFailure(failure);
            testsThatFailed.add(failure.getDescription().getMethodName());
        }
    };

    @Before
    public void setup() {
        this.testsWithFailedAssumptions = new HashSet<>();
        this.testsThatFailed = new HashSet<>();
    }

    @After
    public void tearDown() {
        this.testsWithFailedAssumptions = null;
        this.testsThatFailed = null;
    }

    @Test
    public void testTestsWithRule() {
        JUnitCore junit = new JUnitCore();
        junit.addListener(listener);
        junit.run(TestsWithNetworkDependencyAndSanitiseNetworkDependenciesRule.class);

        assertTrue(testsWithFailedAssumptions.contains("testWithSocketException"));
        assertTrue(testsWithFailedAssumptions.contains("testUnknownHostException"));
        assertTrue(testsWithFailedAssumptions.contains("testIgnore"));
        assertEquals(3,testsWithFailedAssumptions.size());

        assertTrue(testsThatFailed.contains("testFails"));
        assertTrue(testsThatFailed.contains("testOtherException"));
        assertTrue(testsThatFailed.contains("testUnexpectedOtherException"));
        assertEquals(3,testsThatFailed.size());
    }

    @Test
    public void testTestsWithoutRule() {
        JUnitCore junit = new JUnitCore();
        junit.addListener(listener);
        junit.run(TestsWithNetworkDependency.class);

        assertTrue(testsWithFailedAssumptions.contains("testIgnore"));
        assertEquals(1,testsWithFailedAssumptions.size());

        assertTrue(testsThatFailed.contains("testFails"));
        assertTrue(testsThatFailed.contains("testOtherException"));
        assertTrue(testsThatFailed.contains("testUnexpectedOtherException"));
        assertTrue(testsThatFailed.contains("testWithSocketException"));
        assertTrue(testsThatFailed.contains("testUnknownHostException"));
        assertEquals(5,testsThatFailed.size());
    }
}


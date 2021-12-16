package nz.ac.wgtn.saflate.network.junit5;


import org.junit.jupiter.api.BeforeAll;

public class TestTestsWithNetworkDependenciesNoConcTestExecution extends AbstractTestTestsWithNetworkDependencies{

    @BeforeAll
    public static void setupSystemVar() {
        System.clearProperty(SanitiseNetworkDependenciesExtension.SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION);
    }

}


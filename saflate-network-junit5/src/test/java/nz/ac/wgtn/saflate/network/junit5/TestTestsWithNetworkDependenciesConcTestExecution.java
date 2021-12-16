package nz.ac.wgtn.saflate.network.junit5;


import org.junit.jupiter.api.BeforeAll;

public class TestTestsWithNetworkDependenciesConcTestExecution extends AbstractTestTestsWithNetworkDependencies{

    @BeforeAll
    public static void setupSystemVar() {
        System.setProperty(SanitiseNetworkDependenciesExtension.SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION,String.valueOf(true));
    }

    @BeforeAll
    public static void resetSystemVar() {
        System.clearProperty(SanitiseNetworkDependenciesExtension.SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION);
    }

}


package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SanitiseNetworkDependenciesExtension.class)
public class TestsWithNetworkDependencyInAfterEachAndSanitiseNetworkDependenciesExtension {

    @AfterEach
    public void tearDown() throws UnknownHostException {
        throw new UnknownHostException();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}

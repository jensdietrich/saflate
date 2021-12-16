package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test is to document the behaviour of tests if the global fixture @AfterAll encounters an error
 * - then all tests will be ignored (this is different from how errors in @AfterEach are handled).
 * @author jens dietrich
 */
public class TestsWithNetworkDependencyInAfterAll {

    @AfterAll
    public void tearDown() throws UnknownHostException {
        throw new UnknownHostException();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}

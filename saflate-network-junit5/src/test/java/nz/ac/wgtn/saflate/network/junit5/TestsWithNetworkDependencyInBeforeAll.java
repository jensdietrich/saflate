package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test is to document the behaviour of tests if the global fixture @BeforeAll encounters an error
 * - then all tests will be ignored (this is different from how errors in @BeforeEach are handled).
 * @author jens dietrich
 */
public class TestsWithNetworkDependencyInBeforeAll {

    @BeforeAll
    public static void setup() throws UnknownHostException {
        throw new UnknownHostException();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}

package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.*;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestsWithNetworkDependencyInBeforeEach {

    @BeforeEach
    public void setup() throws UnknownHostException {
        throw new UnknownHostException();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}

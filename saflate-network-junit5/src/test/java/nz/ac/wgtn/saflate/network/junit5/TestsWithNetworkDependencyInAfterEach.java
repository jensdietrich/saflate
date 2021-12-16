package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestsWithNetworkDependencyInAfterEach {

    @AfterEach
    public void tearDown() throws UnknownHostException {
        throw new UnknownHostException();
    }

    @Test
    public void test() {
        assertTrue(true);
    }

}

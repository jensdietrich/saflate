package nz.ac.wgtn.saflate.network.junit5;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestsWithNetworkDependency {

    // expected behaviour: test will get ignored
    @Test
    public void testWithSocketException () {
        try {
            throw new SocketException();
        }
        catch (Exception x) {
            Assertions.assertTrue(false);
        }
    }

    // expected behaviour: test should be ignored
    @Test
    public void testUnknownHostException () {
        try {
            throw new UnknownHostException();
        }
        catch (Exception x) {
            Assertions.assertTrue(false);
        }
    }

    // expected behaviour: test should be ignored
    @Test
    public void testNoRouteToHostException () {
        try {
            throw new NoRouteToHostException();
        }
        catch (Exception x) {
            Assertions.assertTrue(false);
        }
    }

    // expected behaviour: test should result in error
    @Test
    public void testOtherException () {
        try {
            throw new NullPointerException();
        }
        catch (Exception x) {
            Assertions.assertTrue(false);
        }
    }

    // expected behaviour: test should succeed
    @Test
    public void testExpectedOtherException () {
        assertThrows(NullPointerException.class, () -> {
            throw new NullPointerException();
        });
    }

    // expected behaviour: test will fail
    @Test
    public void testUnexpectedOtherException () {
        throw new NullPointerException();
    }

    // expected behaviour: test should succeed
    @Test
    public void testSucceeds () {
        Assertions.assertTrue(true);
    }

    // expected behaviour: test should fail
    @Test
    public void testFails () {
        Assertions.assertTrue(false);
    }

    // expected behaviour: test should be ignored
    @Test
    public void testIgnore () {
        Assumptions.assumeTrue(false);
        Assertions.assertTrue(false);
    }

    // expected behaviour: test should be ignored
    @Test
    @Disabled
    public void testSkipped () {
        assertTrue(true);
    }

}

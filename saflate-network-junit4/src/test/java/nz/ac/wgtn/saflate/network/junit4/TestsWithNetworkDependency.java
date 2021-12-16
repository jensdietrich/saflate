package nz.ac.wgtn.saflate.network.junit4;

import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestsWithNetworkDependency {


    // expected behaviour: test will get ignored
    @Test
    public void testWithSocketException () {
        try {
            throw new SocketException();
        }
        catch (Exception x) {
            fail();
        }
    }

    // expected behaviour: test should be ignored
    @Test
    public void testUnknownHostException () {
        try {
            throw new UnknownHostException();
        }
        catch (Exception x) {
            fail();
        }
    }

    // expected behaviour: test should result in error
    @Test
    public void testOtherException () {
        try {
            throw new NullPointerException();
        }
        catch (Exception x) {
            fail();
        }
    }

    // expected behaviour: test should succeed
    @Test(expected = NullPointerException.class)
    public void testExpectedOtherException () {
        throw new NullPointerException();
    }

    // expected behaviour: test will fail
    @Test(expected = IOException.class)
    public void testUnexpectedOtherException () {
        throw new NullPointerException();
    }

    // expected behaviour: test should succeed
    @Test
    public void testSucceeds () {
        assertTrue(true);
    }

    // expected behaviour: test should fail
    @Test
    public void testFails () {
        assertTrue(false);
    }

    // expected behaviour: test should be ignored
    @Test
    public void testIgnore () {
        Assume.assumeTrue(false);
        assertTrue(false);
    }

}

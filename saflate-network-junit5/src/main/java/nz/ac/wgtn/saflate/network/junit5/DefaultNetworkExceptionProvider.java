package nz.ac.wgtn.saflate.network.junit5;

import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Default provider.
 * @author jens dietrich
 */
public class DefaultNetworkExceptionProvider implements NetworkExceptionProvider {
    @Override
    public Class<? extends Exception>[] getNetworkExceptionClassesToBeSanitised() {
        return new Class[]{SocketException.class, UnknownHostException.class, NoRouteToHostException.class};
    }
}

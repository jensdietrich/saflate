package nz.ac.wgtn.saflate.network.junit5;

/**
 * Service to define which exceptions should be sanitised.
 * @author jens dietrich
 */
public interface NetworkExceptionProvider {
    Class<? extends Exception>[] getNetworkExceptionClassesToBeSanitised();
}

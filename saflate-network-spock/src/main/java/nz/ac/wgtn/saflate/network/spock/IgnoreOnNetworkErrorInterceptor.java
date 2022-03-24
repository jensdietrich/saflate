package nz.ac.wgtn.saflate.network.spock;

import org.junit.AssumptionViolatedException;
import org.spockframework.runtime.extension.AbstractMethodInterceptor;
import org.spockframework.runtime.extension.IMethodInvocation;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interceptor providing the semantics of @IgnoreOnNetworkError.
 * @author jens dietrich
 */
public class IgnoreOnNetworkErrorInterceptor extends AbstractMethodInterceptor {

    public static final Set<Class> NETWORK_EXCEPTION_CLASSES =
        Stream.of(SocketException.class, UnknownHostException.class, NoRouteToHostException.class)
        .collect(Collectors.toSet());


    private static boolean isDeepNetworkException(Throwable throwable) {
        for (Class clazz:NETWORK_EXCEPTION_CLASSES) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        Throwable cause = throwable.getCause();
        return cause!=null && isDeepNetworkException(cause);
    }

    // from PendingFeatureBaseInterceptor
    protected Exception markAsSkip(StackTraceElement[] stackTrace) {
        // @TODO -- TestAbortedException or AssumptionViolatedException ?
        Exception exception = new AssumptionViolatedException("Test relies on remote resources that are not available");
        exception.setStackTrace(stackTrace);
        return exception;
    }

    private void doIntercept(IMethodInvocation invocation) throws Throwable {
        try {
            invocation.proceed();
        } catch (AssertionError e) {
            // TODO check for network error
            throw markAsSkip(e.getStackTrace());
        } catch (Throwable e) {
            if (isDeepNetworkException(e)) {
                throw markAsSkip(e.getStackTrace());
            } else {
                throw e;
            }
        }
    }


    @Override
    public void interceptSpecExecution(IMethodInvocation invocation) throws Throwable {
        doIntercept(invocation);
    }

    @Override
    public void interceptFeatureExecution(IMethodInvocation invocation) throws Throwable {
        doIntercept(invocation);
    }

    @Override
    public void interceptFeatureMethod(IMethodInvocation invocation) throws Throwable {
        doIntercept(invocation);
    }

    @Override
    public void interceptIterationExecution(IMethodInvocation invocation) throws Throwable {
        doIntercept(invocation);
    }
}

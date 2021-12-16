package nz.ac.wgtn.saflate.network.junit5;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.junit.jupiter.api.extension.*;
import org.opentest4j.IncompleteExecutionException;
import org.opentest4j.TestAbortedException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Junit rule that catches network-related exceptions and turns them into failed assumptions.
 * For this purpose, the constructors of some network exceptions that are thrown when the network is unavailable are instrumented.
 * Due to issues with classloaders (exception classes in the standard library cannot see classes defined in libraries), the instrumented classes
 * communicate that the exceptions have been instantiated via a system property. This is brittle, a better design might be injecting some static field
 * (perhaps Runnable) that can be used to register observers for instance creation. Also, the event monitored in that instances are being created,
 * not actually be thrown. This could result in FPs, resulting in more tests being ignored.
 * In order to support thread-concurrent concurrency, the property tracked also includes the name of the thread in which the test is executed.
 * @author jens dietrich
 */
public class SanitiseNetworkDependenciesExtension implements TestExecutionExceptionHandler, BeforeTestExecutionCallback, AfterTestExecutionCallback, LifecycleMethodExecutionExceptionHandler {

    public static final String SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION = "saflate.supportconcurrent-test-execution";
    public static final Set<Class> NETWORK_EXCEPTION_CLASSES =
            StreamSupport.stream(ServiceLoader.load(NetworkExceptionProvider.class).spliterator(),false)
                .flatMap(provider -> Arrays.stream(provider.getNetworkExceptionClassesToBeSanitised()))
                .map(clazz -> {
                    System.out.println("saflate sanitisation will be applied to exceptions of type: " + clazz.getName());
                    return clazz;
                })
                .collect(Collectors.toSet());


        //new Class[]{SocketException.class, UnknownHostException.class, NoRouteToHostException.class};

    static class SetSystemPropertyOnNetworkExceptionAdvice {
        public static final String SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED = "saflate.networkexpection.created";

        @Advice.OnMethodExit
        public static void onExit(@Advice.Origin String method, @Advice.AllArguments Object[] para) throws Exception {
            boolean concurrent = Objects.equals(String.valueOf(true),System.getProperty(SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION));
            String propertyName = concurrent ?
                SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED + '.' + Thread.currentThread().getId() :
                SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED ;
            System.setProperty(propertyName, String.valueOf(true));
        }
    }

    static void instrumentNetworkException(Class clazz) {
        ClassReloadingStrategy classReloadingStrategy = ClassReloadingStrategy.fromInstalledAgent(ClassReloadingStrategy.Strategy.REDEFINITION);
        new ByteBuddy()
            .redefine(clazz)
            .constructor(ElementMatchers.anyOf(clazz.getConstructors()))
            .intercept(Advice.to(SetSystemPropertyOnNetworkExceptionAdvice.class))
            .make()
            .load(clazz.getClassLoader(), classReloadingStrategy);
    }

    static {
        // instrument exception classes
        // MatcherAssert.assertThat(ByteBuddyAgent.install(), instanceOf(Instrumentation.class));
        ByteBuddyAgent.install();
        for (Class clazz:NETWORK_EXCEPTION_CLASSES) {
            instrumentNetworkException(clazz);
        }
    }

    private String getTrackedPropertyName() {
        boolean concurrent = Objects.equals(String.valueOf(true),System.getProperty(SYSTEM_PROPERTY_SUPPORT_CONCURRENT_TEST_EXECUTION));
        return concurrent ?
            SetSystemPropertyOnNetworkExceptionAdvice.SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED + '.' + Thread.currentThread().getId() :
            SetSystemPropertyOnNetworkExceptionAdvice.SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED ;
    }

    private void reset() {
        System.clearProperty(getTrackedPropertyName());
    }

    // cleanup
    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        reset();
    }

    // conservative: there is a small chance that a test encountered a tracked exception background processing using the same thread pool
    // as concurrent test execution
    @Override
    public void beforeTestExecution(ExtensionContext extensionContext) {
        reset();
    }

    // only test execution exceptions are handled, so if the test succeeds, this will not be invoked
    private void handleTestExecutionException(Throwable throwable) throws Throwable {

        if (!(throwable instanceof IncompleteExecutionException)) { // error or failure (not skipped)
            // deal with network exceptions leading to error
            if (isDeepNetworkException(throwable)) {
                throw new TestAbortedException("Test relies on remote resources that are not available",throwable);
            }

            // this part deals with exceptions causing failure, not error
            String key = getTrackedPropertyName();
            boolean networkExceptionEncountered = Objects.equals(System.getProperty(key), String.valueOf(true));
            if (networkExceptionEncountered) {
                throw new TestAbortedException("Test relies on remote resources that are not available");
            }
        }
        throw throwable;
    }

    // Indicates whether either throwable is a network exception, or is caused by one
    private boolean isDeepNetworkException(Throwable throwable) {
        for (Class clazz:NETWORK_EXCEPTION_CLASSES) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        Throwable cause = throwable.getCause();
        return cause!=null && isDeepNetworkException(cause);
    }

    // throwable will always be NonNull since this are the callbacks from the  TestExecutionExceptionHandler interface
    @Override
    public void handleTestExecutionException(ExtensionContext extensionContext, Throwable throwable) throws Throwable {
        handleTestExecutionException(throwable);
    }

    // throwable will always be NonNull since this are the callbacks from the LifecycleMethodExecutionExceptionHandler interface
    @Override
    public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleTestExecutionException(throwable);
    }

    // throwable will always be NonNull since this are the callbacks from the LifecycleMethodExecutionExceptionHandler interface
    @Override
    public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        handleTestExecutionException(throwable);
    }

    // NOTE: do not intercept static fixtures with handleAfterAllMethodExecutionException and/or handleBeforeAllMethodExecutionException
    // if an error occurs in those methods, junit will ignore all tests already !


}

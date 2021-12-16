package nz.ac.wgtn.saflate.network.junit4;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.AssumptionViolatedException;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;
import java.lang.instrument.Instrumentation;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * JUnit rule that catches network-related exceptions and turns them into failed assumptions.
 * For this purpose, the constructors of some network exceptions that are thrown when the network is unavailable are instrumented.
 * Due to issues with classloaders (exception classes in the standard library cannot see classes defined in libraries), the instrumented classes
 * communicate that the exceptions have been instantiated via a system property.
 * @author jens dietrich
 */
public class SanitiseNetworkDependenciesRule implements TestRule {

    public static final String SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED = "saflate.junit4.networkexpection.created";

    public static final Set<Class> NETWORK_EXCEPTION_CLASSES =
        StreamSupport.stream(ServiceLoader.load(NetworkExceptionProvider.class).spliterator(),false)
                .flatMap(provider -> Arrays.stream(provider.getNetworkExceptionClassesToBeSanitised()))
                .map(clazz -> {
                    System.out.println("saflate sanitisation will be applied to exceptions of type: " + clazz.getName());
                    return clazz;
                })
                .collect(Collectors.toSet());

    static void instrumentNetworkException(Class clazz) {
        System.out.println("instrumenting: " + clazz);
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
        MatcherAssert.assertThat(ByteBuddyAgent.install(), instanceOf(Instrumentation.class));
        for (Class clazz:NETWORK_EXCEPTION_CLASSES) {
            instrumentNetworkException(clazz);
        }
    }

    static class SetSystemPropertyOnNetworkExceptionAdvice {

        @Advice.OnMethodExit
        public static void onExit(@Advice.Origin String method) throws Exception {
            System.out.println("Intercepting: " + method);
            System.setProperty(SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED,String.valueOf(true));
        }
    }

    static class NetworkException2AssumptionViolatedRuleStatement extends Statement {

        private Statement next;
        public NetworkException2AssumptionViolatedRuleStatement(Statement base) {
            this.next = base;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                next.evaluate();
                // still reset -- perhaps exception has occured but test succeeded
                boolean networkExceptionEncountered = Objects.equals(System.getProperty(SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED), String.valueOf(true));
                if (networkExceptionEncountered) {
                    System.setProperty(SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED, String.valueOf(false));
                }
            } catch (Throwable throwable) {
                boolean networkExceptionEncountered = Objects.equals(System.getProperty(SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED), String.valueOf(true));
                if (networkExceptionEncountered) {
                    // reset
                    System.setProperty(SYSTEM_PROPERTY_NETWORK_EXCEPTION_CREATED, String.valueOf(false));
                }
                if (!(throwable instanceof AssumptionViolatedException)) { // error or failure (not skipped)
                    // deal with network exceptions leading to error
                    if (isDeepNetworkException(throwable)) {
                        throw new AssumptionViolatedException("Test relies on remote resources that are not available",throwable);
                    }

                    // this part deals with exceptions causing failure, not error
                    if (networkExceptionEncountered) {
                        throw new AssumptionViolatedException("Test relies on remote resources that are not available");
                    }
                }
                throw throwable;
            }
        }
    }

    // Indicates whether either throwable is a network exception, or is caused by one
    private static boolean isDeepNetworkException(Throwable throwable) {
        for (Class clazz:NETWORK_EXCEPTION_CLASSES) {
            if (clazz.isAssignableFrom(throwable.getClass())) {
                return true;
            }
        }
        Throwable cause = throwable.getCause();
        return cause!=null && isDeepNetworkException(cause);
    }

    public Statement apply(Statement base, org.junit.runner.Description description) {
        return new NetworkException2AssumptionViolatedRuleStatement(base);
    }
}

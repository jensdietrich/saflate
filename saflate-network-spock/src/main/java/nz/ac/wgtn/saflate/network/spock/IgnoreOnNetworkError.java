package nz.ac.wgtn.saflate.network.spock;

import org.spockframework.runtime.extension.ExtensionAnnotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@ExtensionAnnotation(IgnoreOnNetworkErrorExtension.class)

/**
 * Annotation used to switch the status of tests resulting in error or failure to skip.
 * @author jens dietrich
 */
public @interface IgnoreOnNetworkError {}

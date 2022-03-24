package nz.ac.wgtn.saflate.network.spock;

import org.spockframework.runtime.extension.IAnnotationDrivenExtension;
import org.spockframework.runtime.model.FeatureInfo;

/**
 * Extension providing the semantics of @IgnoreOnNetworkError.
 * @author jens dietrich
 */
public class IgnoreOnNetworkErrorExtension implements IAnnotationDrivenExtension<IgnoreOnNetworkError> {

    @Override
    public void visitFeatureAnnotation(IgnoreOnNetworkError annotation, FeatureInfo feature) {
        IgnoreOnNetworkErrorInterceptor interceptor = new IgnoreOnNetworkErrorInterceptor();
        if (feature.isParameterized()) {
            feature.addInterceptor(interceptor);
        } else {
            feature.getFeatureMethod().addInterceptor(interceptor);
        }
    }
}

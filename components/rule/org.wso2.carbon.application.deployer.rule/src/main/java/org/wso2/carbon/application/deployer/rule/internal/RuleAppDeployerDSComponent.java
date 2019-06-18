package org.wso2.carbon.application.deployer.rule.internal;

import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.wso2.carbon.application.deployer.AppDeployerConstants;
import org.wso2.carbon.application.deployer.AppDeployerUtils;
import org.wso2.carbon.application.deployer.Feature;
import org.wso2.carbon.application.deployer.handler.AppDeploymentHandler;
import org.wso2.carbon.application.deployer.rule.RuleAppDeployer;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component(
        name = "application.deployer.rule",
        immediate = true)
public class RuleAppDeployerDSComponent {

    private static Log log = LogFactory.getLog(RuleAppDeployerDSComponent.class);

    private static Map<String, List<Feature>> requiredFeatures;

    private static ServiceRegistration appHandlerRegistration;

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            // Register BRS deployer as an OSGi service
            RuleAppDeployer brsDeployer = new RuleAppDeployer();
            appHandlerRegistration = ctxt.getBundleContext().registerService(AppDeploymentHandler.class.getName(),
                    brsDeployer, null);
            // read required-features.xml
            URL reqFeaturesResource = ctxt.getBundleContext().getBundle().getResource(AppDeployerConstants
                    .REQ_FEATURES_XML);
            if (reqFeaturesResource != null) {
                InputStream xmlStream = reqFeaturesResource.openStream();
                requiredFeatures = AppDeployerUtils.readRequiredFeaturs(new StAXOMBuilder(xmlStream)
                        .getDocumentElement());
            }
        } catch (Throwable e) {
            log.error("Failed to activate BRS Application Deployer", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {
        // Unregister the OSGi service
        if (appHandlerRegistration != null) {
            appHandlerRegistration.unregister();
        }
    }

    public static Map<String, List<Feature>> getRequiredFeatures() {

        return requiredFeatures;
    }
}

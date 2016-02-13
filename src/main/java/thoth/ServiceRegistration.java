package thoth;

import com.netflix.appinfo.*;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.discovery.*;
import thoth.model.service.ServiceStub;

/**
 * User: Justina Chen
 * Date: 2/13/16
 * Time: 11:05 PM
 */
public class ServiceRegistration {
    public static void main(String[] args) {
        DynamicPropertyFactory configInstance = com.netflix.config.DynamicPropertyFactory.getInstance();
        ApplicationInfoManager applicationInfoManager = ApplicationInfoManager.getInstance();

        DiscoveryManager.getInstance().initComponent(
                new MyDataCenterInstanceConfig(),
                new DefaultEurekaClientConfig());

        EurekaClient eurekaClient = DiscoveryManager.getInstance().getEurekaClient();
        ServiceStub serviceStub = new ServiceStub(applicationInfoManager, eurekaClient, configInstance);
        try {
            serviceStub.start();
        } finally {
            serviceStub.stop();
        }
    }
}

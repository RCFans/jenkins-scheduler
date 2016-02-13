package thoth.model.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.config.DynamicPropertyFactory;
import org.slf4j.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.*;
import java.util.Date;

/**
 * User: Justina Chen
 * Date: 2/13/16
 * Time: 10:22 PM
 */
@Singleton
public class ServiceStub {

    private static final Logger logger = LoggerFactory.getLogger(ServiceStub.class);
    private final ApplicationInfoManager applicationInfoManager;
    private final EurekaClient eurekaClient;
    private final DynamicPropertyFactory configInstance;

    @Inject
    public ServiceStub(ApplicationInfoManager applicationInfoManager,
                       EurekaClient eurekaClient,
                       DynamicPropertyFactory configInstance) {
        this.applicationInfoManager = applicationInfoManager;
        this.eurekaClient = eurekaClient;
        this.configInstance = configInstance;
    }

    @PostConstruct
    public void start() {
        logger.info("Registering service to eureka with STARTING status");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.STARTING);

        logger.info("Simulating service initialization by sleeping for 2 seconds...");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }

        logger.info("Done sleeping, now changing status to UP");
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        waitForRegistrationWithEureka(eurekaClient);
        logger.info("Service started and ready to process requests..");

        try {
            ServerSocket serverSocket = new ServerSocket(configInstance.getIntProperty("eureka.port", 8001).get());
            final Socket s = serverSocket.accept();
            logger.info("Client got connected... processing request from the client");
            processRequest(s);
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }

        logger.info("Simulating service doing work by sleeping for " + 10 + " seconds...");
        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {

        }

        logger.info("Removing registration from eureka");
        this.stop();

        logger.info("Shutting down server.");
    }

    @PreDestroy
    public void stop() {
        if (eurekaClient != null) {
            eurekaClient.shutdown();
        }
    }

    private void waitForRegistrationWithEureka(EurekaClient eurekaClient) {
        String vipAddress = configInstance.getStringProperty("eureka.vipAddress", "jxchen.local").get();
        InstanceInfo nextServerInfo = null;
        while (nextServerInfo == null) {
            try {
                nextServerInfo = eurekaClient.getNextServerFromEureka(vipAddress, false);
                logger.info(nextServerInfo.getAppName());
            } catch (Throwable e) {
                logger.warn("Waiting ... verifying service registration with eureka ...");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e1) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    private void processRequest(final Socket s) {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String line = rd.readLine();
            if (line != null) {
                logger.info("Received a request from the example client: " + line);
            }
            String response = "BAR " + new Date();
            logger.info("Sending the response to the client: " + response);

            PrintStream out = new PrintStream(s.getOutputStream());
            out.println(response);

        } catch (Throwable e) {
            logger.warn("Error processing requests");
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

}

package com.z.framework.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties(
    prefix = "z"
)
public class CommonProperties {

    /**
     * @Description: rest接口前缀
     */
    public static final String RESOURCE_PRE = "/api";

    private final Async async = new Async();
    private final CorsConfiguration cors = new CorsConfiguration();

    private final ClientApp clientApp = new ClientApp();

    public CommonProperties() {
    }

    public Async getAsync() {
        return this.async;
    }
    public CorsConfiguration getCors() {
        return this.cors;
    }

    public static class ClientApp {
        private String name = "ifmisApp";

        public ClientApp() {
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Async {
        private int corePoolSize = 2;
        private int maxPoolSize = 50;
        private int queueCapacity = 10000;

        public Async() {
        }

        public int getCorePoolSize() {
            return this.corePoolSize;
        }

        public void setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public int getMaxPoolSize() {
            return this.maxPoolSize;
        }

        public void setMaxPoolSize(int maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public int getQueueCapacity() {
            return this.queueCapacity;
        }

        public void setQueueCapacity(int queueCapacity) {
            this.queueCapacity = queueCapacity;
        }
    }
}

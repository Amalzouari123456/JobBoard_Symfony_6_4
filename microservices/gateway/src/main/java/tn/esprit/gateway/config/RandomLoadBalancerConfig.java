package tn.esprit.gateway.config;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RandomLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * Configuration du load balancer Random.
 *
 * ATTENTION : volontairement SANS @Configuration. Si cette classe etait scannee par Spring,
 * le bean s'appliquerait a TOUS les services. Elle est seulement referencee par
 * @LoadBalancerClient(name = "candidat", ...) dans CandidatLoadBalancerConfig.
 */
public class RandomLoadBalancerConfig {

    @Bean
    public ReactorLoadBalancer<ServiceInstance> randomLoadBalancer(Environment environment,
                                                                    LoadBalancerClientFactory factory) {
        // nom du service gere par ce contexte ("candidat")
        String name = environment.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
        return new RandomLoadBalancer(
                factory.getLazyProvider(name, ServiceInstanceListSupplier.class), name);
    }
}

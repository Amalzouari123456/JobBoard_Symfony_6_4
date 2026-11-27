package tn.esprit.gateway.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Configuration;

/**
 * Partie 3 - Question 3 : algorithme Random applique UNIQUEMENT au service "candidat".
 *
 * Active avec la propriete gateway.lb.candidat-random=true (false par defaut,
 * pour observer d'abord l'algorithme par defaut en question 2.b).
 *
 * @LoadBalancerClient(name = "candidat") cree un contexte de load balancing propre
 * au service "candidat", configure par RandomLoadBalancerConfig.
 * Les autres services (job, ...) gardent l'algorithme par defaut.
 */
@Configuration
@ConditionalOnProperty(name = "gateway.lb.candidat-random", havingValue = "true")
@LoadBalancerClient(name = "candidat", configuration = RandomLoadBalancerConfig.class)
public class CandidatLoadBalancerConfig {
}

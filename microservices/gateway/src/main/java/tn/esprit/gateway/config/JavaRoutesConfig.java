package tn.esprit.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Methode 1 : routes definies en Java (RouteLocator).
 * Active uniquement avec le profil "java-routes" (voir application-java-routes.yml),
 * pour ne pas doublonner les routes de application.yml (methode 2).
 *
 * Route = Predicate (path) + Filter(s) + URI de destination.
 * - uri("http://localhost:8081") : configuration STATIQUE (sans Eureka)
 * - uri("lb://candidat")         : configuration DYNAMIQUE (via Eureka + load balancing)
 */
@Configuration
@Profile("java-routes")
public class JavaRoutesConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // /candidat/1 -> /api/candidats/1 sur une instance du service CANDIDAT
                .route("Candidat", r -> r.path("/candidat", "/candidat/**")
                        .filters(f -> f.rewritePath("/candidat(?<segment>/?.*)", "/api/candidats${segment}"))
                        .uri("lb://candidat"))
                // /jobs/1 -> /api/jobs/1 sur une instance du service JOB
                .route("Job", r -> r.path("/jobs", "/jobs/**")
                        .filters(f -> f.rewritePath("/jobs(?<segment>/?.*)", "/api/jobs${segment}"))
                        .uri("lb://job"))
                .build();
    }
}

package tn.esprit.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

// @EnableDiscoveryClient : la Gateway s'inscrit dans Eureka et lit le registre
// pour resoudre les URI "lb://nom-du-service".
// Les routes sont definies dans application.yml (methode 2)
// ou dans config/JavaRoutesConfig (methode 1, profil "java-routes").
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}

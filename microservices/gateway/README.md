# Atelier n°6 – API Gateway (Spring Cloud Gateway)

La Gateway (port **8085**) est le point d'entrée unique : les clients appellent `http://localhost:8085/...`,
la Gateway choisit une instance disponible du microservice (via Eureka) et lui transfère la requête.

Versions : Spring Boot 3.5.6, Spring Cloud 2025.0.0, springdoc 2.8.9, Java 17.

## Routes

| Appel via la Gateway | Service | Chemin réel |
|---|---|---|
| `/candidat`, `/candidat/{id}`, `/candidat/hello` | `lb://candidat` | `/api/candidats/...` |
| `/jobs`, `/jobs/{id}`, `/jobs/hello` | `lb://job` | `/api/jobs/...` |
| `/candidat/v3/api-docs`, `/jobs/v3/api-docs` | documentation | `/v3/api-docs` |
| `/api/candidats/**`, `/api/jobs/**` | chemins d'origine (utilisés par Swagger « Try it out ») | inchangés |

Les contrôleurs de ce projet sont sur `/api/candidats` et `/api/jobs` : le filtre `RewritePath`
convertit `/candidat/1` en `/api/candidats/1`.

**Ports de ce projet** : Candidat = 8081 (2e instance 8091), Job = 8082. Ils sont inversés par rapport
à l'énoncé (Candidat 8082, Job 8081).

## Partie 1 – Projet

- `pom.xml` : starters **Gateway** et **Eureka Discovery Client**.
  Depuis Spring Cloud 2025.0, `spring-cloud-starter-gateway` s'appelle
  `spring-cloud-starter-gateway-server-webflux`, et les propriétés commencent par
  `spring.cloud.gateway.server.webflux.` (et non plus `spring.cloud.gateway.`).
- `GatewayApplication` : `@EnableDiscoveryClient`.
- Vérification : la Gateway apparaît sous le nom **GATEWAY** dans http://localhost:8761.

## Partie 2 – Configuration statique

| Méthode | Fichier | Lancement |
|---|---|---|
| 1 – classe Java (`RouteLocator`) | `config/JavaRoutesConfig.java` | `mvn spring-boot:run -Dspring-boot.run.profiles=java-routes` |
| 2 – fichier de configuration | `application-static.yml` (`uri: http://localhost:8081`) | `mvn spring-boot:run -Dspring-boot.run.profiles=static` |

En statique, la Gateway pointe directement sur l'adresse du service, sans passer par Eureka.

## Partie 3 – Configuration dynamique (Eureka)

`application.yml` (lancement par défaut : `mvn spring-boot:run`) utilise `uri: lb://candidat` :
la Gateway demande à Eureka les instances de `candidat` et répartit la charge.
La méthode 1 en Java est dans `JavaRoutesConfig` (avec `lb://`).

### Question 2.b – Algorithme par défaut

Lancer 2 instances de Candidat (8081 et 8091), puis appeler plusieurs fois
`http://localhost:8085/candidat/hello`.

Résultat observé : `8081, 8091, 8081, 8091, 8081, 8091`. Dans la console de la Gateway (logs DEBUG) :

```
Handler is being applied: {uri=http://192.168.137.1:8081/api/candidats/hello, method=GET}
Handler is being applied: {uri=http://192.168.137.1:8091/api/candidats/hello, method=GET}
```

**Réponse : Round Robin** (`RoundRobinLoadBalancer`, stratégie par défaut de Spring Cloud LoadBalancer).
Les instances sont choisies à tour de rôle.

### Question 3 – Autres algorithmes, et Random pour Candidat

Autres stratégies : **Random** (instance au hasard), **Weighted** (pondération par instance),
**Same Instance Preference** (réutilise la même instance si possible),
**Request-based Sticky Session** (même instance pour un même client, via un cookie),
**Zone Preference** (instances de la même zone), **Health-check** (seulement les instances saines),
et, historiquement avec Netflix Ribbon, **Weighted Response Time** et **Zone Avoidance**.

Mise en œuvre (Random **uniquement** pour `candidat`) :

- `config/CandidatLoadBalancerConfig.java` : `@LoadBalancerClient(name = "candidat", configuration = RandomLoadBalancerConfig.class)`
- `config/RandomLoadBalancerConfig.java` : bean `RandomLoadBalancer`. Cette classe n'a volontairement
  **pas** `@Configuration` : sinon elle serait scannée et s'appliquerait à tous les services.

Activation : mettre `gateway.lb.candidat-random: true` dans `application.yml`, puis redémarrer la Gateway.

Résultat observé sur 16 appels : `8091 8081 8081 8091 8091 8091 8091 8081 8081 8091 8081 8091 8081 8091 8081 8081`.
L'ordre est irrégulier, donc c'est Random. Les appels à `/jobs/hello` ne changent pas.

## Partie 4 – Documentation et tests

- Candidat et Job : dépendance `springdoc-openapi-starter-webmvc-ui`, annotations `@Tag`, `@Operation`
  et `@ApiResponse`. Documentation directe : http://localhost:8081/swagger-ui.html et http://localhost:8082/swagger-ui.html
- Gateway : `springdoc-openapi-starter-webflux-ui` (la Gateway est réactive), sources agrégées dans
  `springdoc.swagger-ui.urls`.
- **Portail unifié : http://localhost:8085/swagger-ui.html** (liste déroulante « Candidat / Job »).
- `trusted-proxies` et `server.forward-headers-strategy=framework` : Swagger annonce l'URL de la
  Gateway (`http://localhost:8085`), donc « Try it out » passe par la Gateway.

**Postman** : importer `../docs/Atelier_API_Gateway.postman_collection.json`, cliquer sur **Run**, puis
l'exporter (Export, Collection v2.1).
Pour les tests Postman, lancer **une seule** instance de Candidat : chaque instance a sa propre base H2
en mémoire. Un candidat créé sur 8081 n'existe donc pas sur 8091.

## Livrables

| Livrable | Où |
|---|---|
| Q2.b : algorithme par défaut | Round Robin (ci-dessus) + capture des logs de la Gateway |
| Q3 : Random pour Candidat | `CandidatLoadBalancerConfig` / `RandomLoadBalancerConfig` + capture des logs |
| Capture du Swagger unifié | `../docs/swagger-gateway.png` |
| Collection Postman | `../docs/Atelier_API_Gateway.postman_collection.json` (à réexporter après exécution) |

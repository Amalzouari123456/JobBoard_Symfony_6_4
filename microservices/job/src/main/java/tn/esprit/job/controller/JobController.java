package tn.esprit.job.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.job.entity.Job;
import tn.esprit.job.repository.JobRepository;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@Tag(name = "Job", description = "Gestion des offres d'emploi")
public class JobController {

    private final JobRepository repository;

    // Port de l'instance : utile pour voir quelle instance repond
    @Value("${server.port}")
    private String port;

    public JobController(JobRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Indiquer quelle instance repond (port)")
    @GetMapping("/hello")
    public String hello() {
        return "Hello from JOB (port " + port + ")";
    }

    @Operation(summary = "Lister les offres")
    @GetMapping
    public List<Job> getAll() {
        return repository.findAll();
    }

    @Operation(summary = "Recuperer une offre par ID")
    @ApiResponse(responseCode = "200", description = "Offre trouvee")
    @ApiResponse(responseCode = "404", description = "Offre introuvable")
    @GetMapping("/{id}")
    public ResponseEntity<Job> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Creer une offre")
    @PostMapping
    public Job add(@RequestBody Job job) {
        return repository.save(job);
    }

    @Operation(summary = "Modifier une offre")
    @PutMapping("/{id}")
    public Job update(@PathVariable Long id, @RequestBody Job job) {
        job.setId(id);
        return repository.save(job);
    }

    @Operation(summary = "Supprimer une offre")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

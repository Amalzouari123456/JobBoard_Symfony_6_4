package tn.esprit.candidat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.candidat.entity.Candidat;
import tn.esprit.candidat.repository.CandidatRepository;

import java.util.List;

@RestController
@RequestMapping("/api/candidats")
@Tag(name = "Candidat", description = "Gestion des candidats")
public class CandidatController {

    private final CandidatRepository repository;

    // Port de l'instance : utile pour voir quelle instance repond
    @Value("${server.port}")
    private String port;

    public CandidatController(CandidatRepository repository) {
        this.repository = repository;
    }

    @Operation(summary = "Indiquer quelle instance repond (port)")
    @GetMapping("/hello")
    public String hello() {
        return "Hello from CANDIDAT (port " + port + ")";
    }

    @Operation(summary = "Lister les candidats")
    @GetMapping
    public List<Candidat> getAll() {
        return repository.findAll();
    }

    @Operation(summary = "Recuperer un candidat par ID")
    @ApiResponse(responseCode = "200", description = "Candidat trouve")
    @ApiResponse(responseCode = "404", description = "Candidat introuvable")
    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Creer un candidat")
    @PostMapping
    public Candidat add(@RequestBody Candidat candidat) {
        return repository.save(candidat);
    }

    @Operation(summary = "Modifier un candidat")
    @PutMapping("/{id}")
    public Candidat update(@PathVariable Long id, @RequestBody Candidat candidat) {
        candidat.setId(id);
        return repository.save(candidat);
    }

    @Operation(summary = "Supprimer un candidat")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

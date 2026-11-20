package tn.esprit.candidat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tn.esprit.candidat.entity.Candidat;
import tn.esprit.candidat.repository.CandidatRepository;

import java.util.List;

@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    private final CandidatRepository repository;

    // Port de l'instance : utile pour voir quelle instance repond
    @Value("${server.port}")
    private String port;

    public CandidatController(CandidatRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from CANDIDAT (port " + port + ")";
    }

    @GetMapping
    public List<Candidat> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Candidat getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public Candidat add(@RequestBody Candidat candidat) {
        return repository.save(candidat);
    }

    @PutMapping("/{id}")
    public Candidat update(@PathVariable Long id, @RequestBody Candidat candidat) {
        candidat.setId(id);
        return repository.save(candidat);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

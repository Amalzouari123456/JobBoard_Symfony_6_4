package tn.esprit.job.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tn.esprit.job.entity.Job;
import tn.esprit.job.repository.JobRepository;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobRepository repository;

    // Port de l'instance : utile pour voir quelle instance repond
    @Value("${server.port}")
    private String port;

    public JobController(JobRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from JOB (port " + port + ")";
    }

    @GetMapping
    public List<Job> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Job getById(@PathVariable Long id) {
        return repository.findById(id).orElse(null);
    }

    @PostMapping
    public Job add(@RequestBody Job job) {
        return repository.save(job);
    }

    @PutMapping("/{id}")
    public Job update(@PathVariable Long id, @RequestBody Job job) {
        job.setId(id);
        return repository.save(job);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

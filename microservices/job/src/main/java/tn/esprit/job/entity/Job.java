package tn.esprit.job.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

// Reprend les champs de l'entite Job du monolithe Symfony JobBoard
@Entity
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 180, nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    // CDI, CDD, INTERNSHIP, FREELANCE
    @Column(length = 30)
    private String contractType = "CDI";
    @Column(length = 120)
    private String location;
    private boolean remote = false;
    private boolean published = false;
    // Dans un microservice, on garde seulement le nom de l'entreprise (pas de relation JPA)
    private String companyName;
    private LocalDateTime createdAt;

    public Job() {
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isRemote() { return remote; }
    public void setRemote(boolean remote) { this.remote = remote; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

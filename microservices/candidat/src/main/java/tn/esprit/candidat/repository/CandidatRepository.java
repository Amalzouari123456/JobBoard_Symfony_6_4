package tn.esprit.candidat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.candidat.entity.Candidat;

public interface CandidatRepository extends JpaRepository<Candidat, Long> {
}

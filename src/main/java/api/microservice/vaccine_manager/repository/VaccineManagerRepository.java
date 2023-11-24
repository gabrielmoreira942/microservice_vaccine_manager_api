package api.microservice.vaccine_manager.repository;

import api.microservice.vaccine_manager.entity.VaccineManager;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface VaccineManagerRepository extends MongoRepository<VaccineManager, String> {
    Optional<List<VaccineManager>> findAllByPatientId(String idPatient);
    Optional<VaccineManager> findByPatientId(String idPatient);
    List<VaccineManager> findAllByOrderByCreatedAtDesc();

    @Query(value = "{}", fields = "{ 'patient.address.state' : 1 }")
    List<VaccineManager> findAllStates();
}

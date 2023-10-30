package api.microservice.vaccine_manager.repository;

import api.microservice.vaccine_manager.entity.VaccineManager;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VaccineManagerRepository extends MongoRepository<VaccineManager, String> {
}

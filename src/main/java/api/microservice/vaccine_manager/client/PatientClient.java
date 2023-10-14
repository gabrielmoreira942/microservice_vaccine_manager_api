package api.microservice.vaccine_manager.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "patientClient", url = "http://localhost:8081")
public interface PatientClient {
    @GetMapping("/patient/{id}")
    Optional<?> getByIdPatient(@PathVariable String id);
}

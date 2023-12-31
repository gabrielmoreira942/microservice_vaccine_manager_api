package api.microservice.vaccine_manager.client;

import api.microservice.vaccine_manager.service.dto.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(name = "patientClient", url = "${api.patient}")
public interface PatientClient {
    @GetMapping("/patient/{id}")
    Optional<Patient> getByIdPatient(@PathVariable String id);
}

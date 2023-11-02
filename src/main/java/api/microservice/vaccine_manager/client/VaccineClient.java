package api.microservice.vaccine_manager.client;

import api.microservice.vaccine_manager.dto.Vaccine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;


@FeignClient(name = "vaccineClient", url = "${api.vaccine}")
public interface VaccineClient {

    @GetMapping("/vaccine/{id}")
    Optional<Vaccine> getByIdVaccine(@PathVariable String id);
}

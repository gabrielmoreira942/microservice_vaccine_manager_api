package api.microservice.vaccine_manager.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;


@FeignClient(name = "vaccineClient", url = "http://localhost:8080")
public interface VaccineClient {
    @GetMapping("/vaccine")
    List<?> getAllVaccines();
}

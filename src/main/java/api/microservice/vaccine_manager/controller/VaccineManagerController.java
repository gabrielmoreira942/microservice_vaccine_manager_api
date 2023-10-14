package api.microservice.vaccine_manager.controller;

import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/vaccine-manager")
public class VaccineManagerController {
    @Autowired
    private VaccineManagerService vaccineManagerService;

    @PostMapping
    public ResponseEntity<VaccineManager> create(@RequestBody VaccineManager vaccineManager) {
        Optional<?> vaccineManager1 = vaccineManagerService.create(vaccineManager);
        return ResponseEntity.created(null).build();
    }
}

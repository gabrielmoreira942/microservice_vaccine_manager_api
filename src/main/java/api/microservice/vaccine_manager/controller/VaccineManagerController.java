package api.microservice.vaccine_manager.controller;

import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/vaccine-manager")
public class VaccineManagerController {
    @Autowired
    private VaccineManagerService vaccineManagerService;

    // TODO Link postman: https://blue-moon-738033.postman.co/workspace/Oficial-2-Java~ba414c40-dcc8-44b1-af28-c9f406479095/request/17428469-b8a6e8bf-189c-4988-9226-d45d10684686
    @PostMapping
    public ResponseEntity<VaccineManager> create(@RequestBody @Valid VaccineManager vaccineManager) {
        VaccineManager createdVaccineRegister = vaccineManagerService.create(vaccineManager);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdVaccineRegister.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdVaccineRegister);
    }
}

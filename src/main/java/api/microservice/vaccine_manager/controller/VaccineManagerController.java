package api.microservice.vaccine_manager.controller;

import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.InvalidVaccineDateException;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/vaccine-manager")
public class VaccineManagerController {
    @Autowired
    private VaccineManagerService vaccineManagerService;

    @GetMapping
    public ResponseEntity<List<VaccineManagerDTO>> list(
            @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        List<VaccineManagerDTO> vaccineManagerList = vaccineManagerService.listVaccineManager(state);
        return ResponseEntity.ok(vaccineManagerList);
    }

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

package api.microservice.vaccine_manager.controller;

import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.BadRequestException;
import api.microservice.vaccine_manager.handler.exceptions.InvalidVaccineDateException;
import api.microservice.vaccine_manager.handler.exceptions.NotFoundException;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    public ResponseEntity<List<VaccineManagerDTO>> getAll(
            @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        List<VaccineManagerDTO> vaccineManagerList = vaccineManagerService.listVaccineManager(state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<VaccineManagerDTO>> getAllVaccineOverdue(
            @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        List<VaccineManagerDTO> vaccineManagerList = vaccineManagerService.filterVaccinesOverdue(state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<VaccineManagerDTO>> getAllVaccinesByManufacturer(
            @RequestParam(value = "state", required = false, defaultValue = "") String state,
            @PathVariable String manufacturer
    ) {
        List<VaccineManagerDTO> vaccineManagerList = vaccineManagerService.filterVaccinesByManufacturer(manufacturer, state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VaccineManagerDTO>> listVaccinesByPatientId(@PathVariable String patientId) throws NotFoundException {
        List<VaccineManagerDTO> vaccineManagerList = vaccineManagerService.getAllVaccinesByPatientId(patientId);
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

    @PutMapping("/{id}")
    public ResponseEntity<VaccineManagerDTO> update(
            @PathVariable String id,
            @RequestBody @Valid VaccineManager vaccineManager
    ) throws InvalidVaccineDateException, NotFoundException, BadRequestException {
        return ResponseEntity.ok(vaccineManagerService.update(id, vaccineManager));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VaccineManager> removeLastVaccination(@PathVariable String id) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(vaccineManagerService.removeLastVaccination(id));
    }
}

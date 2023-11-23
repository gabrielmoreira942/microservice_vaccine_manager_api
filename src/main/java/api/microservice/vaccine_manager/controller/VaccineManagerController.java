package api.microservice.vaccine_manager.controller;

import api.microservice.vaccine_manager.service.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.*;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

    @PostMapping
    public ResponseEntity<VaccineManager> create(@RequestBody @Valid VaccineManagerDTO vaccineManagerDTO) throws UnprocessableEntityException, NotFoundException {
        VaccineManager createdVaccineRegister = vaccineManagerService.create(vaccineManagerDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdVaccineRegister.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdVaccineRegister);
    }

    @GetMapping
    public ResponseEntity<List<VaccineManager>> getAll(
            @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        List<VaccineManager> vaccineManagerList = vaccineManagerService.listVaccineManager(state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<VaccineManager>> getAllVaccineOverdue(
            @RequestParam(value = "state", required = false, defaultValue = "") String state
    ) {
        List<VaccineManager> vaccineManagerList = vaccineManagerService.filterVaccinesOverdue(state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/manufacturer/{manufacturer}")
    public ResponseEntity<List<VaccineManager>> getAllVaccinesByManufacturer(
            @RequestParam(value = "state", required = false, defaultValue = "") String state,
            @PathVariable String manufacturer
    ) {
        List<VaccineManager> vaccineManagerList = vaccineManagerService.filterVaccinesByManufacturer(manufacturer, state);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VaccineManager>> listVaccinesByPatientId(@PathVariable String patientId) throws NotFoundException {
        List<VaccineManager> vaccineManagerList = vaccineManagerService.getAllVaccinesByPatientId(patientId);
        return ResponseEntity.ok(vaccineManagerList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VaccineManager> update(
            @PathVariable String id,
            @RequestBody @Valid VaccineManagerDTO vaccineManager
    ) throws InvalidVaccineDateException, NotFoundException, BadRequestException,
            UnequalVaccineManufacturerException, UniqueDoseVaccineException, AmountOfVacinationException,
            ExpiredVaccineException {
        return ResponseEntity.ok(vaccineManagerService.update(id, vaccineManager));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<VaccineManager> removeLastVaccination(@PathVariable String id) throws NotFoundException, BadRequestException {
        return ResponseEntity.ok(vaccineManagerService.removeLastVaccination(id));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        vaccineManagerService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}

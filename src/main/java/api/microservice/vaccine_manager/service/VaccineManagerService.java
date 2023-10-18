package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.dto.Patient;
import api.microservice.vaccine_manager.dto.Vaccine;
import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.repository.VaccineManagerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VaccineManagerService {

    @Autowired
    private VaccineClient vaccineClient;

    @Autowired
    private PatientClient patientClient;

    @Autowired
    private VaccineManagerRepository vaccineManagerRepository;

    public VaccineManager create(VaccineManager vaccineManager) {
        VaccineManager newVaccineManager = new VaccineManager();
        Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManager.getIdPatient());
        if (patientOptional.isPresent()) {
            String idPatient = patientOptional.get().getId();
            newVaccineManager.setIdPatient(idPatient);
        }
        Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManager.getIdVaccine());
        if (vaccineOptional.isPresent()) {
            String idVaccine = vaccineOptional.get().getId();
            newVaccineManager.setIdVaccine(idVaccine);
        }
        newVaccineManager.setVaccineDate(vaccineManager.getVaccineDate());
        newVaccineManager.setListOfDoses(vaccineManager.getListOfDoses());
        newVaccineManager.setIdVaccine(vaccineManager.getIdVaccine());
        newVaccineManager.setNurseProfessional(vaccineManager.getNurseProfessional());
        return vaccineManagerRepository.insert(newVaccineManager);
    }

    public List<VaccineManagerDTO> listVaccineManager(String state) {
        List<VaccineManager> listOfVaccineManger = vaccineManagerRepository.findAll();
        List<VaccineManagerDTO> listOfVaccineManagerDTO = new ArrayList<>();

        listOfVaccineManger.forEach(item -> {
            VaccineManagerDTO managerDTO = new VaccineManagerDTO();
            BeanUtils.copyProperties(item, managerDTO);

            Optional<Vaccine> vaccine = vaccineClient.getByIdVaccine(item.getIdVaccine());
            vaccine.ifPresent(managerDTO::setVaccine);

            Optional<Patient> patient = patientClient.getByIdPatient(item.getIdPatient());

            if (
                    patient.isEmpty()
                            || !state.isEmpty()
                                && !patient.get().getAddress().getState().equalsIgnoreCase(state)
            ) {
                return;
            }

            managerDTO.setPatient(patient.get());
            listOfVaccineManagerDTO.add(managerDTO);
        });

        return listOfVaccineManagerDTO;
    }
}

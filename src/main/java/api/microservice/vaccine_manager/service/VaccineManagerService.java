package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.entity.Patient;
import api.microservice.vaccine_manager.entity.Vaccine;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.repository.VaccineManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        newVaccineManager.setState(vaccineManager.getState());
        return this.vaccineManagerRepository.insert(newVaccineManager);
    }

}

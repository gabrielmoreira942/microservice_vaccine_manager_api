package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.entity.NurseProfessional;
import api.microservice.vaccine_manager.entity.Vaccine;
import api.microservice.vaccine_manager.entity.VaccineManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VaccineManagerService {
    @Autowired
    private VaccineClient vaccineClient;
    @Autowired
    private PatientClient patientClient;


    public Optional<?> create(VaccineManager vaccineManager) {

         VaccineManager vaccineManager1 = new VaccineManager();
        private LocalDate vaccineDate;

        private String patientId;
        private Vaccine vaccine;
        private List<LocalDate> listOfDoses;

        private NurseProfessional nurseProfessional;
        vaccineManager1.setPatientId(patientClient.getByIdPatient(vaccineManager.getPatientId()).get());
        vaccineManager1.setVaccineDate(vaccineManager.getVaccineDate());
        vaccineManager1.setVaccineDate(vaccineManager.getVaccineDate());

    }

}

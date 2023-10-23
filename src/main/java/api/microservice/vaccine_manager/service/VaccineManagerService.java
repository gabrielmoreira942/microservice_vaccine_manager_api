package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.dto.Patient;
import api.microservice.vaccine_manager.dto.Vaccine;
import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.InvalidVaccineDateException;
import api.microservice.vaccine_manager.repository.VaccineManagerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        newVaccineManager.setVaccineDate(vaccineManager.getVaccineDate());

        vaccineManager.getListOfDoses().add(newVaccineManager.getVaccineDate());
        newVaccineManager.setListOfDoses(vaccineManager.getListOfDoses());

        if (vaccineOptional.isPresent()) {
            Vaccine vaccine = vaccineOptional.get();
            String idVaccine = vaccine.getId();
            newVaccineManager.setIdVaccine(idVaccine);


            // ******** MOVER VALIDAÇÂO PARA O PUT ********* //
//            Integer vaccineInterval = vaccine.getIntervalBetweenDoses();
//            LocalDate vaccineValidate = vaccine.getValidateDate();

            // Se a data atual for maior que a data de validade a gente estoura a exception
            // Se a data da vacinação for antes que a data da ultima vacina mais o intervalo estoura a exception também

//            LocalDate lastVacination = vaccineManager.getListOfDoses().get(vaccineManager.getListOfDoses().size() - 1);
//            if (
//                    LocalDate.now().isAfter(vaccineValidate)
//                            || (vaccineManager.getVaccineDate().isBefore(lastVacination.plusDays(vaccineInterval)))
//            ) throw new InvalidVaccineDateException();
        }

        newVaccineManager.setIdVaccine(vaccineManager.getIdVaccine());
        newVaccineManager.setNurseProfessional(vaccineManager.getNurseProfessional());
        return vaccineManagerRepository.insert(newVaccineManager);
    }

    public List<VaccineManagerDTO> listVaccineManager(String state) {
        List<VaccineManager> listOfVaccineManger = vaccineManagerRepository.findAll();
        return filterVaccineManager(state, listOfVaccineManger);
    }

    private List<VaccineManagerDTO> filterVaccineManager(String state, List<VaccineManager> listOfVaccineManger) {
        List<VaccineManagerDTO> listOfVaccineManagerDTO = new ArrayList<>();

        listOfVaccineManger.forEach(item -> {
            VaccineManagerDTO managerDTO = new VaccineManagerDTO();
            BeanUtils.copyProperties(item, managerDTO);

            Optional<Vaccine> vaccine = vaccineClient.getByIdVaccine(item.getIdVaccine());
            vaccine.ifPresent(managerDTO::setVaccine);

            Optional<Patient> patient = patientClient.getByIdPatient(item.getIdPatient());

            LocalDate lastVaccine = managerDTO.getListOfDoses().get(managerDTO.getListOfDoses().size() - 1);
            if (
                    patient.isEmpty()
                            || (!state.isEmpty()
                                && !patient.get().getAddress().getState().equalsIgnoreCase(state))
//                            || lastVaccine.plusDays(vaccine.get().getIntervalBetweenDoses()).isAfter(LocalDate.now())
            ) {
                return;
            }

            managerDTO.setPatient(patient.get());
            listOfVaccineManagerDTO.add(managerDTO);
        });

        return listOfVaccineManagerDTO;
    }
}

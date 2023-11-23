package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.AmountOfVacinationException;
import api.microservice.vaccine_manager.handler.exceptions.BadRequestException;
import api.microservice.vaccine_manager.handler.exceptions.InvalidVaccineDateException;
import api.microservice.vaccine_manager.handler.exceptions.NotFoundException;
import api.microservice.vaccine_manager.handler.exceptions.UnequalVaccineManufacturerException;
import api.microservice.vaccine_manager.handler.exceptions.UniqueDoseVaccineException;
import api.microservice.vaccine_manager.handler.exceptions.UnprocessableEntityException;
import api.microservice.vaccine_manager.repository.VaccineManagerRepository;
import api.microservice.vaccine_manager.service.dto.Patient;
import api.microservice.vaccine_manager.service.dto.Vaccine;
import api.microservice.vaccine_manager.service.dto.VaccineManagerDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Caching(
            evict = {
                    @CacheEvict(value = "vaccine-manager-list", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-by-manufacturer", allEntries = true),
                    @CacheEvict(value = "vaccine-patient-by-id", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-overdue-list", allEntries = true)
            }
    )
    public VaccineManager create(VaccineManagerDTO vaccineManagerDTO) throws UnprocessableEntityException, NotFoundException {
        VaccineManager vaccineManager = new VaccineManager();
        Optional<VaccineManager> patientAlreadyRegistered = vaccineManagerRepository.findByPatientId(vaccineManagerDTO.getIdPatient());

        if (patientAlreadyRegistered.isPresent()) {
            throw new UnprocessableEntityException("O paciente já tomou a sua primeira dose");
        }

        Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManagerDTO.getIdPatient());
        if (patientOptional.isPresent()) {
            vaccineManager.setPatient(patientOptional.get());
        } else {
            throw new NotFoundException("Não existe um paciente com esse id: " + vaccineManagerDTO.getIdPatient());
        }

        Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManagerDTO.getIdVaccine());
        if (vaccineOptional.isPresent()) {
            vaccineManager.setVaccine(vaccineOptional.get());
        } else {
            throw new NotFoundException("Não existe uma vacina com esse id: " + vaccineManagerDTO.getIdVaccine());
        }

        vaccineManager.getListOfDoses().add(vaccineManagerDTO.getLastDateOfVaccine());

        vaccineManager.getNurseProfessionals().add(vaccineManagerDTO.getNurseProfessional());

        return vaccineManagerRepository.insert(vaccineManager);
    }

    @Transactional(readOnly = true)
    @Cacheable("vaccine-manager-list")
    public List<VaccineManager> listVaccineManager(String state) {
        List<VaccineManager> listOfVaccineManger = vaccineManagerRepository.findAllByOrderByCreatedAtDesc();
        return filterVaccineManager(state, listOfVaccineManger);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "vaccine-manager-list", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-by-manufacturer", allEntries = true),
                    @CacheEvict(value = "vaccine-patient-by-id", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-overdue-list", allEntries = true)
            }
    )
    public VaccineManagerDTO update(String id, VaccineManager vaccineManager) throws InvalidVaccineDateException, NotFoundException, BadRequestException, AmountOfVacinationException, UnequalVaccineManufacturerException, UniqueDoseVaccineException {
        Optional<VaccineManager> storedVaccineManagerOptional = vaccineManagerRepository.findById(id);

        if (storedVaccineManagerOptional.isEmpty()) {
            throw new NotFoundException("Registro da vacinação não foi encontrado.");
        }

        VaccineManager storedVaccineManager = storedVaccineManagerOptional.get();
        Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManager.getVaccine().getId());
        Optional<Vaccine> oldVaccineOptional = vaccineClient.getByIdVaccine(storedVaccineManager.getVaccine().getId());
        Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManager.getPatient().getId());

        if (vaccineOptional.isEmpty() || !(vaccineOptional.get() instanceof Vaccine)) {
            throw new NotFoundException("Vacina não encontrada");
        } else if (patientOptional.isEmpty()) {
            throw new NotFoundException("Paciente não encontrado");
        } else if (oldVaccineOptional.isEmpty()) {
            throw new NotFoundException("Vacina antiga não encontrada");
        }

        if (storedVaccineManager.getListOfDoses().isEmpty()) {
            throw new BadRequestException("Você não possui registros a serem atualizados.");
        }

        Vaccine vaccine = vaccineOptional.get();
        int vaccineInterval = vaccine.getIntervalBetweenDoses() != null ? vaccine.getIntervalBetweenDoses() : 0;
        LocalDate vaccineValidate = vaccine.getValidateDate();
        int lastAmountOfDoses = storedVaccineManager.getListOfDoses().size() - 1;
        LocalDate lastVaccinationPlusDays = storedVaccineManager.getListOfDoses().get(lastAmountOfDoses).plusDays(vaccineInterval);
        LocalDate vaccineDate = vaccineManager.getListOfDoses().get(lastAmountOfDoses);

        if (!oldVaccineOptional.get().getManufacturer().equals(vaccine.getManufacturer())) {
            throw new UnequalVaccineManufacturerException(patientOptional.get().getFirstName(), patientOptional.get().getLastName(), oldVaccineOptional.get().getManufacturer());
        }

        verifyIfListOfDosesIsGreaterOfAmountOfDoses(storedVaccineManager.getListOfDoses().size(), vaccine);
        verifyIfVaccineDateIsBeforeLastVaccinationPlusDays(vaccineValidate, vaccineDate, lastVaccinationPlusDays);

        if (vaccineOptional.get().getAmountOfDose() == 1 && vaccineManager.getListOfDoses().size() == 1) {
            throw new UniqueDoseVaccineException(
                    patientOptional.get().getFirstName(),
                    patientOptional.get().getLastName(),
                    vaccine.getManufacturer(),
                    vaccineManager.getListOfDoses().get(lastAmountOfDoses).format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
            );
        }

//        storedVaccineManager.setVaccineDate(vaccineManager.getVaccineDate());
//        storedVaccineManager.setNurseProfessional(vaccineManager.getNurseProfessional());

//        if (vaccineDate.isEqual(lastVaccinationPlusDays)) {
//            storedVaccineManager.getListOfDoses().add(vaccineManager.getVaccineDate());
//        }

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO();
        BeanUtils.copyProperties(storedVaccineManager, vaccineManagerDTO);

//        vaccineManagerDTO.setPatient(patientOptional.get());
//        vaccineManagerDTO.setVaccine(vaccineOptional.get());

        vaccineManagerRepository.save(storedVaccineManager);
        return vaccineManagerDTO;
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "vaccine-manager-list", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-by-manufacturer", allEntries = true),
                    @CacheEvict(value = "vaccine-patient-by-id", allEntries = true),
                    @CacheEvict(value = "vaccine-manager-overdue-list", allEntries = true)
            }
    )
    public VaccineManager removeLastVaccination(String id) throws NotFoundException, BadRequestException {
        Optional<VaccineManager> vaccineManagerOptional = vaccineManagerRepository.findById(id);

        if (vaccineManagerOptional.isEmpty()) {
            throw new NotFoundException("Registro da vacinação não foi encontrado.");
        }
        VaccineManager vaccineManager = vaccineManagerOptional.get();

        int lastVaccineDose = vaccineManager.getListOfDoses().size();

        if (lastVaccineDose <= 0) {
            throw new BadRequestException("Você não possui registros a serem removidos.");
        }
        if (lastVaccineDose == 1){
            throw new BadRequestException("Não é possível remover quando existe apenas uma dose.");
        }

        vaccineManager.getListOfDoses().remove(lastVaccineDose - 1);

        return vaccineManagerRepository.save(vaccineManager);
    }

    @Cacheable("vaccine-manager-overdue-list")
    public List<VaccineManager> filterVaccinesOverdue(String state) {
        List<VaccineManager> allVaccinesManager = vaccineManagerRepository.findAll();
        List<VaccineManager> listOfVaccineManagerDTO = filterVaccineManager(state, allVaccinesManager);
        List<VaccineManager> returnedOfVaccineManagerDTO = new ArrayList<>();
        listOfVaccineManagerDTO.forEach(
                vaccineManagerDTO -> {
                    Vaccine vaccine = vaccineManagerDTO.getVaccine();
                    int sizeOfDoses = (vaccineManagerDTO.getListOfDoses().size() > 0) ? vaccineManagerDTO.getListOfDoses().size() : 1;
                    List<LocalDate> lastVaccinationPlusDays = vaccineManagerDTO.getListOfDoses();
                    try {
                        if (lastVaccinationPlusDays.size() <= 0) {
                            return;
                        }

                        verifyIfVaccineDateIsBeforeLastVaccinationPlusDays(
                                vaccine.getValidateDate(),
                                LocalDate.now(),
                                lastVaccinationPlusDays.get(sizeOfDoses - 1).plusDays(vaccine.getIntervalBetweenDoses())
                        );
                        verifyIfListOfDosesIsGreaterOfAmountOfDoses(vaccineManagerDTO.getListOfDoses().size(), vaccine);
                    } catch (InvalidVaccineDateException | AmountOfVacinationException e) {
                        return;
                    }
                    returnedOfVaccineManagerDTO.add(vaccineManagerDTO);
                }
        );

        return returnedOfVaccineManagerDTO;
    }

    @Cacheable("vaccine-manager-by-manufacturer")
    public List<VaccineManager> filterVaccinesByManufacturer(String manufacturer, String state) {
        List<VaccineManager> allVaccinesManager = vaccineManagerRepository.findAllByOrderByCreatedAtDesc();
        List<VaccineManager> filterVaccineManagers = filterVaccineManager(state, allVaccinesManager);
        List<VaccineManager> returnedOfVaccineManagerDTO = new ArrayList<>();

        filterVaccineManagers.forEach(
                vaccineManagerDTO -> {
                    Vaccine vaccine = vaccineManagerDTO.getVaccine();

                    if (!vaccine.getManufacturer().equalsIgnoreCase(manufacturer)) {
                        return;
                    }

                    returnedOfVaccineManagerDTO.add(vaccineManagerDTO);
                }
        );

        return returnedOfVaccineManagerDTO;
    }

    private List<VaccineManager> filterVaccineManager(String state, List<VaccineManager> listOfVaccineManger) {
        List<VaccineManager> vaccineManagers = new ArrayList<>();

        listOfVaccineManger.forEach(vaccineManager -> {
            VaccineManager manager = new VaccineManager();
            BeanUtils.copyProperties(vaccineManager, manager);
            if (vaccineManager.getVaccine() != null) {
                Optional<Vaccine> vaccine = vaccineClient.getByIdVaccine(vaccineManager.getVaccine().getId());
                vaccine.ifPresent(manager::setVaccine);
            }

            if (vaccineManager.getPatient() != null) {
                Optional<Patient> patient = patientClient.getByIdPatient(vaccineManager.getPatient().getId());
                String patientState = patient.get().getAddress().getState();
                if (
                        patient.isEmpty()
                                || (!state.isEmpty()
                                && !patientState.equalsIgnoreCase(state))
                ) {
                    return;
                }
                manager.setPatient(patient.get());
            }
            vaccineManagers.add(manager);
        });

        return vaccineManagers;
    }

    private void verifyIfVaccineDateIsBeforeLastVaccinationPlusDays(LocalDate vaccineValidate, LocalDate vaccineDate, LocalDate lastVaccinationPlusDays) throws InvalidVaccineDateException {
        if (
                LocalDate.now().isAfter(vaccineValidate)
                        || (vaccineDate.isBefore(lastVaccinationPlusDays))
        ) throw new InvalidVaccineDateException(
                "O paciente só pode se vacinar novamente na data: " + lastVaccinationPlusDays.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

    }

    private void verifyIfListOfDosesIsGreaterOfAmountOfDoses(Integer quantityOfDoses, Vaccine vaccine) throws AmountOfVacinationException {
        if (quantityOfDoses >= vaccine.getAmountOfDose()) {
            throw new AmountOfVacinationException("Não foi possível processar a sua solicitação pois o paciente já recebeu todas as vacinas necessárias!");
        }
    }

    @Cacheable("vaccine-patient-by-id")
    public List<VaccineManager> getAllVaccinesByPatientId(String patientId) throws NotFoundException {
        Optional<List<VaccineManager>> vaccineManagerOptional = vaccineManagerRepository.findAllByPatientId(patientId);

        if (vaccineManagerOptional.isEmpty()) {
            throw new NotFoundException("Paciente não encontrado");
        }

        List<VaccineManager> vaccineManagers = new ArrayList<>();
        vaccineManagerOptional.get().forEach(vaccineManager -> {
            Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManager.getVaccine().getId());
            Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManager.getPatient().getId());

            vaccineManagers.add(
                    new VaccineManager(
                        vaccineManager.getCreatedAt(),
                        vaccineManager.getUpdatedAt(),
                        vaccineManager.getId(),
                        patientOptional.get(),
                        vaccineOptional.get(),
                        vaccineManager.getListOfDoses(),
                        vaccineManager.getNurseProfessionals()
                    )
            );
        });

        return vaccineManagers;
    }

    public void deleteAll() {
        vaccineManagerRepository.deleteAll();
    }
}

package api.microservice.vaccine_manager.service;

import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.dto.Patient;
import api.microservice.vaccine_manager.dto.Vaccine;
import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.*;
import api.microservice.vaccine_manager.repository.VaccineManagerRepository;
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
    public VaccineManager create(VaccineManager vaccineManager) throws UnprocessableEntityException {
        Optional<VaccineManager> patientAlreadyRegistered = vaccineManagerRepository.getByIdPatient(vaccineManager.getIdPatient());

        if (patientAlreadyRegistered.isPresent()) {
            throw new UnprocessableEntityException(
                    "O paciente já tomou a sua primeira dose"
            );
        }

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
        }

        newVaccineManager.setIdVaccine(vaccineManager.getIdVaccine());
        newVaccineManager.setNurseProfessional(vaccineManager.getNurseProfessional());
        return vaccineManagerRepository.insert(newVaccineManager);
    }

    @Cacheable("vaccine-manager-list")
    public List<VaccineManagerDTO> listVaccineManager(String state) {
        List<VaccineManager> listOfVaccineManger = vaccineManagerRepository.findAll();
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
        Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManager.getIdVaccine());
        Optional<Vaccine> oldVaccineOptional = vaccineClient.getByIdVaccine(storedVaccineManager.getIdVaccine());
        Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManager.getIdPatient());

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
        LocalDate vaccineDate = vaccineManager.getVaccineDate();

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

        storedVaccineManager.setVaccineDate(vaccineManager.getVaccineDate());
        storedVaccineManager.setNurseProfessional(vaccineManager.getNurseProfessional());

        if (vaccineDate.isEqual(lastVaccinationPlusDays)) {
            storedVaccineManager.getListOfDoses().add(vaccineManager.getVaccineDate());
        }

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO();
        BeanUtils.copyProperties(storedVaccineManager, vaccineManagerDTO);

        vaccineManagerDTO.setPatient(patientOptional.get());
        vaccineManagerDTO.setVaccine(vaccineOptional.get());

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

        vaccineManager.getListOfDoses().remove(lastVaccineDose - 1);

        return vaccineManagerRepository.save(vaccineManager);
    }

    @Cacheable("vaccine-manager-overdue-list")
    public List<VaccineManagerDTO> filterVaccinesOverdue(String state) {
        List<VaccineManager> allVaccinesManager = vaccineManagerRepository.findAll();
        List<VaccineManagerDTO> listOfVaccineManagerDTO = filterVaccineManager(state, allVaccinesManager);
        List<VaccineManagerDTO> returnedOfVaccineManagerDTO = new ArrayList<>();
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
    public List<VaccineManagerDTO> filterVaccinesByManufacturer(String manufacturer, String state) {
        List<VaccineManager> allVaccinesManager = vaccineManagerRepository.findAll();
        List<VaccineManagerDTO> listOfVaccineManagerDTO = filterVaccineManager(state, allVaccinesManager);
        List<VaccineManagerDTO> returnedOfVaccineManagerDTO = new ArrayList<>();

        listOfVaccineManagerDTO.forEach(
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

    private List<VaccineManagerDTO> filterVaccineManager(String state, List<VaccineManager> listOfVaccineManger) {
        List<VaccineManagerDTO> listOfVaccineManagerDTO = new ArrayList<>();

        listOfVaccineManger.forEach(item -> {
            try {
                VaccineManagerDTO managerDTO = new VaccineManagerDTO();
                BeanUtils.copyProperties(item, managerDTO);

                Optional<Vaccine> vaccine = vaccineClient.getByIdVaccine(item.getIdVaccine());
                vaccine.ifPresent(managerDTO::setVaccine);

                Optional<Patient> patient = patientClient.getByIdPatient(item.getIdPatient());
                String patientState = patient.get().getAddress().getState();

                if (
                        patient.isEmpty()
                                || (!state.isEmpty()
                                && !patientState.equalsIgnoreCase(state))
                ) {
                    return;
                }

                managerDTO.setPatient(patient.get());
                listOfVaccineManagerDTO.add(managerDTO);
            } catch (Exception e) {
                System.out.println(e.getMessage()); // Possível LOG
            }
        });

        return listOfVaccineManagerDTO;
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
    public List<VaccineManagerDTO> getAllVaccinesByPatientId(String patientId) throws NotFoundException {
        Optional<List<VaccineManager>> vaccineManagerOptional = vaccineManagerRepository.findAllByIdPatient(patientId);

        if (vaccineManagerOptional.isEmpty()) {
            throw new NotFoundException("Paciente não encontrado");
        }

        List<VaccineManagerDTO> listOfVaccineManagerDTO = new ArrayList<>();
        vaccineManagerOptional.get().forEach(vaccineManager -> {
            Optional<Vaccine> vaccineOptional = vaccineClient.getByIdVaccine(vaccineManager.getIdVaccine());
            Optional<Patient> patientOptional = patientClient.getByIdPatient(vaccineManager.getIdPatient());

            listOfVaccineManagerDTO.add(
                    new VaccineManagerDTO(
                        vaccineManager.getId(),
                        vaccineManager.getVaccineDate(),
                        patientOptional.get(),
                        vaccineOptional.get(),
                        vaccineManager.getListOfDoses(),
                        vaccineManager.getNurseProfessional()
                    )
            );
        });

        return listOfVaccineManagerDTO;
    }
}

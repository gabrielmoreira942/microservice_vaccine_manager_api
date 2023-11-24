package api.microservice.controller;

import api.microservice.vaccine_manager.VaccineManagerApplication;
import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.entity.NurseProfessional;
import api.microservice.vaccine_manager.service.dto.*;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.BadRequestException;
import api.microservice.vaccine_manager.handler.exceptions.NotFoundException;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import util.JsonHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = VaccineManagerApplication.class)
@AutoConfigureMockMvc
class VaccineManagerControllerTest {

    public static final String RESOURCE_URL = "/vaccine-manager";
    @Autowired
    MockMvc mockMvc;

    @MockBean
    VaccineManagerService vaccineManagerService;

    @MockBean
    VaccineClient vaccineClient;

    @MockBean
    PatientClient patientClient;

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados sem filtro de estado")
    void should_returnedAllPatientRegistered() throws Exception {
        String state = "";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        vaccine.setManufacturer("Test");
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);
        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );
        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.listVaccineManager(state)).thenReturn(vaccineManagerList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn().getResponse();

        VaccineManager[] returnedVaccineManagerList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager[].class);
        VaccineManager firstVaccineManager = Arrays.stream(returnedVaccineManagerList).findFirst().get();

        assertEquals(vaccineManager.getId(), firstVaccineManager.getId());
        assertEquals(dateOfDose, firstVaccineManager.getListOfDoses().get(0));
        assertEquals(vaccineManager.getVaccine(), firstVaccineManager.getVaccine());
        assertEquals(vaccineManager.getPatient(), firstVaccineManager.getPatient());
        assertEquals(vaccineManager.getNurseProfessionals().get(0), firstVaccineManager.getNurseProfessionals().get(0));

        verify(vaccineManagerService, times(1)).listVaccineManager(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados com filtro de estado Bahia")
    void should_returnedAllPatientRegistered_ByStateBahia() throws Exception {
        String state = "Bahia";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        patient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        patient.setContact(contact);
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        vaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);

        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );
        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.listVaccineManager(state)).thenReturn(vaccineManagerList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManager[] returnedVaccineManagerList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager[].class);
        assert returnedVaccineManagerList != null;
        VaccineManager firstVaccineManager = Arrays.stream(returnedVaccineManagerList).findFirst().get();
        assertEquals(1, returnedVaccineManagerList.length);
        assertEquals(state, firstVaccineManager.getPatient().getAddress().getState());
        verify(vaccineManagerService, times(1)).listVaccineManager(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes com vacinas atrasadas")
    void should_returnedAllPatientWithOverdueVaccines() throws Exception {
        String state = "";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        patient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        patient.setContact(contact);
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        vaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);

        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );
        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.filterVaccinesOverdue(state)).thenReturn(vaccineManagerList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "/overdue"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManager[] returnedVaccineManagerList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager[].class);
        VaccineManager firstVaccineManager = Arrays.stream(returnedVaccineManagerList).findFirst().get();

        assertEquals(vaccineManager.getId(), firstVaccineManager.getId());
        assertEquals(dateOfDose, firstVaccineManager.getListOfDoses().get(0));
        assertEquals(vaccineManager.getVaccine(), firstVaccineManager.getVaccine());
        assertEquals(vaccineManager.getPatient(), firstVaccineManager.getPatient());
        assertEquals(vaccineManager.getNurseProfessionals().get(0), firstVaccineManager.getNurseProfessionals().get(0));

        verify(vaccineManagerService, times(1)).filterVaccinesOverdue(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes com vacinas atrasadas filtrados por estado")
    void should_returnedAllPatientWithOverdueVaccines_FilteredByState() throws Exception {
        String state = "Bahia";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        patient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        patient.setContact(contact);
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        vaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);

        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );
        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.filterVaccinesOverdue(state)).thenReturn(vaccineManagerList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "/overdue?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManager[] returnedVaccineManagerList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager[].class);
        VaccineManager firstVaccineManager = Arrays.stream(returnedVaccineManagerList).findFirst().get();
        assertEquals(1, returnedVaccineManagerList.length);
        assertEquals(state, firstVaccineManager.getPatient().getAddress().getState());
        verify(vaccineManagerService, times(1)).filterVaccinesOverdue(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados filtrados fabricante")
    void should_returnedAllPatientRegistered_FilteredByManufacturer() throws Exception {
        String state = "";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        patient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        patient.setContact(contact);
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        vaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);

        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );

        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.filterVaccinesByManufacturer("Test", state)).thenReturn(vaccineManagerList);

        mockMvc.perform(get(RESOURCE_URL + "/manufacturer/Test"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(vaccineManagerService, times(1)).filterVaccinesByManufacturer("Test", state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados filtrados fabricante e estado")
    void should_returnedAllPatientRegistered_FilteredByManufacturerAndState() throws Exception {
        String state = "Bahia";
        List<VaccineManager> vaccineManagerList = new ArrayList<>();
        Patient patient = new Patient();
        patient.setFirstName("Natã");
        patient.setLastName("Ferreira");
        patient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        patient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        patient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        patient.setContact(contact);
        Vaccine vaccine = new Vaccine();
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        vaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate dateOfDose = LocalDate.now().minusDays(5);
        localDates.add(dateOfDose);

        VaccineManager vaccineManager = new VaccineManager(
                "1",
                patient,
                vaccine,
                localDates,
                List.of(new NurseProfessional("Fulano de Tal", "080.625.137-79"))
        );
        vaccineManagerList.add(vaccineManager);

        when(vaccineManagerService.filterVaccinesByManufacturer("Test", state)).thenReturn(vaccineManagerList);

        mockMvc.perform(get(RESOURCE_URL + "/manufacturer/Test?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(vaccineManagerService, times(1)).filterVaccinesByManufacturer("Test", state);
    }

    @Test
    @DisplayName("Deve criar um registro de Patient e retorna os valores no corpo")
    void should_createPatientRegistered_ExpectedCreated() throws Exception {
        Patient mockPatient = createPatientMocked();
        when(patientClient.getByIdPatient("idtestepatient")).thenReturn(Optional.of(mockPatient));

        Vaccine mockVaccine = createVaccineMock();
        when(vaccineClient.getByIdVaccine("idtestevaccine")).thenReturn(Optional.of(mockVaccine));

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO();
        vaccineManagerDTO.setIdVaccine("idtestevaccine");
        vaccineManagerDTO.setIdPatient("idtestepatient");
        vaccineManagerDTO.setLastDateOfVaccine(LocalDate.of(2023, 11, 9));
        vaccineManagerDTO.setNurseProfessional(new NurseProfessional("Joãozinho", "529.876.140-20"));


        VaccineManager vaccineManager = new VaccineManager();
        vaccineManager.setVaccine(mockVaccine);
        vaccineManager.setPatient(mockPatient);
        vaccineManager.setListOfDoses(List.of(mockVaccine.getValidateDate()));
        vaccineManager.setNurseProfessionals(List.of(new NurseProfessional("Joãozinho", "529.876.140-20")));
        vaccineManager.setId("vaccine_manager_test");

        when(vaccineManagerService.create(any(VaccineManagerDTO.class))).thenReturn(vaccineManager);

        MockHttpServletResponse response = mockMvc.perform(
                post(RESOURCE_URL)
                        .content(JsonHelper.toJson(vaccineManagerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated()).andReturn().getResponse();

        VaccineManager returnedVaccineManager = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager.class);
        assertEquals(vaccineManager.getId(), returnedVaccineManager.getId());
        assertEquals(vaccineManager.getVaccine().getId(), returnedVaccineManager.getVaccine().getId());
        assertEquals(vaccineManager.getPatient().getId(), returnedVaccineManager.getPatient().getId());
        assertEquals(vaccineManager.getListOfDoses(), returnedVaccineManager.getListOfDoses());
        assertEquals(vaccineManager.getNurseProfessionals().get(0), returnedVaccineManager.getNurseProfessionals().get(0));

        verify(vaccineManagerService, times(1)).create(any(VaccineManagerDTO.class));
    }

    private Patient createPatientMocked() {
        Patient mockPatient = new Patient();
        mockPatient.setId("idtestepatient");
        mockPatient.setFirstName("Natã");
        mockPatient.setLastName("Ferreira");
        mockPatient.setBirthDate(String.valueOf(LocalDate.of(2001, 2, 25)));
        mockPatient.setGender("M");
        Address address = new Address("2", "Teste", "Teste", "42739195", "Bahia", "Rua teste");
        mockPatient.setAddress(address);
        Contact contact = new Contact("71999885759", "71999885759", "teste@teste.com.br");
        mockPatient.setContact(contact);
        return mockPatient;
    }

    private Vaccine createVaccineMock() {
        Vaccine mockVaccine = new Vaccine();
        mockVaccine.setId("idtestevaccine");
        mockVaccine.setBatch("Astrazenica");
        mockVaccine.setAmountOfDose(2);
        mockVaccine.setIntervalBetweenDoses(30);
        mockVaccine.setManufacturer("Test");
        LocalDate validateDate = LocalDate.now().plusYears(2);
        mockVaccine.setValidateDate(validateDate);

        return mockVaccine;
    }

    @Test
    @DisplayName("Deve deletar um registro de paciente e retorna os valores no corpo")
    void should_deleteLastPatientRegistered_ExpectedOk() throws Exception {
        VaccineManager vaccineManager = new VaccineManager();

        vaccineManager.setId("idvaccinemanager");
        Vaccine vaccineMock = createVaccineMock();
        vaccineManager.setVaccine(vaccineMock);
        vaccineManager.setPatient(createPatientMocked());
        vaccineManager.setNurseProfessionals(List.of(new NurseProfessional("Joãozinho", "529.876.140-20")));
        vaccineManager.setListOfDoses(List.of(vaccineMock.getValidateDate()));
        when(vaccineManagerService.removeLastVaccination(vaccineManager.getId())).thenReturn(vaccineManager);

        MockHttpServletResponse response = mockMvc.perform(
                patch(RESOURCE_URL + "/" + vaccineManager.getId())
                        .content(JsonHelper.toJson(vaccineManager))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse();

        VaccineManager returnedVaccineManager = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager.class);

        assertEquals(vaccineManager.getId(), returnedVaccineManager.getId());
        assertEquals(vaccineManager.getVaccine(), returnedVaccineManager.getVaccine());
        assertEquals(vaccineManager.getPatient(), returnedVaccineManager.getPatient());
        assertEquals(vaccineManager.getListOfDoses(), returnedVaccineManager.getListOfDoses());
        assertEquals(vaccineManager.getNurseProfessionals(), returnedVaccineManager.getNurseProfessionals());

        verify(vaccineManagerService, times(1)).removeLastVaccination(vaccineManager.getId());
    }

    @Test
    @DisplayName("Deve lançar NotFoundException para um ID inválido")
    void shouldThrowNotFoundExceptionForInvalidId() throws Exception {
        String invalidId = "invalidId";

        when(vaccineManagerService.removeLastVaccination(invalidId)).thenThrow(new NotFoundException("Registro da vacinação não foi encontrado."));

        mockMvc.perform(patch(RESOURCE_URL + "/" + invalidId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve lançar BadRequestException para um registro sem vacinas")
    void shouldThrowBadRequestExceptionForEmptyList() throws Exception {
        String idVaccineManager = "idvaccinemanager";

        when(vaccineManagerService.removeLastVaccination(idVaccineManager)).thenThrow(new BadRequestException("Você não possui registros a serem removidos."));

        mockMvc.perform(patch(RESOURCE_URL + "/" + idVaccineManager))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve atualizar um registro de Paciente")
    void should_updateVaccineManagerRegister() throws Exception {
        VaccineManager vaccineManager = new VaccineManager();
        vaccineManager.setId("idvaccinemanager");
        Vaccine vaccineMock = createVaccineMock();
        vaccineManager.setVaccine(vaccineMock);
        Patient patientMocked = createPatientMocked();
        vaccineManager.setPatient(patientMocked);
        vaccineManager.setNurseProfessionals(List.of(new NurseProfessional("Joãozinho", "529.876.140-20")));
        vaccineManager.setListOfDoses(List.of(LocalDate.now()));

        when(patientClient.getByIdPatient("idtestepatient")).thenReturn(Optional.of(patientMocked));
        when(vaccineClient.getByIdVaccine("idtestevaccine")).thenReturn(Optional.of(vaccineMock));

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO();
        vaccineManagerDTO.setIdPatient(patientMocked.getId());
        vaccineManagerDTO.setIdVaccine(vaccineMock.getId());
        vaccineManagerDTO.setLastDateOfVaccine(LocalDate.now());
        vaccineManagerDTO.setNurseProfessional(vaccineManager.getNurseProfessionals().get(0));

        when(vaccineManagerService.update(
                vaccineManager.getId(),
                vaccineManagerDTO)
        ).thenReturn(vaccineManager);

        mockMvc.perform(
                put(RESOURCE_URL + "/" + vaccineManager.getId())
                        .content(JsonHelper.toJson(vaccineManagerDTO))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.listOfDoses").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.patient").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vaccine").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nurseProfessionals").isNotEmpty());

        verify(vaccineManagerService, times(1)).update(vaccineManager.getId(), vaccineManagerDTO);
    }

}

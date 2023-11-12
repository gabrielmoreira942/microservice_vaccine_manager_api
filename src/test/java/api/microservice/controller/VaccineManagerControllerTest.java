package api.microservice.controller;

import api.microservice.vaccine_manager.VaccineManagerApplication;
import api.microservice.vaccine_manager.client.PatientClient;
import api.microservice.vaccine_manager.client.VaccineClient;
import api.microservice.vaccine_manager.dto.Patient;
import api.microservice.vaccine_manager.dto.Vaccine;
import api.microservice.vaccine_manager.dto.VaccineManagerDTO;
import api.microservice.vaccine_manager.entity.Address;
import api.microservice.vaccine_manager.entity.Contact;
import api.microservice.vaccine_manager.entity.NurseProfessional;
import api.microservice.vaccine_manager.entity.VaccineManager;
import api.microservice.vaccine_manager.handler.exceptions.BadRequestException;
import api.microservice.vaccine_manager.handler.exceptions.NotFoundException;
import api.microservice.vaccine_manager.service.VaccineManagerService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uti.JsonHelper;

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
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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
        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.listVaccineManager(state)).thenReturn(vaccineManagerDTOList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(1))
                .andReturn().getResponse();

        VaccineManagerDTO[] returnedVaccineManagerDTOList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManagerDTO[].class);
        VaccineManagerDTO firstVaccineManagerDTO = Arrays.stream(returnedVaccineManagerDTOList).findFirst().get();

        assertEquals(vaccineManagerDTO.getId(), firstVaccineManagerDTO.getId());
        assertEquals(dateOfDose, firstVaccineManagerDTO.getListOfDoses().get(0));
        assertEquals(vaccineManagerDTO.getVaccine(), firstVaccineManagerDTO.getVaccine());
        assertEquals(vaccineManagerDTO.getPatient(), firstVaccineManagerDTO.getPatient());
        assertEquals(vaccineManagerDTO.getNurseProfessional(), firstVaccineManagerDTO.getNurseProfessional());

        verify(vaccineManagerService, times(1)).listVaccineManager(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados com filtro de estado Bahia")
    void should_returnedAllPatientRegistered_ByStateBahia() throws Exception {
        String state = "Bahia";
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.listVaccineManager(state)).thenReturn(vaccineManagerDTOList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManagerDTO[] returnedVaccineManagerDTOList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManagerDTO[].class);
        VaccineManagerDTO firstVaccineManagerDTO = Arrays.stream(returnedVaccineManagerDTOList).findFirst().get();
        assertEquals(1, returnedVaccineManagerDTOList.length);
        assertEquals(state, firstVaccineManagerDTO.getPatient().getAddress().getState());
        verify(vaccineManagerService, times(1)).listVaccineManager(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes com vacinas atrasadas")
    void should_returnedAllPatientWithOverdueVaccines() throws Exception {
        String state = "";
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.filterVaccinesOverdue(state)).thenReturn(vaccineManagerDTOList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "/overdue"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManagerDTO[] returnedVaccineManagerDTOList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManagerDTO[].class);
        VaccineManagerDTO firstVaccineManagerDTO = Arrays.stream(returnedVaccineManagerDTOList).findFirst().get();

        assertEquals(vaccineManagerDTO.getId(), firstVaccineManagerDTO.getId());
        assertEquals(dateOfDose, firstVaccineManagerDTO.getListOfDoses().get(0));
        assertEquals(vaccineManagerDTO.getVaccine(), firstVaccineManagerDTO.getVaccine());
        assertEquals(vaccineManagerDTO.getPatient(), firstVaccineManagerDTO.getPatient());
        assertEquals(vaccineManagerDTO.getNurseProfessional(), firstVaccineManagerDTO.getNurseProfessional());

        verify(vaccineManagerService, times(1)).filterVaccinesOverdue(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes com vacinas atrasadas filtrados por estado")
    void should_returnedAllPatientWithOverdueVaccines_FilteredByState() throws Exception {
        String state = "Bahia";
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.filterVaccinesOverdue(state)).thenReturn(vaccineManagerDTOList);

        MockHttpServletResponse response = mockMvc.perform(get(RESOURCE_URL + "/overdue?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManagerDTO[] returnedVaccineManagerDTOList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManagerDTO[].class);
        VaccineManagerDTO firstVaccineManagerDTO = Arrays.stream(returnedVaccineManagerDTOList).findFirst().get();
        assertEquals(1, returnedVaccineManagerDTOList.length);
        assertEquals(state, firstVaccineManagerDTO.getPatient().getAddress().getState());
        verify(vaccineManagerService, times(1)).filterVaccinesOverdue(state);
    }

    @Test
    @DisplayName("Deve retornar todos os pacientes registrados filtrados fabricante")
    void should_returnedAllPatientRegistered_FilteredByManufacturer() throws Exception {
        String state = "";
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.filterVaccinesByManufacturer("Test", state)).thenReturn(vaccineManagerDTOList);

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
        List<VaccineManagerDTO> vaccineManagerDTOList = new ArrayList<>();
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

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO(
                "1",
                LocalDate.now().minusDays(5),
                patient,
                vaccine,
                localDates,
                new NurseProfessional("Fulano de Tal", "080.625.137-79")
        );
        vaccineManagerDTOList.add(vaccineManagerDTO);

        when(vaccineManagerService.filterVaccinesByManufacturer("Test", state)).thenReturn(vaccineManagerDTOList);

        mockMvc.perform(get(RESOURCE_URL + "/manufacturer/Test?state=Bahia"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        verify(vaccineManagerService, times(1)).filterVaccinesByManufacturer("Test", state);
    }

    @Test
    @DisplayName("Deve criar um registro de Patient e retorna os valores no corpo")
    void should_createPatientRegistered_ExpectedCreated() throws Exception {
        Patient mockPatient = new Patient();
        mockPatient.setId("idtestepatient");
        when(patientClient.getByIdPatient("idtestepatient")).thenReturn(Optional.of(mockPatient));

        Vaccine mockVaccine = new Vaccine();
        mockVaccine.setId("idtestevaccine");
        when(vaccineClient.getByIdVaccine("idtestevaccine")).thenReturn(Optional.of(mockVaccine));

        VaccineManager vaccineManager = new VaccineManager();
        vaccineManager.setId("ajksdii2jkksdkwkejwjb4");
        vaccineManager.setIdVaccine("idtestevaccine");
        vaccineManager.setIdPatient("idtestepatient");
        vaccineManager.setVaccineDate(LocalDate.of(2023, 11, 9));
        vaccineManager.setNurseProfessional(new NurseProfessional("Joãozinho", "529.876.140-20"));

        when(vaccineManagerService.create(any(VaccineManager.class))).thenReturn(vaccineManager);

        MockHttpServletResponse response = mockMvc.perform(
                post(RESOURCE_URL)
                        .content(JsonHelper.toJson(vaccineManager))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isCreated()).andReturn().getResponse();

        VaccineManager returnedVaccineManager = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager.class);
        assertEquals(vaccineManager.getId(), returnedVaccineManager.getId());
        assertEquals(vaccineManager.getIdVaccine(), returnedVaccineManager.getIdVaccine());
        assertEquals(vaccineManager.getIdPatient(), returnedVaccineManager.getIdPatient());
        assertEquals(vaccineManager.getListOfDoses(), returnedVaccineManager.getListOfDoses());
        assertEquals(vaccineManager.getNurseProfessional(), returnedVaccineManager.getNurseProfessional());
        assertEquals(vaccineManager.getVaccineDate(), returnedVaccineManager.getVaccineDate());

        verify(vaccineManagerService, times(1)).create(any(VaccineManager.class));
    }

    @Test
    @DisplayName("Deve deletar um registro de paciente e retorna os valores no corpo")
    void should_deleteLastPatientRegistered_ExpectedOk() throws Exception {
        VaccineManager vaccineManager = new VaccineManager();
        vaccineManager.setId("idvaccinemanager");
        vaccineManager.setIdVaccine("idtestevaccine");
        vaccineManager.setIdPatient("idtestepatient");
        vaccineManager.setVaccineDate(LocalDate.of(2023, 11, 9));
        vaccineManager.setNurseProfessional(new NurseProfessional("Joãozinho", "529.876.140-20"));
        when(vaccineManagerService.removeLastVaccination(vaccineManager.getId())).thenReturn(vaccineManager);

        MockHttpServletResponse response = mockMvc.perform(
                patch(RESOURCE_URL + "/" + vaccineManager.getId())
                        .content(JsonHelper.toJson(vaccineManager))
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn().getResponse();

        VaccineManager returnedVaccineManager = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager.class);

        assertEquals(vaccineManager.getId(), returnedVaccineManager.getId());
        assertEquals(vaccineManager.getIdVaccine(), returnedVaccineManager.getIdVaccine());
        assertEquals(vaccineManager.getIdPatient(), returnedVaccineManager.getIdPatient());
        assertEquals(vaccineManager.getListOfDoses(), returnedVaccineManager.getListOfDoses());
        assertEquals(vaccineManager.getNurseProfessional(), returnedVaccineManager.getNurseProfessional());
        assertEquals(vaccineManager.getVaccineDate(), returnedVaccineManager.getVaccineDate());

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
        Patient patient = new Patient();
        patient.setId("testeId");
        patient.setFirstName("Ivan");
        patient.setLastName("Romão");
        patient.setBirthDate(String.valueOf(LocalDate.of(2023, 3, 10)));
        patient.setGender("M");
        patient.setCpf("76824616672");

        Address address = new Address();
        address.setNumber("123");
        address.setNeighborhood("Sample Neighborhood");
        address.setCounty("Sample County");
        address.setZipCode("12345");
        address.setState("Sample State");
        address.setStreet("Sample Street");

        Contact contact = new Contact();
        contact.setTelephone("(71) 9456-7890");
        contact.setWhatsapp("(71) 9456-7890");
        contact.setEmail("test@example.com");

        patient.setAddress(address);
        patient.setContact(contact);

        Vaccine vaccine = new Vaccine();
        vaccine.setId("13123asdas");
        vaccine.setBatch("Astrazenica");
        vaccine.setAmountOfDose(2);
        vaccine.setIntervalBetweenDoses(30);
        LocalDate validateDate = LocalDate.now().plusYears(2);
        vaccine.setValidateDate(validateDate);

        VaccineManager vaccineManager = new VaccineManager();
        vaccineManager.setId("asdasdsadasdasda");
        vaccineManager.setIdVaccine(vaccine.getId());
        vaccineManager.setIdPatient(patient.getId());
        vaccineManager.setNurseProfessional(new NurseProfessional("Joãozinho", "529.876.140-20"));
        vaccineManager.setVaccineDate(LocalDate.of(2023, 11, 9));

        VaccineManagerDTO vaccineManagerDTO = new VaccineManagerDTO();
        vaccineManagerDTO.setId(vaccineManager.getId());
        vaccineManagerDTO.setVaccineDate(vaccineManager.getVaccineDate());
        vaccineManagerDTO.setNurseProfessional(vaccineManager.getNurseProfessional());
        vaccineManagerDTO.setPatient(patient);
        vaccineManagerDTO.setVaccine(vaccine);

        when(vaccineManagerService.update(
                vaccineManager.getId(),
                vaccineManager)
        ).thenReturn(vaccineManagerDTO);

        ObjectMapper mapper = new ObjectMapper();

        MockHttpServletResponse response = mockMvc.perform(
                put(RESOURCE_URL + "/" + vaccineManagerDTO.getId())
                        .content(JsonHelper.toJson(vaccineManager))
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vaccineDate").value(vaccineManagerDTO.getVaccineDate().toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.patient").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.vaccine").isNotEmpty())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nurseProfessional").value(vaccineManager.getNurseProfessional()))
                .andReturn().getResponse();

        verify(vaccineManagerService, times(1)).update(vaccineManagerDTO.getId(), vaccineManager);
    }

}

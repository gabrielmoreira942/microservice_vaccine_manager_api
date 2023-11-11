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
import uti.JsonHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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
                .andExpect(MockMvcResultMatchers.status().isOk())
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        VaccineManagerDTO[] returnedVaccineManagerDTOList = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManagerDTO[].class);
        VaccineManagerDTO firstVaccineManagerDTO = Arrays.stream(returnedVaccineManagerDTOList).findFirst().get();
        assertEquals(1, returnedVaccineManagerDTOList.length);
        assertEquals(state, firstVaccineManagerDTO.getPatient().getAddress().getState());
        verify(vaccineManagerService, times(1)).listVaccineManager(state);
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
                ).andExpect(MockMvcResultMatchers.status().isCreated()).andReturn().getResponse();

        VaccineManager returnedVaccineManager = JsonHelper.toObject(response.getContentAsByteArray(), VaccineManager.class);
        assertNotNull(returnedVaccineManager.getId());
        assertNotNull(returnedVaccineManager.getIdVaccine());
        assertNotNull(returnedVaccineManager.getIdPatient());
        assertNotNull(returnedVaccineManager.getListOfDoses());
        assertNotNull(returnedVaccineManager.getNurseProfessional());
        assertNotNull(returnedVaccineManager.getVaccineDate());
    }
}

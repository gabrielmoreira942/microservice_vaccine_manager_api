package api.microservice.vaccine_manager.dto;

import api.microservice.vaccine_manager.entity.NurseProfessional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VaccineManagerDTO {
    private String id;
    private LocalDate vaccineDate;
    private Patient patient;
    private Vaccine vaccine;
    private List<LocalDate> listOfDoses;
    private NurseProfessional nurseProfessional;
}

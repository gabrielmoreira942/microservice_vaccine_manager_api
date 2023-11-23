package api.microservice.vaccine_manager.service.dto;

import api.microservice.vaccine_manager.entity.NurseProfessional;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class VaccineManagerDTO {

    @NotEmpty(message = "Insira o id da patiente")
    private String idPatient;

    @NotEmpty(message = "Insira o id da vacina")
    private String idVaccine;

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate lastDateOfVaccine;

    @NotNull(message = "Insira o profissional de enfermagem que aplicou a vacina")
    private NurseProfessional nurseProfessional;
}

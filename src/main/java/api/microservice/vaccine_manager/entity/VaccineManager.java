package api.microservice.vaccine_manager.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineManager {

    @Id
    private String id;

    @PastOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate vaccineDate;

    @NotEmpty
    private String idPatient;

    @NotEmpty
    private String idVaccine;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private List<LocalDate> listOfDoses = new ArrayList<>();

    @NotNull(message = "Insira o profissional de enfermagem que aplicou a vacina")
    private NurseProfessional nurseProfessional;

}

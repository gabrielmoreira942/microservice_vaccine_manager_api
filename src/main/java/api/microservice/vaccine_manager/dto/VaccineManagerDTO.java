package api.microservice.vaccine_manager.dto;

import api.microservice.vaccine_manager.entity.NurseProfessional;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.web.JsonPath;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class VaccineManagerDTO {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private LocalDate vaccineDate;
    private Patient patient;
    private Vaccine vaccine;
    private List<LocalDate> listOfDoses;
    private NurseProfessional nurseProfessional;
}

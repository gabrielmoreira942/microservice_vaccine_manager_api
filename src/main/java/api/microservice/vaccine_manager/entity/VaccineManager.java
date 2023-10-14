package api.microservice.vaccine_manager.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class VaccineManager {
    @Id
    private String id;
    private LocalDate vaccineDate;

    private String patientId;
    private Vaccine vaccine;
    private List<LocalDate> listOfDoses;

    private NurseProfessional nurseProfessional;
}

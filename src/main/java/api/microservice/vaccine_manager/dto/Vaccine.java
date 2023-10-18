package api.microservice.vaccine_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vaccine {

    private String id;

    private String manufacturer;

    private String batch;

    private LocalDate validateDate;

    private Integer amountOfDose;

    private Integer intervalBetweenDoses;

}

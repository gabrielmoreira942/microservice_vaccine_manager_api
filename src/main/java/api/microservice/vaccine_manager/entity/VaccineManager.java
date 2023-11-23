package api.microservice.vaccine_manager.entity;

import api.microservice.vaccine_manager.entity.util.DatabaseObject;
import api.microservice.vaccine_manager.service.dto.Patient;
import api.microservice.vaccine_manager.service.dto.Vaccine;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document
public class VaccineManager extends DatabaseObject {

    @Id
    private String id;

    private Patient patient;

    private Vaccine vaccine;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "UTC")
    private List<LocalDate> listOfDoses = new ArrayList<>();

    private List<NurseProfessional> nurseProfessionals = new ArrayList<>();

    public VaccineManager(LocalDateTime createdAt, LocalDateTime updatedAt, String id, Patient patient, Vaccine vaccine, List<LocalDate> listOfDoses, List<NurseProfessional> nurseProfessionals) {
        super(createdAt, updatedAt);
        this.id = id;
        this.patient = patient;
        this.vaccine = vaccine;
        this.listOfDoses = listOfDoses;
        this.nurseProfessionals = nurseProfessionals;
    }

    public VaccineManager(String id, Patient patient, Vaccine vaccine, List<LocalDate> listOfDoses, List<NurseProfessional> nurseProfessionals) {
        this.id = id;
        this.patient = patient;
        this.vaccine = vaccine;
        this.listOfDoses = listOfDoses;
        this.nurseProfessionals = nurseProfessionals;
    }
}

package api.microservice.vaccine_manager.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    private String id;

    private String firstName;

    private String lastName;

    private String gender;

    private String cpf;

    private String birthDate;

    private Contact contact;

    private Address address;

}


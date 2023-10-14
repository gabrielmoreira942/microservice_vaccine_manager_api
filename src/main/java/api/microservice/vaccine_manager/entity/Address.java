package api.microservice.vaccine_manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    private String number;

    private String neighborhood;

    private String county;

    private String zipCode;

    private String state;

    private String street;

}

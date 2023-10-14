package api.microservice.vaccine_manager.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseProfessional {

    @NotEmpty(message = "O nome do profissional de enfermagem não foi informado")
    private String name;

    @CPF(message = "O CPF informado está inválido")
    @NotEmpty(message = "O CPF do profissional de enfermagem não foi informado")
    private String cpf;

}

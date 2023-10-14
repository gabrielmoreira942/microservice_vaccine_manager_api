package api.microservice.vaccine_manager.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineManager {

    @Id
    private String id;
    // TODO Não deixar salvar no futuro?
    private LocalDate vaccineDate;

    // TODO Deixei o id para conseguir usar o 404 da API de Patient
    private String idPatient;

    // TODO Deixei o id para conseguir usar o 404 da API de Vaccine
    private String idVaccine;

    private List<LocalDate> listOfDoses;

    @NotNull(message = "Insira o profissional de enfermagem que aplicou a vacina")
    private NurseProfessional nurseProfessional;

    // TODO Verificar qual anotação é melhor. Usar @Size + @Pattern ou User somente um @Pattern com todos os estados do BR
//    @Size(min = 2, max = 2, message = "Digite o estado em duas letras. Exemplo: BA")
//    @Pattern(regexp = "^[A-Z]{2}$", message = "O estado deve conter apenas letras maiúsculas. Exemplo: BA")
    @Pattern(regexp = "^(AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SE|SP|TO)$", message = "O estado deve conter apenas letras maiúsculas e ser uma sigla válida de estado brasileiro. Exemplo: BA")
    private String state;

}

package api.microservice.vaccine_manager.handler.exceptions;

import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

public class CustomVaccineErrorDecode implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == HttpStatus.NOT_FOUND.value()) {
            return new NotFoundException("Vacina não encontrada");
        }

        return FeignException.errorStatus(methodKey, response);
    }
}
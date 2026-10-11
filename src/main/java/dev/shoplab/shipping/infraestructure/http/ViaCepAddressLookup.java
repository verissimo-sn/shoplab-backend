package dev.shoplab.shipping.infraestructure.http;

import dev.shoplab.shipping.application.port.AddressLookup;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.logging.Logger;

@ApplicationScoped
public class ViaCepAddressLookup implements AddressLookup {

    private static final Logger LOG = Logger.getLogger(ViaCepAddressLookup.class.getName());
    private final ViaCepClient viaCepClient;

    public ViaCepAddressLookup(@RestClient ViaCepClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    @Override
    @Timeout(2000)
    @Retry(maxRetries = 2, delay = 2000, jitter = 100)
    @CircuitBreaker(requestVolumeThreshold = 10, failureRatio = 0.5, delay = 15000, successThreshold = 2)
    @Fallback(fallbackMethod = "unavailable")
    public Result lookup(String zipCode) {
        var response = viaCepClient.lookup(zipCode);
        if(Boolean.TRUE.equals(response.erro())){
            return new Result.NotFound();
        }
        return new Result.Found(response.localidade(), response.uf());
    }

    Result unavailable(String zipCode, Throwable cause) {
        LOG.warning(String.format("ViaCep unavailable for %s %s", zipCode, cause.toString()));
        return new Result.Unavailable(cause.getClass().getSimpleName());
    }
}

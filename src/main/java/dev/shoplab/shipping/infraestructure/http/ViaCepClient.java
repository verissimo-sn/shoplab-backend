package dev.shoplab.shipping.infraestructure.http;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "viacep")
@Path("/ws")
public interface ViaCepClient {
    record ViaCepResponse(String cep, String localidade, String uf, Boolean erro){}

    @GET
    @Path("/{cep}/json/")
    ViaCepResponse lookup(@PathParam("cep") String cep);
}

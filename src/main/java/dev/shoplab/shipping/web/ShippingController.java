package dev.shoplab.shipping.web;

import dev.shoplab.shipping.application.QuoteShippingUseCase;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;


@Path("/api/v1/shippings")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ShippingController {
    private final QuoteShippingUseCase quoteShippingUC;

    public ShippingController(QuoteShippingUseCase quoteShippingUC) {
        this.quoteShippingUC = quoteShippingUC;
    }

    @GET
    @Path("/quotes")
    public ShippingResponse quote(@QueryParam("zipCode") String zipCode) {
        return ShippingResponse.from(quoteShippingUC.execute(zipCode));
    }
}

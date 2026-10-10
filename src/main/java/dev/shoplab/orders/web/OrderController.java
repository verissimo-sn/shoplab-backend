package dev.shoplab.orders.web;

import dev.shoplab.catalog.api.CatalogApi;
import dev.shoplab.orders.application.CreateOrderUseCase;
import dev.shoplab.orders.application.GetOrderUseCase;
import dev.shoplab.orders.application.port.OrderRepository;
import dev.shoplab.shared.events.DomainEventPublisher;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.UUID;

@Path("/api/v1/orders")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class OrderController {
    private final CreateOrderUseCase createOrderUC;
    private final GetOrderUseCase getOrderUC;

    public OrderController(CreateOrderUseCase createOrderUC, GetOrderUseCase getOrderUc) {
        this.createOrderUC = createOrderUC;
        this.getOrderUC = getOrderUc;
    }

    @POST
    public RestResponse<OrderResponse> create(@Valid CreateOrderRequest request) {
        var order = createOrderUC.execute(request.toCommand());
        return RestResponse.ResponseBuilder
                .<OrderResponse>created(URI.create("/api/v1/orders/" + order.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    public OrderResponse get(@PathParam("id") UUID id) {
        return OrderResponse.from(getOrderUC.execute(id));
    }
}

package dev.shoplab.catalog.web;

import dev.shoplab.catalog.application.CreateProductUseCase;
import dev.shoplab.catalog.application.GetProductUseCase;
import dev.shoplab.catalog.application.ListProductsUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/api/v1/products")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductController {
    private final CreateProductUseCase createProductUC;
    private final GetProductUseCase getProductUC;
    private final ListProductsUseCase listProductsUC;

    public ProductController(
            CreateProductUseCase createProductUC,
            GetProductUseCase getProductUC,
            ListProductsUseCase listProductsUC
    ){
        this.createProductUC = createProductUC;
        this.getProductUC = getProductUC;
        this.listProductsUC = listProductsUC;
    }

    @POST
    public RestResponse<ProductResponse> create(@Valid CreateProductRequest request) {
        var product = createProductUC.execute(request.toCommand());
        return RestResponse.ResponseBuilder
                .<ProductResponse>created(URI.create("/api/v1/products/" + product.getId()))
                .build();
    }

    @GET
    @Path("/{id}")
    public ProductResponse get(@PathParam("id") UUID id) {
        return ProductResponse.from(getProductUC.execute(id));
    }

    @GET
    public List<ProductResponse> list(
            @QueryParam("page") @DefaultValue("0") @PositiveOrZero int page,
            @QueryParam("size") @DefaultValue("20") @Min(0) @Max(100) int size
    ){
        return listProductsUC.execute(page, size).stream().map(ProductResponse::from).toList();
    }

}

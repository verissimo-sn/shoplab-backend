package dev.shoplab.catalog.web;

import dev.shoplab.catalog.application.CreateProductUseCase;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max =40) String sku,
        @NotBlank @Size(max=200) String name,
        @NotNull @Positive @Digits(integer=10, fraction = 2) BigDecimal price,
        @PositiveOrZero int initialStock
        ) {

    CreateProductUseCase.Command toCommand() {
        return new CreateProductUseCase.Command(sku, name, price, initialStock);
    }
}

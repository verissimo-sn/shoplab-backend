package dev.shoplab.orders.web;

import dev.shoplab.orders.application.CreateOrderUseCase;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotBlank @Email String customerEmail,
        @NotBlank @Pattern(regexp = "\\d{8}", message = "must have 8 digits") String zipCode,
        @NotEmpty @Size(max = 20)List<@Valid ItemRequest> items
        ) {

    private record ItemRequest(@NotNull UUID productId, @Min(1) @Max(100) int quantity) { }

    CreateOrderUseCase.Command toCommand() {
        var items = items().stream().map((@Valid ItemRequest i) -> new CreateOrderUseCase.Command.Item(i.productId(), i.quantity())).toList();
        return new CreateOrderUseCase.Command(customerEmail, zipCode, items);
    }
}

package dev.shoplab.catalog.application;

import dev.shoplab.catalog.testing.InMemoryProductRepository;
import dev.shoplab.catalog.domain.Product;
import dev.shoplab.shared.errors.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

public class CreateProductUseCaseTest {
    private final InMemoryProductRepository repository = spy(new InMemoryProductRepository());
    private final CreateProductUseCase createProductUseCase = new CreateProductUseCase(repository);

    @Test
    void rejectsDuplicatedSku() {
        var existingProduct = repository.given("SKU-1", "Mouse", "99.00", 5);
        clearInvocations(repository);
        var cmd = new CreateProductUseCase.Command(
                existingProduct.getSku(),
                existingProduct.getName(),
                existingProduct.getPrice(),
                existingProduct.getStock()
        );
        assertThatThrownBy(() -> createProductUseCase.execute(cmd))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "SKU_ALREADY_EXISTS");
        verify(repository, never()).save(any());
    }

    @Test
    void returnCreatedProduct() {
        var cmd = new CreateProductUseCase.Command("SKU-1", "Mouse", new BigDecimal("99.00"), 5);
        var createdProduct = createProductUseCase.execute(cmd);
        var savedProduct = ArgumentCaptor.forClass(Product.class);
        verify(repository).findBySku(cmd.sku());
        verify(repository).save(savedProduct.capture());
        assertThat(savedProduct.getValue()).isSameAs(createdProduct);
    }
}

package dev.shoplab.catalog.application;

import dev.shoplab.catalog.testing.InMemoryProductRepository;
import dev.shoplab.shared.errors.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class GetProductUseCaseTest {
    private final InMemoryProductRepository repository = spy(new InMemoryProductRepository());
    private final GetProductUseCase getProductUseCase = new GetProductUseCase(repository);

    @Test
    void getProductById() {
        var createdProduct = repository.given("SKU-1", "Monitor", "799.00", 2);
        var foundProduct = getProductUseCase.execute(createdProduct.getId());
        verify(repository).findById(createdProduct.getId());
        assertThat(createdProduct).isSameAs(foundProduct);
    }

    @Test
    void rejectNotFoundProduct() {
        var fakeId = UUID.randomUUID();
        assertThatThrownBy(() -> getProductUseCase.execute(fakeId))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}

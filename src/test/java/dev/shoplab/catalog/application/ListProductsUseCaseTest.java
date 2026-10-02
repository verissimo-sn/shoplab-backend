package dev.shoplab.catalog.application;

import dev.shoplab.catalog.domain.Product;
import dev.shoplab.catalog.testing.InMemoryProductRepository;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ListProductsUseCaseTest {
    private final InMemoryProductRepository repository = new InMemoryProductRepository();
    private final ListProductsUseCase listProductsUseCase = new ListProductsUseCase(repository);

    // ordenados por nome: Keyboard, Monitor, Mouse
    private void givenThreeProducts() {
        repository.given("SKU-1", "Monitor", "799.99", 5);
        repository.given("SKU-2", "Mouse", "159.99", 15);
        repository.given("SKU-3", "Keyboard", "199.99", 8);
    }

    @Test
    void returnsFirstPageOrderedByName() {
        givenThreeProducts();

        var products = listProductsUseCase.execute(0, 2);

        assertThat(products).extracting(Product::getName).containsExactly("Keyboard", "Monitor");
    }

    @Test
    void returnsPartialLastPage() {
        givenThreeProducts();

        var products = listProductsUseCase.execute(1, 2);

        assertThat(products).extracting(Product::getName).containsExactly("Mouse");
    }

    @Test
    void returnsEmptyListWhenPageIsBeyondTheEnd() {
        givenThreeProducts();

        var products = listProductsUseCase.execute(2, 2);

        assertThat(products).isEmpty();
    }

    @Test
    void returnsEmptyListWhenThereAreNoProducts() {
        var products = listProductsUseCase.execute(0, 10);

        assertThat(products).isNotNull().isEmpty();
    }

    @Test
    void returnsAllProductsWhenSizeIsLargerThanTotal() {
        givenThreeProducts();

        var products = listProductsUseCase.execute(0, 10);

        assertThat(products).extracting(Product::getName).containsExactly("Keyboard", "Monitor", "Mouse");
    }
}

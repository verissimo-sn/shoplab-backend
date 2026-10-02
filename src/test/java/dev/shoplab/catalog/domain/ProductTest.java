package dev.shoplab.catalog.domain;

import dev.shoplab.shared.errors.BusinessRuleException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


public class ProductTest {

    @Test
    void reserveDecreasesStock() {
        var product = Product.create("SKU-1", "Mouse", new BigDecimal("99.99"), 5);
        product.reserve(2);
        assertThat(product.getStock()).isEqualTo(3);
    }

    @Test
    void cannotReserveMoreThanAvailable(){
        var product = Product.create("SKU-1", "Mouse", new BigDecimal("99.99"), 1);
        assertThatThrownBy(() -> product.reserve(2))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "INSUFFICIENT_STOCK");
    }

    @Test
    void cannotReserveNonPositiveAvailable(){
        var product = Product.create("SKU-1", "Mouse", new BigDecimal("99.99"), 1);
        assertThatThrownBy(() -> product.reserve(0))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_QUANTITY");
    }

    @Test
    void cannotCreateProductWithNegativePrice(){
        assertThatThrownBy(() -> Product.create("SKU-1", "Mouse", new BigDecimal("-99.99"), 1))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_PRICE");
    }

    @Test
    void cannotCreateProductWithNegativeStock(){
        assertThatThrownBy(() -> Product.create("SKU-1", "Mouse", new BigDecimal("99.99"), -1))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("code", "INVALID_STOCK");
    }

    @Test
    void increaseStock(){
        var product = Product.create("SKU-1", "Mouse", new BigDecimal("99.99"), 1);
        product.release(2);
        assertThat(product.getStock()).isEqualTo(3);
    }
}

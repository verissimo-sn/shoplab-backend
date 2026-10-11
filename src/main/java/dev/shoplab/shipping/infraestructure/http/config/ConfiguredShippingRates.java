package dev.shoplab.shipping.infraestructure.http.config;

import dev.shoplab.shipping.application.port.ShippingRates;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class ConfiguredShippingRates implements ShippingRates {
    private final ShippingConfig shippingConfig;

    public ConfiguredShippingRates(ShippingConfig shippingConfig) {
        this.shippingConfig = shippingConfig;
    }

    public BigDecimal rateFor(String state) {
        return shippingConfig.rates().getOrDefault(state, shippingConfig.defaultRate());
    }

    public BigDecimal fallbackRate() {
        return shippingConfig.defaultRate();
    }
}

package dev.shoplab.shipping.infraestructure.http.config;

import io.smallrye.config.ConfigMapping;

import java.math.BigDecimal;
import java.util.Map;

@ConfigMapping(prefix = "shoplab.shipping")
public interface ShippingConfig {
    BigDecimal defaultRate();
    Map<String, BigDecimal> rates();
}

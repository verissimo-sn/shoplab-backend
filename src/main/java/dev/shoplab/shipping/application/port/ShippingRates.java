package dev.shoplab.shipping.application.port;

import java.math.BigDecimal;

public interface ShippingRates {
    BigDecimal rateFor(String zipCode);
    BigDecimal fallbackRate();
}

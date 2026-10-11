package dev.shoplab.shipping.api;

import java.math.BigDecimal;

public interface ShippingApi {
    record Quote(String zipCode, String city, String state, BigDecimal cost, boolean estimated){}

    Quote quote(String zipCode);
}

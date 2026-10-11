package dev.shoplab.shipping.web;

import dev.shoplab.shipping.api.ShippingApi;

import java.math.BigDecimal;

public record ShippingResponse(String zipCode, String city, String state, BigDecimal cost, boolean estimated) {

    public static ShippingResponse from(ShippingApi.Quote quote) {
        return new ShippingResponse(quote.zipCode(), quote.city(), quote.state(), quote.cost(), quote.estimated());
    }
}

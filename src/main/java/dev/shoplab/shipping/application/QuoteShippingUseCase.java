package dev.shoplab.shipping.application;

import dev.shoplab.shared.errors.BusinessRuleException;
import dev.shoplab.shipping.api.ShippingApi;
import dev.shoplab.shipping.application.port.AddressLookup;
import dev.shoplab.shipping.application.port.ShippingRates;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class QuoteShippingUseCase {
    private final AddressLookup addressLookup;
    private final ShippingRates shippingRates;

    public QuoteShippingUseCase(AddressLookup addressLookup, ShippingRates shippingRates) {
        this.addressLookup = addressLookup;
        this.shippingRates = shippingRates;
    }

    public ShippingApi.Quote execute(String zipCode) {
        return switch (addressLookup.lookup(zipCode)) {
            case AddressLookup.Result.Found f ->
                    new ShippingApi.Quote(zipCode, f.city(), f.state(), shippingRates.rateFor(f.state()), false);
            case AddressLookup.Result.NotFound n ->
                    throw new BusinessRuleException("INVALID_ZIP_CODE", "Zip code not found: " + zipCode);
            case AddressLookup.Result.Unavailable u ->
                    new ShippingApi.Quote(zipCode, null, null, shippingRates.fallbackRate(), true);
        };
    }
}

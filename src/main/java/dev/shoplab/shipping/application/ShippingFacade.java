package dev.shoplab.shipping.application;

import dev.shoplab.shipping.api.ShippingApi;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ShippingFacade implements ShippingApi {
    private final QuoteShippingUseCase quoteShippingUC;

    public ShippingFacade(QuoteShippingUseCase quoteShippingUC) {
        this.quoteShippingUC = quoteShippingUC;
    }

    @Override
    public ShippingApi.Quote quote(String zipCode){
        return quoteShippingUC.execute(zipCode);
    }
}

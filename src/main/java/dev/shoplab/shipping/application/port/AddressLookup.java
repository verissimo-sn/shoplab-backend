package dev.shoplab.shipping.application.port;

public interface AddressLookup {

    sealed interface Result {
        record Found(String city, String state) implements Result {}
        record NotFound() implements Result {}
        record Unavailable(String reason) implements Result {}
    }

    Result lookup(String zipCode);
}

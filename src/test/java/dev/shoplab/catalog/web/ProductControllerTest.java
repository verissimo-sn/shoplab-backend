package dev.shoplab.catalog.web;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class ProductControllerTest {

    private static final String BASE_PATH = "/api/v1/products";

    private static String uniqueSku() {
        return "T-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static String payload(String sku, String name, String price, int initialStock) {
        return """
                {"sku":"%s","name":"%s","price":%s,"initialStock":%d}
                """.formatted(sku, name, price, initialStock);
    }

    private static String createProduct(String sku, String name, String price, int initialStock) {
        return given()
                .contentType(ContentType.JSON)
                .body(payload(sku, name, price, initialStock))
                .when().post(BASE_PATH)
                .then().statusCode(201)
                .extract().header("Location");
    }

    // ---------- POST /api/v1/products ----------

    @Test
    void createsAndFetchesProduct() {
        var sku = uniqueSku();
        var location = createProduct(sku, "Test product", "10.00", 5);

        given().when().get(location)
                .then().statusCode(200)
                .body("sku", is(sku))
                .body("stock", is(5));
    }

    @Test
    void createReturnsLocationHeaderWithProductId() {
        var location = createProduct(uniqueSku(), "Test product", "10.00", 1);

        assertThat(location, containsString(BASE_PATH + "/"));
        var id = location.substring(location.lastIndexOf('/') + 1);
        assertThat(UUID.fromString(id), notNullValue());
    }

    @Test
    void fetchedProductExposesAllFields() {
        var sku = uniqueSku();
        var location = createProduct(sku, "Keyboard", "199.90", 12);

        given().when().get(location)
                .then().statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("sku", is(sku))
                .body("name", is("Keyboard"))
                .body("price", is(199.90f))
                .body("stock", is(12));
    }

    @Test
    void acceptsZeroInitialStock() {
        var location = createProduct(uniqueSku(), "Out of stock", "5.00", 0);

        given().when().get(location)
                .then().statusCode(200)
                .body("stock", is(0));
    }

    @Test
    void rejectsDuplicateSku() {
        var sku = uniqueSku();
        createProduct(sku, "First", "10.00", 1);

        given().contentType(ContentType.JSON)
                .body(payload(sku, "Second", "20.00", 2))
                .when().post(BASE_PATH)
                .then().statusCode(422)
                .contentType("application/problem+json")
                .body("code", is("SKU_ALREADY_EXISTS"))
                .body("status", is(422));
    }

    @Test
    void rejectsInvalidPayload() {
        given().contentType(ContentType.JSON)
                .body("{\"sku\":\"\",\"name\":\"x\",\"price\":-1,\"initialStock\":0}")
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItems("sku", "price"));
    }

    @Test
    void rejectsBlankName() {
        given().contentType(ContentType.JSON)
                .body(payload(uniqueSku(), " ", "10.00", 1))
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("name"));
    }

    @Test
    void rejectsZeroPrice() {
        given().contentType(ContentType.JSON)
                .body(payload(uniqueSku(), "Free", "0", 1))
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("price"));
    }

    @Test
    void rejectsPriceWithMoreThanTwoDecimals() {
        given().contentType(ContentType.JSON)
                .body(payload(uniqueSku(), "Precise", "10.999", 1))
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("price"));
    }

    @Test
    void rejectsNegativeInitialStock() {
        given().contentType(ContentType.JSON)
                .body(payload(uniqueSku(), "Negative", "10.00", -1))
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("initialStock"));
    }

    @Test
    void rejectsSkuLongerThan40Chars() {
        given().contentType(ContentType.JSON)
                .body(payload("S".repeat(41), "Long sku", "10.00", 1))
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("sku"));
    }

    @Test
    void rejectsMissingPrice() {
        given().contentType(ContentType.JSON)
                .body("{\"sku\":\"" + uniqueSku() + "\",\"name\":\"No price\",\"initialStock\":1}")
                .when().post(BASE_PATH)
                .then().statusCode(400)
                .body("code", is("VALIDATION_ERROR"))
                .body("errors.field", hasItem("price"));
    }

    @Test
    void rejectsMalformedJson() {
        given().contentType(ContentType.JSON)
                .body("{not-json")
                .when().post(BASE_PATH)
                .then().statusCode(400);
    }

    @Test
    void rejectsUnsupportedMediaType() {
        given().contentType(ContentType.TEXT)
                .body("hello")
                .when().post(BASE_PATH)
                .then().statusCode(415);
    }

    // ---------- GET /api/v1/products/{id} ----------

    @Test
    void returns404ForUnknownProduct() {
        given().when().get(BASE_PATH + "/" + UUID.randomUUID())
                .then().statusCode(404)
                .body("code", is("NOT_FOUND"));
    }

    @Test
    void returns404ForMalformedId() {
        given().when().get(BASE_PATH + "/not-a-uuid")
                .then().statusCode(404);
    }

    // ---------- GET /api/v1/products ----------

    @Test
    void listsCreatedProducts() {
        var sku = uniqueSku();
        createProduct(sku, "Listed", "10.00", 3);

        given().queryParam("size", 100)
                .when().get(BASE_PATH)
                .then().statusCode(200)
                .contentType(ContentType.JSON)
                .body("sku", hasItem(sku));
    }

    @Test
    void listRespectsPageSize() {
        createProduct(uniqueSku(), "A", "10.00", 1);
        createProduct(uniqueSku(), "B", "10.00", 1);
        createProduct(uniqueSku(), "C", "10.00", 1);

        given().queryParam("page", 0).queryParam("size", 2)
                .when().get(BASE_PATH)
                .then().statusCode(200)
                .body("size()", is(2));
    }

    @Test
    void listReturnsEmptyForPageBeyondData() {
        given().queryParam("page", 100000).queryParam("size", 10)
                .when().get(BASE_PATH)
                .then().statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void listRejectsSizeAboveMaximum() {
        given().queryParam("size", 101)
                .when().get(BASE_PATH)
                .then().statusCode(400);
    }

    @Test
    void listRejectsNegativePage() {
        given().queryParam("page", -1)
                .when().get(BASE_PATH)
                .then().statusCode(400);
    }
}

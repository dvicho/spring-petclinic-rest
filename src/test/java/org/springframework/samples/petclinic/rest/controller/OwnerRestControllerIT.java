package org.springframework.samples.petclinic.rest.controller;

import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.*;

@Feature("Gestión de Propietarios")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    properties = "petclinic.security.enable=false"
)
class OwnerRestControllerIT {

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(context);
        RestAssuredMockMvc.basePath = "/api";
    }

    // -------------------------------------------------------------------------
    // GET /owners
    // -------------------------------------------------------------------------

    @Story("Listar todos los propietarios")
    @Description("Obtener todos los propietarios del sistema. Debe retornar al menos 10 propietarios de inicio.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void listOwners_returnsAllSeedOwners() {
        given()
            .when()
            .get("/owners")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("$", hasSize(greaterThanOrEqualTo(10)));
    }

    @Story("Listar propietarios filtrados por apellido")
    @Description("Buscar propietarios por apellido. El filtro 'Franklin' debe retornar a George Franklin.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void listOwners_filteredByLastName_returnsMatchingOwner() {
        given()
            .queryParam("lastName", "Franklin")
            .when()
            .get("/owners")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("[0].lastName", equalTo("Franklin"))
            .body("[0].firstName", equalTo("George"));
    }

    @Story("Listar propietarios filtrados por apellido")
    @Description("Buscar un propietario inexistente. Debe retornar 404 cuando no hay coincidencias.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void listOwners_filteredByLastName_notFound() {
        given()
            .queryParam("lastName", "NonExistentOwnerXYZ")
            .when()
            .get("/owners")
            .then()
            .statusCode(404);
    }

    // -------------------------------------------------------------------------
    // GET /owners/{ownerId}
    // -------------------------------------------------------------------------

    @Story("Obtener propietario por ID")
    @Description("Obtener un propietario específico por ID. Verificar que todos los datos del propietario se retornan correctamente.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void getOwner_returnsOwner() {
        given()
            .when()
            .get("/owners/1")
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", equalTo(1))
            .body("firstName", equalTo("George"))
            .body("lastName", equalTo("Franklin"))
            .body("address", equalTo("110 W. Liberty St."))
            .body("city", equalTo("Madison"))
            .body("telephone", equalTo("6085551023"))
            .body("pets", notNullValue());
    }

    @Story("Obtener propietario por ID")
    @Description("Intentar obtener un propietario inexistente. Debe retornar 404.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void getOwner_notFound() {
        given()
            .when()
            .get("/owners/9999")
            .then()
            .statusCode(404);
    }

    // -------------------------------------------------------------------------
    // POST /owners
    // -------------------------------------------------------------------------

    @Story("Crear nuevo propietario")
    @Description("Crear un nuevo propietario con datos válidos. Debe retornar 201 con el propietario creado incluyendo un ID generado.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void addOwner_createsOwnerAndReturns201() {
        String body = """
            {
                "firstName": "John",
                "lastName": "Smith",
                "address": "123 Main St",
                "city": "Springfield",
                "telephone": "5551234567"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/owners")
            .then()
            .statusCode(201)
            .contentType(ContentType.JSON)
            .body("id", notNullValue())
            .body("firstName", equalTo("John"))
            .body("lastName", equalTo("Smith"))
            .body("address", equalTo("123 Main St"))
            .body("city", equalTo("Springfield"))
            .body("telephone", equalTo("5551234567"));
    }

    @Story("Crear nuevo propietario")
    @Description("Intentar crear un propietario con campos requeridos faltantes. Debe retornar 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void addOwner_badRequest_whenMissingRequiredFields() {
        String body = """
            {
                "firstName": "John"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/owners")
            .then()
            .statusCode(400);
    }

    @Story("Crear nuevo propietario")
    @Description("Intentar crear un propietario con formato de teléfono inválido. Debe retornar 400 Bad Request.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void addOwner_badRequest_whenTelephoneContainsLetters() {
        String body = """
            {
                "firstName": "John",
                "lastName": "Smith",
                "address": "123 Main St",
                "city": "Springfield",
                "telephone": "invalid-phone"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/owners")
            .then()
            .statusCode(400);
    }

    // -------------------------------------------------------------------------
    // PUT /owners/{ownerId}
    // -------------------------------------------------------------------------

    @Story("Actualizar propietario existente")
    @Description("Actualizar un propietario existente con nuevos datos válidos. Debe retornar 204 Sin contenido.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void updateOwner_returns204() {
        int ownerId = createOwner("UpdateMe", "Owner");

        String body = """
            {
                "firstName": "Updated",
                "lastName": "Owner",
                "address": "456 New Ave",
                "city": "Newtown",
                "telephone": "5559876543"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .put("/owners/" + ownerId)
            .then()
            .statusCode(204);
    }

    @Story("Actualizar propietario existente")
    @Description("Intentar actualizar un propietario inexistente. Debe retornar 404 No encontrado.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void updateOwner_notFound() {
        String body = """
            {
                "firstName": "John",
                "lastName": "Smith",
                "address": "123 Main St",
                "city": "Springfield",
                "telephone": "5551234567"
            }
            """;

        given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .put("/owners/9999")
            .then()
            .statusCode(404);
    }

    // -------------------------------------------------------------------------
    // DELETE /owners/{ownerId}
    // -------------------------------------------------------------------------

    @Story("Eliminar propietario")
    @Description("Eliminar un propietario existente. Debe retornar 204 y el siguiente GET debe retornar 404.")
    @Severity(SeverityLevel.CRITICAL)
    @Test
    void deleteOwner_returns204AndOwnerIsGone() {
        int ownerId = createOwner("ToDelete", "Owner");

        given()
            .when()
            .delete("/owners/" + ownerId)
            .then()
            .statusCode(204);

        given()
            .when()
            .get("/owners/" + ownerId)
            .then()
            .statusCode(404);
    }

    @Story("Eliminar propietario")
    @Description("Intentar eliminar un propietario inexistente. Debe retornar 404 No encontrado.")
    @Severity(SeverityLevel.NORMAL)
    @Test
    void deleteOwner_notFound() {
        given()
            .when()
            .delete("/owners/9999")
            .then()
            .statusCode(404);
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    private int createOwner(String firstName, String lastName) {
        String body = String.format("""
            {
                "firstName": "%s",
                "lastName": "%s",
                "address": "1 Test St",
                "city": "Testville",
                "telephone": "5550000000"
            }
            """, firstName, lastName);

        return given()
            .contentType(ContentType.JSON)
            .body(body)
            .when()
            .post("/owners")
            .then()
            .statusCode(201)
            .extract()
            .path("id");
    }
}

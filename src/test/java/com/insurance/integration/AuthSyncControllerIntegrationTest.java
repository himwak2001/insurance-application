package com.insurance.integration;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;

// Tell Spring to load the "application-test.yaml" file for configuration.
@ActiveProfiles("test")

// Start the ENTIRE Spring Boot application just like it would in real life, but run it on a random, available port so it doesn't crash if port 8080 is busy.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthSyncControllerIntegrationTest {
    // Go into the application-test.yaml file, find these exact keys,  and copy their values into these variables.
    @Value("${keycloak.test.url}")
    private String keycloakUrl;

    @Value("${keycloak.test.realm}")
    private String realmName;

    @Value("${keycloak.test.client-id}")
    private String clientId;

    @Value("${keycloak.test.client-secret}")
    private String clientSecret;

    @Value("${keycloak.test.username}")
    private String username;

    @Value("${keycloak.test.password}")
    private String password;

    // Since Spring Boot started on a random port, we need to know what that port is.This annotation automatically grabs that random port number.
    @LocalServerPort
    private int port;

    // @BeforeEach means "run this tiny block of code before EVERY single @Test method."
    @BeforeEach
    void setUp() {
        // We tell RestAssured (our robot web browser):
        // "Whenever you make a request, send it to this specific random port."
        RestAssured.port = port;
    }

    // This is a helper method. It acts exactly like a user typing their username and password into a login screen.
    private String getBearerToken() {
        return given()      // "Given these details..."
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .formParam("client_id", clientId)
                .formParam("client_secret", clientSecret)
                .formParam("username", username)
                .formParam("password", password)
                .formParam("grant_type", "password")
                .when()     // "...When I send a POST request to Keycloak..."
                .post(keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token")
                .then()     // "...Then I expect Keycloak to say OK (200)..."
                .statusCode(200) // Ensures Keycloak responded successfully
                .extract()      // "...And I want to extract the 'access_token' from Keycloak's reply."
                .path("access_token");
    }

    @Test
    void givenAuthenticatedUser_whenGetUsers_thenReturns200() {
        String token = getBearerToken();
        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .get("/api/v1/me")
                .then()
                .statusCode(200);
    }

    @Test
    void givenAuthenticatedUser_whenSyncUser_thenReturn200(){
        String token = getBearerToken();
        given()
                .header("Authorization", "Bearer " + token)
                .when().post("api/v1/sync")
                .then().statusCode(200);
    }

    @Test
    void givenUnauthenticatedUser_whenGetUsers_thenReturns401() {
        // Hit the endpoint WITHOUT a token
        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(401);
    }
}

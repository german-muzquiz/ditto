package com.ditto;

import io.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import spark.Spark;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;


public class ReplayTest {

    @BeforeClass
    public static void setAllUp() throws NoSuchAlgorithmException, IOException, KeyManagementException {
        Ditto.main(new String[]{"replay", TestUtils.TEST_PORT, "src/test/resources/test_messages.txt"});
        Spark.awaitInitialization();

        RestAssured.baseURI = TestUtils.TEST_HOST;
        RestAssured.port = Integer.parseInt(TestUtils.TEST_PORT);
    }

    @AfterClass
    public static void tearAllDown() {
        Spark.stop();
    }

    @Test
    public void testGetNoParams() {
        get("/service/user/456")
                .then()
                .statusCode(200)
                .and()
                .body("id", equalTo(456));
    }

    @Test
    public void testGetWithMultiValueParams() {
        get("/service/user/123?param1=value&param1=otherValue&param2=value2")
                .then()
                .statusCode(200)
                .and()
                .body("name", equalTo("Sam"));
    }

    @Test
    public void testHead() {
        head("/otherservice/orders/456")
                .then()
                .statusCode(200);
    }

    @Test
    public void testPost() {
        given()
                .body("{\"operation\":\"GET\"}")
                .when()
                .post("/service/user")
                .then()
                .statusCode(500);
    }

    @Test
    public void testPostDifferentBody() {
        given()
                .body("{\"operation\":\"GAT\"}")
                .when()
                .post("/service/user")
                .then()
                .statusCode(404);
    }

    @Test
    public void testPostWildcardMatch() {
        given()
                .body("{\r\n" +
                        "    \"operation\":\"GET\",\r\n" +
                        "    \"id\": 3\r\n" +
                        "}\r\n")
                .when()
                .post("/service/user/wildcard")
                .then()
                .statusCode(200);

        given()
                .body("{\r\n" +
                        "    \"operation\":\"SET\",\r\n" +
                        "    \"id\": 3\r\n" +
                        "}\r\n")
                .when()
                .post("/service/user/wildcard")
                .then()
                .statusCode(200);
    }

    @Test
    public void testPut() {
        given()
                .body("{\"operation\":\"GET\"}")
                .when()
                .put("/service/user")
                .then()
                .statusCode(200);
    }

    @Test
    public void testPutDifferentBody() {
        given()
                .body("{\"operation\":\"SET\"}")
                .when()
                .put("/service/user")
                .then()
                .statusCode(404);
    }

    @Test
    public void testDelete() {
        delete("/service/user")
                .then()
                .statusCode(401);
    }
}

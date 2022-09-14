import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

public class HelloWorldTest {

    @Test
    public void testHelloWorld() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();
        response.prettyPrint();
    }

    @Test
    public void testGetTextRequest() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_text")
                .andReturn();

        response.body().print();
    }

    @Test
    public void testRestAssuredSetParams() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John Rambo");

        Response response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .andReturn();

        response.prettyPrint();
    }

    @Test
    public void testRestAssuredParseJson() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "John Rambo");

        JsonPath response = RestAssured
                .given()
                .queryParams(params)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();

        String name = response.get("answer2");
        if (name == null) {
            System.out.println("The key 'answer2' is absent");
        } else {
            System.out.println(name);
        }
    }

    @Test
    public void testCheckTypeWithGet() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.prettyPrint();
    }

    @Test
    public void testCheckTypeWithGetAndQueryParam() {
        Response response = RestAssured
                .given()
                .queryParam("param1","value1")
                .queryParam("param2", "value2")
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testCheckTypeWithPostAndSetParamsByBodyAsString() {
        Response response = RestAssured
                .given()
                .body("param1=value1&param2=value2")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testCheckTypeWithPostAndSetParamsByBodyAsJSON() {
        Response response = RestAssured
                .given()
                .body("{\"param1\":\"value1\",\"param2\":\"value2\"}")
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testCheckTypeWithPostAndSetParamsByBodyAsJSONByMap() {
        Map<String, Object> body = new HashMap<>();
        body.put("param1", "value1");
        body.put("param2", "value2");
        Response response = RestAssured
                .given()
                .body(body)
                .post("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        response.print();
    }

    @Test
    public void testGetStatusCode200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/check_type")
                .andReturn();

        Integer statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
    }

    @Test
    public void testGetStatusCode500() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_500")
                .andReturn();

        Integer statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
    }

    @Test
    public void testGetStatusCode404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/get_something")
                .andReturn();

        Integer statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
    }

    @Test
    public void testGetRedirectWithFalseFollow() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        Integer statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
    }

    @Test
    public void testGetRedirectWithTrueFollow() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(true)
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        Integer statusCode = response.getStatusCode();
        System.out.println("Status code is: " + statusCode);
    }

    @Test
    public void testGetShowAllHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("myHeader1", "myValue1");
        headers.put("myHeader2", "myValue2");

        Response response = RestAssured
                .given()
                .headers(headers)
                .when()
                .get("https://playground.learnqa.ru/api/show_all_headers")
                .andReturn();

        response.prettyPrint();

        Headers responseHeaders = response.getHeaders();
        System.out.println(responseHeaders);
    }
}

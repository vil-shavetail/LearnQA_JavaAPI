package tests.hello;

import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

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
                .queryParam("param1", "value1")
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

    @Test
    public void testGetLocationHeader() {
        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/get_303")
                .andReturn();

        String locationHeader = response.getHeader("Location");
        System.out.println("Location header is: " + locationHeader);
    }

    @Test
    public void testGetAuthCookie() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response response = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = response.getCookie("auth_cookie");
        System.out.println(responseCookie);
    }

    @Test
    public void testCheckAuthCookie() {
        Map<String, String> data = new HashMap<>();
        data.put("login", "secret_login");
        data.put("password", "secret_pass");

        Response responseForGet = RestAssured
                .given()
                .body(data)
                .when()
                .post("https://playground.learnqa.ru/api/get_auth_cookie")
                .andReturn();

        String responseCookie = responseForGet.getCookie("auth_cookie");

        Map<String, String> cookies = new HashMap<>();
        if (responseCookie != null) {
            cookies.put("auth_cookie", responseCookie);
        }


        Response responseForCheck = RestAssured
                .given()
                .body(data)
                .cookies(cookies)
                .when()
                .post("https://playground.learnqa.ru/api/check_auth_cookie")
                .andReturn();

        responseForCheck.print();
    }

    @Test
    public void testParseSecondMessageTextAndTimestamp() {
        JsonPath response = RestAssured
                .get("https://playground.learnqa.ru/api/get_json_homework")
                .jsonPath();

        List<Map<String, String>> responseList = response.getJsonObject("messages");
        Map<String, String> secondStringMap = responseList.get(1);
        System.out.println("The text of the second message is: " + "\"" + secondStringMap.get("message") + "\"");
        System.out.println("The timestamp of the second message is: " + "\"" + secondStringMap.get("timestamp") + "\"");
    }

    @Test
    public void testGetLastRedirectUrl() {
        Response firstResponse = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get("https://playground.learnqa.ru/api/long_redirect")
                .andReturn();

        String firstRedirectUrl = firstResponse.getHeader("Location");
        System.out.println("The first redirect URL is: " + firstRedirectUrl);

        Response secondResponse = RestAssured
                .given()
                .redirects()
                .follow(false)
                .when()
                .get(firstRedirectUrl)
                .andReturn();

        String lastRedirectUrl = secondResponse.getHeader("Location");
        System.out.println("The last redirect URL is: " + lastRedirectUrl);
    }

    @Test
    public void testGetQuantityOfTheRedirects() {
        Integer statusCode = 301;
        String redirectUrl = "https://playground.learnqa.ru/api/long_redirect";
        Integer quantityOfTheRedirects = 0;
        while (statusCode != 200) {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .when()
                    .get(redirectUrl)
                    .andReturn();

            redirectUrl = response.getHeader("Location");
            statusCode = response.getStatusCode();
            quantityOfTheRedirects++;
            System.out.println("Status code is: " + statusCode);
            if (statusCode != 200) {
                System.out.println("The redirect URL is: " + redirectUrl);
            }

        }
        System.out.println("Quantity of the Redirects are: " + quantityOfTheRedirects);
    }

    @Test
    public void testGetToken() throws InterruptedException {
        String requestUrl = "https://playground.learnqa.ru/ajax/api/longtime_job";

        JsonPath firstResponse = RestAssured
                .get(requestUrl)
                .jsonPath();

        String token = firstResponse.getString("token");
        Integer seconds = firstResponse.get("seconds");
        System.out.println("Token is: " + token);
        System.out.println("Sleep to: " + seconds + " seconds");

        Map<String, String> params = new HashMap<>();
        params.put("token", token);

        JsonPath secondResponse = RestAssured
                .given()
                .queryParams(params)
                .get(requestUrl)
                .jsonPath();

        String status = secondResponse.get("status");
        System.out.println("The status is: " + status);

        if (status.contains("Job is NOT ready")) {
            Thread.sleep(seconds * 1000);
            JsonPath thirdResponse = RestAssured
                    .given()
                    .queryParams(params)
                    .get(requestUrl)
                    .jsonPath();

            status = thirdResponse.get("status");
            System.out.println("The status is: " + status);
            String result = thirdResponse.get("result");
            System.out.println("The result is: " + result);
        }
    }

    @Test
    public void testGetSecretPassword() {
        String login = "super_admin";
        String[] splashData = {"password", "123456", "123456789", "12345678", "12345", "qwerty",
                "abc123", "football", "1234567", "monkey", "111111", "letmein", "1234", "1234567890",
                "dragon", "baseball", "sunshine", "iloveyou", "trustno1", "princess", "adobe123", "123123",
                "welcome", "login", "admin", "qwerty123", "solo", "1q2w3e4r", "master", "666666", "photoshop",
                "1qaz2wsx", "qwertyuiop", "ashley", "mustang", "121212", "starwars", "654321", "bailey",
                "access", "flower", "555555", "passw0rd", "shadow", "lovely", "7777777", "michael", "!@#$%^&*",
                "jesus", "password1", "superman", "hello", "charlie", "888888", "696969", "hottie", "freedom",
                "aa123456", "qazwsx", "ninja", "azerty", "loveme", "whatever", "donald", "batman", "zaq1zaq1",
                "Football", "000000", "123qwe"};

        for (String splashDatum : splashData) {
            Map<String, String> params = new HashMap<>();
            params.put("login", login);
            params.put("password", splashDatum);

            Response getAuthCookieResponse = RestAssured
                    .given()
                    .body(params)
                    .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                    .andReturn();

            String auth_cookie = getAuthCookieResponse.getCookie("auth_cookie");

            Map<String, String> cookies = new HashMap<>();
            cookies.put("auth_cookie", auth_cookie);

            Response checkCookieResponse = RestAssured
                    .given()
                    .cookies(cookies)
                    .get("https://playground.learnqa.ru/ajax/api/check_auth_cookie")
                    .andReturn();

            String responseMessage = checkCookieResponse.asString();
            if (!responseMessage.contains("You are NOT authorized")) {
                System.out.println("The valid password for the \"super_admin\" user is: " + splashDatum);
                System.out.println("The response message is: " + responseMessage);
                break;
            }
        }
    }

    @Test
    public void testForStatusCode200() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map")
                .andReturn();
        assertEquals(200, response.statusCode(), "Unexpected status code");
    }

    @Test
    public void testForStatusCode404() {
        Response response = RestAssured
                .get("https://playground.learnqa.ru/api/map2")
                .andReturn();
        assertEquals(404, response.statusCode(), "Unexpected status code");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "John", "Pete"})
    public void testHelloWorldWithoutName(String name) {
        Map<String, String> queryParams = new HashMap<>();

        if(name.length() >0) {
            queryParams.put("name", name);
        }

        JsonPath response = RestAssured
                .given()
                .queryParams(queryParams)
                .get("https://playground.learnqa.ru/api/hello")
                .jsonPath();
        String answer = response.getString("answer");
        String expectedName = (name.length() > 0) ? name : "someone";
        assertEquals("Hello, " + expectedName, answer, "The answer is not expected");

    }

    @Test
    public void testAssertCookie() {
        Response responseGetCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        Map<String, String> cookie = responseGetCookie.getCookies();
        assertTrue(cookie.containsKey("HomeWork"), "Response doesn't contain cookie with name \"HomeWork\"");
        assertTrue(cookie.containsValue("hw_value"), "Response doesn't contain HomeWork cookie with value \"hm_value\"");
    }

    @ParameterizedTest
    @ValueSource(strings = {"This message has got length greater than 15", "Small phrase"})
    public void testAssertPhraseLength(String phrase) {
        assertTrue(phrase.length() > 15, "Expected phrase has got length less than 15 symbols");
    }

    @Test
    public void testAssertHeaders() {
        Map<String, String> expHeaders = new HashMap<>();
        expHeaders.put("Date", "Thu, 22 Sep 2022 11:01:18 GMT");
        expHeaders.put("Content-Type", "application/json");
        expHeaders.put("Content-Length", "15");
        expHeaders.put("Connection", "keep-alive");
        expHeaders.put("Keep-Alive", "timeout=10");
        expHeaders.put("Server", "Apache");
        expHeaders.put("x-secret-homework-header", "Some secret value");
        expHeaders.put("Cache-Control", "max-age=0");
        expHeaders.put("Expires", "Thu, 22 Sep 2022 11:01:18 GMT");

        Response getHeaderResponse = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        Headers headers = getHeaderResponse.getHeaders();
        headers.asList();
        for (int i = 0; i < headers.size(); i++) {
            assertTrue(expHeaders.containsKey(headers.asList().get(i).getName()),
                    "Doesn't contain expected header with name " + headers.asList().get(i).getName());
            if (!headers.asList().get(i).getName().equals("Date") & !headers.asList().get(i).getName().equals("Expires")) {
                assertTrue(expHeaders.containsValue(headers.asList().get(i).getValue()),
                        "Doesn't contain expected value " + headers.asList().get(i).getValue() + " for header " + headers.asList().get(i).getName());
            } else {
                assertTrue(headers.asList().get(i).getValue().length() > 0);
            }
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30 | platform: Mobile, browser: No, device: Android",
            "Mozilla/5.0 (iPad; CPU OS 13_2 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/91.0.4472.77 Mobile/15E148 Safari/604.1 | platform: Mobile, browser: Chrome, device: iOS",
            "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html) | platform: Googlebot, browser: Unknown, device: Unknown",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.77 Safari/537.36 Edg/91.0.100.0 | platform: Web, browser: Chrome, device: No",
            "Mozilla/5.0 (iPad; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 | platform: Mobile, browser: No, device: iPhone"
    }, delimiter = '|')
    public void testAssertUserAgent(String agent, String expectedValues) {
        Response response = RestAssured
                .given()
                .header("User-Agent", agent)
                .post("https://playground.learnqa.ru/ajax/api/user_agent_check")
                .andReturn();

        String actualPlatform = response.jsonPath().getString("platform");
        String actualBrowser = response.jsonPath().getString("browser");
        String actualDevice = response.jsonPath().getString("device");

        String actualValues = "platform: " + actualPlatform + ", browser: " + actualBrowser + ", device: " + actualDevice;
        String[] keyValuesPair = expectedValues.split(",");
        Map<String, String> expected = new HashMap<>();
        for (String initKey : keyValuesPair) {
            String[] strings = initKey.split(":");
            expected.put(strings[0].trim(), strings[1].trim());
        }

        if (!expectedValues.equals(actualValues)) {
            if(!actualPlatform.equals(expected.get("platform"))) {
                System.out.println("Expected value of the field platform for user-agent: {" + agent
                        + "} is " + expected.get("platform") + " but actual value is " + actualPlatform);
            } else if (!actualBrowser.equals(expected.get("browser"))) {
                System.out.println("Expected value of the field browser for user-agent: {" + agent
                        + "} is " + expected.get("browser") + " but actual value is " + actualBrowser);
            } else if (!actualDevice.equals(expected.get("device"))) {
                System.out.println("Expected value of the field device for user-agent: {" + agent
                        + "} is " + expected.get("device") + " but actual value is " + actualDevice);
            }
        } else {
            assertEquals(expectedValues, actualValues);
        }

    }
}

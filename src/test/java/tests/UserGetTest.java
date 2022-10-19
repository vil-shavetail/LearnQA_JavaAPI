package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Get user details cases")
@Feature("Get user details tests")
public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Description("This test checks that response return only username for request with only user_id field and not authorized user")
    @DisplayName("Test negative")
    @Test
    public void testGetUserDataNotAuth() {
        int userId = 2;

        Response responseUserData = apiCoreRequests
                .makeGetUserDetailsRequestOnlyWithUserId("https://playground.learnqa.ru/api/user/", userId);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("This test successfully return user details for authorized user")
    @DisplayName("Test positive")
    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        int userId = this.getIntFromJson(responseGetAuth, "user_id");

        Response responseUserData = apiCoreRequests
                .makeGetUserDetailsRequest("https://playground.learnqa.ru/api/user/", header, cookie, userId);

        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);
    }

    @Description("This test checks that response return only username for request with authorized user and with user_id from not authorized user")
    @DisplayName("Test negative")
    @Test
    public void testGetUserDetailsAuthorizedUserWithNotTheSameUserId() {
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");
        int userId = 13456;

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseUserData = apiCoreRequests
                .makeGetUserDetailsRequest("https://playground.learnqa.ru/api/user/", header, cookie, userId);

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }
}

package tests.hello;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


@Epic("Delete user cases")
@Feature("Make to delete user")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test check's that early created user successfully delete user")
    @DisplayName("Test positive, successfully delete early created user")
    @Test
    public void testDeleteEarlyCreatedUserTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);


        String userIdOnCreate = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Assertions.assertJsonByName(responseGetAuth, "user_id", userIdOnCreate);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
            .makeDeleteUserRequest(
                    "https://playground.learnqa.ru/api/user/",
                    this.getHeader(responseGetAuth, "x-csrf-token"),
                    this.getCookie(responseGetAuth, "auth_sid"),
                    Integer.parseInt(userIdOnCreate)
            );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //CHECKS THAT USER DELETE
        //TRY TO GET USER DETAILS AFTER DELETE
        Response responseGetUserDetails = apiCoreRequests
                .makeGetUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        Integer.parseInt(userIdOnCreate));

        Assertions.assertResponseCodeEquals(responseGetUserDetails, 404);
        Assertions.assertResponseTextEquals(responseGetUserDetails, "User not found");
    }

    @Description("This test check's that system user with ID=2 can't be delete")
    @DisplayName("Test positive, can't be delete system user with ID=2")
    @Test
    public void testTryToDeleteSystemUser() {
        //LOGIN
        int expectedUserId = 2;
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        Assertions.assertJsonByName(responseGetAuth, "user_id", expectedUserId);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteUserRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        expectedUserId
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 400);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

        //CHECKS THAT SYSTEM USER WITH ID CAN'T BE DELETE.
        Response responseGetUserDetails = apiCoreRequests
                .makeGetUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        expectedUserId);

        Assertions.assertResponseCodeEquals(responseGetUserDetails, 200);
        Assertions.assertJsonHasField(responseGetUserDetails, "id");
        Assertions.assertJsonHasField(responseGetUserDetails, "email");
    }

    @Description("This test check's this test check's successfully delete not the same user as authorized")
    @DisplayName("Test negative, try to delete not the same user as authorized")
    @Test
    public void testTryToDeleteNotTheSameUserAsAuthorized() {
        //GENERATE FIRST USER
        Map<String, String> firstUserData = DataGenerator.getRegistrationData();

        Response responseCreateAuthFirstUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", firstUserData);


        String firstUserIdOnCreate = responseCreateAuthFirstUser.jsonPath().getString("id");

        //GENERATE SECOND USER
        Map<String, String> secondUserData = DataGenerator.getRegistrationData();

        Response responseCreateAuthSecondUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", secondUserData);


        String secondUserIdOnCreate = responseCreateAuthSecondUser.jsonPath().getString("id");

        //FIRST USER LOGIN
        Map<String, String> firstUserAuthData = new HashMap<>();
        firstUserAuthData.put("email", firstUserData.get("email"));
        firstUserAuthData.put("password", firstUserData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", firstUserAuthData);

        Assertions.assertJsonByName(responseGetAuth, "user_id", firstUserIdOnCreate);

        //DELETE
        Response responseDeleteUser = apiCoreRequests
                .makeDeleteUserRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        Integer.parseInt(secondUserIdOnCreate)
                );

        Assertions.assertResponseCodeEquals(responseDeleteUser, 200);

        //CHECKS THAT USER DELETE
        //TRY TO GET SECOND USER DETAILS AFTER DELETE
        Response responseGetSecondUserDetails = apiCoreRequests
                .makeGetUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        Integer.parseInt(secondUserIdOnCreate));


        Assertions.assertResponseCodeEquals(responseGetSecondUserDetails, 200);
        Assertions.assertJsonHasField(responseGetSecondUserDetails, "username");

        Response responseGetFirstUserDetails = apiCoreRequests
                .makeGetUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        Integer.parseInt(firstUserIdOnCreate));

        Assertions.assertResponseCodeEquals(responseGetFirstUserDetails, 404);
        Assertions.assertResponseTextEquals(responseGetFirstUserDetails, "User not found");

        throw new IllegalArgumentException("We have got unexpected result. In the end of the test" +
                " must be delete the second user with id: " + secondUserIdOnCreate + ". " +
                "But was delete the first user with id: " + firstUserIdOnCreate);
    }
}

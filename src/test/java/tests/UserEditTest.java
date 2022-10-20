package tests;

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

@Epic("Edit User details cases")
@Feature("Edit user details")
public class UserEditTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test successfully update user detail fields")
    @DisplayName("Test positive")
    @Test
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/", userData);


        String userId = responseCreateAuth.jsonPath().getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makeAnEditUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData,
                        Integer.parseInt(userId)
                );

        //GET
        Response responseUserData = apiCoreRequests
                .makeGetUserDetailsRequest(
                    "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        Integer.parseInt(userId)
                );

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Description("This test checks that user detail fields can't be update by not authorized user ")
    @DisplayName("Test negative, can't update user details by not authorized user")
    @Test
    public void testEditUserDetailsByNotAuthorizedUser() {
        int userId = 46754;

        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.
                makeAnEditUserDetailsRequestByNotAuthorizedUser(
                        "https://playground.learnqa.ru/api/user/",
                        editData,
                        userId
                );

        Assertions.assertResponseTextEquals(responseEditUser, "Auth token not supplied");
    }

    @Description("This test checks, that can't update user detail fields for another user by authorized user")
    @DisplayName("Test negative, can't update user detail fields for another user")
    @Test
    public void testEditUserDetailsByNotTheSameAuthorizedUser() {
        int expectedFirstUserId = 46793;
        int expectedSecondUserId = 46754;
        String firstUserEmail = "learnqa20221020183521@example.com";
        String firstUserPassword = "123";
        String lastNameNewValue = "aqnrael";
        String secondUserEmail = "learnqa20221020170727@example.com";
        String secondUserPassword = "123";

        //LOGIN BY FIRST USER
        Map<String, String> firstUserAuthData = new HashMap<>();
        firstUserAuthData.put("email", firstUserEmail);
        firstUserAuthData.put("password", firstUserPassword);

        Response responseGetAuthFirstUser = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", firstUserAuthData);

        int firstUserId = Integer.parseInt(responseGetAuthFirstUser.jsonPath().getString("user_id"));
        Assertions.assertJsonByName(responseGetAuthFirstUser, "user_id", expectedFirstUserId);

        //EDIT FIELD LASTNAME FOR SECOND USER
        Map<String, String> editData = new HashMap<>();
        editData.put("lastName", lastNameNewValue);

        Response responseEditUser = apiCoreRequests
                .makeAnEditUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuthFirstUser, "x-csrf-token"),
                        this.getCookie(responseGetAuthFirstUser, "auth_sid"),
                        editData,
                        expectedSecondUserId
                );

        if(responseEditUser.statusCode() == 200) {
            //LOGIN BY SECOND USER
            Map<String, String> secondUserAuthData = new HashMap<>();
            secondUserAuthData.put("email", secondUserEmail);
            secondUserAuthData.put("password", secondUserPassword);

            Response responseGetAuthSecondUser = apiCoreRequests
                    .makePostRequest("https://playground.learnqa.ru/api/user/login", secondUserAuthData);

            int secondUserId = Integer.parseInt(responseGetAuthSecondUser.jsonPath().getString("user_id"));
            Assertions.assertJsonByName(responseGetAuthSecondUser, "user_id", expectedSecondUserId);

            //CHECKS THAT SECOND USER'S FIELD lastName WAS NOT MODIFIED
            Response responseGetSecondUserDetails = apiCoreRequests
                    .makeGetUserDetailsRequest(
                            "https://playground.learnqa.ru/api/user/",
                            this.getHeader(responseGetAuthSecondUser, "x-csrf-token"),
                            this.getCookie(responseGetAuthSecondUser, "auth_sid"),
                            secondUserId
                    );

            Assertions.assertJsonByName(responseGetSecondUserDetails, "lastName", "learnqa");

            //CHECKS THAT FIRST USER'S FIELD lastName WAS NOT MODIFIED
            Response responseGetFirstUserDetails = apiCoreRequests
                    .makeGetUserDetailsRequest(
                            "https://playground.learnqa.ru/api/user/",
                            this.getHeader(responseGetAuthFirstUser, "x-csrf-token"),
                            this.getCookie(responseGetAuthFirstUser, "auth_sid"),
                            firstUserId
                    );

            Assertions.assertJsonByName(responseGetFirstUserDetails, "lastName", "learnqa");

        } else {
            Assertions.assertResponseCodeEquals(responseEditUser, 400);
        }
    }

    @Description("This test checks, that can't be update user detail fields with too short value")
    @DisplayName("Test negative, can't update user detail fields with too short value")
    @Test
    public void testEditUserEmailFieldWithIncorrectEmailValue() {
        String emailNewValue = "learnqa20221020170727example.com";
        String email = "learnqa20221020170727@example.com";
        String password = "123";
        int expectedUserId = 46754;

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        int userId = Integer.parseInt(responseGetAuth.jsonPath().getString("user_id"));
        Assertions.assertJsonByName(responseGetAuth, "user_id", expectedUserId);

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("email", emailNewValue);

        Response responseEditUser = apiCoreRequests
                .makeAnEditUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData,
                        userId
                );

        Assertions.assertResponseTextEquals(responseEditUser, "Invalid email format");
    }

    @Description("This test checks, that request return error about invalid email format ")
    @DisplayName("Test negative, can't update user email with incorrect email value")
    @Test
    public void testEditUserFirstNameFieldWithTooSmallValue() {
        String firstNameNewValue = "Q";
        String email = "learnqa20221020173228@example.com";
        String password = "123";
        int expectedUserId = 46755;

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", email);
        authData.put("password", password);

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        int userId = Integer.parseInt(responseGetAuth.jsonPath().getString("user_id"));
        Assertions.assertJsonByName(responseGetAuth, "user_id", expectedUserId);

        //EDIT
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", firstNameNewValue);

        Response responseEditUser = apiCoreRequests
                .makeAnEditUserDetailsRequest(
                        "https://playground.learnqa.ru/api/user/",
                        this.getHeader(responseGetAuth, "x-csrf-token"),
                        this.getCookie(responseGetAuth, "auth_sid"),
                        editData,
                        userId
                );

        Assertions.assertJsonByName(
                responseEditUser,
                "error",
                "Too short value for field firstName"
        );
    }
}

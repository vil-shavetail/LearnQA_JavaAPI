package tests.user;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Create user cases")
@Feature("Create user")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Description("This test checks that can't create user with already used email ")
    @DisplayName("Test negative create user with already used email")
    @Owner("Ethan Demidovich")
    @Test
    public void testCreateUserWithExistingEmail() {
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);


        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Users with email '" + email + "' already exists");
    }

    @Description("This test successfully create user")
    @DisplayName("Test positive create user")
    @Owner("Ethan Demidovich")
    @Test
    public void testCreateUserSuccessfully() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
    }

    @Description("This test checks that can't create user with incorrect email ")
    @DisplayName("Test negative create user with incorrect email")
    @Owner("Ethan Demidovich")
    @Test
    public void testCreateUserWithIncorrectEmail() {
        String incorrectEmail = "vinkotovexample.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", incorrectEmail);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "Invalid email format");
    }

    @Description("This test checks that can't create user with too short username field")
    @DisplayName("Test negative create user with too short username field")
    @Owner("Ethan Demidovich")
    @Test
    public void testCreateUserWithTooShortUserNameField() {
        String username = "l";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too short");
    }

    @Description("This test checks that can't create user with too long username field ")
    @DisplayName("Test negative create user with too long username field")
    @Owner("Ethan Demidovich")
    @Test
    public void testCreateUserWithTooLongUserNameField() {
        String username = "lafafafafafffafafafafaafafafafafa" +
                "fafafafafafafafafafaaafafafafafaafafafaafaf" +
                "aafafafafafafafafafafafafafafafafafafafafac" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "aafafafafaaffafafafafafafafafafafafafafafaf" +
                "afafafafafafafafafafafafafafafafafafafafafa" +
                "ffafafafafafafafafafafafafafafafafafafafafa" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "afafafafafafafafafafafafafafafafafafafafafa" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "afafafafafafafafafafafafafafafafafafafafafa" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "afafafafafafafafafafafafafafafafafafafafafa" +
                "fafafafafafafafafafafafafafafafafafafafafaf" +
                "afafafafafafafafafafafafafafafafafafafafafa";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The value of 'username' field is too long");
    }

    @Description("This test checks that can't create user with missed fields ")
    @DisplayName("Test negative create user with missed field")
    @Owner("Ethan Demidovich")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "username", "firstName", "lastName"})
    public void testCreateUserWithMissedField(String field) {
        Map<String, String> userData = new HashMap<>();
        userData.put(field, null);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user", userData);

        Assertions.assertResponseCodeEquals(responseCreateAuth, 400);
        Assertions.assertResponseTextEquals(responseCreateAuth, "The following required params are missed: " + field);
    }

}

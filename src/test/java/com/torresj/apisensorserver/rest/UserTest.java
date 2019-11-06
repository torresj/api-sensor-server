package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.data.domain.Page;


public class UserTest extends BasicRestTest {

  //User Controller
  private final String USERS = "v1/users";

  @AfterClass
  public static void ChangeSetUp() {
    SetUpFalse();
  }

  @Test
  public void testAdminLogin() throws IOException {
    getAdminAuthorization();
  }

  @Test
  public void testUserLogin() throws IOException {
    getUserAuthorization();
  }

  @Test
  public void createNewUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(BASE_URL + port + PATH + USERS);

    String json = "{\"username\":\"test\",\"password\":\"test\",\"role\":\"USER\"}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpPost);

    Optional<User> user = userRepository.findByUsername("test");

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(user.isPresent(), equalTo(true));
    userRepository.delete(user.get());
    client.close();
  }

  @Test
  public void createNewUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(BASE_URL + port + PATH + USERS);

    String json = "{\"username\":\"test\",\"password\":\"test\",\"role\":\"USER\"}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpPost);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
    client.close();
  }

  @Test
  public void getAllUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<User> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<User>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(3));

    client.close();
  }

  @Test
  public void getAllUserAsAdminWithFilters() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
            BASE_URL + port + PATH + USERS + "?filter=" + "2" + "&role=" + Role.USER + "&page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<User> page = objectMapper
            .readValue(jsonFromResponse, new TypeReference<RestPage<User>>() {
            });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(1));

    client.close();
  }

  @Test
  public void getAllUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getUserByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    User userResponse = objectMapper
        .readValue(jsonFromResponse, User.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userResponse, equalTo(user));

    client.close();
  }

  @Test
  public void getUserByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("Admin").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getUserByIdAsUserLoggued() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    User userResponse = objectMapper
        .readValue(jsonFromResponse, User.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userResponse.getId(), equalTo(user.getId()));
    assertThat(userResponse.getUsername(), equalTo(user.getUsername()));
    assertThat(userResponse.getPassword(), equalTo(user.getPassword()));

    client.close();
  }

  @Test
  public void getUserLoggued() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/logged");

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    User userResponse = objectMapper
        .readValue(jsonFromResponse, User.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userResponse.getId(), equalTo(user.getId()));
    assertThat(userResponse.getUsername(), equalTo(user.getUsername()));
    assertThat(userResponse.getPassword(), equalTo(user.getPassword()));

    client.close();
  }

  @Test
  public void getAdminLoggued() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    User user = userRepository.findByUsername("Admin").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/logged");

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());
    User userResponse = objectMapper
        .readValue(jsonFromResponse, User.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userResponse.getId(), equalTo(user.getId()));
    assertThat(userResponse.getUsername(), equalTo(user.getUsername()));
    assertThat(userResponse.getPassword(), equalTo(user.getPassword()));

    client.close();
  }

  @Test
  public void getHousesByUserIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses");

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    List<House> houses = objectMapper
        .readValue(jsonFromResponse, new TypeReference<List<House>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(houses.size(), equalTo(1));

    client.close();
  }

  @Test
  public void getHousesByUserIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses");

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    List<House> houses = objectMapper
        .readValue(jsonFromResponse, new TypeReference<List<House>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(houses.size(), equalTo(2));

    client.close();
  }

  @Test
  public void getHousesByUserIdToOtherUserId() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("Admin").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses");

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void updateUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + USERS);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationAdmin);

    String json = "{\"username\":\"User\",\"password\":\"test\",\"role\":\"ADMIN\"}";
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    User user = objectMapper
        .readValue(jsonFromResponse, User.class);

    User userDB = userRepository.findByUsername("User").get();

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userDB.getRole(), equalTo(user.getRole()));

    userDB.setRole(Role.USER);
    userRepository.save(userDB);

    client.close();
  }

  @Test
  public void updateUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + USERS);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    String json = "{\"username\":\"User\",\"password\":\"test\",\"role\":\"ADMIN\"}";
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    User user = objectMapper
        .readValue(jsonFromResponse, User.class);

    User userDB = userRepository.findByUsername("User").get();

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(userDB.getRole(), equalTo(user.getRole()));

    userDB.setRole(Role.USER);
    userRepository.save(userDB);

    client.close();
  }

  @Test
  public void updateUserAsUserToOtherUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + USERS);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    String json = "{\"username\":\"Admin\",\"password\":\"test\",\"role\":\"USER\"}";
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void addHouseToUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = houseRepository.findByName("House1").get();
    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses/" + house.getId());

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseHouse, equalTo(house));
    assertThat(userHouseRelationRepository.findByUserId(user.getId()).size(), equalTo(2));

    client.close();
  }

  @Test
  public void addHouseToUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House1").get();
    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses/" + house.getId());

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpPut);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
    client.close();
  }

  @Test
  public void removeHouseToUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = houseRepository.findByName("House2").get();
    User user = userRepository.findByUsername("User2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses/" + house.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseHouse, equalTo(house));
    assertThat(userHouseRelationRepository.findByUserId(user.getId()).size(), equalTo(1));

    client.close();
  }

  @Test
  public void removeHouseToUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House2").get();
    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + USERS + "/" + user.getId() + "/houses/" + house.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpDelete);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
    client.close();
  }

  @Test
  public void removeUserAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    User user = userRepository.findByUsername("User2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + USERS + "/" + user.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    User responseUser = objectMapper
        .readValue(jsonFromResponse, User.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseUser, equalTo(user));
    assertThat(userRepository.findByUsername("User2").isPresent(), equalTo(false));
    assertThat(userHouseRelationRepository.findByUserId(user.getId()).isEmpty(), equalTo(true));

    client.close();
  }

  @Test
  public void removeUserAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    User user = userRepository.findByUsername("User").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + USERS + "/" + user.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpDelete);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }
}
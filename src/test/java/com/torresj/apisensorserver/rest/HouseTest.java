package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;
import java.io.IOException;
import java.time.LocalDateTime;
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

public class HouseTest extends BasicRestTest {

  //Record Controller
  private final String HOUSES = "v1/houses";

  @AfterClass
  public static void ChangeSetUp() {
    SetUpFalse();
  }

  @Test
  public void getAllHousesAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<House> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<House>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(3
    ));

    client.close();
  }

  @Test
  public void getAllHousesAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getHouseByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = houseRepository.findByName("House1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseHouse, equalTo(house));

    client.close();
  }

  @Test
  public void getHouseByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseHouse, equalTo(house));

    client.close();
  }

  @Test
  public void getHouseByIdAsUserToNotAllowedHouse() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getSensorsByHouseIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = houseRepository.findByName("House2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId() + "/sensors?page=" + nPage
            + "&elements="
            + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Sensor> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Sensor>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(2));

    client.close();
  }

  @Test
  public void getSensorsByHouseIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId() + "/sensors?page=" + nPage
            + "&elements="
            + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Sensor> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Sensor>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(2));

    client.close();
  }

  @Test
  public void getSensorsByHouseIdAsUserToNotAllowedHouse() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = houseRepository.findByName("House1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId() + "/sensors?page=" + nPage
            + "&elements="
            + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void updateHouseAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + HOUSES);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationAdmin);

    House house = houseRepository.findByName("House2").get();

    StringEntity entity = new StringEntity(objectMapper.writeValueAsString(house));

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(responseHouse, equalTo(house));

    client.close();
  }

  @Test
  public void updateHouseAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + HOUSES);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    House house = houseRepository.findByName("House2").get();

    StringEntity entity = new StringEntity(objectMapper.writeValueAsString(house));

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void removeHouseAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = houseRepository.findByName("House3").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + HOUSES + "/" + house.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House responseHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseHouse, equalTo(house));
    assertThat(houseRepository.findByName("House3").isPresent(), equalTo(false));
    assertThat(userHouseRelationRepository.findByHouseId(house.getId()).isEmpty(), equalTo(true));

    houseRepository.save(house);
    client.close();
  }

  @Test
  public void registerHouseByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    House house = new House(null, "House4", LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + HOUSES);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationAdmin);

    String json = objectMapper.writeValueAsString(house);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    House respondeHouse = objectMapper
        .readValue(jsonFromResponse, House.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(respondeHouse, equalTo(houseRepository.findByName("House4").get()));

    client.close();
  }

  @Test
  public void registerHouseByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    House house = new House(null, "House4", LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + HOUSES);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationUser);

    String json = objectMapper.writeValueAsString(house);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }
}
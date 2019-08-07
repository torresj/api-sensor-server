package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.entities.SensorType;
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

public class SensorTypeTest extends BasicRestTest {

  //SensorType Controller
  private final String TYPE = "v1/sensortypes";

  @AfterClass
  public static void ChangeSetUp() {
    SetUpFalse();
  }

  @Test
  public void getAllSensorTypesAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + TYPE + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<SensorType> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<SensorType>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(2));

    client.close();
  }

  @Test
  public void getAllSensorTypesAsUser() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + TYPE + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<SensorType> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<SensorType>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(3));

    client.close();
  }

  @Test
  public void getSensorTypeByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + TYPE + "/" + type.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    SensorType responseType = objectMapper
        .readValue(jsonFromResponse, SensorType.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseType, equalTo(type));

    client.close();
  }

  @Test
  public void getSensorTypeByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + TYPE + "/" + type.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    SensorType responseType = objectMapper
        .readValue(jsonFromResponse, SensorType.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseType, equalTo(type));

    client.close();
  }

  @Test
  public void createSensorTypeAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    SensorType type = new SensorType(null, "type3", "Type for testing",
        "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + TYPE);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationAdmin);

    String json = objectMapper.writeValueAsString(type);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    SensorType responseType = objectMapper
        .readValue(jsonFromResponse, SensorType.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(responseType, notNullValue());
    assertThat(sensorTypeRepository.findByName("type3"), notNullValue());

    client.close();
  }

  @Test
  public void createSensorTypeAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    SensorType type = new SensorType(null, "type3", "Type for testing",
        "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + TYPE);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationUser);

    String json = objectMapper.writeValueAsString(type);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void updateSensorTypeByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type1").get();
    type.setDescription("Description modified");

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + TYPE);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationAdmin);

    String json = objectMapper.writeValueAsString(type);
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    SensorType responseType = objectMapper
        .readValue(jsonFromResponse, SensorType.class);
    SensorType typeModified = sensorTypeRepository.findByName("type1").get();

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseType, equalTo(typeModified));
    assertThat(responseType.getDescription(), equalTo("Description modified"));

    client.close();
  }

  @Test
  public void updateSensorTypeByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + TYPE);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    String json = objectMapper.writeValueAsString(type);
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void deleteSensorTypeByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type3").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + TYPE + "/" + type.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    SensorType responseType = objectMapper
        .readValue(jsonFromResponse, SensorType.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseType, equalTo(type));
    assertThat(sensorTypeRepository.findById(type.getId()).isPresent(), equalTo(false));

    client.close();
  }

  @Test
  public void deleteSensorTypeByIdAsAdminWithSensorRelation() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + TYPE + "/" + type.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(404));

    client.close();
  }

  @Test
  public void deleteSensorTypeByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    SensorType type = sensorTypeRepository.findByName("type2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + TYPE + "/" + type.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpDelete);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
    client.close();
  }
}



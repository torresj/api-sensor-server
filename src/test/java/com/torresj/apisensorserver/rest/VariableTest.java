package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.Variable;
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


public class VariableTest extends BasicRestTest {

  //Variable Controller
  private final String VARIABLES = "v1/variables";

  @AfterClass
  public static void ChangeSetUp() {
    SetUpFalse();
  }

  @Test
  public void getAllVariablesAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Variable> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(2));

    client.close();
  }

  @Test
  public void getVariablesAsAdminByName() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "?page=" + nPage + "&elements=" + elements + "&name="
            + variable.getName());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Variable> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(1));
    assertThat(page.getContent().get(0), equalTo(variable));

    client.close();
  }

  @Test
  public void getAllVariablesAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "?page=" + nPage + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getVariableByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable responseVariable = objectMapper
        .readValue(jsonFromResponse, Variable.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseVariable, equalTo(variable));

    client.close();
  }

  @Test
  public void getVariableByIdAsUserNotHasVariable() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getVariableByIdAsUserHasVariable() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable3").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId());

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable responseVariable = objectMapper
        .readValue(jsonFromResponse, Variable.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseVariable, equalTo(variable));

    client.close();
  }

  @Test
  public void updateVariableByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable3").get();
    variable.setDescription("Description modified");

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + VARIABLES);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationAdmin);

    String json = objectMapper.writeValueAsString(variable);
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable responseVariable = objectMapper
        .readValue(jsonFromResponse, Variable.class);
    Variable variableModified = variableRepository.findByName("Variable3").get();

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(responseVariable, equalTo(variableModified));
    assertThat(responseVariable.getDescription(), equalTo("Description modified"));

    client.close();
  }

  @Test
  public void updateVariableByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();
    variable.setDescription("Description modified");

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + VARIABLES);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", authorizationUser);

    String json = objectMapper.writeValueAsString(variable);
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void createVariableAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = new Variable(null, "Variable4", "units", "Variable for testing",
        LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + VARIABLES);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationAdmin);

    String json = objectMapper.writeValueAsString(variable);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable responseVariable = objectMapper
        .readValue(jsonFromResponse, Variable.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
    assertThat(responseVariable, notNullValue());
    assertThat(variableRepository.findByName("Variable4"), notNullValue());

    client.close();
  }

  @Test
  public void createVariableIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = new Variable(null, "Variable4", "units", "Variable for testing",
        LocalDateTime.now());

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + VARIABLES);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", authorizationUser);

    String json = objectMapper.writeValueAsString(variable);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void deleteVariableByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable1").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpDelete);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable responseVariable = objectMapper
        .readValue(jsonFromResponse, Variable.class);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(responseVariable, equalTo(variable));
    assertThat(variableRepository.findById(variable.getId()).isPresent(), equalTo(false));
    assertThat(variableSensorRelationRepository.findByVariableId(variable.getId()).isEmpty(),
        equalTo(true));

    client.close();
  }

  @Test
  public void deleteVariableByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpDelete httpDelete = new HttpDelete(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId());

    httpDelete.setHeader("Content-type", "application/json");
    httpDelete.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpDelete);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

  @Test
  public void getVariableSensorsByIdAsAdmin() throws IOException {
    if (authorizationAdmin == null) {
      getAdminAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId() + "/sensors?page=" + nPage
            + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationAdmin);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Variable> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
        });

    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(page.getContent().size(), equalTo(2));

    client.close();
  }

  @Test
  public void getVariableSensorsByIdAsUser() throws IOException {
    if (authorizationUser == null) {
      getUserAuthorization();
    }

    Variable variable = variableRepository.findByName("Variable2").get();

    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "/" + variable.getId() + "/sensors?page=" + nPage
            + "&elements=" + elements);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", authorizationUser);

    CloseableHttpResponse response = client.execute(httpGet);

    assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

    client.close();
  }

}

package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.ApiSensorApplication;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.jpa.HouseRepository;
import com.torresj.apisensorserver.jpa.RecordRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.SensorTypeRepository;
import com.torresj.apisensorserver.jpa.UserHouseRelationRepository;
import com.torresj.apisensorserver.jpa.UserRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.jpa.VariableSensorRelationRepository;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.User.Role;
import com.torresj.apisensorserver.models.UserHouseRelation;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiSensorApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = Replace.ANY)
public class VariableTest {

  @LocalServerPort
  private int port;

  private final String BASE_URL = "http://localhost:";
  private final String PATH = "/services/api/";
  private final String LOGIN = "login";

  private final int nPage = 0;
  private final int elements = 20;

  //Variable Controller
  private final String VARIABLES = "v1/variables";

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private HouseRepository houseRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SensorTypeRepository sensorTypeRepository;

  @Autowired
  private VariableSensorRelationRepository variableSensorRelationRepository;

  @Autowired
  private UserHouseRelationRepository userHouseRelationRepository;

  @Autowired
  private RecordRepository recordRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  private String authorizationAdmin;

  private String authorizationUser;


  @Before
  public void setDataBase() {

    clearDB();

    //Create variables
    Variable variable1 = new Variable(null, "Variable1", "units", "Variable for testing",
        LocalDateTime.now());
    Variable variable2 = new Variable(null, "Variable2", "units", "Variable for testing",
        LocalDateTime.now());
    Variable variable3 = new Variable(null, "Variable3", "units", "Variable for testing",
        LocalDateTime.now());

    variable1 = variableRepository.save(variable1);
    variable2 = variableRepository.save(variable2);
    variable3 = variableRepository.save(variable3);

    //Create Sensor type
    SensorType type1 = new SensorType(null, "type1", "Type for testing", "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());
    SensorType type2 = new SensorType(null, "type2", "Type for testing", "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());

    type1 = sensorTypeRepository.save(type1);
    type2 = sensorTypeRepository.save(type2);

    //Create House
    House house1 = new House(null, "House1", LocalDateTime.now());
    House house2 = new House(null, "House2", LocalDateTime.now());

    house1 = houseRepository.save(house1);
    house2 = houseRepository.save(house2);

    //Create Sensor
    Sensor sensor1 = new Sensor(null, "Sensor1", type1.getId(), house1.getId(), "MAC1",
        "192.168.0.1", LocalDateTime.now(),
        LocalDateTime.now());
    Sensor sensor2 = new Sensor(null, "Sensor2", type2.getId(), house1.getId(), "MAC2",
        "192.168.0.2", LocalDateTime.now(),
        LocalDateTime.now());
    Sensor sensor3 = new Sensor(null, "Sensor3", type1.getId(), house2.getId(), "MAC3",
        "192.168.0.3", LocalDateTime.now(),
        LocalDateTime.now());
    Sensor sensor4 = new Sensor(null, "Sensor4", type2.getId(), house2.getId(), "MAC4",
        "192.168.0.4", LocalDateTime.now(),
        LocalDateTime.now());

    sensor1 = sensorRepository.save(sensor1);
    sensor2 = sensorRepository.save(sensor2);
    sensor3 = sensorRepository.save(sensor3);
    sensor4 = sensorRepository.save(sensor4);

    //Create variable - sensor
    VariableSensorRelation vsRelation1 = new VariableSensorRelation(null, sensor1.getId(),
        variable1.getId());
    VariableSensorRelation vsRelation2 = new VariableSensorRelation(null, sensor1.getId(),
        variable2.getId());
    VariableSensorRelation vsRelation3 = new VariableSensorRelation(null, sensor1.getId(),
        variable3.getId());
    VariableSensorRelation vsRelation4 = new VariableSensorRelation(null, sensor2.getId(),
        variable1.getId());
    VariableSensorRelation vsRelation5 = new VariableSensorRelation(null, sensor2.getId(),
        variable2.getId());
    VariableSensorRelation vsRelation6 = new VariableSensorRelation(null, sensor3.getId(),
        variable3.getId());
    VariableSensorRelation vsRelation8 = new VariableSensorRelation(null, sensor4.getId(),
        variable3.getId());

    variableSensorRelationRepository.save(vsRelation1);
    variableSensorRelationRepository.save(vsRelation2);
    variableSensorRelationRepository.save(vsRelation3);
    variableSensorRelationRepository.save(vsRelation4);
    variableSensorRelationRepository.save(vsRelation5);
    variableSensorRelationRepository.save(vsRelation6);
    variableSensorRelationRepository.save(vsRelation8);

    //Create User
    User user1 = new User(null, "Admin", bCryptPasswordEncoder.encode("test"), Role.ADMIN,
        LocalDateTime.now(),
        LocalDateTime.now());
    User user2 = new User(null, "User", bCryptPasswordEncoder.encode("test"), Role.USER,
        LocalDateTime.now(),
        LocalDateTime.now());
    User user3 = new User(null, "Station", bCryptPasswordEncoder.encode("test"), Role.STATION,
        LocalDateTime.now(),
        LocalDateTime.now());

    user1 = userRepository.save(user1);
    user2 = userRepository.save(user2);
    user3 = userRepository.save(user3);

    //Create User - House
    UserHouseRelation uhRelation1 = new UserHouseRelation(null, user1.getId(), house1.getId());
    UserHouseRelation uhRelation2 = new UserHouseRelation(null, user1.getId(), house2.getId());
    UserHouseRelation uhRelation3 = new UserHouseRelation(null, user2.getId(), house2.getId());

    userHouseRelationRepository.save(uhRelation1);
    userHouseRelationRepository.save(uhRelation2);
    userHouseRelationRepository.save(uhRelation3);

    //Create records
    Record record1 = new Record(null, sensor1.getId(), variable1.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record2 = new Record(null, sensor1.getId(), variable2.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record3 = new Record(null, sensor1.getId(), variable3.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record4 = new Record(null, sensor2.getId(), variable1.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record5 = new Record(null, sensor2.getId(), variable2.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record6 = new Record(null, sensor2.getId(), variable2.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record7 = new Record(null, sensor3.getId(), variable1.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record8 = new Record(null, sensor4.getId(), variable1.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record9 = new Record(null, sensor4.getId(), variable3.getId(), new Random().nextDouble(),
        LocalDateTime.now());
    Record record10 = new Record(null, sensor1.getId(), variable2.getId(),
        new Random().nextDouble(), LocalDateTime.now());

    recordRepository.save(record1);
    recordRepository.save(record2);
    recordRepository.save(record3);
    recordRepository.save(record4);
    recordRepository.save(record5);
    recordRepository.save(record6);
    recordRepository.save(record7);
    recordRepository.save(record8);
    recordRepository.save(record9);
    recordRepository.save(record10);

    objectMapper.registerModule(new JavaTimeModule());

  }

  @After
  public void clearDB() {
    variableRepository.deleteAll();
    sensorRepository.deleteAll();
    houseRepository.deleteAll();
    userRepository.deleteAll();
    sensorTypeRepository.deleteAll();
    variableSensorRelationRepository.deleteAll();
    userHouseRelationRepository.deleteAll();
    recordRepository.deleteAll();
  }

  public void getAdminAuthorization() throws IOException {

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(BASE_URL + port + PATH + LOGIN);

    String json = "{\"username\":\"Admin\",\"password\":\"test\"}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");

    CloseableHttpResponse response = client.execute(httpPost);
    authorizationAdmin = response.getFirstHeader("Authorization").getValue();
    client.close();
  }

  public void getUserAuthorization() throws IOException {

    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(BASE_URL + port + PATH + LOGIN);

    String json = "{\"username\":\"User\",\"password\":\"test\"}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");

    CloseableHttpResponse response = client.execute(httpPost);
    authorizationUser = response.getFirstHeader("Authorization").getValue();
    client.close();
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
    assertThat(page.getContent().size(), equalTo(3));

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

    Variable variable = variableRepository.findByName("Variable1").get();

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

    Variable variable = variableRepository.findByName("Variable1").get();

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

    Variable variable = variableRepository.findByName("Variable1").get();
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
    Variable variableModified = variableRepository.findByName("Variable1").get();

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

    Variable variable = variableRepository.findByName("Variable1").get();
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

    Variable variable = variableRepository.findByName("Variable1").get();

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

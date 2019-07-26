package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.ApiSensorApplication;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiSensorApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = Replace.ANY)
public class BasicRestTest {

  protected static boolean SETUP = false;

  @LocalServerPort
  protected int port;

  @Autowired
  protected VariableRepository variableRepository;

  @Autowired
  protected SensorRepository sensorRepository;

  @Autowired
  protected HouseRepository houseRepository;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected SensorTypeRepository sensorTypeRepository;

  @Autowired
  protected VariableSensorRelationRepository variableSensorRelationRepository;

  @Autowired
  protected UserHouseRelationRepository userHouseRelationRepository;

  @Autowired
  protected RecordRepository recordRepository;

  @Autowired
  protected BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  protected ObjectMapper objectMapper;

  protected String authorizationAdmin;

  protected String authorizationUser;

  protected final String BASE_URL = "http://localhost:";
  protected final String PATH = "/services/api/";
  protected final String LOGIN = "login";

  protected final int nPage = 0;
  protected final int elements = 20;


  @Before
  public void setDataBase() {
    if (!SETUP) {
      SETUP = true;

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
      SensorType type1 = new SensorType(null, "type1", "Type for testing",
          "ACTION1:ACTION2,ACTION3",
          LocalDateTime.now());
      SensorType type2 = new SensorType(null, "type2", "Type for testing",
          "ACTION1:ACTION2,ACTION3",
          LocalDateTime.now());

      type1 = sensorTypeRepository.save(type1);
      type2 = sensorTypeRepository.save(type2);

      //Create House
      House house1 = new House(null, "House1", LocalDateTime.now());
      House house2 = new House(null, "House2", LocalDateTime.now());
      House house3 = new House(null, "House3", LocalDateTime.now());

      house1 = houseRepository.save(house1);
      house2 = houseRepository.save(house2);
      house3 = houseRepository.save(house3);

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
      VariableSensorRelation vsRelation7 = new VariableSensorRelation(null, sensor4.getId(),
          variable1.getId());
      VariableSensorRelation vsRelation8 = new VariableSensorRelation(null, sensor4.getId(),
          variable3.getId());

      variableSensorRelationRepository.save(vsRelation1);
      variableSensorRelationRepository.save(vsRelation2);
      variableSensorRelationRepository.save(vsRelation3);
      variableSensorRelationRepository.save(vsRelation4);
      variableSensorRelationRepository.save(vsRelation5);
      variableSensorRelationRepository.save(vsRelation6);
      variableSensorRelationRepository.save(vsRelation7);
      variableSensorRelationRepository.save(vsRelation8);

      //Create User
      User user1 = new User(null, "Admin", bCryptPasswordEncoder.encode("test"), Role.ADMIN,
          LocalDateTime.now(),
          LocalDateTime.now());
      User user2 = new User(null, "User", bCryptPasswordEncoder.encode("test"), Role.USER,
          LocalDateTime.now(),
          LocalDateTime.now());
      User user3 = new User(null, "User2", bCryptPasswordEncoder.encode("test"), Role.USER,
          LocalDateTime.now(),
          LocalDateTime.now());

      user1 = userRepository.save(user1);
      user2 = userRepository.save(user2);
      userRepository.save(user3);

      //Create User - House
      UserHouseRelation uhRelation1 = new UserHouseRelation(null, user1.getId(), house1.getId());
      UserHouseRelation uhRelation2 = new UserHouseRelation(null, user1.getId(), house2.getId());
      UserHouseRelation uhRelation3 = new UserHouseRelation(null, user1.getId(), house3.getId());
      UserHouseRelation uhRelation4 = new UserHouseRelation(null, user2.getId(), house2.getId());
      UserHouseRelation uhRelation5 = new UserHouseRelation(null, user3.getId(), house1.getId());
      UserHouseRelation uhRelation6 = new UserHouseRelation(null, user3.getId(), house2.getId());

      userHouseRelationRepository.save(uhRelation1);
      userHouseRelationRepository.save(uhRelation2);
      userHouseRelationRepository.save(uhRelation3);
      userHouseRelationRepository.save(uhRelation4);
      userHouseRelationRepository.save(uhRelation5);
      userHouseRelationRepository.save(uhRelation6);

      //Create records
      Record record1 = new Record(null, sensor1.getId(), variable1.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record2 = new Record(null, sensor1.getId(), variable2.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record3 = new Record(null, sensor1.getId(), variable3.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record4 = new Record(null, sensor2.getId(), variable1.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record5 = new Record(null, sensor2.getId(), variable2.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record6 = new Record(null, sensor2.getId(), variable2.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record7 = new Record(null, sensor3.getId(), variable3.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record8 = new Record(null, sensor4.getId(), variable1.getId(),
          new Random().nextDouble(),
          LocalDateTime.now());
      Record record9 = new Record(null, sensor4.getId(), variable3.getId(),
          new Random().nextDouble(),
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

  }

  private void clearDB() {
    variableRepository.deleteAll();
    sensorRepository.deleteAll();
    houseRepository.deleteAll();
    userRepository.deleteAll();
    sensorTypeRepository.deleteAll();
    variableSensorRelationRepository.deleteAll();
    userHouseRelationRepository.deleteAll();
    recordRepository.deleteAll();
  }

  @Test
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
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(authorizationAdmin, notNullValue());
    client.close();
  }

  @Test
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
    assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
    assertThat(authorizationUser, notNullValue());
    client.close();
  }

  protected static void SetUpFalse() {
    SETUP = false;
  }

}

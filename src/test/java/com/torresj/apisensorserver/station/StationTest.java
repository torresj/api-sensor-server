package com.torresj.apisensorserver.station;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.torresj.apisensorserver.ApiSensorApplication;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.entities.Record;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.models.entities.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.RecordRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiSensorApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2, replace = Replace.ANY)
@TestPropertySource(locations =
    "classpath:application-test.properties")
public class StationTest {

  private long variableId;
  private long sensorId;

  @LocalServerPort
  private int port;

  @Autowired
  private VariableRepository variableRepository;

  @Autowired
  private SensorRepository sensorRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private SensorTypeRepository sensorTypeRepository;

  @Autowired
  private VariableSensorRelationRepository variableSensorRelationRepository;

  @Autowired
  private RecordRepository recordRepository;

  @Autowired
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @Autowired
  private ObjectMapper objectMapper;

  private static String AUTHORIZATION;

  private static final String SENSORS = "v1/sensors";
  private static final String VARIABLES = "v1/variables";
  private static final String TYPE = "v1/sensortypes";

  private static final String RABBITMQ = "tcp://localhost:1883";
  private static final String MQTT_TOPIC = "mqtt/topic";
  private static final String MQTT_USER = "mqtt-user";
  private static final String MQTT_PASSWORD = "mqtt-user";


  private final String BASE_URL = "http://localhost:";
  private final String PATH = "/services/api/";
  private final String LOGIN = "login";

  private final int nPage = 0;
  private final int elements = 20;

  @Before
  public void setDataBase() throws IOException {
    clearDB();

    //Create variables
    Variable variable1 = new Variable(null, "Variable1", "units", "Variable for testing",
        LocalDateTime.now());

    variable1 = variableRepository.save(variable1);

    //Create Sensor type
    SensorType type1 = new SensorType(null, "type1", "Type for testing",
        "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());

    type1 = sensorTypeRepository.save(type1);

    //Create Sensor
    Sensor sensor1 = new Sensor(null, "Sensor1", type1.getId(), null, "MAC1",
        "192.168.0.1", "192.168.0.1", LocalDateTime.now(),
        LocalDateTime.now());

    sensor1 = sensorRepository.save(sensor1);

    //Create variable - sensor
    VariableSensorRelation vsRelation1 = new VariableSensorRelation(null, sensor1.getId(),
        variable1.getId());

    variableSensorRelationRepository.save(vsRelation1);

    //Create User
    User user = new User(null, "Station", bCryptPasswordEncoder.encode("test"), Role.STATION,
        LocalDateTime.now(),
        LocalDateTime.now());

    userRepository.save(user);

    objectMapper.registerModule(new JavaTimeModule());

    stationLogin();

  }

  private void clearDB() {
    variableRepository.deleteAll();
    sensorRepository.deleteAll();
    userRepository.deleteAll();
    sensorTypeRepository.deleteAll();
    variableSensorRelationRepository.deleteAll();
    recordRepository.deleteAll();
  }

  private void stationLogin() throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(BASE_URL + port + PATH + LOGIN);

    String json = "{\"username\":\"Station\",\"password\":\"test\"}";
    StringEntity entity = new StringEntity(json);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/json");
    httpPost.setHeader("Content-type", "application/json");

    CloseableHttpResponse response = client.execute(httpPost);
    AUTHORIZATION = response.getFirstHeader("Authorization").getValue();
  }

  @Test
  public void initStationTestSensorAndVariableExists() throws IOException {
    //Init sensor with hardware values
    Sensor sensor = new Sensor();
    sensor.setPrivateIp("192.168.1.1");
    sensor.setPublicIp("192.168.1.1");
    sensor.setMac("MAC1");
    sensor.setName("test");

    Variable variable = new Variable();
    variable.setName("Variable1");

    SensorType type = getSensorType("type1");
    sensor.setSensorTypeId(type.getId());

    Sensor sensorRest = RegisterOrUpdateSensorFromRest(sensor);
    Variable variableRest = getVariableFromRest(variable.getName());

    variableId = variableRest.getId();
    sensorId = sensorRest.getId();

    addVariable();

    VariableSensorRelation relation = variableSensorRelationRepository
        .findBySensorIdAndVariableId(sensorId, variableId).orElse(null);

    assertThat(sensorRest, equalTo(sensorRepository.findByMac(sensor.getMac()).get()));
    assertThat(variableRest, equalTo(variableRepository.findByName(variable.getName()).get()));
    assertThat(relation, notNullValue());

  }

  @Test
  public void initStationTestSenorNotExistsAndVariableExists() throws IOException {
    //Init sensor with hardware values
    Sensor sensor = new Sensor();
    sensor.setPrivateIp("192.168.1.1");
    sensor.setPublicIp("192.168.1.1");
    sensor.setMac("00:0a:95:9d:68:16");
    sensor.setName("test");

    Variable variable = new Variable();
    variable.setName("Variable1");

    SensorType type = getSensorType("type1");
    sensor.setSensorTypeId(type.getId());

    Sensor sensorRest = RegisterOrUpdateSensorFromRest(sensor);
    Variable variableRest = getVariableFromRest(variable.getName());

    variableId = variableRest.getId();
    sensorId = sensorRest.getId();

    addVariable();

    VariableSensorRelation relation = variableSensorRelationRepository
        .findBySensorIdAndVariableId(sensorId, variableId).orElse(null);

    assertThat(sensorRest, equalTo(sensorRepository.findByMac(sensor.getMac()).get()));
    assertThat(variableRest, equalTo(variableRepository.findByName(variable.getName()).get()));
    assertThat(relation, notNullValue());

  }

  @Test
  public void initStationTestSenorExistsAndVariableNotExists() throws IOException {
    //Init sensor with hardware values
    Sensor sensor = new Sensor();
    sensor.setPrivateIp("192.168.1.1");
    sensor.setPublicIp("192.168.1.1");
    sensor.setMac("MAC1");
    sensor.setName("test");

    Variable variable = new Variable();
    variable.setName("VariableTest");

    SensorType type = getSensorType("type1");
    sensor.setSensorTypeId(type.getId());

    Sensor sensorRest = RegisterOrUpdateSensorFromRest(sensor);
    Variable variableRest = getVariableFromRest(variable.getName());

    variableId = variableRest.getId();
    sensorId = sensorRest.getId();

    addVariable();

    VariableSensorRelation relation = variableSensorRelationRepository
        .findBySensorIdAndVariableId(sensorId, variableId).orElse(null);

    assertThat(sensorRest, equalTo(sensorRepository.findByMac(sensor.getMac()).get()));
    assertThat(variableRest, equalTo(variableRepository.findByName(variable.getName()).get()));
    assertThat(relation, notNullValue());
  }

  @Test
  public void initStationTestSenorNotExistsAndVariableNotExists() throws IOException {
    //Init sensor with hardware values
    Sensor sensor = new Sensor();
    sensor.setPrivateIp("192.168.1.1");
    sensor.setPublicIp("192.168.1.1");
    sensor.setMac("00:0a:95:9d:68:16");
    sensor.setName("test");

    Variable variable = new Variable();
    variable.setName("VariableTest");

    SensorType type = getSensorType("type1");
    sensor.setSensorTypeId(type.getId());

    Sensor sensorRest = RegisterOrUpdateSensorFromRest(sensor);
    Variable variableRest = getVariableFromRest(variable.getName());

    variableId = variableRest.getId();
    sensorId = sensorRest.getId();

    addVariable();

    VariableSensorRelation relation = variableSensorRelationRepository
        .findBySensorIdAndVariableId(sensorId, variableId).orElse(null);

    assertThat(sensorRest, equalTo(sensorRepository.findByMac(sensor.getMac()).get()));
    assertThat(variableRest, equalTo(variableRepository.findByName(variable.getName()).get()));
    assertThat(relation, notNullValue());
  }

  @Test
  @Ignore
  public void sendValidRecord()
      throws IOException, MqttException, JSONException, InterruptedException {
    variableId = variableRepository.findByName("Variable1").get().getId();
    sensorId = sensorRepository.findByMac("MAC1").get().getId();

    Record record = new Record();
    record.setSensorId(sensorId);
    record.setVariableId(variableId);
    record.setDate(LocalDateTime.now());
    record.setValue(ThreadLocalRandom.current().nextLong(100));

    String jsonString = objectMapper.writeValueAsString(record);
    JSONObject json = new JSONObject(jsonString);
    json.put("type", "record");
    byte[] payload = json.toString().getBytes();

    IMqttClient publisher = connectToRabbitMQ();

    MqttMessage msg = new MqttMessage(payload);
    msg.setQos(0);
    msg.setRetained(true);
    publisher.publish(MQTT_TOPIC, msg);

    Thread.sleep(1000);

    Record recordDB = recordRepository
        .findBySensorIdAndVariableIdAndDate(sensorId, variableId, record.getDate()).orElse(null);
    assertThat(recordDB, notNullValue());
  }

  @Test
  @Ignore
  public void sendInvalidRecord()
      throws IOException, MqttException, JSONException, InterruptedException {

    Record record = new Record();
    record.setSensorId(new Random().nextLong());
    record.setVariableId(new Random().nextLong());
    record.setDate(LocalDateTime.now());
    record.setValue(ThreadLocalRandom.current().nextLong(100));

    String jsonString = objectMapper.writeValueAsString(record);
    JSONObject json = new JSONObject(jsonString);
    json.put("type", "record");
    byte[] payload = json.toString().getBytes();

    IMqttClient publisher = connectToRabbitMQ();

    MqttMessage msg = new MqttMessage(payload);
    msg.setQos(0);
    msg.setRetained(true);
    publisher.publish(MQTT_TOPIC, msg);

    Thread.sleep(1000);

    Record recordDB = recordRepository
        .findBySensorIdAndVariableIdAndDate(record.getSensorId(), record.getVariableId(),
            record.getDate()).orElse(null);
    assertThat(recordDB, nullValue());
  }

  private SensorType getSensorType(String typeName) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + TYPE + "?page=" + nPage + "&elements=" + elements
            + "&name=" + typeName);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", AUTHORIZATION);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<SensorType> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<SensorType>>() {
        });

    client.close();
    if (page.getContent().isEmpty()) {
      return null;
    } else {
      return page.getContent().get(0);
    }
  }

  private Sensor RegisterOrUpdateSensorFromRest(Sensor sensor) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + SENSORS);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", AUTHORIZATION);

    String json = objectMapper.writeValueAsString(sensor);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Sensor sensorRest = objectMapper
        .readValue(jsonFromResponse, Sensor.class);

    client.close();

    if (response.getStatusLine().getStatusCode() == 201) {
      return sensorRest;
    } else if (response.getStatusLine().getStatusCode() == 202) {
      sensorRest.setPublicIp(sensor.getPublicIp());
      return updateSensor(sensorRest);
    } else {
      return null;
    }
  }

  private Sensor updateSensor(Sensor sensor) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + SENSORS);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", AUTHORIZATION);

    String json = objectMapper.writeValueAsString(sensor);
    StringEntity entity = new StringEntity(json);

    httpPut.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPut);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Sensor sensorRest = objectMapper
        .readValue(jsonFromResponse, Sensor.class);

    client.close();

    return sensorRest;
  }

  private Variable getVariableFromRest(String name) throws IOException {
    Variable variableRest;
    CloseableHttpClient client = HttpClients.createDefault();
    HttpGet httpGet = new HttpGet(
        BASE_URL + port + PATH + VARIABLES + "?page=" + nPage + "&elements=" + elements
            + "&name=" + name);

    httpGet.setHeader("Content-type", "application/json");
    httpGet.setHeader("Authorization", AUTHORIZATION);

    CloseableHttpResponse response = client.execute(httpGet);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Page<Variable> page = objectMapper
        .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
        });

    if (page.getContent().isEmpty()) {
      variableRest = registerVariable(name);
    } else {
      variableRest = page.getContent().get(0);
    }
    client.close();
    return variableRest;
  }

  private Variable registerVariable(String name)
      throws IOException {
    Variable variable = new Variable();
    variable.setName(name);
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPost httpPost = new HttpPost(
        BASE_URL + port + PATH + VARIABLES);

    httpPost.setHeader("Content-type", "application/json");
    httpPost.setHeader("Authorization", AUTHORIZATION);

    String json = objectMapper.writeValueAsString(variable);
    StringEntity entity = new StringEntity(json);

    httpPost.setEntity(entity);

    CloseableHttpResponse response = client.execute(httpPost);

    String jsonFromResponse = EntityUtils.toString(response.getEntity());

    Variable variableRest = objectMapper
        .readValue(jsonFromResponse, Variable.class);

    client.close();

    return variableRest;
  }

  private void addVariable() throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();
    HttpPut httpPut = new HttpPut(
        BASE_URL + port + PATH + SENSORS + "/" + sensorId + "/variables/" + variableId);

    httpPut.setHeader("Content-type", "application/json");
    httpPut.setHeader("Authorization", AUTHORIZATION);

    client.execute(httpPut);
  }

  private IMqttClient connectToRabbitMQ() throws MqttException {
    String publisherId = UUID.randomUUID().toString();
    IMqttClient publisher = new MqttClient(RABBITMQ, publisherId);

    MqttConnectOptions options = new MqttConnectOptions();
    options.setAutomaticReconnect(true);
    options.setCleanSession(true);
    options.setConnectionTimeout(10);
    options.setUserName(MQTT_USER);
    options.setPassword(MQTT_PASSWORD.toCharArray());
    publisher.connect(options);

    return publisher;
  }
}

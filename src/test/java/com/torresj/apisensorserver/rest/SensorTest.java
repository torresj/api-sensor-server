package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.models.entities.Variable;

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

public class SensorTest extends BasicRestTest {

    //SensorType Controller
    private final String SENSORS = "v1/sensors";

    @AfterClass
    public static void ChangeSetUp() {
        SetUpFalse();
    }

    @Test
    public void getAllSensorsAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "?page=" + nPage + "&elements=" + elements);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Sensor> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Sensor>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(), equalTo(5));

        client.close();
    }

    @Test
    public void getAllSensorsWithoutPaginationAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/all");

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        List<Sensor> sensors = objectMapper
                .readValue(jsonFromResponse, new TypeReference<List<Sensor>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(sensors.size(), equalTo(5));

        client.close();
    }

    @Test
    public void getAllSensorsWithoutPaginationAndSensorTypeIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        SensorType type = sensorTypeRepository.findByName("type1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/all?sensorTypeId="+type.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        List<Sensor> sensors = objectMapper
                .readValue(jsonFromResponse, new TypeReference<List<Sensor>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(sensors.size(), equalTo(2));

        client.close();
    }

    @Test
    public void getAllSensorsWithoutPaginationAsUser() throws IOException {
        if (authorizationUser == null) {
            getAllSensorsAsUser();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/all");

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);


        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void getAllSensorsAsAdminByType() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        SensorType type = sensorTypeRepository.findByName("type1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "?page=" + nPage + "&elements=" + elements
                        + "&sensorTypeId=" + type.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Sensor> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Sensor>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(), equalTo(3));
        for (Sensor sensor : page.getContent()) {
            assertThat(sensor.getSensorTypeId(), equalTo(type.getId()));
        }

        client.close();
    }

    @Test
    public void getAllSensorsAsAdminByName() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "?page=" + nPage + "&elements=" + elements
                        + "&name=" + sensor.getName());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Sensor> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Sensor>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(), equalTo(1));
        assertThat(page.getContent().get(0), equalTo(sensor));

        client.close();
    }

    @Test
    public void getAllSensorsAsUser() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "?page=" + nPage + "&elements=" + elements);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void getSensorByIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseSensor, equalTo(sensor));

        client.close();
    }

    @Test
    public void getSensorByIdAsUserToSensorAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseSensor, equalTo(sensor));

        client.close();
    }

    @Test
    public void getSensorByIdAsUserToSensorNotAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void getAllVariablesFromSensorAsAdminById() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables?page=" + nPage
                        + "&elements=" + elements);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Variable> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(),
                equalTo(variableSensorRelationRepository.findBySensorId(sensor.getId()).size()));

        client.close();
    }

    @Test
    public void getAllVariablesFromSensorAsUserByIdToSensorAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables?page=" + nPage
                        + "&elements=" + elements);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Variable> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Variable>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(),
                equalTo(variableSensorRelationRepository.findBySensorId(sensor.getId()).size()));

        client.close();
    }

    @Test
    public void getAllVariablesFromSensorAsUserByIdToSensorNotAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables?page=" + nPage
                        + "&elements=" + elements);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void registerVariableToSensorAsAdminById() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables/" + variable
                        .getId());

        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpPut);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Variable responseVariable = objectMapper
                .readValue(jsonFromResponse, Variable.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseVariable, equalTo(variable));
        assertThat(variableSensorRelationRepository.findBySensorId(sensor.getId()).size(), equalTo(2));

        client.close();
    }

    @Test
    public void registerVariableToSensorAsUserById() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable1").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables/" + variable
                        .getId());

        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpPut);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
        client.close();
    }

    @Test
    public void deleteVariableToSensorAsAdminById() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables/" + variable
                        .getId());

        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpDelete);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Variable responseVariable = objectMapper
                .readValue(jsonFromResponse, Variable.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseVariable, equalTo(variable));
        assertThat(variableSensorRelationRepository.findBySensorId(sensor.getId()).size(), equalTo(1));

        client.close();
    }

    @Test
    public void deleteVariableToSensorAsUserById() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId() + "/variables/" + variable
                        .getId());

        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpDelete);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));
        client.close();
    }

    @Test
    public void updateSensorByIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        sensor.setPublicIp("IP modified");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(
                BASE_URL + port + PATH + SENSORS);

        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", authorizationAdmin);

        String json = objectMapper.writeValueAsString(sensor);
        StringEntity entity = new StringEntity(json);

        httpPut.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPut);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);
        Sensor sensorModified = sensorRepository.findByMac("MAC3").get();

        assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
        assertThat(responseSensor, equalTo(sensorModified));
        assertThat(responseSensor.getPublicIp(), equalTo("IP modified"));

        client.close();
    }

    @Test
    public void updateSensorByIdAsUser() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        sensor.setPublicIp("IP modified");

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPut httpPut = new HttpPut(
                BASE_URL + port + PATH + SENSORS);

        httpPut.setHeader("Content-type", "application/json");
        httpPut.setHeader("Authorization", authorizationUser);

        String json = objectMapper.writeValueAsString(sensor);
        StringEntity entity = new StringEntity(json);

        httpPut.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPut);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void registerSensorByIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        SensorType type = sensorTypeRepository.findByName("type1").get();
        House house = houseRepository.findByName("House1").get();
        Sensor sensor = new Sensor(null, "test", type.getId(), house.getId(), "MAC5",
                "192.168.0.5", "192.168.0.5", LocalDateTime.now(),
                LocalDateTime.now());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
                BASE_URL + port + PATH + SENSORS);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", authorizationAdmin);

        String json = objectMapper.writeValueAsString(sensor);
        StringEntity entity = new StringEntity(json);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(201));
        assertThat(responseSensor, equalTo(sensorRepository.findByMac("MAC5").get()));

        client.close();
    }

    @Test
    public void registerSensorByIdAsAdminWithMacExist() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        SensorType type = sensorTypeRepository.findByName("type1").get();
        House house = houseRepository.findByName("House1").get();
        Sensor sensor = new Sensor(null, "test", type.getId(), house.getId(), "MAC2",
                "192.168.0.4", "192.168.0.4", LocalDateTime.now(),
                LocalDateTime.now());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
                BASE_URL + port + PATH + SENSORS);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", authorizationAdmin);

        String json = objectMapper.writeValueAsString(sensor);
        StringEntity entity = new StringEntity(json);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);

        System.out.println(responseSensor);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(202));
        assertThat(responseSensor, equalTo(sensorRepository.findByMac("MAC2").get()));

        client.close();
    }

    @Test
    public void registerSensorByIdAsUser() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        SensorType type = sensorTypeRepository.findByName("type1").get();
        House house = houseRepository.findByName("House1").get();
        Sensor sensor = new Sensor(null, "test", type.getId(), house.getId(), "MAC5",
                "192.168.0.4", "192.168.0.4", LocalDateTime.now(),
                LocalDateTime.now());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(
                BASE_URL + port + PATH + SENSORS);

        httpPost.setHeader("Content-type", "application/json");
        httpPost.setHeader("Authorization", authorizationUser);

        String json = objectMapper.writeValueAsString(sensor);
        StringEntity entity = new StringEntity(json);

        httpPost.setEntity(entity);

        CloseableHttpResponse response = client.execute(httpPost);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void deleteSensorByIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId());

        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpDelete);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Sensor responseSensor = objectMapper
                .readValue(jsonFromResponse, Sensor.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseSensor, equalTo(sensor));
        assertThat(sensorRepository.findById(sensor.getId()).isPresent(), equalTo(false));
        assertThat(variableSensorRelationRepository.findBySensorId(sensor.getId()).isEmpty(),
                equalTo(true));

        client.close();
    }

    @Test
    public void deleteSensorByIdAsUser() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC2").get();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpDelete httpDelete = new HttpDelete(
                BASE_URL + port + PATH + SENSORS + "/" + sensor.getId());

        httpDelete.setHeader("Content-type", "application/json");
        httpDelete.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpDelete);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }
}

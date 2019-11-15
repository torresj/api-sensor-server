package com.torresj.apisensorserver.rest;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.torresj.apisensorserver.jackson.RestPage;
import com.torresj.apisensorserver.models.entities.Record;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.Variable;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public class RecordTest extends BasicRestTest {

    //Record Controller
    private final String RECORDS = "v1/records";

    @AfterClass
    public static void ChangeSetUp() {
        SetUpFalse();
    }

    @Test
    public void getAllRecordsAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();
        Variable variable = variableRepository.findByName("Variable1").get();

        String from = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        String to = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "?sensorId=" + sensor.getId() + "&variableId=" + variable
                        .getId() + "&page=" + nPage + "&elements=" + elements + "&from=" + from + "&to=" + to);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Record> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Record>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(), equalTo(1));

        client.close();
    }

    @Test
    public void getAllRecordsAsUser() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable3").get();

        String from = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        String to = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "?sensorId=" + sensor.getId() + "&variableId=" + variable
                        .getId() + "&page=" + nPage + "&elements=" + elements + "&from=" + from + "&to=" + to);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Page<Record> page = objectMapper
                .readValue(jsonFromResponse, new TypeReference<RestPage<Record>>() {
                });

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(page.getContent().size(), equalTo(1));

        client.close();
    }

    @Test
    public void getAllRecordsAsUserToSensorNotAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();
        Variable variable = variableRepository.findByName("Variable1").get();

        String from = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_DATE);
        String to = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "?sensorId=" + sensor.getId() + "&variableId=" + variable
                        .getId() + "&page=" + nPage + "&elements=" + elements + "&from=" + from + "&to=" + to);

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

    @Test
    public void getRecordsByIdAsAdmin() throws IOException {
        if (authorizationAdmin == null) {
            getAdminAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable3").get();
        Record record = recordRepository
                .findBySensorIdAndVariableIdAndCreateAtBetween(sensor.getId(), variable.getId(),
                        LocalDateTime.now().minusDays(1), LocalDateTime.now(), PageRequest.of(nPage, elements))
                .getContent()
                .get(0);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "/" + record.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationAdmin);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Record responseRecord = objectMapper
                .readValue(jsonFromResponse, Record.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseRecord, equalTo(record));

        client.close();
    }

    @Test
    public void getRecordsByIdAsUserToSensorAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC3").get();
        Variable variable = variableRepository.findByName("Variable3").get();
        Record record = recordRepository
                .findBySensorIdAndVariableIdAndCreateAtBetween(sensor.getId(), variable.getId(),
                        LocalDateTime.now().minusDays(1), LocalDateTime.now(), PageRequest.of(nPage, elements))
                .getContent()
                .get(0);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "/" + record.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        String jsonFromResponse = EntityUtils.toString(response.getEntity());

        Record responseRecord = objectMapper
                .readValue(jsonFromResponse, Record.class);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        assertThat(responseRecord, equalTo(record));

        client.close();
    }

    @Test
    public void getRecordsByIdAsUsertoSensorNotAllowed() throws IOException {
        if (authorizationUser == null) {
            getUserAuthorization();
        }

        Sensor sensor = sensorRepository.findByMac("MAC1").get();
        Variable variable = variableRepository.findByName("Variable1").get();
        Record record = recordRepository
                .findBySensorIdAndVariableIdAndCreateAtBetween(sensor.getId(), variable.getId(),
                        LocalDateTime.now().minusDays(1), LocalDateTime.now(), PageRequest.of(nPage, elements))
                .getContent()
                .get(0);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(
                BASE_URL + port + PATH + RECORDS + "/" + record.getId());

        httpGet.setHeader("Content-type", "application/json");
        httpGet.setHeader("Authorization", authorizationUser);

        CloseableHttpResponse response = client.execute(httpGet);

        assertThat(response.getStatusLine().getStatusCode(), equalTo(403));

        client.close();
    }

}

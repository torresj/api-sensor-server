package com.torresj.apisensorserver.utils;

import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.UserHouseRelation;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {

  private static final Random random = new Random();

  public static Sensor getExampleSensor(long id, long houseId, long sensorType) {
    Sensor sensor = new Sensor();
    sensor.setCreateAt(LocalDateTime.now());
    sensor.setId(id);
    sensor.setIp("192.168.0.X");
    sensor.setLastConnection(LocalDateTime.now());
    sensor.setMac("mac" + id);
    sensor.setName("Sensor" + id);
    sensor.setHouseId(houseId);
    sensor.setSensorTypeId(sensorType);
    return sensor;
  }

  public static SensorType getExampleSensorType(long id) {
    SensorType type = new SensorType();
    type.setName("Type" + id);
    type.setId(id);
    type.setDescription("Type" + id);
    type.setActions("Action1;Action2;Action3");
    return type;
  }

  public static House getExampleHouse(long id) {
    House house = new House();
    house.setId(id);
    house.setName("House" + id);
    house.setCreateAt(LocalDateTime.now());
    return house;
  }

  public static Record getExampleRecord(long sensor, long variable) {
    Record record = new Record();
    record.setCreateAt(LocalDateTime.now());
    record.setId(random.nextLong());
    record.setSensorId(sensor);
    record.setVariableId(variable);
    record.setValue(new Random().nextDouble());
    return record;
  }

  public static User getExampleUser(String name, String password, User.Role rol) {
    User user = new User();
    user.setCreateAt(LocalDateTime.now());
    user.setLastConnection(LocalDateTime.now());
    user.setId(random.nextLong());
    user.setUsername(name);
    user.setPassword(password);
    user.setRole(rol);
    return user;
  }

  public static Variable getExampleVariable(long id) {
    Variable variable = new Variable();
    variable.setId(id);
    variable.setCreateAt(LocalDateTime.now());
    variable.setDescription("Description variable " + id);
    variable.setName("Variable" + id);
    variable.setUnits("unit");
    return variable;
  }

  public static List<Variable> getExampleVariables(int n) {
    List<Variable> testList = new ArrayList();
    for (int i = 1; i <= n; i++) {
      testList.add(getExampleVariable((long) i));
    }
    return testList;
  }

  public static List<House> getExampleHouses(int n) {
    List<House> testList = new ArrayList();
    for (int i = 0; i < n; i++) {
      testList.add(getExampleHouse((long) i));
    }
    return testList;
  }

  public static List<Record> getExampleRecords(int n, long sensor, long variable) {
    List<Record> testList = new ArrayList();
    for (int i = 0; i < n; i++) {
      testList.add(getExampleRecord(sensor, variable));
    }
    return testList;
  }

  public static VariableSensorRelation getExampleVariableRelation(long variableId, long sensorId) {
    VariableSensorRelation relation = new VariableSensorRelation();
    relation.setId(random.nextLong());
    relation.setVariableId(variableId);
    relation.setSensorId(sensorId);
    return relation;
  }

  public static UserHouseRelation getExampleUserHouseRelation(long userId, long houseId) {
    UserHouseRelation relation = new UserHouseRelation();
    relation.setId(random.nextLong());
    relation.setHouseId(houseId);
    relation.setUserId(userId);
    return relation;
  }
}

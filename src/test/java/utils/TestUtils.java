package utils;

import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Record;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.Variable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {

  private static final Random random = new Random();

  public static Sensor getExampleSensor(long id) {
    Sensor sensor = new Sensor();
    sensor.setCreateAt(LocalDateTime.now());
    sensor.setId(id);
    sensor.setIp("192.168.0.X");
    sensor.setLastConnection(LocalDateTime.now());
    sensor.setMac("mac" + id);
    sensor.setName("Sensor" + id);
    return sensor;
  }

  private static SensorType getExampleSensorType(String name) {
    SensorType type = new SensorType();
    type.setName(name);
    type.setId(random.nextLong());
    type.setDescription(name);
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

  public static User getExampleUser(String name, String password, String rol) {
    User user = new User();
    user.setCreateAt(LocalDateTime.now());
    user.setLastConnection(LocalDateTime.now());
    user.setId(random.nextLong());
    user.setUsername(name);
    user.setPassword(password);
    user.setRol(rol);
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
    for (int i = 0; i < n; i++) {
      testList.add(getExampleVariable((long) i));
    }
    return testList;
  }

  public static List<Sensor> getExampleSensors(int n) {
    List<Sensor> testList = new ArrayList();
    for (int i = 0; i < n; i++) {
      testList.add(getExampleSensor((long) i));
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


}

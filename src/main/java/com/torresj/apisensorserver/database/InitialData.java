package com.torresj.apisensorserver.database;

import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.models.User;
import com.torresj.apisensorserver.models.User.Role;
import com.torresj.apisensorserver.models.UserHouseRelation;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.RecordRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import java.time.LocalDateTime;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class InitialData {

  private VariableRepository variableRepository;

  private SensorRepository sensorRepository;

  private HouseRepository houseRepository;

  private UserRepository userRepository;

  private SensorTypeRepository sensorTypeRepository;

  private VariableSensorRelationRepository variableSensorRelationRepository;

  private UserHouseRelationRepository userHouseRelationRepository;

  private RecordRepository recordRepository;

  private BCryptPasswordEncoder bCryptPasswordEncoder;

  public InitialData(VariableRepository variableRepository,
      SensorRepository sensorRepository,
      HouseRepository houseRepository,
      UserRepository userRepository,
      SensorTypeRepository sensorTypeRepository,
      VariableSensorRelationRepository variableSensorRelationRepository,
      UserHouseRelationRepository userHouseRelationRepository,
      RecordRepository recordRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.variableRepository = variableRepository;
    this.sensorRepository = sensorRepository;
    this.houseRepository = houseRepository;
    this.userRepository = userRepository;
    this.sensorTypeRepository = sensorTypeRepository;
    this.variableSensorRelationRepository = variableSensorRelationRepository;
    this.userHouseRelationRepository = userHouseRelationRepository;
    this.recordRepository = recordRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  @Bean
  public void initDataBase() {
    clearDB();
    //Create variables
    Variable variable1 = new Variable(null, "Temperature", "˚C", "Temperature sensor",
        LocalDateTime.now());
    Variable variable2 = new Variable(null, "Humidity", "%", "Humidity sensoer",
        LocalDateTime.now());
    Variable variable3 = new Variable(null, "Barometric Pressure", "Ba",
        "Digital Barometric pressure sensor",
        LocalDateTime.now());

    variable1 = variableRepository.save(variable1);
    variable2 = variableRepository.save(variable2);
    variable3 = variableRepository.save(variable3);

    //Create Sensor type
    SensorType type1 = new SensorType(null, "weather", "Weather station type",
        "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());
    SensorType type2 = new SensorType(null, "Actuator", "Actuator station type",
        "ACTION1:ACTION2,ACTION3",
        LocalDateTime.now());

    type1 = sensorTypeRepository.save(type1);
    type2 = sensorTypeRepository.save(type2);

    //Create House
    House house1 = new House(null, "House1", LocalDateTime.now());
    House house2 = new House(null, "House2", LocalDateTime.now());

    house1 = houseRepository.save(house1);
    house2 = houseRepository.save(house2);

    //Create Sensor
    Sensor sensor1 = new Sensor(null, "Weather Station", type1.getId(), house1.getId(), "MAC1",
        "192.168.0.1", LocalDateTime.now(),
        LocalDateTime.now());
    Sensor sensor2 = new Sensor(null, "Actuator Station", type2.getId(), house1.getId(), "MAC2",
        "192.168.0.2", LocalDateTime.now(),
        LocalDateTime.now());
    Sensor sensor3 = new Sensor(null, "Weather Station", type1.getId(), house2.getId(), "MAC2",
        "192.168.0.2", LocalDateTime.now(),
        LocalDateTime.now());

    sensor1 = sensorRepository.save(sensor1);
    sensorRepository.save(sensor2);
    sensor3 = sensorRepository.save(sensor3);

    //Variable-sensor relation
    VariableSensorRelation vsRelation1 = new VariableSensorRelation(null, sensor1.getId(),
        variable1.getId());
    VariableSensorRelation vsRelation2 = new VariableSensorRelation(null, sensor1.getId(),
        variable2.getId());
    VariableSensorRelation vsRelation3 = new VariableSensorRelation(null, sensor1.getId(),
        variable3.getId());
    VariableSensorRelation vsRelation4 = new VariableSensorRelation(null, sensor3.getId(),
        variable1.getId());
    VariableSensorRelation vsRelation5 = new VariableSensorRelation(null, sensor3.getId(),
        variable2.getId());

    variableSensorRelationRepository.save(vsRelation1);
    variableSensorRelationRepository.save(vsRelation2);
    variableSensorRelationRepository.save(vsRelation3);
    variableSensorRelationRepository.save(vsRelation4);
    variableSensorRelationRepository.save(vsRelation5);

    //Create User
    User user1 = new User(null, "Administrator", bCryptPasswordEncoder.encode("admin"), Role.ADMIN,
        LocalDateTime.now(),
        LocalDateTime.now());
    User user2 = new User(null, "Jaime", bCryptPasswordEncoder.encode("test"), Role.USER,
        LocalDateTime.now(),
        LocalDateTime.now());
    User user3 = new User(null, "Station", bCryptPasswordEncoder.encode("station"), Role.STATION,
        LocalDateTime.now(),
        LocalDateTime.now());

    user1 = userRepository.save(user1);
    user2 = userRepository.save(user2);
    userRepository.save(user3);

    //User-House relation
    UserHouseRelation uhRelation2 = new UserHouseRelation(null, user2.getId(), house2.getId());

    userHouseRelationRepository.save(uhRelation2);

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

}

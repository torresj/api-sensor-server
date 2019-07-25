package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.jpa.HouseRepository;
import com.torresj.apisensorserver.jpa.SensorRepository;
import com.torresj.apisensorserver.jpa.SensorTypeRepository;
import com.torresj.apisensorserver.jpa.UserHouseRelationRepository;
import com.torresj.apisensorserver.jpa.UserRepository;
import com.torresj.apisensorserver.jpa.VariableRepository;
import com.torresj.apisensorserver.jpa.VariableSensorRelationRepository;
import com.torresj.apisensorserver.models.House;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.SensorType;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import com.torresj.apisensorserver.services.impl.SensorServiceImpl;
import com.torresj.apisensorserver.utils.TestUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RunWith(MockitoJUnitRunner.class)
public class SensorServiceTest {

  @Mock
  private SensorRepository sensorRepository;
  @Mock
  private VariableRepository variableRepository;
  @Mock
  private VariableSensorRelationRepository variableSensorRelationRepository;
  @Mock
  private SensorTypeRepository sensorTypeRepository;
  @Mock
  private HouseRepository houseRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private UserHouseRelationRepository userHouseRelationRepository;

  private static final int nPage = 0;
  private static final int elements = 20;
  private static final PageRequest pageRequest = PageRequest
      .of(nPage, elements, Sort.by("createAt").descending());


  @InjectMocks
  private SensorService sensorService = new SensorServiceImpl(sensorRepository, variableRepository,
      variableSensorRelationRepository, sensorTypeRepository, houseRepository, userRepository,
      userHouseRelationRepository);

  @Test
  public void getSensors() {
    //Given
    List<Sensor> sensors = new ArrayList<>();
    sensors.add(TestUtils.getExampleSensor(1, 1, 1));
    sensors.add(TestUtils.getExampleSensor(2, 1, 1));
    sensors.add(TestUtils.getExampleSensor(3, 1, 1));

    //When
    when(sensorRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(sensors));
    List<Sensor> sensorsActual = new ArrayList<>(
        sensorService.getSensors(nPage, elements).getContent());

    //then
    assertEquals(sensors, sensorsActual);
  }

  @Test
  public void getSensorsEmptyList() {
    //When
    when(sensorRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));
    Page<Sensor> page = sensorService.getSensors(nPage, elements);

    //then
    assertEquals(true, page.getContent().isEmpty());
  }

  @Test
  public void getSensorsFilterByType() throws EntityNotFoundException {
    //Given
    List<Sensor> sensors = new ArrayList<>();
    SensorType type = TestUtils.getExampleSensorType(1);
    sensors.add(TestUtils.getExampleSensor(1, 1, 1));
    sensors.add(TestUtils.getExampleSensor(2, 1, 1));
    sensors.add(TestUtils.getExampleSensor(3, 1, 1));

    //When
    when(sensorTypeRepository.findById(anyLong())).thenReturn(Optional.of(type));
    when(sensorRepository.findBySensorTypeId(1L, pageRequest))
        .thenReturn(new PageImpl<>(sensors));
    List<Sensor> sensorsActual = new ArrayList<>(
        sensorService.getSensors(nPage, elements, 1L).getContent());

    //then
    assertEquals(sensors, sensorsActual);
  }

  @Test
  public void getSensor() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    Sensor sensorExpected = TestUtils.getExampleSensor(1, 1, 1);

    //When
    when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
    Sensor sensorActual = sensorService.getSensor(1);

    //Then
    assertEquals(sensorExpected.getName(), sensorActual.getName());
    assertEquals(sensorExpected.getId(), sensorActual.getId());
    assertEquals(sensorExpected.getMac(), sensorActual.getMac());
    assertEquals(sensorExpected.getIp(), sensorActual.getIp());

  }

  @Test(expected = EntityNotFoundException.class)
  public void getSensorEntityNotFound() throws EntityNotFoundException {
    //When
    sensorService.getSensor(1);
  }

  @Test
  public void getVariables() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    List<Variable> variables = TestUtils.getExampleVariables(3);
    List<Long> ids = Arrays.asList(1L, 2L, 3L);
    List<VariableSensorRelation> relations = new ArrayList<>();
    relations.add(TestUtils.getExampleVariableRelation(1, 1));
    relations.add(TestUtils.getExampleVariableRelation(2, 1));
    relations.add(TestUtils.getExampleVariableRelation(3, 1));

    //When
    when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
    when(variableSensorRelationRepository.findBySensorId(anyLong())).thenReturn(relations);
    when(variableRepository.findByIdIn(ids, pageRequest)).thenReturn(new PageImpl<>(variables));
    List<Variable> listActual = sensorService.getVariables(1, 0, 20).getContent();

    //Then
    assertEquals(variables, listActual);
  }

  @Test
  public void addVariable() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    Variable variable = TestUtils.getExampleVariable(1);
    Variable variableExpected = TestUtils.getExampleVariable(1);

    //When
    when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
    when(variableRepository.findById(anyLong())).thenReturn(Optional.of(variable));
    Variable variableActual = sensorService.addVariable(1, 1);

    //Then
    assertEquals(variableExpected.getName(), variableActual.getName());
    assertEquals(variableExpected.getId(), variableActual.getId());
    assertEquals(variableExpected.getDescription(), variableActual.getDescription());
    assertEquals(variableExpected.getUnits(), variableActual.getUnits());

  }

  @Test
  public void update() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    Sensor sensorExpected = TestUtils.getExampleSensor(1, 1, 1);
    SensorType type = TestUtils.getExampleSensorType(1);
    House house = TestUtils.getExampleHouse(1);

    //When
    when(sensorRepository.findByMac(anyString())).thenReturn(Optional.of(sensor));
    when(sensorTypeRepository.findById(anyLong())).thenReturn(Optional.of(type));
    when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
    when(sensorRepository.save(sensor)).thenReturn(sensor);
    Sensor sensorActual = sensorService.update(sensor);

    //Then
    assertEquals(sensorExpected.getName(), sensorActual.getName());
    assertEquals(sensorExpected.getId(), sensorActual.getId());
    assertEquals(sensorExpected.getMac(), sensorActual.getMac());
    assertEquals(sensorExpected.getIp(), sensorActual.getIp());
  }

  @Test
  public void register() throws EntityNotFoundException, EntityAlreadyExists {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    Sensor sensorExpected = TestUtils.getExampleSensor(1, 1, 1);
    SensorType type = TestUtils.getExampleSensorType(1);
    House house = TestUtils.getExampleHouse(1);

    //When
    when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
    when(sensorTypeRepository.findById(anyLong())).thenReturn(Optional.of(type));
    when(sensorRepository.save(any())).thenReturn(sensor);
    Sensor sensorActual = sensorService.register(sensor);

    //Then
    assertEquals(sensorExpected.getName(), sensorActual.getName());
    assertEquals(sensorExpected.getId(), sensorActual.getId());
    assertEquals(sensorExpected.getMac(), sensorActual.getMac());
    assertEquals(sensorExpected.getIp(), sensorActual.getIp());
  }

  @Test
  public void removeSensor() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);

    //When
    when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
    sensorService.removeSensor(1);

  }

  @Test
  public void removeVariable() throws EntityNotFoundException {
    //Given
    Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);
    Variable variable = TestUtils.getExampleVariable(1);
    VariableSensorRelation relation = TestUtils.getExampleVariableRelation(1, 1);

    //When
    when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
    when(variableRepository.findById(anyLong())).thenReturn(Optional.of(variable));
    when(variableSensorRelationRepository.findBySensorIdAndVariableId(1l, 1l))
        .thenReturn(Optional.of(relation));
    sensorService.removeVariable(1, 1);
  }
}
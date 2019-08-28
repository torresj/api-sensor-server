package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityHasRelationsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.SensorType;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.SensorTypeRepository;
import com.torresj.apisensorserver.services.impl.SensorTypeServiceImpl;
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
public class SensorTypeServiceTest {

  @Mock
  private SensorTypeRepository sensorTypeRepository;

  @Mock
  private SensorRepository sensorRepository;

  private static final int nPage = 0;
  private static final int elements = 20;
  private static final PageRequest pageRequest = PageRequest
      .of(nPage, elements, Sort.by("createAt").descending());

  @InjectMocks
  private SensorTypeService sensorTypeService = new SensorTypeServiceImpl(sensorTypeRepository,
      sensorRepository);

  @Test
  public void getSensorTypes() {
    //Given
    List<SensorType> sensorsType = new ArrayList<>(Arrays
        .asList(TestUtils.getExampleSensorType(1), TestUtils.getExampleSensorType(2),
            TestUtils.getExampleSensorType(3)));

    //When
    when(sensorTypeRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(sensorsType));
    List<SensorType> variableActual = new ArrayList<>(
        sensorTypeService.getSensorTypes(nPage, elements).getContent());

    //then
    assertEquals(sensorsType, variableActual);
  }

  @Test
  public void getSensorTypeEmptyList() {
    //When
    when(sensorTypeRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));
    Page<SensorType> page = sensorTypeService.getSensorTypes(nPage, elements);

    //then
    assertEquals(true, page.getContent().isEmpty());
  }

  @Test
  public void getSensorType() throws EntityNotFoundException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    SensorType sensorTypeExpected = TestUtils.getExampleSensorType(1);

    //When
    when(sensorTypeRepository.findById(anyLong())).thenReturn(Optional.of(sensorType));
    SensorType sensorTypeActual = sensorTypeService.getSensorType(1);

    //Then
    assertEquals(sensorTypeExpected.getName(), sensorTypeActual.getName());
    assertEquals(sensorTypeExpected.getId(), sensorTypeActual.getId());
    assertEquals(sensorTypeExpected.getDescription(), sensorTypeActual.getDescription());
    assertEquals(sensorTypeExpected.getActions(), sensorTypeActual.getActions());
  }

  @Test(expected = EntityNotFoundException.class)
  public void getSensorTypeEntityNotFound() throws EntityNotFoundException {
    //When
    sensorTypeService.getSensorType(1);
  }

  @Test
  public void register() throws EntityAlreadyExistsException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    SensorType sensorTypeExpected = TestUtils.getExampleSensorType(1);

    //When
    when(sensorTypeRepository.save(sensorType)).thenReturn(sensorType);
    SensorType sensorTypeActual = sensorTypeService.register(sensorType);

    //Then
    assertEquals(sensorTypeExpected.getName(), sensorTypeActual.getName());
    assertEquals(sensorTypeExpected.getId(), sensorTypeActual.getId());
    assertEquals(sensorTypeExpected.getDescription(), sensorTypeActual.getDescription());
    assertEquals(sensorTypeExpected.getActions(), sensorTypeActual.getActions());
  }

  @Test(expected = EntityAlreadyExistsException.class)
  public void registerEntityAlreadyExists() throws EntityAlreadyExistsException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    SensorType sensorTypeExpected = TestUtils.getExampleSensorType(1);

    //When
    when(sensorTypeRepository.findByName(anyString())).thenReturn(Optional.of(sensorType));
    sensorTypeService.register(sensorType);

  }

  @Test
  public void update() throws EntityNotFoundException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    SensorType sensorTypeExpected = TestUtils.getExampleSensorType(1);

    //When
    when(sensorTypeRepository.findByName(anyString())).thenReturn(Optional.of(sensorType));
    when(sensorTypeRepository.save(sensorType)).thenReturn(sensorType);
    SensorType sensorTypeActual = sensorTypeService.update(sensorType);

    //Then
    assertEquals(sensorTypeExpected.getName(), sensorTypeActual.getName());
    assertEquals(sensorTypeExpected.getId(), sensorTypeActual.getId());
    assertEquals(sensorTypeExpected.getDescription(), sensorTypeActual.getDescription());
    assertEquals(sensorTypeExpected.getActions(), sensorTypeActual.getActions());
  }

  @Test(expected = EntityNotFoundException.class)
  public void updateEntityNotFound() throws EntityNotFoundException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    TestUtils.getExampleSensorType(1);

    //When
    sensorTypeService.update(sensorType);
  }

  @Test
  public void remove() throws EntityHasRelationsException, EntityNotFoundException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    SensorType sensorTypeExpected = TestUtils.getExampleSensorType(1);
    List<Sensor> sensors = new ArrayList<>();
    sensors.add(TestUtils.getExampleSensor(1, 1, 1));
    sensors.add(TestUtils.getExampleSensor(2, 1, 1));
    sensors.add(TestUtils.getExampleSensor(3, 1, 1));

    //When
    when(sensorTypeRepository.findById(1l)).thenReturn(Optional.of(sensorType));
    SensorType sensorTypeActual = sensorTypeService.remove(1);

    //Then
    assertEquals(sensorTypeExpected.getName(), sensorTypeActual.getName());
    assertEquals(sensorTypeExpected.getId(), sensorTypeActual.getId());
    assertEquals(sensorTypeExpected.getDescription(), sensorTypeActual.getDescription());
    assertEquals(sensorTypeExpected.getActions(), sensorTypeActual.getActions());
  }

  @Test(expected = EntityNotFoundException.class)
  public void removeEntityNotFound() throws EntityHasRelationsException, EntityNotFoundException {

    //When
    sensorTypeService.remove(1);
  }

  @Test(expected = EntityHasRelationsException.class)
  public void removeEntityHasRelation()
      throws EntityHasRelationsException, EntityNotFoundException {
    //Given
    SensorType sensorType = TestUtils.getExampleSensorType(1);
    List<Sensor> sensors = new ArrayList<>();
    sensors.add(TestUtils.getExampleSensor(1, 1, 1));
    sensors.add(TestUtils.getExampleSensor(2, 1, 1));
    sensors.add(TestUtils.getExampleSensor(3, 1, 1));

    //When
    when(sensorTypeRepository.findById(1l)).thenReturn(Optional.of(sensorType));
    when(sensorRepository.findBySensorTypeId(1l, PageRequest.of(0, 1)))
        .thenReturn(new PageImpl<>(sensors));
    sensorTypeService.remove(1);

  }
}
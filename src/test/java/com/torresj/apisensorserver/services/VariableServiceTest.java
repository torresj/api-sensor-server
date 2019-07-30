package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExists;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.Sensor;
import com.torresj.apisensorserver.models.Variable;
import com.torresj.apisensorserver.models.VariableSensorRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.repositories.VariableSensorRelationRepository;
import com.torresj.apisensorserver.services.impl.VariableServiceImpl;
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
public class VariableServiceTest {

  @Mock
  private VariableRepository variableRepository;
  @Mock
  private SensorRepository sensorRepository;
  @Mock
  private VariableSensorRelationRepository variableSensorRelationRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private HouseRepository houseRepository;
  @Mock
  private UserHouseRelationRepository userHouseRelationRepository;

  private static final int nPage = 0;
  private static final int elements = 20;
  private static final PageRequest pageRequest = PageRequest
      .of(nPage, elements, Sort.by("createAt").descending());

  @InjectMocks
  private VariableService variableService = new VariableServiceImpl(variableRepository,
      sensorRepository, userRepository, variableSensorRelationRepository, houseRepository,
      userHouseRelationRepository);


  @Test
  public void getVariables() {
    //Given
    List<Variable> variables = TestUtils.getExampleVariables(3);

    //When
    when(variableRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(variables));
    List<Variable> variableActual = new ArrayList<>(
        variableService.getVariables(nPage, elements).getContent());

    //then
    assertEquals(variables, variableActual);
  }

  @Test
  public void getVariablesEmptyList() {
    //When
    when(variableRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(new ArrayList<>()));
    Page<Variable> page = variableService.getVariables(nPage, elements);

    //then
    assertEquals(true, page.getContent().isEmpty());
  }

  @Test
  public void getVariable() throws EntityNotFoundException {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);
    Variable variableExpected = TestUtils.getExampleVariable(1);

    //When
    when(variableRepository.findById(anyLong())).thenReturn(Optional.of(variable));
    Variable variableActual = variableService.getVariable(1);

    //Then
    assertEquals(variableExpected.getName(), variableActual.getName());
    assertEquals(variableExpected.getId(), variableActual.getId());
    assertEquals(variableExpected.getDescription(), variableActual.getDescription());
    assertEquals(variableExpected.getUnits(), variableActual.getUnits());
  }

  @Test(expected = EntityNotFoundException.class)
  public void getVariableEntityNotFound() throws EntityNotFoundException {
    //When
    variableService.getVariable(1);
  }

  @Test
  public void update() throws EntityNotFoundException {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);
    Variable variableExpected = TestUtils.getExampleVariable(1);

    //When
    when(variableRepository.findByName(variable.getName())).thenReturn(Optional.of(variable));
    when(variableRepository.save(variable)).thenReturn(variable);
    Variable variableActual = variableService.update(variable);

    //Then
    assertEquals(variableExpected.getName(), variableActual.getName());
    assertEquals(variableExpected.getId(), variableActual.getId());
    assertEquals(variableExpected.getDescription(), variableActual.getDescription());
    assertEquals(variableExpected.getUnits(), variableActual.getUnits());
  }

  @Test(expected = EntityNotFoundException.class)
  public void updateEntityNotFound() throws EntityNotFoundException {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);

    //When
    variableService.update(variable);
  }

  @Test
  public void register() throws EntityAlreadyExists {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);
    Variable variableExpected = TestUtils.getExampleVariable(1);

    //When
    when(variableRepository.save(variable)).thenReturn(variable);
    Variable variableActual = variableService.register(variable);

    //Then
    assertEquals(variableExpected.getName(), variableActual.getName());
    assertEquals(variableExpected.getId(), variableActual.getId());
    assertEquals(variableExpected.getDescription(), variableActual.getDescription());
    assertEquals(variableExpected.getUnits(), variableActual.getUnits());
  }

  @Test(expected = EntityAlreadyExists.class)
  public void registerEntityAlreadyExists() throws EntityAlreadyExists {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);

    //When
    when(variableRepository.findByName(anyString())).thenReturn(Optional.of(variable));
    variableService.register(variable);

  }

  @Test
  public void deleteVariable() throws EntityNotFoundException {
    //Given
    Variable variable = TestUtils.getExampleVariable(1);
    Variable variableExpected = TestUtils.getExampleVariable(1);

    //When
    when(variableRepository.findById(anyLong())).thenReturn(Optional.of(variable));
    Variable variableActual = variableService.deleteVariable(1);

    //Then
    assertEquals(variableExpected.getName(), variableActual.getName());
    assertEquals(variableExpected.getId(), variableActual.getId());
    assertEquals(variableExpected.getDescription(), variableActual.getDescription());
    assertEquals(variableExpected.getUnits(), variableActual.getUnits());

  }

  @Test(expected = EntityNotFoundException.class)
  public void deleteVariableEntityNotFound() throws EntityNotFoundException {
    //When
    variableService.deleteVariable(1);
  }

  @Test
  public void getSensors() {
    //Given
    List<VariableSensorRelation> relations = new ArrayList<>();
    relations.add(TestUtils.getExampleVariableRelation(1, 1));
    relations.add(TestUtils.getExampleVariableRelation(1, 2));
    relations.add(TestUtils.getExampleVariableRelation(1, 3));
    List<Sensor> sensors = new ArrayList<>();
    sensors.add(TestUtils.getExampleSensor(1, 1, 1));
    sensors.add(TestUtils.getExampleSensor(2, 1, 1));
    sensors.add(TestUtils.getExampleSensor(3, 1, 1));

    //When
    when(variableSensorRelationRepository.findByVariableId(1l)).thenReturn(relations);
    when(sensorRepository.findByIdIn(new ArrayList<>(Arrays.asList(1l, 2l, 3l)), pageRequest))
        .thenReturn(new PageImpl<>(sensors));
    List<Sensor> result = new ArrayList<>(
        variableService.getSensors(1, nPage, elements).getContent());

    //Then
    assertEquals(3, result.size());
  }
}
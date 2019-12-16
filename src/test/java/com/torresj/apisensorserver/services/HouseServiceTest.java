package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.services.impl.HouseServiceImpl;
import com.torresj.apisensorserver.utils.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@RunWith(MockitoJUnitRunner.class)
public class HouseServiceTest {

    private static final int nPage = 0;

    private static final int elements = 20;

    private static final PageRequest pageRequest = PageRequest
            .of(nPage, elements, Sort.by("createAt").descending());

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private HouseRepository houseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHouseRelationRepository userHouseRelationRepository;

    @InjectMocks
    private HouseService houseService = new HouseServiceImpl(houseRepository, sensorRepository,
            userRepository, userHouseRelationRepository);

    @Test
    public void getHouses() {
        //Given
        List<House> houses = TestUtils.getExampleHouses(3);

        //When
        when(houseRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(houses));
        List<House> houseActual = new ArrayList<>(
                houseService.getHouses(null, nPage, elements).getContent());

        //then
        assertEquals(houses, houseActual);
    }

    @Test
    public void getHousesAll() {
        //Given
        List<House> houses = TestUtils.getExampleHouses(3);

        //When
        when(houseRepository.findAll()).thenReturn(houses);
        List<House> houseActual = houseService.getHouses();

        //then
        assertEquals(houses, houseActual);
    }

    @Test
    public void getHouse() throws EntityNotFoundException {
        //Given
        House house = TestUtils.getExampleHouse(1);
        House houseExpected = TestUtils.getExampleHouse(1);

        //When
        when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
        House houseActual = houseService.getHouse(1);

        //Then
        assertEquals(houseExpected.getId(), houseActual.getId());
        assertEquals(houseExpected.getName(), houseActual.getName());
    }

    @Test
    public void getSensors() throws EntityNotFoundException {
        //Given
        House house = TestUtils.getExampleHouse(1);
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(TestUtils.getExampleSensor(1, 1, 1));
        sensors.add(TestUtils.getExampleSensor(2, 1, 1));
        sensors.add(TestUtils.getExampleSensor(3, 1, 1));

        //When
        when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
        when(sensorRepository.findByHouseId(1L))
                .thenReturn(sensors);
        List<Sensor> sensorsactual = houseService.getSensors(1);

        //Then
        assertEquals(sensors, sensorsactual);
    }

    @Test
    public void update() throws EntityNotFoundException {
        //Given
        House house = TestUtils.getExampleHouse(1);

        //When
        when(houseRepository.findByName(anyString())).thenReturn(Optional.of(house));
        when(houseRepository.save(any())).thenReturn(house);
        House houseActual = houseService.update(house);

        //then
        assertEquals(house, houseActual);
    }

    @Test
    public void register() throws EntityAlreadyExistsException {
        //Given
        House house = TestUtils.getExampleHouse(1);

        //When
        when(houseRepository.save(any())).thenReturn(house);
        House houseActual = houseService.register(house);

        //then
        assertEquals(house, houseActual);
    }

    @Test
    public void removeHouse() throws EntityNotFoundException {
        //Given
        House house = TestUtils.getExampleHouse(1);
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(TestUtils.getExampleSensor(1, 1, 1));
        sensors.add(TestUtils.getExampleSensor(2, 1, 1));
        sensors.add(TestUtils.getExampleSensor(3, 1, 1));

        //When
        when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
        when(sensorRepository.findByHouseId(anyLong())).thenReturn(sensors);
        House houseActual = houseService.removeHouse(1);

        //Then
        sensors.stream().forEach(sensor -> assertEquals(null, sensor.getHouseId()));

    }

    @Test
    public void updateSensors() throws EntityNotFoundException {
        //Given
        House house = TestUtils.getExampleHouse(1);
        List<Sensor> sensors = new ArrayList<>();
        sensors.add(TestUtils.getExampleSensor(1, 1, 1));
        sensors.add(TestUtils.getExampleSensor(2, 1, 1));
        sensors.add(TestUtils.getExampleSensor(3, 1, 1));
        Sensor sensor = TestUtils.getExampleSensor(4, 3, 1);

        //When
        when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
        when(sensorRepository.findById(2l)).thenReturn(Optional.of(sensors.get(1)));
        when(sensorRepository.findById(3l)).thenReturn(Optional.of(sensors.get(2)));
        when(sensorRepository.findById(4l)).thenReturn(Optional.of(sensor));
        List<Sensor> finalSensors = houseService.updateSensors(1, Arrays.asList(2l,3l,4l));

        //Then
        assertEquals(3, finalSensors.size());
        assertEquals(true, finalSensors.contains(sensor));
        assertEquals(false, finalSensors.contains(sensors.get(0)));

    }
}
package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.Record;
import com.torresj.apisensorserver.models.entities.Sensor;
import com.torresj.apisensorserver.models.entities.Variable;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.RecordRepository;
import com.torresj.apisensorserver.repositories.SensorRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.repositories.VariableRepository;
import com.torresj.apisensorserver.services.impl.RecordServiceImpl;
import com.torresj.apisensorserver.utils.TestUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@RunWith(MockitoJUnitRunner.class)
public class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private VariableRepository variableRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHouseRelationRepository userHouseRelationRepository;

    @Mock
    private HouseRepository houseRepository;

    @Mock
    private SimpMessagingTemplate template;

    @InjectMocks
    private RecordService recordService = new RecordServiceImpl(recordRepository, variableRepository,
            sensorRepository, userRepository, userHouseRelationRepository, houseRepository, template);

    private static final int nPage = 0;

    private static final int elements = 20;

    private static final PageRequest pageRequest = PageRequest
            .of(nPage, elements, Sort.by("createAt").descending());

    @Test
    public void register() throws EntityNotFoundException {
        //Given
        Record record = TestUtils.getExampleRecord(1, 1);
        Variable variable = TestUtils.getExampleVariable(1);
        Sensor sensor = TestUtils.getExampleSensor(1, 1, 1);

        //When
        when(variableRepository.findById(anyLong())).thenReturn(Optional.of(variable));
        when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
        when(recordRepository.save(record)).thenReturn(record);
        Record recordActual = recordService.register(record);

        //Then
        assertNotEquals(null, recordActual);
    }

    @Test(expected = EntityNotFoundException.class)
    public void registerEntityNotFound() throws EntityNotFoundException {
        //Given
        Record record = TestUtils.getExampleRecord(1, 1);

        //When
        recordService.register(record);

    }

    @Test
    public void getRecords() {
        //Given
        List<Record> records = TestUtils.getExampleRecords(10, 1, 1);
        LocalDate date = LocalDate.of(2018, Month.JANUARY, 1);

        //When
        when(recordRepository.findBySensorIdAndVariableIdAndCreateAtBetween(1, 1,
                date.atStartOfDay(), date.atTime(23, 59), pageRequest)).thenReturn(new PageImpl<>(records));
        List<Record> recordsActual = recordService
                .getRecords(1, 1, 0, 20, date, date).getContent();

        //Then
        assertEquals(10, recordsActual.size());
    }

    @Test
    public void getRecord() throws EntityNotFoundException {
        //Given
        Record record = TestUtils.getExampleRecord(1, 1);

        //When
        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(record));
        Record recordActual = recordService.getRecord(1);

        //Then
        assertEquals(record.getId(), recordActual.getId());
        assertEquals(record.getSensorId(), recordActual.getSensorId());
        assertEquals(record.getVariableId(), recordActual.getVariableId());
    }
}
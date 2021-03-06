package com.torresj.apisensorserver.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.torresj.apisensorserver.exceptions.EntityAlreadyExistsException;
import com.torresj.apisensorserver.exceptions.EntityNotFoundException;
import com.torresj.apisensorserver.models.entities.House;
import com.torresj.apisensorserver.models.entities.User;
import com.torresj.apisensorserver.models.entities.User.Role;
import com.torresj.apisensorserver.models.entities.UserHouseRelation;
import com.torresj.apisensorserver.repositories.HouseRepository;
import com.torresj.apisensorserver.repositories.UserHouseRelationRepository;
import com.torresj.apisensorserver.repositories.UserRepository;
import com.torresj.apisensorserver.services.impl.UserServiceImpl;
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
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHouseRelationRepository userHouseRelationRepository;

    @Mock
    private HouseRepository houseRepository;

    private static final int nPage = 0;

    private static final int elements = 20;

    private static final PageRequest pageRequest = PageRequest
            .of(nPage, elements, Sort.by("createAt").descending());

    @InjectMocks
    private UserService userService = new UserServiceImpl(userRepository, userHouseRelationRepository,
            houseRepository);

    @Test
    public void getUsers() {
        //Given
        List<User> users = new ArrayList<>();
        users.add(TestUtils.getExampleUser("test1", "test1", Role.USER));
        users.add(TestUtils.getExampleUser("test2", "test1", Role.USER));
        users.add(TestUtils.getExampleUser("test2", "test1", Role.USER));

        //When
        when(userRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(users));
        List<User> usersActual = userService.getUsers(null, null, 0, 20).getContent();

        //Then
        assertEquals(users, usersActual);
    }

    @Test
    public void getUser() throws EntityNotFoundException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        User userActual = userService.getUser(1);

        //Then
        assertEquals(user, userActual);
    }

    @Test
    public void getUserByName() throws EntityNotFoundException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //When
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        User userActual = userService.getUser("");

        //Then
        assertEquals(user, userActual);
    }

    @Test
    public void register() throws EntityAlreadyExistsException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //Given
        when(userRepository.save(user)).thenReturn(user);
        User userActual = userService.register(user);

        //Then
        assertEquals(user, userActual);
    }

    @Test(expected = EntityAlreadyExistsException.class)
    public void registerEntityAlreadyExists() throws EntityAlreadyExistsException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        userService.register(user);
    }

    @Test
    public void getHouses() throws EntityNotFoundException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);
        List<House> houses = TestUtils.getExampleHouses(3);
        List<Long> ids = Arrays.asList(1L, 2L, 3L);
        List<UserHouseRelation> relations = new ArrayList<>();
        relations.add(TestUtils.getExampleUserHouseRelation(1, 1));
        relations.add(TestUtils.getExampleUserHouseRelation(1, 2));
        relations.add(TestUtils.getExampleUserHouseRelation(1, 3));

        //When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userHouseRelationRepository.findByUserId(anyLong())).thenReturn(relations);
        when(houseRepository.findByIdIn(ids)).thenReturn(houses);
        List<House> listActual = userService.getHouses(1);

        //Then
        assertEquals(houses, listActual);
    }

    @Test
    public void addHouse() throws EntityNotFoundException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);
        House house = TestUtils.getExampleHouse(1);

        //When
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(houseRepository.findById(anyLong())).thenReturn(Optional.of(house));
        House houseActual = userService.addHouse(1, 1);

        //Then
        assertEquals(house, houseActual);

    }

    @Test
    public void update() throws EntityNotFoundException {
        //Given
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //Given
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        User userActual = userService.update(user);

        //Then
        assertEquals(user, userActual);
    }

    @Test
    public void setHouses() throws EntityNotFoundException {
        //Given
        List<Long> ids = Arrays.asList(1l,2l,3l);
        House house1 = TestUtils.getExampleHouse(1l);
        House house2 = TestUtils.getExampleHouse(2l);
        House house3 = TestUtils.getExampleHouse(3l);
        User user = TestUtils.getExampleUser("test1", "test1", Role.USER);

        //when
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(houseRepository.findById(1l)).thenReturn(Optional.of(house1));
        when(houseRepository.findById(2l)).thenReturn(Optional.of(house2));
        when(houseRepository.findById(3l)).thenReturn(Optional.of(house3));

        List<House> houses = userService.setHouses(user.getId(),ids);
        List<Long> idsActuals = houses.stream().map(house -> house.getId()).collect(Collectors.toList());

        assertEquals(idsActuals,ids);
    }
}
package com.hitachi.digital.drone.service;

import com.hitachi.digital.drone.dto.request.LoadMedicationRequest;
import com.hitachi.digital.drone.dto.request.RegisterDroneRequest;
import com.hitachi.digital.drone.dto.response.AvailableDrone;
import com.hitachi.digital.drone.dto.response.GetDroneInfoResponse;
import com.hitachi.digital.drone.dto.response.LoadedMedicationResponse;
import com.hitachi.digital.drone.entity.Drone;
import com.hitachi.digital.drone.entity.Medication;
import com.hitachi.digital.drone.enumeration.StateEnum;
import com.hitachi.digital.drone.repository.DroneRepository;
import com.hitachi.digital.drone.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class DroneServiceTest {


    @InjectMocks
    private DroneService droneService;

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    @Test
    void test_registerDrone() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:202242324");
        drone.setModel("Lightweight");
        drone.setState(StateEnum.IDLE.name());
        drone.setWeightLimit(1000);

        RegisterDroneRequest request = new RegisterDroneRequest();
        request.setModel("Lightweight");
        request.setSerialNumber("SN12000023");
        request.setWeightLimit(1000);

        String response = droneService.registerDrone(request);

        assertEquals("Drone Registered Successfully", response);

        Mockito.verify(droneRepository, times(1)).save(any());
    }

    @Test
    void test_getAvailableDrones() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:202242324");
        drone.setModel("Lightweight");
        drone.setState(StateEnum.IDLE.name());
        drone.setWeightLimit(1000);

        Medication medication = new Medication();
        medication.setDrone(drone);
        medication.setCode("8012");
        medication.setId(UUID.randomUUID());
        medication.setName("Tempra");
        medication.setWeight(100);
        medication.setImageUrl("https://google.com/tempra");

        Mockito.when(droneRepository.findByStateIn(anyList())).thenReturn(List.of(drone));
        Mockito.when(medicationRepository.findByDrone(any())).thenReturn(List.of(medication));

        List<AvailableDrone> response = droneService.getAvailableDrones();

        assertEquals(response.get(0).getModel(), drone.getModel());
        assertEquals(response.get(0).getSerialNumber(), drone.getSerialNumber());
        assertEquals(response.get(0).getState(), drone.getState());
    }

    @Test
    void test_getDroneInformation() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:203442324");
        drone.setModel("Cruiserweight");
        drone.setState(StateEnum.IDLE.name());
        drone.setWeightLimit(1000);

        Mockito.when(droneRepository.findById(any())).thenReturn(Optional.of(drone));

        GetDroneInfoResponse response = droneService.getDroneInformation(drone.getId());

        assertEquals(response.getModel(), drone.getModel());
        assertEquals(response.getBatteryCapacity(), drone.getBatteryCapacity());
        assertEquals(response.getState(), drone.getState());
        assertEquals(response.getSerialNumber(), drone.getSerialNumber());
    }

    @Test
    void test_loadDroneMedication() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:203442324");
        drone.setModel("Cruiserweight");
        drone.setState(StateEnum.IDLE.name());
        drone.setBatteryCapacity(100D);
        drone.setWeightLimit(1000);

        Mockito.when(droneRepository.findById(any())).thenReturn(Optional.of(drone));

        Medication medication = new Medication();
        medication.setDrone(drone);
        medication.setCode("8012");
        medication.setId(UUID.randomUUID());
        medication.setName("Tempra");
        medication.setWeight(100);
        medication.setImageUrl("https://google.com/tempra");

        Mockito.when(medicationRepository.findByDrone(any())).thenReturn(List.of(medication));

        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setName("Biogesic");
        request.setCode("8007");
        request.setWeight(200);
        request.setImageUrl("https://google.com");

        String response = droneService.loadDroneMedication(request, drone.getId());

        assertEquals("Drone Medication Loaded Successfully", response);
    }

    @Test
    void test_getLoadedMedications() {
        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:203442324");
        drone.setModel("Cruiserweight");
        drone.setState(StateEnum.IDLE.name());
        drone.setBatteryCapacity(100D);
        drone.setWeightLimit(1000);

        Mockito.when(droneRepository.findById(any())).thenReturn(Optional.of(drone));

        Medication medication = new Medication();
        medication.setDrone(drone);
        medication.setCode("8012");
        medication.setId(UUID.randomUUID());
        medication.setName("Tempra");
        medication.setWeight(100);
        medication.setImageUrl("https://google.com/tempra");

        Mockito.when(medicationRepository.findByDrone(any())).thenReturn(List.of(medication));

        List<LoadedMedicationResponse> response = droneService.getLoadedMedications(drone.getId());

        assertEquals(response.get(0).getCode(), medication.getCode());
        assertEquals(response.get(0).getWeight(), medication.getWeight());
        assertEquals(response.get(0).getName(), medication.getName());
        assertEquals(response.get(0).getImageUrl(), medication.getImageUrl());
    }
}

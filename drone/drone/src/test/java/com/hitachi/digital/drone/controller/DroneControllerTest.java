package com.hitachi.digital.drone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitachi.digital.drone.dto.request.LoadMedicationRequest;
import com.hitachi.digital.drone.dto.request.RegisterDroneRequest;
import com.hitachi.digital.drone.dto.response.GetDroneInfoResponse;
import com.hitachi.digital.drone.entity.Drone;
import com.hitachi.digital.drone.enumeration.StateEnum;
import com.hitachi.digital.drone.repository.DroneRepository;
import com.hitachi.digital.drone.repository.MedicationRepository;
import com.hitachi.digital.drone.service.DroneService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.hitachi.digital.drone.constant.DroneConstants.API_BASE_PATH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DroneController.class)
class DroneControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DroneService droneService;

    @Mock
    private DroneRepository droneRepository;

    @Mock
    private MedicationRepository medicationRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void test_getDroneInformations() throws Exception {
        UUID droneId = UUID.fromString("695fda8e-df3c-4dfa-9e60-48766356a69e");

        GetDroneInfoResponse response = new GetDroneInfoResponse();
        response.setSerialNumber("SN:202242324");
        response.setModel("Lightweight");
        response.setBatteryCapacity(75D);
        response.setState("IDLE");
        response.setWeightLimit(200);

        Mockito.when(droneService.getDroneInformation(eq(droneId))).thenReturn(response);

        String endpoint = API_BASE_PATH + "/drone/info/" + droneId;

        mockMvc.perform(get(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value("SN:202242324"));
    }

    @Test
    void test_getAvailableDrones() throws Exception {
       Drone drone = new Drone();
       drone.setId(UUID.randomUUID());
       drone.setSerialNumber("SN:202242324");
       drone.setModel("Lightweight");
       drone.setState(StateEnum.IDLE.name());

       Mockito.when(droneRepository.findAll()).thenReturn(List.of(drone));

       String endpoint = API_BASE_PATH + "/drone/available";
       mockMvc.perform(get(endpoint)
               .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    void test_registerDrone() throws Exception {

        RegisterDroneRequest request = new RegisterDroneRequest();
        request.setModel("Lightweight");
        request.setSerialNumber("SN12000023");
        request.setWeightLimit(1000);

        String endpoint = API_BASE_PATH + "/drone";
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }

    @Test
    void test_loadDroneMedication() throws Exception {

        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:20253432");
        drone.setModel("Heavyweight");
        drone.setState(StateEnum.LOADING.name());

        LoadMedicationRequest request = new LoadMedicationRequest();
        request.setName("Biogesic");
        request.setCode("8007");
        request.setWeight(200);
        request.setImageUrl("https://google.com");

        Mockito.when(droneRepository.findById(any())).thenReturn(Optional.of(drone));

        String endpoint = API_BASE_PATH + "/drone/medication/" + drone.getId();
        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }


    @Test
    void test_getLoadedMedications() throws Exception {

        Drone drone = new Drone();
        drone.setId(UUID.randomUUID());
        drone.setSerialNumber("SN:20343432");
        drone.setModel("Heavyweight");
        drone.setState(StateEnum.LOADING.name());

        RegisterDroneRequest request = new RegisterDroneRequest();
        request.setModel("Lightweight");
        request.setSerialNumber("SN12000023");
        request.setWeightLimit(1000);

        String endpoint = API_BASE_PATH + "/drone/medications/" +drone.getId();

        mockMvc.perform(get(endpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());;
    }
}

package com.hitachi.digital.drone.controller;

import com.hitachi.digital.drone.dto.request.LoadMedicationRequest;
import com.hitachi.digital.drone.dto.request.RegisterDroneRequest;
import com.hitachi.digital.drone.dto.response.AvailableDrone;
import com.hitachi.digital.drone.dto.response.GetDroneInfoResponse;
import com.hitachi.digital.drone.dto.response.LoadedMedicationResponse;
import com.hitachi.digital.drone.service.DroneService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static com.hitachi.digital.drone.constant.DroneConstants.API_BASE_PATH;

@RestController
@RequestMapping(
        path = API_BASE_PATH
)
public class DroneController {

    private final DroneService droneService;

    @Autowired
    public DroneController(DroneService droneService) {
        this.droneService = droneService;
    }

    /**
     * Method used for registering a drone
     *
     * @param registerDroneRequest object containing new drone information.
     *
     * @return {@link String} returns successful registration.
     */
    @PostMapping("/drone")
    public ResponseEntity<String> registerDrone(@RequestBody @Valid RegisterDroneRequest registerDroneRequest) {
        return new ResponseEntity<>(droneService.registerDrone(registerDroneRequest), HttpStatus.CREATED);
    }

    /**
     * Method used to check drone availability for medication loading.
     *
     * @return {@link List<AvailableDrone>} returns list of available drone that can be loaded with medications.
     */
    @GetMapping("/drone/available")
    public ResponseEntity<List<AvailableDrone>> getAvailableDrones() {
        return new ResponseEntity<List<AvailableDrone>>(droneService.getAvailableDrones(), HttpStatus.OK);
    }

    /**
     * Method used for checking drone information.
     *
     * @param droneId drone id used to find a specific drone to get information.
     *
     * @return {@link GetDroneInfoResponse} returns drone information.
     */
    @GetMapping("/drone/info/{droneId}")
    public ResponseEntity<GetDroneInfoResponse> getDroneInformation(@PathVariable UUID droneId) {
        return new ResponseEntity<GetDroneInfoResponse>(droneService.getDroneInformation(droneId), HttpStatus.OK);
    }

    /**
     * Method used to add/load medication on the drone for delivery.
     *
     * @param loadMedicationRequest object containing medication details.
     *
     * @param droneId drone id to find what drone to be used.
     *
     * @return {@link String} return successful load medication.
     */
    @PostMapping("/drone/medication/{droneId}")
    public ResponseEntity<String> loadDroneMedications(@RequestBody @Valid LoadMedicationRequest loadMedicationRequest,
                                                       @PathVariable UUID droneId) {
        return new ResponseEntity<>(droneService.loadDroneMedication(loadMedicationRequest, droneId),
                HttpStatus.CREATED);
    }

    /**
     * Method used to check current medications loaded on a given drone.
     *
     * @param droneId drone id to find what drone to be checked.
     * @return
     */
    @GetMapping("/drone/medications/{droneId}")
    public ResponseEntity<List<LoadedMedicationResponse>> getLoadedMedications(@PathVariable UUID droneId) {
        return new ResponseEntity<List<LoadedMedicationResponse>>(droneService.getLoadedMedications(droneId),
                HttpStatus.OK);
    }
}

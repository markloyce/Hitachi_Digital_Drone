package com.hitachi.digital.drone.service;

import com.hitachi.digital.drone.dto.request.LoadMedicationRequest;
import com.hitachi.digital.drone.dto.request.RegisterDroneRequest;
import com.hitachi.digital.drone.dto.response.AvailableDrone;
import com.hitachi.digital.drone.dto.response.GetDroneInfoResponse;
import com.hitachi.digital.drone.dto.response.LoadedMedicationResponse;
import com.hitachi.digital.drone.entity.Drone;
import com.hitachi.digital.drone.entity.Medication;
import com.hitachi.digital.drone.enumeration.StateEnum;
import com.hitachi.digital.drone.exception.DataNotFoundException;
import com.hitachi.digital.drone.exception.ValidationException;
import com.hitachi.digital.drone.repository.DroneRepository;
import com.hitachi.digital.drone.repository.MedicationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DroneService {

    private static final Logger log = LoggerFactory.getLogger(DroneService.class);
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    @Autowired
    public DroneService(DroneRepository droneRepository, MedicationRepository medicationRepository){
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    /**
     * Method used for registering a drone
     *
     * @param registerDroneRequest object containing new drone information.
     *
     * @return {@link String} returns successful registration.
     */
    public String registerDrone(RegisterDroneRequest registerDroneRequest) {

        // validate if serial number already exists in db
        if (droneRepository.existsBySerialNumber(registerDroneRequest.getSerialNumber())) {
            throw new ValidationException("Serial Number already exists");
        }

        Drone drone = new Drone();
        drone.setSerialNumber(registerDroneRequest.getSerialNumber());
        drone.setModel(registerDroneRequest.getModel());
        drone.setWeightLimit(registerDroneRequest.getWeightLimit());
        drone.setBatteryCapacity(100D);
        drone.setState(StateEnum.IDLE.name());
        registerDroneRequest.setWeightLimit(registerDroneRequest.getWeightLimit());

        droneRepository.save(drone);

        return "Drone Registered Successfully";
    }

    /**
     * Method used to check drone availability for medication loading.
     *
     * @return {@link List<AvailableDrone>} returns list of available drone that can be loaded with medications.
     */
    public List<AvailableDrone> getAvailableDrones() {
        List<Drone> allDrones = droneRepository.findByStateIn(List.of(StateEnum.IDLE.name(),
                    StateEnum.LOADING.name()));

        List<AvailableDrone> getAvailableDrones = new ArrayList<>();

        if (allDrones.isEmpty()) {
            return getAvailableDrones;
        } else {
            allDrones.forEach(drone -> {
                List<Medication> droneMedications = medicationRepository.findByDrone(drone);

                if (droneMedications.isEmpty()) {
                    AvailableDrone availableDrone = new AvailableDrone();

                    availableDrone.setId(drone.getId());
                    availableDrone.setModel(drone.getModel());
                    availableDrone.setSerialNumber(drone.getSerialNumber());
                    availableDrone.setWeightAvailable(drone.getWeightLimit());
                    availableDrone.setWeightLimit(drone.getWeightLimit());
                    availableDrone.setState(drone.getState());
                    availableDrone.setBatteryPercentage(drone.getBatteryCapacity());

                    getAvailableDrones.add(availableDrone);
                } else {
                    int totalMedicationWeight = droneMedications.stream()
                            .map(Medication::getWeight)
                            .reduce(0, Integer::sum);

                    int availableWeight = drone.getWeightLimit() - totalMedicationWeight;

                    if(availableWeight >= 0) {
                        AvailableDrone availableDrone = new AvailableDrone();

                        availableDrone.setId(drone.getId());
                        availableDrone.setModel(drone.getModel());
                        availableDrone.setSerialNumber(drone.getSerialNumber());
                        availableDrone.setWeightAvailable(availableWeight);
                        availableDrone.setWeightLimit(drone.getWeightLimit());
                        availableDrone.setState(drone.getState());
                        availableDrone.setBatteryPercentage(drone.getBatteryCapacity());

                        getAvailableDrones.add(availableDrone);
                    }
                }
            });
        }

        return getAvailableDrones;
    }

    /**
     * Method used for checking drone information.
     *
     * @param droneId drone id used to find a specific drone to get information.
     *
     * @return {@link GetDroneInfoResponse} returns drone information.
     */
    public GetDroneInfoResponse getDroneInformation(UUID droneId) {

        Drone drone = droneRepository.findById(droneId)
                .orElseThrow(() -> new DataNotFoundException("Data Not Found"));

        GetDroneInfoResponse response = new GetDroneInfoResponse();
        response.setSerialNumber(drone.getSerialNumber());
        response.setModel(drone.getModel());
        response.setBatteryCapacity(drone.getBatteryCapacity());
        response.setState(drone.getState());
        response.setWeightLimit(drone.getWeightLimit());

        return response;
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
    public String loadDroneMedication(LoadMedicationRequest loadMedicationRequest, UUID droneId) {
        Drone drone = droneRepository.findById(droneId)
                .orElseThrow(() -> new DataNotFoundException("Drone does not exists."));

        // validate if drone is in idle and loading state
        if (!List.of(StateEnum.IDLE.name(), StateEnum.LOADING.name()).contains(drone.getState())) {
            throw new ValidationException("Drone is not in idle and loading state...cannot add medication for now");
        }

        // validate if the battery of the drone is below 25%
        if (drone.getBatteryCapacity() < 25) {
            throw new ValidationException("Drone Battery is currently below 25%, drone cannot be loaded...");
        }

        // validate if the new medication weight does not surpassed the available weight on the current drone
        List<Medication> medications = medicationRepository.findByDrone(drone);

        Integer currentMedicationsWeight = medications.stream()
                .map(Medication::getWeight)
                .reduce(0, Integer::sum);

        int availableWeight = drone.getWeightLimit() - currentMedicationsWeight;

        if (loadMedicationRequest.getWeight() <= availableWeight) {
            Medication medication = new Medication();
            medication.setDrone(drone);
            medication.setCode(loadMedicationRequest.getCode());
            medication.setName(loadMedicationRequest.getName());
            medication.setWeight(loadMedicationRequest.getWeight());
            medication.setImageUrl(loadMedicationRequest.getImageUrl());

            // save new medication
            medicationRepository.save(medication);

            // update drone state to loading if state is idle
            if (drone.getState().equalsIgnoreCase(StateEnum.IDLE.name())) {
                drone.setState(StateEnum.LOADING.name());
                droneRepository.save(drone);
            }
        } else {
            throw new ValidationException("New medication weight exceeds the available weight the drone have. Available Weight: " +availableWeight);
        }

        return "Drone Medication Loaded Successfully";
    }

    /**
     * Method used to check current medications loaded on a given drone.
     *
     * @param droneId drone id to find what drone to be checked.
     * @return
     */
    public List<LoadedMedicationResponse> getLoadedMedications(UUID droneId) {
        Drone drone = droneRepository.findById(droneId)
                .orElseThrow(() -> new DataNotFoundException("Drone does not exists."));

        List<Medication> medications = medicationRepository.findByDrone(drone);
        List<LoadedMedicationResponse> loadedMedicationResponses = new ArrayList<>();
        if(!medications.isEmpty()) {
            medications.forEach(medication -> {
                LoadedMedicationResponse loadedMedicationResponse = new LoadedMedicationResponse();
                loadedMedicationResponse.setCode(medication.getCode());
                loadedMedicationResponse.setName(medication.getName());
                loadedMedicationResponse.setWeight(medication.getWeight());
                loadedMedicationResponse.setImageUrl(medication.getImageUrl());

                loadedMedicationResponses.add(loadedMedicationResponse);
            });
        }

        return loadedMedicationResponses;
    }
}

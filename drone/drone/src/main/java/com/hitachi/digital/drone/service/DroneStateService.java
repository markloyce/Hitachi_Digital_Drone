package com.hitachi.digital.drone.service;

import com.hitachi.digital.drone.entity.Drone;
import com.hitachi.digital.drone.entity.Medication;
import com.hitachi.digital.drone.enumeration.StateEnum;
import com.hitachi.digital.drone.repository.DroneRepository;
import com.hitachi.digital.drone.repository.MedicationRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DroneStateService {

    private static final Logger log = LoggerFactory.getLogger(DroneStateService.class);
    private final DroneRepository droneRepository;
    private final MedicationRepository medicationRepository;

    public DroneStateService(DroneRepository droneRepository, MedicationRepository medicationRepository) {
        this.droneRepository = droneRepository;
        this.medicationRepository = medicationRepository;
    }

    @Transactional
    public void transitionLoadingToLoaded() {
        // LOADING -> LOADED
        List<Drone> loadingDrones = droneRepository.findByState(StateEnum.LOADING.name());
        for (Drone drone : loadingDrones) {
            drone.setState(StateEnum.LOADED.name());
            log.info("Drone: {} {}, Transitioning from: {} to: {}", drone.getSerialNumber(), drone.getModel(),
                    StateEnum.LOADING.name(), StateEnum.LOADED.name());
        }

        // Save updated drones
        droneRepository.saveAll(loadingDrones);
    }

    @Transactional
    public void transitionLoadedToDelivering() {
        // LOADED -> DELIVERING
        List<Drone> loadedDrones = droneRepository.findByState(StateEnum.LOADED.name());
        for (Drone drone : loadedDrones) {
            drone.setState(StateEnum.DELIVERING.name());
            log.info("Drone: {} {}, Transitioning from: {} to: {}", drone.getSerialNumber(), drone.getModel(),
                    StateEnum.LOADED.name(), StateEnum.DELIVERED.name());
        }

        // Save updated drones
        droneRepository.saveAll(loadedDrones);
    }

    @Transactional
    public void transitionDeliveringToDelivered() {
        // DELIVERING -> DELIVERED
        List<Drone> deliveringDrones = droneRepository.findByState(StateEnum.DELIVERING.name());
        for (Drone drone : deliveringDrones) {
            drone.setState(StateEnum.DELIVERED.name());
            log.info("Drone: {} {}, Transitioning from: {} to: {}", drone.getSerialNumber(), drone.getModel(),
                    StateEnum.DELIVERING.name(), StateEnum.DELIVERED.name());
        }

        // Save updated drones
        droneRepository.saveAll(deliveringDrones);
    }

    @Transactional
    public void transitionDeliveredToReturning() {
        // DELIVERED -> RETURNING
        List<Drone> deliveredDrones = droneRepository.findByState(StateEnum.DELIVERED.name());
        for (Drone drone : deliveredDrones) {
            drone.setState(StateEnum.RETURNING.name());
            log.info("Drone: {} {}, Transitioning from: {} to: {}", drone.getSerialNumber(), drone.getModel(),
                    StateEnum.DELIVERED.name(), StateEnum.RETURNING.name());

            // get and delete medications after delivered delivered
            List<Medication> medicationsToBeDeleted = medicationRepository.findByDrone(drone);
            medicationRepository.deleteAll(medicationsToBeDeleted);
        }

        // Save updated drones
        droneRepository.saveAll(deliveredDrones);
    }

    @Transactional
    public void transitionReturningToIdle() {
        // RETURNING -> IDLE
        List<Drone> returningDrones = droneRepository.findByState(StateEnum.RETURNING.name());
        for (Drone drone : returningDrones) {
            drone.setState(StateEnum.IDLE.name());
            // decreased current battery by 20%
            drone.setBatteryCapacity(drone.getBatteryCapacity() - 20);
            log.info("Drone: {} {}, Transitioning from: {} to: {}", drone.getSerialNumber(), drone.getModel(),
                    StateEnum.RETURNING.name(), StateEnum.IDLE.name());
        }

        // Save updated drones
        droneRepository.saveAll(returningDrones);
    }
}

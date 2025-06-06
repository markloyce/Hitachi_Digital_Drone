package com.hitachi.digital.drone.scheduler;

import com.hitachi.digital.drone.service.DroneStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DroneStateScheduler {

    @Autowired
    private DroneStateService droneStateService;

    // 60 seconds
    @Scheduled(fixedRate = 60000)
    public void transitionFromLoadingToLoaded() {
        droneStateService.transitionLoadingToLoaded();
    }

    // 120 seconds
    @Scheduled(fixedRate = 120000)
    public void transitionFromLoadedToDelivering() {
        droneStateService.transitionLoadedToDelivering();
    }

    // 180 seconds
    @Scheduled(fixedRate = 180000)
    public void transitionFromDeliveringToDelivered() {
        droneStateService.transitionDeliveringToDelivered();
    }

    // 240 seconds
    @Scheduled(fixedRate = 240000)
    public void transitionFromDeliveredToReturning() {
        droneStateService.transitionDeliveredToReturning();
    }

    // 300 seconds
    @Scheduled(fixedRate = 300000)
    public void transitionFromReturningToIdle() {
        droneStateService.transitionReturningToIdle();
    }
}

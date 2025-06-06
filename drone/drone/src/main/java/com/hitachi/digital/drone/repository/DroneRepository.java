package com.hitachi.digital.drone.repository;

import com.hitachi.digital.drone.entity.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DroneRepository extends JpaRepository<Drone, UUID> {

    List<Drone> findByState(String state);

    List<Drone> findByStateIn(List<String> state);

    boolean existsBySerialNumber(String serialNumber);
}

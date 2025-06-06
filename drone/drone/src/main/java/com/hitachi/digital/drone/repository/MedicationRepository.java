package com.hitachi.digital.drone.repository;

import com.hitachi.digital.drone.entity.Drone;
import com.hitachi.digital.drone.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, UUID> {

    List<Medication> findByDrone(Drone drone);
}

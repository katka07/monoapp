package com.example.hostinfo.repository;

import com.example.hostinfo.model.InstanceStartup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InstanceStartupRepository extends JpaRepository<InstanceStartup, Long> {

    List<InstanceStartup> findTop10ByOrderByStartedAtDesc();
}
package com.osaebros.eventplanner.repository;

import com.osaebros.eventplanner.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long>, JpaSpecificationExecutor<ServiceProvider> {

    Boolean existsByEmail(String email);
    Optional<ServiceProvider> findByUserAccountRef(String userAccountRef);

    Optional<ServiceProvider> findByEmail(String email);
}

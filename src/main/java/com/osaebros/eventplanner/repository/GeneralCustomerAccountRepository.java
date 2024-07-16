package com.osaebros.eventplanner.repository;

import com.osaebros.eventplanner.entity.GeneralCustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralCustomerAccountRepository extends JpaRepository<GeneralCustomerAccount,Long> {
}

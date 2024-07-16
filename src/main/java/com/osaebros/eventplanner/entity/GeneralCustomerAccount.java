package com.osaebros.eventplanner.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Table
@Entity
@SuperBuilder
@Data
@EqualsAndHashCode(callSuper = true)
public class GeneralCustomerAccount extends UserAccount {




}

package org.camunda.platform.runtime.example.service.equipement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDto implements Serializable {

    private String serialNumber;
    private Long type;
    private BigDecimal purchasePrice;
    private BigDecimal balancePrice;
    private LocalDate commissioningDate;
    private LocalDate decommissioningDate;
    private Long user;
}

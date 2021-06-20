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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EquipmentDto that = (EquipmentDto) o;
        return serialNumber.equals(that.serialNumber) && Objects.equals(type, that.type) && Objects.equals(purchasePrice, that.purchasePrice) && Objects.equals(balancePrice, that.balancePrice) && Objects.equals(commissioningDate, that.commissioningDate) && Objects.equals(decommissioningDate, that.decommissioningDate) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber, type, purchasePrice, balancePrice, commissioningDate, decommissioningDate, user);
    }
}

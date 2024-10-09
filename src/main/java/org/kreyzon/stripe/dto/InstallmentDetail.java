package org.kreyzon.stripe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstallmentDetail {
    private int installmentCount; // The installment number
    private Long remainingBalance; // The remaining balance for this installment

    public InstallmentDetail(int installmentCount, Long remainingBalance) {
        this.installmentCount = installmentCount;
        this.remainingBalance = remainingBalance;
    }
}

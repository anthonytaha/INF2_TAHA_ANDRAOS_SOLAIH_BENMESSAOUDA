package com.inf2.dto;
import java.math.BigDecimal;

public class BalanceUpdateRequest {
    private BigDecimal amount;
    private boolean credit; // true pour crédit, false pour débit

    public BalanceUpdateRequest() {}

    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public boolean isCredit() {
        return credit;
    }
    public void setCredit(boolean credit) {
        this.credit = credit;
    }
}

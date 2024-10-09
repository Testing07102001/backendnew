package org.kreyzon.stripe.dto;

public class CurrentMonthPaymentDTO {
    private Long amountDueThisMonth;
    private Long pendingAmountForMissedMonths;
    private Long remainingCourseFee;
    private Long remainingRegistrationFee; // Add this field for registration fee
    private Long totalCourseFee;
    private Long totalRegistrationFee; // Add this field for total registration fee
    private Long totalPaidSoFar;
    private Long totalRemainingBalance; // Add this field for remaining balance
    private String message;

    public CurrentMonthPaymentDTO() {
    }

    // Getters and setters
    public Long getAmountDueThisMonth() {
        return amountDueThisMonth;
    }

    public void setAmountDueThisMonth(Long amountDueThisMonth) {
        this.amountDueThisMonth = amountDueThisMonth;
    }

    public Long getPendingAmountForMissedMonths() {
        return pendingAmountForMissedMonths;
    }

    public void setPendingAmountForMissedMonths(Long pendingAmountForMissedMonths) {
        this.pendingAmountForMissedMonths = pendingAmountForMissedMonths;
    }

    public Long getRemainingCourseFee() {
        return remainingCourseFee;
    }

    public void setRemainingCourseFee(Long remainingCourseFee) {
        this.remainingCourseFee = remainingCourseFee;
    }

    public Long getRemainingRegistrationFee() { // Add getter for remaining registration fee
        return remainingRegistrationFee;
    }

    public void setRemainingRegistrationFee(Long remainingRegistrationFee) { // Add setter for remaining registration fee
        this.remainingRegistrationFee = remainingRegistrationFee;
    }

    public Long getTotalCourseFee() {
        return totalCourseFee;
    }

    public void setTotalCourseFee(Long totalCourseFee) {
        this.totalCourseFee = totalCourseFee;
    }

    public Long getTotalRegistrationFee() { // Add getter for total registration fee
        return totalRegistrationFee;
    }

    public void setTotalRegistrationFee(Long totalRegistrationFee) { // Add setter for total registration fee
        this.totalRegistrationFee = totalRegistrationFee;
    }

    public Long getTotalPaidSoFar() {
        return totalPaidSoFar;
    }

    public void setTotalPaidSoFar(Long totalPaidSoFar) {
        this.totalPaidSoFar = totalPaidSoFar;
    }

    public Long getTotalRemainingBalance() { // Add getter for remaining balance
        return totalRemainingBalance;
    }

    public void setTotalRemainingBalance(Long totalRemainingBalance) { // Add setter for remaining balance
        this.totalRemainingBalance = totalRemainingBalance;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    // toString method for easy debugging/logging

    @Override
    public String toString() {
        return "CurrentMonthPaymentDTO{" +
                "amountDueThisMonth=" + amountDueThisMonth +
                ", pendingAmountForMissedMonths=" + pendingAmountForMissedMonths +
                ", remainingCourseFee=" + remainingCourseFee +
                ", remainingRegistrationFee=" + remainingRegistrationFee +
                ", totalCourseFee=" + totalCourseFee +
                ", totalRegistrationFee=" + totalRegistrationFee +
                ", totalPaidSoFar=" + totalPaidSoFar +
                ", totalRemainingBalance=" + totalRemainingBalance +
                ", message='" + message + '\'' +
                '}';
    }
}

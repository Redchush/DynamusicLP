package dynamusic.lp.order.payment;


public class LoyaltyPointPaymentInfo {

    private String profileId;
    private String orderId;
    private Integer amountInPL;
    private String description;
    private Double amountInCurrency;

    private LoyaltyPointPaymentStatus paymentStatus;

    public LoyaltyPointPaymentInfo() {}

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getAmountInPL() {
        return amountInPL;
    }

    public void setAmountInPL(Integer amountInPL) {
        this.amountInPL = amountInPL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LoyaltyPointPaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(LoyaltyPointPaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }


    public Double getAmountInCurrency() {
        return amountInCurrency;
    }

    public void setAmountInCurrency(Double amountInCurrency) {
        this.amountInCurrency = amountInCurrency;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoyaltyPointPaymentInfo{");

        sb.append("profileId='").append(profileId).append('\'');
        sb.append(", orderId='").append(orderId).append('\'');
        sb.append(", amountInPL=").append(amountInPL);
        sb.append(", description='").append(description).append('\'');
        sb.append(", plToSpendInCurrency=").append(amountInCurrency);
        sb.append(", paymentStatus=").append(paymentStatus);
        sb.append('}');
        return sb.toString();
    }
}

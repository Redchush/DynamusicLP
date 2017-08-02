package dynamusic.lp.order;


import atg.commerce.order.PaymentGroupImpl;
import dynamusic.lp.PLOrderConstants;

public class LoyaltyPointPayment extends PaymentGroupImpl {

    public LoyaltyPointPayment() {}

    public Integer getSpendPL() {
        return (Integer) this.getPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_SPENDPL_PRN);
    }

    public void setSpendPL(Integer spendPL) {
        this.setPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_SPENDPL_PRN, spendPL);
    }

    public String getConversion() {
        return (String) this.getPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_CONVERSION_PRN);
    }

    public void setConversion(String conversion) {
        this.setPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_CONVERSION_PRN, conversion);
    }

    public Integer getPaymentRoundRule() {
        return (Integer) this.getPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_PAYMENTROUNDRULE_PRN);
    }

    public void setPaymentRoundRule(Integer paymentRoundRule) {
        this.setPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_PAYMENTROUNDRULE_PRN, paymentRoundRule);
    }

    public void setProfileId(String profileId){
        this.setPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_PROFILE_PRN, profileId);
    }

    public String getProfileId(){
        return (String) this.getPropertyValue(PLOrderConstants.PL_PAYMENT_GROUP_PROFILE_PRN);
    }

}

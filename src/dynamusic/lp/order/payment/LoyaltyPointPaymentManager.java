package dynamusic.lp.order.payment;


import atg.commerce.payment.PaymentManager;

public class LoyaltyPointPaymentManager extends PaymentManager {

    private LoyaltyPointPaymentProcessor loyaltyPointPaymentProcessor;

    public LoyaltyPointPaymentProcessor getLoyaltyPointPaymentProcessor() {
        return loyaltyPointPaymentProcessor;
    }

    public void setLoyaltyPointPaymentProcessor(
            LoyaltyPointPaymentProcessor loyaltyPointPaymentProcessor) {
        this.loyaltyPointPaymentProcessor = loyaltyPointPaymentProcessor;
    }

}

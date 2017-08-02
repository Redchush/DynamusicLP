package dynamusic.lp.order.payment;


import atg.payment.PaymentStatus;

public interface LoyaltyPointPaymentStatus extends PaymentStatus {

    OrderTransaction getTransaction();

    void setTransaction(OrderTransaction transaction);
}

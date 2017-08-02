package dynamusic.lp.order.payment.impl;


import atg.payment.PaymentStatusImpl;
import dynamusic.lp.order.payment.LoyaltyPointPaymentStatus;
import dynamusic.lp.order.payment.OrderTransaction;

import java.util.Date;

public class LoyaltyPointPaymentStatusImpl extends PaymentStatusImpl implements LoyaltyPointPaymentStatus {

    private OrderTransaction transaction;

    public LoyaltyPointPaymentStatusImpl() {
        super();
    }

    public LoyaltyPointPaymentStatusImpl(OrderTransaction transaction) {
        super();
        this.transaction = transaction;
    }


    public LoyaltyPointPaymentStatusImpl(String pTransactionId, double pAmount, boolean pTransactionSuccess,
                                         String pErrorMessage, Date pTransactionTimestamp) {
        super(pTransactionId, pAmount, pTransactionSuccess, pErrorMessage, pTransactionTimestamp);
    }

    public LoyaltyPointPaymentStatusImpl(String pTransactionId, double pAmount, boolean pTransactionSuccess,
                                         String pErrorMessage, Date pTransactionTimestamp,
                                         OrderTransaction transaction) {
        super(pTransactionId, pAmount, pTransactionSuccess, pErrorMessage, pTransactionTimestamp);
        this.transaction = transaction;
    }

    public OrderTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(OrderTransaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoyaltyPointPaymentStatusImpl{");
        sb.append(super.toString()).append(" ");

        sb.append("transaction=").append(transaction);
        sb.append('}');
        return sb.toString();
    }
}

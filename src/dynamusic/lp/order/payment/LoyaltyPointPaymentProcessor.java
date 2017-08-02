package dynamusic.lp.order.payment;


import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.payment.PaymentStatus;
import atg.repository.RepositoryItem;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.payment.impl.LoyaltyPointPaymentStatusImpl;

public interface LoyaltyPointPaymentProcessor {

    /**
     * Authorize the amount in LoyaltyPointPayment
     *
     * @param loyaltyPointPaymentInfo the LoyaltyPointPaymentInfo reference which contains
     * all the authorization data
     * @return a LoyaltyPointPaymentStatusImpl object detailing the results of the
     * authorization
     */
    LoyaltyPointPaymentStatus authorize(LoyaltyPointPaymentInfo loyaltyPointPaymentInfo) throws CommerceException;
    /**
     * Debit the amount in LoyaltyPointPayment after authorization
     *
     * @param loyaltyPointPaymentInfo the LoyaltyPointPaymentInfo reference which contains
     * all the debit data
     * @param pStatus the LoyaltyPointPaymentStatusImpl object which contains
     * information about the transaction. This should be the object
     * which was returned from authorize().
     * @return a LoyaltyPointPaymentStatusImpl object detailing the results of the debit
     */
    LoyaltyPointPaymentStatus debit(LoyaltyPointPaymentInfo loyaltyPointPaymentInfo,
                                    PaymentStatus pStatus) throws CommerceException;


    /**
     * Credit the amount in LoyaltyPointPayment after debiting
     *
     * @param loyaltyPointPaymentInfo the LoyaltyPointPaymentInfo reference which contains
     * all the credit data
     * @param pStatus the StorePointsStatus object which contains
     * information about the transaction. This should be the object
     * which was returned from debit().
     * @return a LoyaltyPointPaymentStatusImpl object detailing the results of the
     * credit
     */

    LoyaltyPointPaymentStatus credit(LoyaltyPointPaymentInfo loyaltyPointPaymentInfo,
                                     PaymentStatus pStatus);
    /**
     * Credit the amount in LoyaltyPointPayment outside the context of an Order
     *
     * @param loyaltyPointPaymentInfo the LoyaltyPointPaymentInfo reference which contains
     * all the credit data
     * @return a LoyaltyPointPaymentStatusImpl object detailing the results of the
     * credit
     */
    LoyaltyPointPaymentStatus credit(LoyaltyPointPaymentInfo loyaltyPointPaymentInfo);


    RepositoryItem createBonusTransaction(Order order) throws CommerceException;


}

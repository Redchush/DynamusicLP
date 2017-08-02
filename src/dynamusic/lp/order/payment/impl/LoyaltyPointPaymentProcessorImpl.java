package dynamusic.lp.order.payment.impl;


import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderImpl;
import atg.nucleus.GenericService;
import atg.payment.PaymentStatus;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;
import dynamusic.lp.order.payment.LoyaltyPointPaymentProcessor;
import dynamusic.lp.order.pricing.LoyaltyOrderPriceInfo;
import dynamusic.system.validator.impl.ConcatMsgCallback;

import java.util.Date;

public class LoyaltyPointPaymentProcessorImpl extends GenericService implements LoyaltyPointPaymentProcessor {

    private static final String SUCCESS_FORMAT = "Successfully %s by transaction %s";
    private static final String FAIL_FORMAT = "Fail to %s by transaction %s. %s";

    private OrderLoyaltyManager orderLoyaltyManager;


    public LoyaltyPointPaymentStatusImpl authorize(LoyaltyPointPaymentInfo lpp) throws CommerceException {
        debug("authorize : ", lpp);

        ConcatMsgCallback concatMsgCallback = new ConcatMsgCallback();
        if (lpp.getAmountInPL() != null && orderLoyaltyManager.userHasSum(lpp.getProfileId(), lpp.getAmountInPL()))
        {
            RepositoryItem transaction = orderLoyaltyManager.createOrderTransaction(lpp, concatMsgCallback,true);
            return transaction != null ? createSuccessStatus(transaction, lpp)
                                       : createFailStatus(null, lpp, "authorize", concatMsgCallback.getMsgString());
        }
        debug("Fail to authorize: " + concatMsgCallback.getMsgString(), null);
        return createFailStatus(null, lpp, "check if user has sum", concatMsgCallback.getMsgString());

    }

    public LoyaltyPointPaymentStatusImpl debit(LoyaltyPointPaymentInfo lpp,
                                               PaymentStatus pStatus) throws CommerceException {

        debug("Debit by info authorized transaction id: "+ pStatus.getTransactionId(), lpp);
        ConcatMsgCallback concatMsgCallback = new ConcatMsgCallback();
        if (pStatus.getTransactionId() == null)
        {
            throw new InvalidParameterException("The authorized transaction has not created");
        }
        RepositoryItem transaction = orderLoyaltyManager
                .updateOrderTransaction(pStatus.getTransactionId(), null, null, lpp.getDescription(), null, concatMsgCallback);
        return transaction != null ? createSuccessStatus(transaction, lpp)
                                   : createFailStatus(pStatus.getTransactionId(), lpp, "debit",
                                           concatMsgCallback.getMsgString());

    }

    public LoyaltyPointPaymentStatusImpl credit(LoyaltyPointPaymentInfo lpp,
                                                PaymentStatus pStatus) {
        debug("Credit by info authorized transaction id: "+ pStatus.getTransactionId(), lpp);
        ConcatMsgCallback concatMsgCallback = new ConcatMsgCallback();

        RepositoryItem transaction = orderLoyaltyManager
                .updateOrderTransaction(pStatus.getTransactionId(), null, 0, lpp.getDescription(), null,
                        concatMsgCallback);
        return transaction != null ? createSuccessStatus(transaction, lpp)
                                   : createFailStatus(pStatus.getTransactionId(), lpp, "credit",
                                           concatMsgCallback.getMsgString());
    }

    public LoyaltyPointPaymentStatusImpl credit(LoyaltyPointPaymentInfo lpp) {
        return credit(lpp, null);
    }

    private LoyaltyPointPaymentStatusImpl createSuccessStatus(RepositoryItem orderTransaction, LoyaltyPointPaymentInfo lpp){
        return new LoyaltyPointPaymentStatusImpl(orderTransaction.getRepositoryId(), lpp.getAmountInCurrency(), true,
                "", new Date(), new OrderTransactionImpl (orderTransaction));
    }

    private LoyaltyPointPaymentStatusImpl createFailStatus(String transactionId, LoyaltyPointPaymentInfo lpp,
                                                           String operation, String errorDescription){
        return new LoyaltyPointPaymentStatusImpl(transactionId, lpp.getAmountInCurrency(), false,
                String.format(FAIL_FORMAT, operation, transactionId, errorDescription), new Date());
    }

    public RepositoryItem createBonusTransaction(Order order) throws CommerceException {
        LoyaltyOrderPriceInfo priceInfo = (LoyaltyOrderPriceInfo) order.getPriceInfo();
        RepositoryItem result;
        try {
            RepositoryItem orderRI = (order instanceof OrderImpl) ? ((OrderImpl) order).getRepositoryItem() :
                                     orderLoyaltyManager.getOrderRepository().getItem(
                                             order.getId(), PLOrderConstants.ORDER_TYPE);
            RepositoryItem priceInfoRI = (RepositoryItem) orderRI.getPropertyValue("priceInfo");
            RepositoryItem transactionRI =
                    (RepositoryItem) priceInfoRI.getPropertyValue(PLOrderConstants.BONUL_PL_INFO_TRANSACTION_PRN);
            if (transactionRI == null)
            {
                result = orderLoyaltyManager.createOrderTransaction(order.getProfileId(), (int) priceInfo.getAmount(),
                        "bonus: " + order.getId(), order.getId(), null, true);
                String idPriceInfo = priceInfoRI.getRepositoryId();
                MutableRepositoryItem priceInfoRIForUpdate = orderLoyaltyManager
                        .getOrderRepository()
                        .getItemForUpdate(idPriceInfo, PLOrderConstants.BONUS_ORDER_INFO_TYPE);
                priceInfoRIForUpdate.setPropertyValue(PLOrderConstants.BONUL_PL_INFO_TRANSACTION_PRN, result);
                orderLoyaltyManager.getOrderRepository().updateItem(priceInfoRIForUpdate);
            } else
            {
                result = orderLoyaltyManager.updateOrderTransaction(priceInfoRI.getRepositoryId(), null,
                        (int) priceInfo.getAmount(), null, null, null);
            }
        } catch (RepositoryException e) {
            throw new CommerceException(e);
        }
        return result;
    }


    private void debug(String prefix, LoyaltyPointPaymentInfo lpp){
        if (isLoggingDebug())
        {
            logDebug(prefix + " " + lpp);
        }
    }

    public OrderLoyaltyManager getOrderLoyaltyManager() {
        return orderLoyaltyManager;
    }

    public void setOrderLoyaltyManager(OrderLoyaltyManager orderLoyaltyManager) {
        this.orderLoyaltyManager = orderLoyaltyManager;
    }

}

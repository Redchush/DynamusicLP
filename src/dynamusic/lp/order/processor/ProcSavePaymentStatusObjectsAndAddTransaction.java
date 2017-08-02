package dynamusic.lp.order.processor;


import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.processor.ProcSavePaymentStatusObjects;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryItem;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.payment.LoyaltyPointPaymentStatus;

public class ProcSavePaymentStatusObjectsAndAddTransaction extends ProcSavePaymentStatusObjects {

    private MutableRepository loyaltyRepository;

    @Override
    protected boolean saveStatusProperties(Order order, Object paymentStatus, MutableRepositoryItem mutItem,
                                           MutableRepository mutRep, OrderManager orderManager, OrderTools orderTools)
            throws Exception {
        boolean changed = super.saveStatusProperties(order, paymentStatus, mutItem, mutRep, orderManager, orderTools);
        logDebug("Add transaction");
        boolean changedTransaction = false;
        if (paymentStatus instanceof LoyaltyPointPaymentStatus)
        {
            LoyaltyPointPaymentStatus status = (LoyaltyPointPaymentStatus) paymentStatus;
            if (status.getTransaction() != null && status.getTransaction().getRepositoryItem() != null)
            {
                mutItem.setPropertyValue(PLOrderConstants.PL_PAYMENT_STATUS_TRANSACTION_PRN, status.getTransaction().getRepositoryItem());
                changedTransaction = true;
            } else
            {
                RepositoryItem transaction = null;
                if (status.getTransactionId() != null && (transaction = loyaltyRepository.getItem(status.getTransactionId(),
                                PLOrderConstants.PL_ORDER_TRANSACTION_TYPE ))!= null)
                {
                    mutItem.setPropertyValue(PLOrderConstants.PL_PAYMENT_STATUS_TRANSACTION_PRN, transaction);
                    changedTransaction = true;
                }

            }
        }
        return changed || changedTransaction;
    }

    public MutableRepository getLoyaltyRepository() {
        return loyaltyRepository;
    }

    public void setLoyaltyRepository(MutableRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }
}

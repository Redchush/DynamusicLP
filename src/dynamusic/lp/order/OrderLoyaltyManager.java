package dynamusic.lp.order;


import atg.commerce.order.Order;
import atg.dtm.TransactionDemarcation;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import dynamusic.lp.LoyaltyConstants;
import dynamusic.lp.LoyaltyManager;
import dynamusic.lp.order.command.CreateOrUpdateOrderTransaction;
import dynamusic.lp.order.command.PLOrderCommandHelper;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;
import dynamusic.lp.order.pricing.LoyaltyCalculatorTools;
import dynamusic.system.command.commandimpl.ChangeItemProperty;
import dynamusic.system.command.commandimpl.ChangeSingleProperty;
import dynamusic.system.validator.ValidationStrategy;
import dynamusic.system.validator.Validator;
import dynamusic.system.validator.ValidatorCallback;

import java.math.BigDecimal;
import java.util.Map;

import static dynamusic.lp.LoyaltyConstants.USER_TRANSACTION_AMOUNT_PRN;
import static dynamusic.lp.PLOrderConstants.*;

public class OrderLoyaltyManager extends LoyaltyManager {

    private PLOrderCommandHelper plOrderCommandHelper;

    private LoyaltyCalculatorTools loyaltyCalculatorTools;
    private ValidatorCallback errorCallback;
    private MutableRepository orderRepository;

    private Validator<Map<String,Object>, Void>[] plOrderValidators;

    @Override
    public void doStartService() throws ServiceException {
        super.doStartService();
        errorCallback = this.new ErrorLogCallback();
    }

    public RepositoryItem getOrderTransaction(String id){
        try {
            return getLoyaltyRepository().getItem(id, PL_ORDER_TRANSACTION_TYPE);
        } catch (RepositoryException e) {
            logError("Transaction not found");
        }
        return null;
    }

    public boolean userHasSum(String profileId, Integer plAmount) {
        try {
            RepositoryItem item = getProfileRepository().getItem(profileId, LoyaltyConstants.USER_ITEM_DRN);
            if (item != null )
            {
                return plAmount >= 0 || userHasSum(item, -plAmount);
            }
        } catch (RepositoryException e) {
            logError("User not found");
        }
        return false;
    }

    public boolean userHasSum(RepositoryItem profile, Integer plToSpend) {
        Integer userAmountPL = (Integer) profile.getPropertyValue(USER_TRANSACTION_AMOUNT_PRN);
        return plToSpend != null && userAmountPL >= plToSpend;
    }

    public Integer maxPlToSpendForOrder(RepositoryItem profile, Order order){
        return loyaltyCalculatorTools.maxLoyaltyToSpend((Integer) profile.getPropertyValue(USER_TRANSACTION_AMOUNT_PRN),
                order.getPriceInfo().getTotal(), order.getPriceInfo().getCurrencyCode());
    }


    public Double plToSpendInCurrency(Integer plToSpend, Order order){
        return loyaltyCalculatorTools.convertLPToCurrency(order.getPriceInfo().getCurrencyCode(),
                new BigDecimal(plToSpend), true).doubleValue();
    }

    public RepositoryItem updateOrderTransaction(String transactionId, LoyaltyPointPaymentInfo info,
                                                 ValidatorCallback callback){
        Map<String, Object> propertyMap = getPlOrderCommandHelper().createPropertyMap(info, true);
        return updateOrderTransactionInternal(transactionId, propertyMap, callback);
    }

    public RepositoryItem updateOrderTransaction(String transactionId, String profileId, Integer amount, String
            description, String orderId, ValidatorCallback callback){
        Map<String, Object> propertyMap =
                getPlOrderCommandHelper().createPropertyMap(profileId, amount,description, orderId, true);
        return updateOrderTransactionInternal(transactionId, propertyMap, callback);
    }

    private RepositoryItem updateOrderTransactionInternal(String transactionId,
                                                          Map<String, Object> propertyMap,
                                                          ValidatorCallback callback){
        ValidatorCallback currentCallback = callback != null ? callback : errorCallback;
        RepositoryItem result = null;
        if (getValidationStrategy().executeValidation(getPlOrderValidators(), propertyMap,currentCallback))
        {
            CreateOrUpdateOrderTransaction cmd = new CreateOrUpdateOrderTransaction(this,propertyMap, false);
            cmd.setUpdateId(transactionId);
            result = (RepositoryItem) getInvoker().executeDefault(cmd);
            logDebug("item updated: " + result);
        }
        return result;
    }

    public RepositoryItem createOrderTransaction(LoyaltyPointPaymentInfo info, ValidatorCallback callback,
                                                 boolean withLinks){
        Map<String, Object> propertyMap = plOrderCommandHelper.createPropertyMap(info, false);
        return createOrderTransactionInternal(propertyMap, callback, withLinks);
    }

    public RepositoryItem createOrderTransaction(String profileId, Integer amount, String description,
                                                 String orderId, ValidatorCallback callback, boolean withLinks){
        Map<String, Object> propertyMap  =
                plOrderCommandHelper.createPropertyMap(profileId, amount, description, orderId, false);
        return createOrderTransactionInternal(propertyMap, callback, withLinks);
    }

    private RepositoryItem createOrderTransactionInternal(Map<String, Object> propertyMap, ValidatorCallback callback,
                                                          boolean withLinks){
        ValidatorCallback currentCallback = callback != null ? callback : errorCallback;
        RepositoryItem result = null;
        if (getValidationStrategy().executeValidation(getPlOrderValidators(), propertyMap, currentCallback))
        {
            result = (RepositoryItem) getInvoker().executeDefault(new CreateOrUpdateOrderTransaction(this, propertyMap, withLinks));
            logDebug("item created: " + result);
        }
        return result;
    }

    /**
     * @return if operation has success
     */
    public boolean addOrderTransactionRelationships(String orderId, String profileId, String transactionId){
        return setOrderToTransaction(profileId, transactionId) && addTransactionToUser(orderId, transactionId);
    }

    public boolean setOrderToTransaction(String orderId, String transactionId){
        ChangeItemProperty command = plOrderCommandHelper.fillChangeCommand(new ChangeSingleProperty(), transactionId,
                orderId);
        return getInvoker().executeAndIsSuccess(command, TransactionDemarcation.REQUIRED, false);
    }

    private class ErrorLogCallback implements ValidatorCallback<Void>{

        public void executeCallback(String errorMsg, Void result) {
            logError("Validation failed: reason - " + errorMsg);
        }
    }

//    getters & setters

    public MutableRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(MutableRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public PLOrderCommandHelper getPlOrderCommandHelper() {
        return plOrderCommandHelper;
    }

    public void setPlOrderCommandHelper(PLOrderCommandHelper plOrderCommandHelper) {
        this.plOrderCommandHelper = plOrderCommandHelper;
    }

    public Validator<Map<String, Object>, Void>[] getPlOrderValidators() {
        return plOrderValidators;
    }

    public void setPlOrderValidators(
            Validator<Map<String, Object>, Void>[] plOrderValidators) {
        this.plOrderValidators = plOrderValidators;
    }

    public LoyaltyCalculatorTools getLoyaltyCalculatorTools() {
        return loyaltyCalculatorTools;
    }

    public void setLoyaltyCalculatorTools(LoyaltyCalculatorTools loyaltyCalculatorTools) {
        this.loyaltyCalculatorTools = loyaltyCalculatorTools;
    }
}

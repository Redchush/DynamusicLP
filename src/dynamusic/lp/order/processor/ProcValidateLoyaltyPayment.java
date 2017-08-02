package dynamusic.lp.order.processor;


import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.pricing.PricingConstants;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.userprofiling.Profile;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.PaymentGroupLoyaltyManager;
import dynamusic.lp.order.validator.SpendPLValidatorFacade;
import dynamusic.system.validator.ValidatorCallback;

import java.util.HashMap;
import java.util.Map;

import static dynamusic.lp.LoyaltyConstants.USER_TRANSACTION_AMOUNT_PRN;

public class ProcValidateLoyaltyPayment extends ApplicationLoggingImpl implements PipelineProcessor {

    private PaymentGroupLoyaltyManager paymentGroupLoyaltyManager;
    private SpendPLValidatorFacade spendPLValidatorFacade;


    private final int SUCCESS = 1;
    private final int NO_PAYMENT_GROUP_DETECTED = 2;


    public int runProcess(Object params, final PipelineResult pipelineResult) throws Exception {
        Map<String, Object> map = (Map<String, Object>) params;
        Profile profile = (Profile) map.get(PricingConstants.PROFILE_PARAM);
        Order order = (Order) map.get(PricingConstants.ORDER_PARAM);
        Integer spendPl = (Integer) map.get(PLOrderConstants.SPENDPL_PARAM);
        if (profile == null)
        {
            throw new InvalidParameterException("Profile must be set as parameter");
        }
        if (spendPl == null)
        {
            LoyaltyPointPayment loyaltyPtGr;
            if (order == null || order.getPaymentGroups() == null ||
                 (loyaltyPtGr = paymentGroupLoyaltyManager.findLoyaltyPointPayment(order, false)) == null )
            {
                return NO_PAYMENT_GROUP_DETECTED;
            } else
            {
                spendPl = loyaltyPtGr.getSpendPL();
            }
        }

        if (spendPLValidatorFacade.validate(profile, order, spendPl, new ValidatorCallback() {
            public void executeCallback(String errorMsg, Object result) {
                addHashedError(pipelineResult, "InvalidSpendPL", "InvalidSpendPL" , new CommerceException(errorMsg));
            }
        }))
        {
            return SUCCESS;
        } else {
           return PipelineProcessor.STOP_CHAIN_EXECUTION_AND_ROLLBACK;
        }
    }

    protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
    {
        Object error = pResult.getError(pKey);
        if (error == null) {
            HashMap map = new HashMap(5);
            pResult.addError(pKey, map);
            map.put(pId, pError);
        }
        else if (error instanceof Map) {
            Map map = (Map) error;
            map.put(pId, pError);
        }
    }

    public int[] getRetCodes() {
        return new int[]{SUCCESS, NO_PAYMENT_GROUP_DETECTED};
    }

    protected boolean userHasPLSum(Profile profile, Integer plToSpend){
        Integer userAmountPL = (Integer) profile.getPropertyValue(USER_TRANSACTION_AMOUNT_PRN);
        return userAmountPL > plToSpend;
    }

    public PaymentGroupLoyaltyManager getPaymentGroupLoyaltyManager() {
        return paymentGroupLoyaltyManager;
    }

    public void setPaymentGroupLoyaltyManager(PaymentGroupLoyaltyManager paymentGroupLoyaltyManager) {
        this.paymentGroupLoyaltyManager = paymentGroupLoyaltyManager;
    }

    public SpendPLValidatorFacade getSpendPLValidatorFacade() {
        return spendPLValidatorFacade;
    }

    public void setSpendPLValidatorFacade(SpendPLValidatorFacade spendPLValidatorFacade) {
        this.spendPLValidatorFacade = spendPLValidatorFacade;
    }
}

package dynamusic.lp.order.processor;


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

import java.util.Map;

import static dynamusic.lp.LoyaltyConstants.USER_TRANSACTION_AMOUNT_PRN;

public class ProcValidateLoyaltyPayment extends ApplicationLoggingImpl implements PipelineProcessor {

    private PaymentGroupLoyaltyManager paymentGroupLoyaltyManager;

    private final int SUCCESS = 1;
    private final int FAILURE = 2;
    private final int NO_PAYMENT_GROUP_DETECTED = 3;


    public int runProcess(Object params, PipelineResult pipelineResult) throws Exception {
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
            }
            spendPl = loyaltyPtGr.getSpendPL();

            double amount = loyaltyPtGr.getAmount();
            if (amount > spendPl){
                return PipelineProcessor.STOP_CHAIN_EXECUTION_AND_ROLLBACK;
            }
        }
        if (userHasPLSum(profile, spendPl))
        {
            return SUCCESS;
        } else {
            return PipelineProcessor.STOP_CHAIN_EXECUTION_AND_ROLLBACK;
        }
    }

    public int[] getRetCodes() {
        return new int[]{SUCCESS, FAILURE, NO_PAYMENT_GROUP_DETECTED};
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
}

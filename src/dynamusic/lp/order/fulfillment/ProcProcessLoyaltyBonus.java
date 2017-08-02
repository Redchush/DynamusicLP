package dynamusic.lp.order.fulfillment;


import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import dynamusic.lp.order.payment.LoyaltyPointPaymentProcessor;

import java.util.HashMap;

public class ProcProcessLoyaltyBonus extends GenericService implements PipelineProcessor {

    private static final int SUCCESS = 1;
    private LoyaltyPointPaymentProcessor loyaltyPointPaymentProcessor;

    public int runProcess(Object params, PipelineResult pipelineResult) throws Exception {
        HashMap map = (HashMap) params;
        Order order = (Order) map.get(PipelineConstants.ORDER);
        if (order == null)
        {
            throw new InvalidParameterException("order isn't set");
        }
        try{
            loyaltyPointPaymentProcessor.createBonusTransaction(order);
        } catch (Exception e){
            logError(e);
            throw e;
        }
        return SUCCESS;
    }

    public int[] getRetCodes() {
        return new int[]{SUCCESS};
    }

    public LoyaltyPointPaymentProcessor getLoyaltyPointPaymentProcessor() {
        return loyaltyPointPaymentProcessor;
    }

    public void setLoyaltyPointPaymentProcessor(
            LoyaltyPointPaymentProcessor loyaltyPointPaymentProcessor) {
        this.loyaltyPointPaymentProcessor = loyaltyPointPaymentProcessor;
    }
}

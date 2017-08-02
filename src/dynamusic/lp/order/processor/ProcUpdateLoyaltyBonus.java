package dynamusic.lp.order.processor;


import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.pricing.OrderPriceInfo;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import dynamusic.lp.order.pricing.LoyaltyCalculatorTools;
import dynamusic.lp.order.pricing.LoyaltyOrderPriceInfo;

import java.math.BigDecimal;
import java.util.HashMap;

public class ProcUpdateLoyaltyBonus extends ApplicationLoggingImpl implements PipelineProcessor{

    private LoyaltyCalculatorTools loyaltyCalculatorTools;

    private final int SUCCESS = 1;

    public ProcUpdateLoyaltyBonus() {}

    public int runProcess(Object param, PipelineResult pipelineResult) throws Exception {

        HashMap map = (HashMap) param;
        Order order = (Order) map.get(PipelineConstants.ORDER);
        if (order == null)
            throw new InvalidParameterException("Order mustn't be null");
        OrderPriceInfo priceInfo = order.getPriceInfo();
        if (! (priceInfo instanceof LoyaltyOrderPriceInfo)){
            throw new InvalidParameterException("Order priceInfo must be instanceof LoyaltyOrderPriceInfo");
        }
        BigDecimal bonusPL = loyaltyCalculatorTools.applyAddBonusPL((LoyaltyOrderPriceInfo) priceInfo);
        order.setPriceInfo(priceInfo);
        logDebug("Bonus applied " + bonusPL + ". Price Info : " + order.getPriceInfo());
        return SUCCESS;
    }

    public int[] getRetCodes() {
        return new int[]{SUCCESS};
    }

    public LoyaltyCalculatorTools getLoyaltyCalculatorTools() {
        return loyaltyCalculatorTools;
    }

    public void setLoyaltyCalculatorTools(LoyaltyCalculatorTools loyaltyCalculatorTools) {
        this.loyaltyCalculatorTools = loyaltyCalculatorTools;
    }
}

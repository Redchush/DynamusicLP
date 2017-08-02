package dynamusic.lp.order.pricing.calculators;


import atg.commerce.order.Order;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.OrderPricingCalculator;
import atg.commerce.pricing.PricingException;
import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.RepositoryItem;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.lp.order.pricing.LoyaltyCalculatorTools;
import dynamusic.lp.order.pricing.LoyaltyOrderPriceInfo;

import java.util.Locale;
import java.util.Map;

public class OrderPLReceivedCalculator extends GenericService implements OrderPricingCalculator{

    private OrderLoyaltyManager orderLoyaltyManager;
    private LoyaltyCalculatorTools loyaltyCalculatorTools;

    @Override
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (loyaltyCalculatorTools == null){
            logError("LoyaltyCalculatorTools isn't set");
        }
        logDebug("OrderPLReceivedCalculator started");
    }

    public void priceOrder(OrderPriceInfo orderPriceInfo, Order order, RepositoryItem repositoryItem, Locale locale,
                           RepositoryItem repositoryItem1, Map map) throws PricingException {
        logDebug(" called ");
        loyaltyCalculatorTools.applyAddBonusPL((LoyaltyOrderPriceInfo) orderPriceInfo);
    }

    public LoyaltyCalculatorTools getLoyaltyCalculatorTools() {
        return loyaltyCalculatorTools;
    }

    public void setLoyaltyCalculatorTools(LoyaltyCalculatorTools loyaltyCalculatorTools) {
        this.loyaltyCalculatorTools = loyaltyCalculatorTools;
    }

    public OrderLoyaltyManager getOrderLoyaltyManager() {
        return orderLoyaltyManager;
    }

    public void setOrderLoyaltyManager(OrderLoyaltyManager orderLoyaltyManager) {
        this.orderLoyaltyManager = orderLoyaltyManager;
    }



}

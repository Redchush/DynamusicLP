package dynamusic.lp.handler.impl;


import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroupManager;
import atg.nucleus.ServiceException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.PaymentGroupLoyaltyManager;
import dynamusic.lp.handler.ExpressCheckoutWithPL;

import javax.servlet.ServletException;
import java.io.IOException;

public class SimpleExpressCheckoutWithPL extends ExpressCheckoutWithPL {

    private PaymentGroupLoyaltyManager paymentGroupLoyaltyManager;

    @Override
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (paymentGroupLoyaltyManager == null)
        {
            PaymentGroupManager pGrManager = getConfiguration().getPaymentGroupManager();
            setPaymentGroupLoyaltyManager((PaymentGroupLoyaltyManager) pGrManager);
        }
    }

    @Override
    public void preExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        super.preExpressCheckout(pRequest, pResponse);
        if (getPlToSpend() > NULL_PL_AMOUNT)
        {
            try {
                LoyaltyPointPayment plPtGr = paymentGroupLoyaltyManager.findLoyaltyPointPayment(getOrder(), true);
                if (plPtGr == null)
                {
                    plPtGr = paymentGroupLoyaltyManager.addNewLoyaltyPaymentGroupToOrder(getOrder(),
                            getPlToSpend(), getOrder().getPriceInfo().getCurrencyCode(), null);
                    logDebug("New loyaltyPointPayment in simple checkout created.");
                } else {
                    paymentGroupLoyaltyManager.initializeLoyaltyPaymentGroup(plPtGr, getPlToSpend(), getOrder
                            ().getPriceInfo().getCurrencyCode(), null);
                }
                getOrderManager().addOrderAmountToPaymentGroup(getOrder(), plPtGr.getId(),
                        plPtGr.getAmount());
                getOrderManager().addRemainingOrderAmountToPaymentGroup(getOrder(), getPaymentGroup().getId());
            } catch (CommerceException e) {
                logError(e);
            }
        }
    }

    public PaymentGroupLoyaltyManager getPaymentGroupLoyaltyManager() {
        return paymentGroupLoyaltyManager;
    }

    public void setPaymentGroupLoyaltyManager(PaymentGroupLoyaltyManager paymentGroupLoyaltyManager) {
        this.paymentGroupLoyaltyManager = paymentGroupLoyaltyManager;
    }
}

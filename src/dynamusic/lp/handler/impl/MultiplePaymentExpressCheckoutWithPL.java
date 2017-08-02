package dynamusic.lp.handler.impl;


import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.droplet.DropletFormException;
import atg.service.pipeline.PipelineResult;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.handler.ExpressCheckoutWithPL;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;

public class MultiplePaymentExpressCheckoutWithPL extends ExpressCheckoutWithPL {

    private static final Integer ORDER_MANAGER_PROCESS_MAP_CAPACITY =13;
    private static final Integer PROCESS_MAP_CAPACITY = ORDER_MANAGER_PROCESS_MAP_CAPACITY + 1;

    /**
     * The same method as in parent ExpressCheckoutFormHandler, but it added the
     * PLOrderConstants.SPENDPL_PARAM for FUTURE processing pl payment
     * <code>commitOrder</code> commits the Order.
     *
     * @param pRequest a <code>DynamoHttpServletRequest</code> value
     * @param pResponse a <code>DynamoHttpServletResponse</code> value
     * @exception CommerceException if an error occurs
     * @exception ServletException if an error occurs
     * @exception IOException if an error occurs
     */
    @Override
    protected void commitOrder (DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
            throws CommerceException, ServletException, IOException
    {
        // make sure they are not trying to double submit an order
        Order lastOrder = getShoppingCart().getLast();
        String orderId = getOrderId();
        if (orderId != null && lastOrder != null && orderId.equals(lastOrder.getId())) {
            // invalid number given for quantity of item to add
            String msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_ORDER_ALREADY_SUBMITTED));
        }
        else {
            Order order = getShoppingCart().getCurrent();
            HashMap<String, Object> map = new HashMap<String, Object>(PROCESS_MAP_CAPACITY);
            map.put(PLOrderConstants.LOCALE_PARAM, getUserLocale());

            if (getSpendPLHolder().getSpendPL() != null && getSpendPLHolder().getSpendPL() > NULL_PL_AMOUNT)
            {
                logDebug("Put SPENDPL_PARAM before processMap : " + getSpendPLHolder().getSpendPL() );
                map.put(PLOrderConstants.SPENDPL_PARAM, getSpendPLHolder().getSpendPL());

            }
            synchronized (order) {
                PipelineResult result = getOrderManager().processOrder(order, "processOrder", map);
                if (! processPipelineErrors(result)) {
                    if (getShoppingCart() != null) {
                        getShoppingCart().setLast(order);
                        getShoppingCart().setCurrent(null);
                    }
                }
            }
        }
    }
}

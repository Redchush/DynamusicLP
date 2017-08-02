package dynamusic.lp.order.processor;


import atg.commerce.order.*;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.PaymentGroupLoyaltyManager;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;



public class ProcHandleLoyaltyPayment extends ApplicationLoggingImpl implements PipelineProcessor {

    private PaymentGroupLoyaltyManager paymentGroupLoyaltyManager;

    private final int SUCCESS_AND_NEXT = 1;
    private final int SUCCESS_AND_NEW_ROUND = 2;

    public ProcHandleLoyaltyPayment() {}

    public int runProcess(Object param, PipelineResult pipelineResult) throws Exception {

        HashMap<String, Object> map = (HashMap<String, Object>) param;
        Integer spend = (Integer) map.get(PLOrderConstants.SPENDPL_PARAM);
        if (spend == null)
        {
            logDebug("Not need to apply PL payment");
            return SUCCESS_AND_NEXT;
        }
        Order order = (Order) map.get(PipelineConstants.ORDER);
        OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
        if (order == null)
        {
            throw new InvalidParameterException();
        }
        if (Boolean.TRUE.equals(map.get(PLOrderConstants.COMPLETE_PL_PARAM)))
        {
            logDebug("Second round competed");
            return SUCCESS_AND_NEXT;
        }

        LoyaltyPointPayment loyaltyPayment = paymentGroupLoyaltyManager.findLoyaltyPointPayment(order, true);
        BigDecimal amountForSubtract = null;
        if (loyaltyPayment == null) {
            loyaltyPayment = paymentGroupLoyaltyManager.addNewLoyaltyPaymentGroupToOrder(order, spend, null, null);
            amountForSubtract = new BigDecimal(loyaltyPayment.getAmount());
            logDebug("Created new LoyaltyPointPayment group.");
        } else {
            amountForSubtract = paymentGroupLoyaltyManager.getLoyaltyCalculatorTools()
                                                          .recalculateLoyaltyPayment(loyaltyPayment);
        }

        List<PaymentGroupRelationship> allPaymentGroupRelationships =
                paymentGroupLoyaltyManager.getAllPaymentGroupRelationships(order);
        for (PaymentGroupRelationship relationship : allPaymentGroupRelationships) {
            PaymentGroup currentPG = relationship.getPaymentGroup();

            logDebug("for replaced Group: " + currentPG.getPaymentGroupClassType());
            logDebug("replaced Group: " + currentPG);
            BigDecimal currentPGAmount = new BigDecimal(currentPG.getAmount());
            int i = currentPGAmount.compareTo(amountForSubtract);
            switch (i)
            {
                case 0:
                {
                    relationship.setPaymentGroup(loyaltyPayment);
                    paymentGroupLoyaltyManager.removePaymentGroupFromOrder(order, currentPG.getId());
                    map.put(PLOrderConstants.COMPLETE_PL_PARAM, true);
                    logDebug("equals: " + currentPG);
                    return SUCCESS_AND_NEW_ROUND;
                }
                case 1:
                {
                    if (relationship.getRelationshipClassType().equals("paymentGroupCommerceItem") ||
                            relationship.getRelationshipClassType().equals("paymentGroupOrder") ||
                            relationship.getRelationshipClassType().equals("paymentGroupShippingGroup")){



                        BigDecimal remainToPay = currentPGAmount.subtract(amountForSubtract);
                        currentPG.setAmount(remainToPay.doubleValue());

                        PaymentGroupRelationship slittedRelationship =
                                (PaymentGroupRelationship) orderManager.getOrderTools()
                                                                       .createRelationship(relationship.getRelationshipClassType());
                        slittedRelationship.setPaymentGroup(loyaltyPayment);
                        slittedRelationship.setAmount(amountForSubtract.doubleValue());
                        order.addPaymentGroupRelationship(slittedRelationship);

                        if (relationship.getRelationshipClassType().equals("paymentGroupCommerceItem")){
                            CommerceItemRelationship splittedCiRel = (CommerceItemRelationship) slittedRelationship;
                            CommerceItemRelationship currentCiRel = (CommerceItemRelationship) relationship;
                            splittedCiRel.setCommerceItem(currentCiRel.getCommerceItem());
                            splittedCiRel.setQuantity(currentCiRel.getQuantity());
                            splittedCiRel.setStateDetail(currentCiRel.getStateDetail());
                            splittedCiRel.setReturnedQuantity(currentCiRel.getReturnedQuantity());
                            splittedCiRel.setState(currentCiRel.getState());
                            splittedCiRel.setRelationshipType(RelationshipTypes.PAYMENTAMOUNT);
                            splittedCiRel.setRelationshipType(RelationshipTypes.PAYMENTAMOUNTREMAINING);
                        }
                        if (relationship.getRelationshipClassType().equals("paymentGroupOrder")){
                            OrderRelationship splittedCiRel = (OrderRelationship) slittedRelationship;
                            OrderRelationship currentCiRel = (OrderRelationship) relationship;
                            splittedCiRel.setOrder(order);
                            splittedCiRel.setRelationshipType(RelationshipTypes.ORDERAMOUNT);
                            splittedCiRel.setRelationshipType(RelationshipTypes.ORDERAMOUNTREMAINING);
                        }

                        if (relationship.getRelationshipClassType().equals("paymentGroupShippingGroup")){
                            ShippingGroupRelationship splittedCiRel = (ShippingGroupRelationship) slittedRelationship;
                            ShippingGroupRelationship currentCiRel = (ShippingGroupRelationship) relationship;
                            splittedCiRel.setShippingGroup(currentCiRel.getShippingGroup());
                            splittedCiRel.setRelationshipType(RelationshipTypes.SHIPPINGAMOUNT);
                            splittedCiRel.setRelationshipType(RelationshipTypes.SHIPPINGAMOUNTREMAINING);
                        }
                    }
                    //split
                    map.put(PLOrderConstants.COMPLETE_PL_PARAM, true);
                    return SUCCESS_AND_NEW_ROUND;
                }
                case -1:
                {
                    relationship.setPaymentGroup(loyaltyPayment);
                    paymentGroupLoyaltyManager.removePaymentGroupFromOrder(order, currentPG.getId());
                    amountForSubtract = amountForSubtract.subtract(currentPGAmount);
                }
            }
        }
        if (amountForSubtract.compareTo(BigDecimal.ZERO) == 1){
            loyaltyPayment.setAmount(new BigDecimal(loyaltyPayment.getAmount()).subtract(amountForSubtract).doubleValue());
        }
        map.put(PLOrderConstants.COMPLETE_PL_PARAM, true);
        return SUCCESS_AND_NEW_ROUND;
    }




    public int[] getRetCodes() {
        return new int[]{SUCCESS_AND_NEXT, SUCCESS_AND_NEW_ROUND};
    }

    public PaymentGroupLoyaltyManager getPaymentGroupLoyaltyManager() {
        return paymentGroupLoyaltyManager;
    }

    public void setPaymentGroupLoyaltyManager(PaymentGroupLoyaltyManager paymentGroupLoyaltyManager) {
        this.paymentGroupLoyaltyManager = paymentGroupLoyaltyManager;
    }


}

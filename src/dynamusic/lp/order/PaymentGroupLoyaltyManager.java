package dynamusic.lp.order;


import atg.commerce.CommerceException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.nucleus.ServiceException;
import atg.userprofiling.Profile;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.pricing.LoyaltyCalculatorTools;

import java.util.ArrayList;
import java.util.List;

import static dynamusic.lp.LoyaltyConstants.USER_TRANSACTION_AMOUNT_PRN;

public class PaymentGroupLoyaltyManager extends PaymentGroupManager{

    private LoyaltyCalculatorTools loyaltyCalculatorTools;
    private PLOrderConfig plOrderConfig;

    @Override
    public void doStartService() throws ServiceException {
        super.doStartService();
        if (loyaltyCalculatorTools == null || plOrderConfig == null){
            logError("Not all dependencies set!");
        }
    }

    public LoyaltyPointPayment createLoyaltyPaymentGroup() throws CommerceException {
        return (LoyaltyPointPayment) createPaymentGroup(PLOrderConstants.PL_PAYMENT_GROUP_TYPE);
    }

    public LoyaltyPointPayment addNewLoyaltyPaymentGroupToOrder(Order order, Integer spendPL,
                                                                String currencyCode, String profileId)
            throws CommerceException {
        LoyaltyPointPayment plPtGr = createLoyaltyPaymentGroup();
        initializeLoyaltyPaymentGroup(plPtGr, spendPL, defineCurrencyCode(order, currencyCode), profileId);
        addPaymentGroupToOrder(order, plPtGr);
        return plPtGr;
    }

    public LoyaltyPointPayment addNewLoyaltyPaymentGroupToOrder(Order order, Integer spendPL, String currencyCode,
                                                                String profileId,
                                                                int index)
            throws CommerceException {
        LoyaltyPointPayment loyaltyPaymentGroup = createLoyaltyPaymentGroup();
        initializeLoyaltyPaymentGroup(loyaltyPaymentGroup, spendPL, defineCurrencyCode(order, currencyCode), profileId);
        addPaymentGroupToOrder(order, loyaltyPaymentGroup, index);
        return loyaltyPaymentGroup;
    }

      /**
     *
     * @param profileId - if null means that profileId equals profileId of order owner
     */

    public void initializeLoyaltyPaymentGroup(LoyaltyPointPayment paymentGroup, Integer spendPL,
                                              String currencyCode, String profileId ){
        paymentGroup.setSpendPL(spendPL);
        paymentGroup.setCurrencyCode(currencyCode);
        paymentGroup.setProfileId(profileId);
        loyaltyCalculatorTools.recalculateLoyaltyPayment(paymentGroup);
    }


    /**
     * find first LoyaltyPointPayment;
     * @param order
     * @return
     */
    public LoyaltyPointPayment findLoyaltyPointPayment(Order order, boolean mergeIfNotOne) throws CommerceException{
        if (order == null || order.getPaymentGroups() == null){
            throw new InvalidParameterException(String.format("Order or it's payment groups must not be null.Order: " +
                    "%s",order));
        }
        LoyaltyPointPayment result = null;
        List<PaymentGroup> groupList = order.getPaymentGroups();
        List<String> idToRemove = new ArrayList<String>();
        for (PaymentGroup ptGr : groupList){
            if (ptGr.getPaymentGroupClassType().equals(PLOrderConstants.PL_PAYMENT_GROUP_TYPE)){
                if (result == null){
                    result = (LoyaltyPointPayment) ptGr;
                    if (!mergeIfNotOne){
                        break;
                    }
                } else {
                    result.setSpendPL(result.getSpendPL() + ((LoyaltyPointPayment) ptGr).getSpendPL());
                    ptGr.setAmount(0);
                    idToRemove.add(ptGr.getId());
                }
            }
        }
        for (String id: idToRemove){
            removePaymentGroupFromOrder(order, id);
        }
        return result;
    }


    private String defineCurrencyCode(Order order, String currencyCode){
        return currencyCode !=null ? currencyCode : order.getPriceInfo() != null
                                                    ? order.getPriceInfo().getCurrencyCode() : null;
    }

    public LoyaltyCalculatorTools getLoyaltyCalculatorTools() {
        return loyaltyCalculatorTools;
    }

    public void setLoyaltyCalculatorTools(LoyaltyCalculatorTools loyaltyCalculatorTools) {
        this.loyaltyCalculatorTools = loyaltyCalculatorTools;
    }

    public PLOrderConfig getPlOrderConfig() {
        return plOrderConfig;
    }

    public void setPlOrderConfig(PLOrderConfig plOrderConfig) {
        this.plOrderConfig = plOrderConfig;
    }
}

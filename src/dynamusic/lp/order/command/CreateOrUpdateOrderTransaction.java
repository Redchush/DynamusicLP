package dynamusic.lp.order.command;


import atg.repository.MutableRepository;
import dynamusic.lp.CreateOrUpdateTransaction;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;

import java.util.Map;

public class CreateOrUpdateOrderTransaction extends CreateOrUpdateTransaction {

    public CreateOrUpdateOrderTransaction() { super();}

    public CreateOrUpdateOrderTransaction(OrderLoyaltyManager loyaltyManager,
                                          LoyaltyPointPaymentInfo info,
                                          String updateId, boolean createLink) {
        Map<String, Object> propertyMap =
                loyaltyManager.getPlOrderCommandHelper().createPropertyMap(info, updateId == null);
        setDescriptorName(PLOrderConstants.PL_ORDER_TRANSACTION_TYPE);
        setMapProperties(propertyMap);
        setCreateLink(createLink);
        setRepository((MutableRepository) loyaltyManager.getLoyaltyRepository());
    }

    public CreateOrUpdateOrderTransaction(OrderLoyaltyManager loyaltyManager,
                                          Map<String, Object> properties, boolean createLink) {
        setLoyaltyManager(loyaltyManager);
        setDescriptorName(PLOrderConstants.PL_ORDER_TRANSACTION_TYPE);
        setMapProperties(properties);
        setCreateLink(createLink);
        setRepository((MutableRepository) getLoyaltyManager().getLoyaltyRepository());
    }


}

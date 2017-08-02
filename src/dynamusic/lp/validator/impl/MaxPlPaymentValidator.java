package dynamusic.lp.validator.impl;


import atg.commerce.order.Order;
import atg.repository.RepositoryItem;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.system.validator.ValidationResult;
import dynamusic.system.validator.Validator;

import java.util.Collections;
import java.util.Map;

public class MaxPlPaymentValidator implements Validator<Map, Void>{

    private final String ERROR_MSG_FORMAT = "The sum %d is more than order total. Max amount of PL that you can spend" +
            " is %d PL";
    private OrderLoyaltyManager orderLoyaltyManager;

    public ValidationResult<Void> validate(Map values) {
        return validate( (RepositoryItem) values.get(PLOrderConstants.PROFILE_PARAM),
                (Order) values.get(PLOrderConstants.ORDER_PARAM), (Integer) values.get(PLOrderConstants.SPENDPL_PARAM));
    }

    private ValidationResult<Void> validate(RepositoryItem profile, Order order, Integer spendPL){
        Integer maxSum = orderLoyaltyManager.maxPlToSpendForOrder(profile, order);
        if (spendPL > maxSum)
        {
            return new ValidationResult<Void>(Collections.singletonList(String.format(ERROR_MSG_FORMAT, spendPL, maxSum)),
                    ValidationResult.STATUS_FAIL);
        }
        return new ValidationResult<Void>(ValidationResult.STATUS_OK);
    }

    public OrderLoyaltyManager getOrderLoyaltyManager() {
        return orderLoyaltyManager;
    }

    public void setOrderLoyaltyManager(OrderLoyaltyManager orderLoyaltyManager) {
        this.orderLoyaltyManager = orderLoyaltyManager;
    }
}

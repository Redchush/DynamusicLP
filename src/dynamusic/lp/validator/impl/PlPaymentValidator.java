package dynamusic.lp.validator.impl;


import atg.repository.RepositoryItem;
import dynamusic.lp.PLOrderConstants;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.system.validator.ValidationResult;
import dynamusic.system.validator.Validator;

import java.util.Collections;
import java.util.Map;

public class PlPaymentValidator implements Validator<Map, Void> {

    private OrderLoyaltyManager orderLoyaltyManager;

    public ValidationResult<Void> validate(Map values) {
        return validate( (RepositoryItem) values.get(PLOrderConstants.PROFILE_PARAM),
                (Integer) values.get(PLOrderConstants.SPENDPL_PARAM));
    }

    private ValidationResult<Void> validate(RepositoryItem profile, Integer spendPL){
        if (spendPL <= 0)
        {
            return new ValidationResult<Void>(Collections.singletonList("PL amount must be positive integer."),
                    ValidationResult.STATUS_FAIL);
        }

        if (!orderLoyaltyManager.userHasSum(profile, spendPL))
        {
            return new ValidationResult<Void>(Collections.singletonList("You have not enough PL to pay."),
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

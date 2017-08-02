package dynamusic.lp.order.validator;


import atg.commerce.order.Order;
import atg.repository.RepositoryItem;
import dynamusic.lp.PLOrderConstants;
import dynamusic.system.validator.ValidationStrategy;
import dynamusic.system.validator.Validator;
import dynamusic.system.validator.ValidatorCallback;

import java.util.HashMap;
import java.util.Map;

public class SpendPLValidatorFacade {

    private ValidationStrategy validationStrategy;
    private Validator[] validators;

    public boolean validate(RepositoryItem profile, Order order, Integer plToSpend, ValidatorCallback callback){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(PLOrderConstants.PROFILE_PARAM, profile);
        map.put(PLOrderConstants.ORDER_PARAM, order);
        map.put(PLOrderConstants.SPENDPL_PARAM, plToSpend);
        return validationStrategy.executeValidation(validators, map, callback);
    }

    public ValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    public void setValidationStrategy(ValidationStrategy validationStrategy) {
        this.validationStrategy = validationStrategy;
    }

    public Validator[] getValidators() {
        return validators;
    }

    public void setValidators(Validator[] validators) {
        this.validators = validators;
    }
}

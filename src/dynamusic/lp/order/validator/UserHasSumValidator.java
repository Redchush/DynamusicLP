package dynamusic.lp.order.validator;


import atg.userprofiling.Profile;
import dynamusic.lp.PLOrderConstants;
import dynamusic.system.validator.DictionaryValidator;
import dynamusic.system.validator.ValidationResult;

import java.util.Dictionary;

public class UserHasSumValidator implements DictionaryValidator {

    private Profile profile;

    public ValidationResult validate(Dictionary values) {
        values.get(PLOrderConstants.SPENDPL_PARAM);
//        public boolean validate(){
//            Integer userAmountPL = (Integer) getProfile().getPropertyValue(USER_TRANSACTION_AMOUNT_PRN);
//            return userAmountPL > plToSpend;
//        }
        return null;
    }




    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}

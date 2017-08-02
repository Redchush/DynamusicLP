package dynamusic.lp.handler;


import atg.commerce.order.purchase.ExpressCheckoutFormHandler;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import dynamusic.lp.order.OrderLoyaltyManager;
import dynamusic.lp.order.validator.SpendPLValidatorFacade;
import dynamusic.system.validator.ValidationStrategy;
import dynamusic.system.validator.Validator;
import dynamusic.system.validator.impl.AddFormExceptionCallback;

import javax.servlet.ServletException;
import java.io.IOException;

public abstract class ExpressCheckoutWithPL extends ExpressCheckoutFormHandler {

    protected static final Integer NULL_PL_AMOUNT = 0;

    protected SpendPLHolder spendPLHolder;
    protected Boolean renovateSpendPL;

    private Integer maxValueToSpend;

    protected ValidationStrategy validationStrategy;
    protected Validator[] validators;

    protected SpendPLValidatorFacade spendPLValidatorFacade;
    protected OrderLoyaltyManager orderLoyaltyManager;

    @Override
    public void preExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        super.preExpressCheckout(pRequest, pResponse);
        if (renovateSpendPL != null && renovateSpendPL)
        {
            renovateSpendPL();
        }
    }

    @Override
    public void postExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
        super.postExpressCheckout(pRequest, pResponse);
        if (isCommitOrder() && !getFormError())
        {
            renovateSpendPL();
        }
    }

    protected void renovateSpendPL(){
        spendPLHolder.setSpendPL(0);
        spendPLHolder.setAmountInCurrency(0.0);
    }

    public Boolean getRenovateSpendPL() {
        return renovateSpendPL;
    }

    public void setRenovateSpendPL(Boolean renovateSpendPL) {
        this.renovateSpendPL = renovateSpendPL;
    }

    public Integer getPlToSpend() {
        return getSpendPLHolder().getSpendPL() == null ? NULL_PL_AMOUNT : getSpendPLHolder().getSpendPL();
    }

    public void setPlToSpend(Integer plToSpend) {
        if (plToSpend != null)
        {
            if(spendPLValidatorFacade.validate(getProfile(), getOrder(), plToSpend, new AddFormExceptionCallback(this)))
            {
                spendPLHolder.setSpendPL(plToSpend);
                spendPLHolder.setAmountInCurrency(getOrderLoyaltyManager().plToSpendInCurrency(plToSpend, getOrder()));
            }
        }
    }

    public Integer getMaxValueToSpend() {
        return orderLoyaltyManager.maxPlToSpendForOrder(getProfile(), getOrder());
    }

    public void setMaxValueToSpend(Integer maxValueToSpend) {
        this.maxValueToSpend = maxValueToSpend;
    }

    public SpendPLHolder getSpendPLHolder() {
        return spendPLHolder;
    }

    public void setSpendPLHolder(SpendPLHolder spendPLHolder) {
        this.spendPLHolder = spendPLHolder;
    }


    public OrderLoyaltyManager getOrderLoyaltyManager() {
        return orderLoyaltyManager;
    }

    public void setOrderLoyaltyManager(OrderLoyaltyManager orderLoyaltyManager) {
        this.orderLoyaltyManager = orderLoyaltyManager;
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

package dynamusic.lp.order.payment.processor;


import atg.commerce.CommerceException;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.Constants;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.commerce.payment.processor.ProcProcessPaymentGroup;
import atg.payment.PaymentStatus;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;
import dynamusic.lp.order.payment.LoyaltyPointPaymentManager;
import dynamusic.lp.order.payment.LoyaltyPointPaymentProcessor;
import dynamusic.lp.order.payment.LoyaltyPointPaymentStatus;

public class ProcProcessLoyaltyPointPayment extends ProcProcessPaymentGroup {


    public LoyaltyPointPaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
        LoyaltyPointPaymentInfo lpp = (LoyaltyPointPaymentInfo) pParams.getPaymentInfo();
        return getLoyaltyPointPaymentProcessor(pParams).authorize(lpp);
    }

    public LoyaltyPointPaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
        PaymentGroup pg = pParams.getPaymentGroup();
        PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);
        try{
            return getLoyaltyPointPaymentProcessor(pParams).debit((LoyaltyPointPaymentInfo) pParams.getPaymentInfo(),
                    authStatus);
        } catch (ClassCastException e){
            throw new PaymentException(Constants.INVALID_AUTH_STATUS);
        }
    }

    public LoyaltyPointPaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams) throws CommerceException {
        PaymentGroup pg = pParams.getPaymentGroup();
        PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);
        try {
           return getLoyaltyPointPaymentProcessor(pParams).credit((LoyaltyPointPaymentInfo) pParams.getPaymentInfo(),
                    authStatus);
        } catch (ClassCastException e){
            throw new PaymentException(Constants.INVALID_AUTH_STATUS);
        }
    }

    private LoyaltyPointPaymentProcessor getLoyaltyPointPaymentProcessor(PaymentManagerPipelineArgs pParams) {
        return ((LoyaltyPointPaymentManager) pParams.getPaymentManager()).getLoyaltyPointPaymentProcessor();
    }


}

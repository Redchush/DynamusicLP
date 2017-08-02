package dynamusic.lp.order.payment.processor;


import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.payment.PaymentManagerAction;
import atg.commerce.payment.PaymentManagerPipelineArgs;
import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;

public class ProcCreateLoyaltyPointPaymentInfo extends GenericService
        implements PipelineProcessor {

    private static final int SUCCESS = 1;
    protected String loyaltyPointPaymentInfoClass = "dynamusic.lp.order.payment.LoyaltyPointPaymentInfo";

    public int[] getRetCodes() {
        return new int[]{SUCCESS};
    }

    public int runProcess(Object params, PipelineResult pipelineResult) throws Exception {
        PaymentManagerPipelineArgs args = (PaymentManagerPipelineArgs)params;

        LoyaltyPointPayment group = (LoyaltyPointPayment) args.getPaymentGroup();
        Order order = args.getOrder();
        if (group == null || order == null)
        {
            throw new InvalidParameterException("Not all PaymentManagerPipelineArgs presented group: " + group + " " +
                    ",order"  + order);
        }

        LoyaltyPointPaymentInfo info = new LoyaltyPointPaymentInfo();
        info.setProfileId(order.getProfileId());
        info.setOrderId(order.getId());

        StringBuilder builder = new StringBuilder(" payment for ").append(order.getId())
                                                                  .append(" in by payment group ")
                                                                  .append(group.getId());
        setAmounts(args.getAction(), group, info, builder);
        info.setAmountInCurrency(group.getAmount());
        info.setDescription(builder.toString());

        if (isLoggingDebug())
        {
            logDebug("created info: " + info);
        }
        args.setPaymentInfo(info);
        return SUCCESS;
    }

    public void setAmounts(PaymentManagerAction action, LoyaltyPointPayment group,
                           LoyaltyPointPaymentInfo info, StringBuilder builder) throws InvalidParameterException {

        if (action.equals(PaymentManagerAction.AUTHORIZE))
        {
            builder.insert(0, "Authorized:");
            info.setAmountInPL(-group.getSpendPL());
            return;
        }
        if (action.equals(PaymentManagerAction.DEBIT))
        {
            builder.insert(0, "Completed:");
            info.setAmountInPL(-group.getSpendPL());
            return;
        }
        if (action.equals(PaymentManagerAction.CREDIT))
        {
            builder.insert(0, "Rejected:");
            info.setAmountInPL(group.getSpendPL());
         } else {
            throw new InvalidParameterException("Unknown action " + action);
        }

    }

    public String getLoyaltyPointPaymentInfoClass() {
        return loyaltyPointPaymentInfoClass;
    }

    public void setLoyaltyPointPaymentInfoClass(String loyaltyPointPaymentInfoClass) {
        this.loyaltyPointPaymentInfoClass = loyaltyPointPaymentInfoClass;
    }
}

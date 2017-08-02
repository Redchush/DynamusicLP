package dynamusic.lp.order.pricing;


import atg.commerce.pricing.OrderPriceInfo;
import dynamusic.lp.order.payment.OrderTransaction;

public class LoyaltyOrderPriceInfo extends OrderPriceInfo {

    private Integer receivedPL;
    private String conversion;
    private Integer bonusPercent;
    private Integer bonusRoundRule;
    private OrderTransaction transaction;

    public LoyaltyOrderPriceInfo() {}

    public Integer getReceivedPL() {
        return receivedPL;
    }

    public void setReceivedPL(Integer receivedPL) {
        this.receivedPL = receivedPL;
    }

    public String getConversion() {
        return conversion;
    }

    public void setConversion(String conversion) {
        this.conversion = conversion;
    }

    public Integer getBonusRoundRule() {
        return bonusRoundRule;
    }

    public void setBonusRoundRule(Integer bonusRoundRule) {
        this.bonusRoundRule = bonusRoundRule;
    }

    public Integer getBonusPercent() {
        return bonusPercent;
    }

    public void setBonusPercent(Integer bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public OrderTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(OrderTransaction transaction) {
        this.transaction = transaction;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("LoyaltyOrderPriceInfo{");
        sb.append(super.toString()).append(" ");

        sb.append("receivedPL=").append(receivedPL);
        sb.append(", conversion='").append(conversion).append('\'');
        sb.append(", bonusPercent=").append(bonusPercent);
        sb.append(", bonusRoundRule=").append(bonusRoundRule);
        sb.append(", transaction=").append(transaction);
        sb.append('}');
        return sb.toString();
    }
}

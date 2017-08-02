package dynamusic.lp.handler;


public class SpendPLHolder {

    private Integer spendPL;
    private Double amountInCurrency;

    public SpendPLHolder() {
        this.spendPL = 0;
        this.amountInCurrency = 0.0;
    }

    public Integer getSpendPL() {
        return spendPL;
    }

    public void setSpendPL(Integer spendPL) {
        this.spendPL = spendPL;
    }

    public Double getAmountInCurrency() {
        return amountInCurrency;
    }

    public void setAmountInCurrency(Double amountInCurrency) {
        this.amountInCurrency = amountInCurrency;
    }
}

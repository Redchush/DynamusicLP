package dynamusic.lp.order.pricing;


import java.math.BigDecimal;

public class Conversion {

    private BigDecimal weightPL;
    private BigDecimal weightCurrency;

    public Conversion() {}

    public Conversion(Integer weightPL, Integer weightCurrency) {
        this.weightPL = new BigDecimal(weightPL);
        this.weightCurrency = new BigDecimal(weightCurrency);
    }

    public Conversion(BigDecimal weightPL, BigDecimal weightCurrency) {
        this.weightPL = weightPL;
        this.weightCurrency = weightCurrency;
    }

    public BigDecimal getWeightPL() {
        return weightPL;
    }

    public void setWeightPL(BigDecimal weightPL) {
        this.weightPL = weightPL;
    }

    public BigDecimal getWeightCurrency() {
        return weightCurrency;
    }

    public void setWeightCurrency(BigDecimal weightCurrency) {
        this.weightCurrency = weightCurrency;
    }

    public String getString(){
        return weightPL + ":" + weightCurrency;
    }
}

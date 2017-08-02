package dynamusic.lp.order.pricing;


import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import dynamusic.lp.order.LoyaltyPointPayment;
import dynamusic.lp.order.PLOrderConfig;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LoyaltyCalculatorTools extends GenericService {

    private static final BigDecimal PERCENT_100 = new BigDecimal(100);

    private static final Integer CURRENCY_SCALE = 2;
    private static final Integer BONUS_SCALE = 2;
    private static final Integer PL_SCALE = 0;
    private BigDecimal BONUS;

    private PLOrderConfig plOrderConfig;

    public LoyaltyCalculatorTools() {}


    @Override
    public void doStartService() throws ServiceException {
        BONUS = new BigDecimal(plOrderConfig.getBonusPercent()).divide(PERCENT_100, BONUS_SCALE,
                 plOrderConfig.getBonusRoundRule());
    }

    public Integer maxLoyaltyToSpend(Integer profilePL, double onAmount, String currencyCode){
        BigDecimal orderAmount = new BigDecimal(onAmount);
        if (profilePL < 1 || orderAmount.compareTo(BigDecimal.ZERO) != 1)
        {
            return 0;
        }
        BigDecimal orderAmountInLP = convertCurrencyToLP(currencyCode,orderAmount,plOrderConfig.isApplyDefaultIfEmptyConversion(),
                RoundingMode.CEILING);
        return profilePL < orderAmountInLP.intValue() ? profilePL : orderAmountInLP.intValue();
    }


    public BigDecimal recalculateLoyaltyPayment(LoyaltyPointPayment payment){
        BigDecimal amountInCurrency = calculatePaymentAmount(payment.getCurrencyCode(), payment.getSpendPL());
        payment.setAmount(amountInCurrency.doubleValue());
        payment.setPaymentRoundRule(getPlOrderConfig().getBonusRoundRule());
        payment.setConversion(getConversionString(payment.getCurrencyCode(),
                plOrderConfig.isApplyDefaultIfEmptyConversion()));
        return amountInCurrency;
    }

    /**
     * @param priceInfo
     * @return applied bonus in PL. Use default conversion if conversion for this currency isn't found
     */
    public BigDecimal applyAddBonusPL(LoyaltyOrderPriceInfo priceInfo){
        BigDecimal result = calculateBonus(priceInfo.getCurrencyCode(), priceInfo.getAmount());
        priceInfo.setReceivedPL(result.intValue());
        priceInfo.setBonusPercent(plOrderConfig.getBonusPercent());
        priceInfo.setConversion(getConversionString(priceInfo.getCurrencyCode(),
                plOrderConfig.isApplyDefaultIfEmptyConversion()));
        priceInfo.setBonusRoundRule(plOrderConfig.getBonusRoundRule());
        return result;

    }

    public BigDecimal calculateBonus(String currencyCode, double orderAmount){
        BigDecimal result = new BigDecimal(orderAmount).setScale(2, plOrderConfig.getPaymentRoundRule()).multiply(BONUS);
        return convertCurrencyToLPForBonus(currencyCode, result, plOrderConfig.isApplyDefaultIfEmptyConversion());
    }

    public BigDecimal calculatePaymentAmount(String currencyCode, Integer plSpend){
        return convertLPToCurrency(currencyCode, new BigDecimal(plSpend), plOrderConfig.isApplyDefaultIfEmptyConversion());
    }

    /**
     * @return null if this currency not maintained now  and applyDefaultIfEmpty is false;
     * result of usage default conversion if this currency not maintained now  and applyDefaultIfEmpty is true;
     * otherwise return result of usage special conversion;
     *
     */
    public BigDecimal convertCurrencyToLPForBonus(String currencyCode, BigDecimal currency, boolean applyDefaultIfEmpty){
        return convertCurrencyToLP(currencyCode, currency, applyDefaultIfEmpty, RoundingMode.valueOf(plOrderConfig.getBonusRoundRule()));
    }

    protected BigDecimal convertCurrencyToLP(String currencyCode, BigDecimal currency,
                                             boolean applyDefaultIfEmpty, RoundingMode roundingMode){
        Conversion conversion = getConversion(currencyCode, applyDefaultIfEmpty);
        if (conversion == null)
        {
            return null;
        }
        return currency.multiply(conversion.getWeightPL())
                       .divide(conversion.getWeightCurrency(), PL_SCALE, roundingMode);
    }


    public BigDecimal convertLPToCurrency(String currencyCode, BigDecimal pl, boolean applyDefaultIfEmpty){
        Conversion conversion = getConversion(currencyCode, applyDefaultIfEmpty);
        if (conversion == null)
        {
            return null;
        }
        return pl.multiply(conversion.getWeightCurrency()).divide(conversion.getWeightPL(),
                CURRENCY_SCALE, RoundingMode.valueOf(getPlOrderConfig().getPaymentRoundRule()));

    }

    public String getConversionString(String currencyCode, boolean applyDefaultIfEmpty){
        Conversion conversion = getConversion(currencyCode, applyDefaultIfEmpty);
        return  conversion == null ? null : conversion.getString();
    }

    private Conversion getConversion (String currencyCode, boolean applyDefaultIfEmpty){
        Conversion conversion = null;
        if (plOrderConfig.getConversionMap()== null ||
                (conversion = plOrderConfig.getConversionMap().get(currencyCode)) == null)
        {
            logWarning("There was no special conversion for " + currencyCode);
            if (!applyDefaultIfEmpty)
            {
                return null;
            }
            return plOrderConfig.getDefaultConversion();
        }
        return conversion;
    }

    public boolean hasSpecialConversion(String currencyCode){
        return plOrderConfig.getConversionMap().containsKey(currencyCode);
    }

    public PLOrderConfig getPlOrderConfig() {
        return plOrderConfig;
    }

    public void setPlOrderConfig(PLOrderConfig plOrderConfig) {
        this.plOrderConfig = plOrderConfig;
    }


}

package dynamusic.lp.order;


import atg.nucleus.GenericService;
import atg.nucleus.ServiceException;
import atg.repository.MutableRepository;
import atg.repository.Repository;
import dynamusic.lp.order.pricing.Conversion;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PLOrderConfig extends GenericService {

    private MutableRepository orderRepository;
    private MutableRepository profileRepository;
    private MutableRepository loyaltyRepository;

    private Map<String, Conversion> conversionMap;
    private Conversion defaultConversion;

    private Map<String, String> conversionStringMap;
    private String defaultStringConversion;

    private Integer bonusPercent;
    private Integer bonusRoundRule;
    private Integer paymentRoundRule;
    private boolean applyDefaultIfEmptyConversion;


    public PLOrderConfig() {}

    @Override
    public void doStartService() throws ServiceException {
        reconfigure();
    }

    public void reconfigure(){
        this.defaultConversion = parseConversion(defaultStringConversion);
        parseAndSetConversionMap();
    }

    private void parseAndSetConversionMap(){
        if (conversionStringMap != null)
        {
            this.conversionMap = new HashMap<String, Conversion>(conversionStringMap.size());
            for(Map.Entry<String, String> entry : conversionStringMap.entrySet())
            {
                conversionMap.put(entry.getKey(), parseConversion(entry.getValue()));
            }
        } else {
            logWarning("String conversion map isn't set! ");
        }
    }

    private Conversion parseConversion(String conversion){
        String[] data = conversion.split(":");
        Integer pl = Integer.parseInt(data[0]);
        Integer currency = Integer.parseInt(data[1]);
        return new Conversion(pl, currency);
    }

    public Integer getBonusPercent() {
        return bonusPercent;
    }

    public void setBonusPercent(Integer bonusPercent) {
        this.bonusPercent = bonusPercent;
    }

    public Integer getBonusRoundRule() {
        return bonusRoundRule;
    }

    public void setBonusRoundRule(Integer bonusRoundRule) {
        this.bonusRoundRule = bonusRoundRule;
    }

    public Integer getPaymentRoundRule() {
        return paymentRoundRule;
    }

    public void setPaymentRoundRule(Integer paymentRoundRule) {
        this.paymentRoundRule = paymentRoundRule;
    }

    public String getDefaultStringConversion() {
        return defaultStringConversion;
    }

    public void setDefaultStringConversion(String defaultStringConversion) {
        this.defaultStringConversion = defaultStringConversion;
    }

    /**
     * Represent conversion rules from current currency to PL, mapped
     * by currency code.
     * Format: currency:pl
     */
    public Map<String, String> getConversionStringMap() {
        return conversionStringMap;
    }

    public void setConversionStringMap(Map<String, String> conversionStringMap) {
        this.conversionStringMap = conversionStringMap;
    }

    public Map<String, Conversion> getConversionMap() {
        return conversionMap;
    }

    public void setConversionMap(Map<String, Conversion> conversionMap) {
        this.conversionMap = conversionMap;
    }

    public Conversion getDefaultConversion() {
        return defaultConversion;
    }

    public void setDefaultConversion(Conversion defaultConversion) {
        this.defaultConversion = defaultConversion;
    }

    public boolean isApplyDefaultIfEmptyConversion() {
        return applyDefaultIfEmptyConversion;
    }

    public void setApplyDefaultIfEmptyConversion(boolean applyDefaultIfEmptyConversion) {
        this.applyDefaultIfEmptyConversion = applyDefaultIfEmptyConversion;
    }

    public static class RoundRules{
        public static class Bonus{
            public static final int CUSTOMER_PROFIT = BigDecimal.ROUND_CEILING; // 2;
            public static final int SHOP_PROFIT = BigDecimal.ROUND_FLOOR; //3
        }

        public static class Payment{
            public static final int CUSTOMER_PROFIT = BigDecimal.ROUND_HALF_UP; //4
            public static final int SHOP_PROFIT = BigDecimal.ROUND_HALF_DOWN; //5
        }
    }

    public MutableRepository getOrderRepository() {
        return orderRepository;
    }

    public void setOrderRepository(MutableRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public MutableRepository getProfileRepository() {
        return profileRepository;
    }

    public void setProfileRepository(MutableRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public MutableRepository getLoyaltyRepository() {
        return loyaltyRepository;
    }

    public void setLoyaltyRepository(MutableRepository loyaltyRepository) {
        this.loyaltyRepository = loyaltyRepository;
    }
}

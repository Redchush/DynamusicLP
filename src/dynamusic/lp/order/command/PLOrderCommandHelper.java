package dynamusic.lp.order.command;


import atg.repository.MutableRepository;
import dynamusic.lp.CommandHelper;
import dynamusic.lp.order.PLOrderConfig;
import dynamusic.lp.order.payment.LoyaltyPointPaymentInfo;
import dynamusic.system.command.commandimpl.ChangeItemProperty;

import java.util.Map;

import static dynamusic.lp.PLOrderConstants.*;

public class PLOrderCommandHelper {

    private PLOrderConfig plOrderConfig;

    public Map<String, Object> createPropertyMap(LoyaltyPointPaymentInfo info, boolean ignoreNulls){
        Map<String, Object> result = CommandHelper.createPropertyMap(info.getProfileId(), info.getAmountInPL(),
                                                                     info.getDescription(), ignoreNulls);
        return addSpecificOrderTransactionProperties(info.getOrderId(), result, ignoreNulls);
    }

    public Map<String, Object> createPropertyMap(String profileId, Integer amount, String description,
                                                 String orderId, boolean ignoreNulls){
        Map<String, Object> result = CommandHelper.createPropertyMap(profileId, amount, description, ignoreNulls);
        return addSpecificOrderTransactionProperties(orderId, result, ignoreNulls);
    }

    public Map<String, Object> addSpecificOrderTransactionProperties(String orderId, Map<String, Object>
            container, boolean ignoreNulls){
        if (orderId != null || !ignoreNulls)
        {
            container.put(PL_TRANSACTION_ORDER_PRN, orderId);
        }
        return container;
    }

    public ChangeItemProperty fillChangeCommand(ChangeItemProperty command, String mainItemIdValue, String
            slaveItemIdValue){
        command.setMainRepository(plOrderConfig.getLoyaltyRepository());
        command.setSlaveRepository(plOrderConfig.getOrderRepository());

        command.setMainItemType(PL_ORDER_TRANSACTION_TYPE);
        command.setSlaveItemType(ORDER_TYPE);

        command.setMainItemIdValue(mainItemIdValue);
        command.setSlaveItemIdValue(slaveItemIdValue);
        command.setMainItemSlaveProperty(PL_TRANSACTION_ORDER_PRN);
        return command;
    }

    public PLOrderConfig getPlOrderConfig() {
        return plOrderConfig;
    }

    public void setPlOrderConfig(PLOrderConfig plOrderConfig) {
        this.plOrderConfig = plOrderConfig;
    }



    //    private ChangeListProperty fillChangeCommand(ChangeListProperty command, String mainItemIdValue, String
//            slaveItemIdValue){
//        command.setMainRepository((MutableRepository) getOrderRepository());
//        command.setSlaveRepository(getLoyaltyRepository());
//        command.setMainItemType(ORDER_TYPE);
//        command.setSlaveItemType(PL_ORDER_TRANSACTION_TYPE);
//        command.setMainItemIdValue(mainItemIdValue);
//        command.setSlaveItemIdValue(slaveItemIdValue);
//        command.setMainItemSlaveProperty(ORDER_TRANSACTIONS_PRN);
//        return command;
//    }


}

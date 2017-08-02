package dynamusic.lp.order.payment.impl;


import atg.commerce.order.Order;
import atg.repository.RepositoryItem;
import dynamusic.lp.LoyaltyConstants;
import dynamusic.lp.order.payment.OrderTransaction;

import java.util.Date;

public class OrderTransactionImpl implements OrderTransaction {

    private RepositoryItem repositoryItem;

    private String id;
    private Integer amount;
    private String description;
    private Date date;
    private String profileId;

    private Order order;

    public OrderTransactionImpl() {}

    public OrderTransactionImpl(RepositoryItem repositoryItem) {
        this.repositoryItem = repositoryItem;
        this.id = (String) repositoryItem.getPropertyValue(LoyaltyConstants.ID_TRANSACTION_PRN);
        this.amount = (Integer) repositoryItem.getPropertyValue(LoyaltyConstants.AMOUNT_PRN);
        this.description = (String) repositoryItem.getPropertyValue(LoyaltyConstants.DESCRIPTION_PRN);
        this.date = (Date) repositoryItem.getPropertyValue(LoyaltyConstants.CREATION_DATE_PRN);
        this.profileId = (String) repositoryItem.getPropertyValue(LoyaltyConstants.PROFILE_ID_PRN);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public RepositoryItem getRepositoryItem() {
        return repositoryItem;
    }

    public void setRepositoryItem(RepositoryItem repositoryItem) {
        this.repositoryItem = repositoryItem;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("OrderTransactionImpl{");

        sb.append("repositoryItem=").append(repositoryItem);
        sb.append(", id='").append(id).append('\'');
        sb.append(", amount=").append(amount);
        sb.append(", description='").append(description).append('\'');
        sb.append(", date=").append(date);
        sb.append(", profileId='").append(profileId).append('\'');
        sb.append(", order=").append(order);
        sb.append('}');
        return sb.toString();
    }
}

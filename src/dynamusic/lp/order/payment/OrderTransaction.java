package dynamusic.lp.order.payment;


import atg.repository.RepositoryItem;

import java.util.Date;

public interface OrderTransaction {
    String getId();

    void setId(String id);

    Integer getAmount();

    void setAmount(Integer amount);

    String getDescription();

    void setDescription(String description);

    Date getDate();

    void setDate(Date date);

    String getProfileId();

    void setProfileId(String profileId);

    RepositoryItem getRepositoryItem();

    void setRepositoryItem(RepositoryItem repositoryItem);
}

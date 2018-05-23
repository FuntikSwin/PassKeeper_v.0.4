package com.fsa.passkeeper.Model;

import java.io.Serializable;

public class CardGroup implements Serializable {

    private long Id;
    private String GroupName;

    public CardGroup(long id, String typeName) {
        Id = id;
        GroupName = typeName;
    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getGroupName() {
        return GroupName;
    }

    public void setGroupName(String groupName) {
        GroupName = groupName;
    }

}

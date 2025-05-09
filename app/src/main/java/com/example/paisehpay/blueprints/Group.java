package com.example.paisehpay.blueprints;
import java.util.HashMap;
import java.util.Map;

public class Group {
    // group object used in recycler view of group homepage
    private String groupId;
    private String groupName;
    private String groupCreatedDate;
    private String groupAmount;
    private String createdBy;
    private HashMap<String, String> members;
    private boolean isSelected;

    // constructors
    public Group() {
        //default constructor required for firebase
    }

    public Group(String id,
                 String groupName,
                 String groupCreatedDate,
                 String groupAmount,
                 String createdBy,
                 HashMap<String, String> members) {
        this.groupId = id;
        this.groupName = groupName;
        this.groupCreatedDate = groupCreatedDate;
        this.groupAmount = groupAmount;
        this.createdBy = createdBy;
        this.members = members;
        this.isSelected = false;
    }

    // getters
    public String getGroupId() {
        return groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public String getGroupCreatedDate() {
        return groupCreatedDate;
    }
    public String getGroupAmount() {
        return groupAmount;
    }
    public HashMap<String, String> getMembers() {return members;}
    public String getCreatedBy() {
        return createdBy;
    }

    // setters
    public void setGroupId(String key) {
        this.groupId = key;
    }
    public boolean isSelected() {return isSelected;}
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    // string of all details of an group object
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("groupName", groupName);
        result.put("groupCreatedDate", groupCreatedDate);
        result.put("groupAmount", groupAmount);
        result.put("createdBy", createdBy);
        result.put("members", members);
        return result;
    }

}
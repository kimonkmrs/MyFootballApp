package com.myapp.minifootballstats;

import com.google.gson.annotations.SerializedName;

public class Group {
    @SerializedName("GroupID")
    private int groupID;

    @SerializedName("GroupName")
    private String groupName;
    public Group(int groupID, String groupName) {
        this.groupID = groupID;
        this.groupName = groupName;
    }
    public int getGroupID() { return groupID; }
    public void setGroupID(int groupID) { this.groupID = groupID; }
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
}

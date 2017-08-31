package com.teamtreehouse.instateam.web;

public enum  Status {
    ACTIVE("Active", "active"),
    ARCHIVED("Archived", "archived"),
    NOT_STARTED("Not Started", "notstarted");

    private final String name;
    private final String tag;

    Status(String name, String tag) {
        this.name = name;
        this.tag = tag;
    }

    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }
}

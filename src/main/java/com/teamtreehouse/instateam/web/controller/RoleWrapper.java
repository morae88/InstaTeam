package com.teamtreehouse.instateam.web.controller;

import com.teamtreehouse.instateam.model.Role;

import java.util.List;

public class RoleWrapper {
    public List<Role> roles;

    public RoleWrapper(List<Role> roles) {
        this.roles = roles;
    }

    public RoleWrapper() {
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}

package com.smartstudent.model;

import java.time.LocalDateTime;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private int roleId;
    private String roleName;
    private boolean isActive;
    private LocalDateTime createdAt;

    public User() {}

    public User(int userId, String username, int roleId, String roleName, boolean isActive) {
        this.userId   = userId;
        this.username = username;
        this.roleId   = roleId;
        this.roleName = roleName;
        this.isActive = isActive;
    }

    public int       getUserId()      { return userId; }
    public String    getUsername()    { return username; }
    public String    getPasswordHash(){ return passwordHash; }
    public int       getRoleId()      { return roleId; }
    public String    getRoleName()    { return roleName; }
    public boolean   isActive()       { return isActive; }
    public LocalDateTime getCreatedAt(){ return createdAt; }

    public void setUserId(int userId)              { this.userId = userId; }
    public void setUsername(String username)        { this.username = username; }
    public void setPasswordHash(String hash)        { this.passwordHash = hash; }
    public void setRoleId(int roleId)               { this.roleId = roleId; }
    public void setRoleName(String roleName)        { this.roleName = roleName; }
    public void setActive(boolean active)           { this.isActive = active; }
    public void setCreatedAt(LocalDateTime dt)      { this.createdAt = dt; }

    @Override
    public String toString() { return username + " [" + roleName + "]"; }
}

package cn.bdqfork.kotlin.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class User {

    int id;
    String username;
    boolean active;
    @JsonFormat(pattern = "YYYY")
    Date createDate;

    public int getId() {
        return id;
    }

    public User setId(int id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User setUsername(String username) {
        this.username = username;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public User setActive(boolean active) {
        this.active = active;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public User setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }
}

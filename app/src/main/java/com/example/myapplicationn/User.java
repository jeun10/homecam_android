package com.example.myapplicationn;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {

    public String userName;

    public String select;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String userName, String select) {
        this.userName = userName;

        this.select = select;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSelect() { return select;}

    public void setSelect(String select){this.select=select;}

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", Select='" + select + '\'' +
                '}';
    }
}
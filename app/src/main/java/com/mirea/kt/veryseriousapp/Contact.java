package com.mirea.kt.veryseriousapp;

import java.util.Objects;

public class Contact {
    public boolean hasAvatar;
    private String name;
    private String phoneNumber;
    private String avatar;

    Contact(String name, String phoneNumber, String avatar) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.avatar = avatar;

        this.hasAvatar = !Objects.equals(avatar, "");
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phoneNumber;
    }

    public String getAvatar() {
        return avatar;
    }
}

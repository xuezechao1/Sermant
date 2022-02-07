package com.huawei.dubbo.common.domain;

import java.io.Serializable;

public class User implements Serializable {
    private String name;

    private String age;

    private String date;

    public User() {
    }

    public User(String name, String age, String date) {
        this.name = name;
        this.age = age;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age='" + age + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}

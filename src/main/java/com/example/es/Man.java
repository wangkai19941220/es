/*
 * Copyright (c) 2019 maoyan.com
 * All rights reserved.
 *
 */
package com.example.es;

import java.util.Date;

/**
 * 在这里编写类的功能描述
 *
 * @author wangkai
 * @created 2019/4/3
 */

public class Man {
    private String name;
    private Integer age;
    private Date date;
    private String country;
    private String sex;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
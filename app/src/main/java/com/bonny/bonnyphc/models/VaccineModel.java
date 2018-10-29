package com.bonny.bonnyphc.models;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Aditya Kulkarni
 */

public class VaccineModel implements Serializable{
    private int baby, week;
    private String vaccine, status;
    private String tentative_date;

    public int getBaby() {
        return baby;
    }

    public void setBaby(int baby) {
        this.baby = baby;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getVaccine() {
        return vaccine;
    }

    public void setVaccine(String vaccine) {
        this.vaccine = vaccine;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTentative_date() {
        return tentative_date;
    }

    public void setTentative_date(String tentative_date) {
        this.tentative_date = tentative_date;
    }
}

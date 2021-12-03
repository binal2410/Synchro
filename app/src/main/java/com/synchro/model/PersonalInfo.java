package com.synchro.model;

import java.io.Serializable;

public class PersonalInfo implements Serializable
{
    private String fullName;
    private String id;
    private String unit;
    private String subUnit;
    private String company;
    private String platoon;
    private String dateFreeStyle;
    private String dateZeroing;
    private String dateReaction;
    private String yearFreeStyle;
    private String yearZeroing;
    private String yearReaction;
    private String locationFreeStyle;
    private String locationZeroing;
    private String locationReaction;

    public String getDateFreeStyle() {
        return dateFreeStyle;
    }

    public void setDateFreeStyle(String dateFreeStyle) {
        this.dateFreeStyle = dateFreeStyle;
    }

    public String getDateZeroing() {
        return dateZeroing;
    }

    public void setDateZeroing(String dateZeroing) {
        this.dateZeroing = dateZeroing;
    }

    public String getDateReaction() {
        return dateReaction;
    }

    public void setDateReaction(String dateReaction) {
        this.dateReaction = dateReaction;
    }

    public String getYearFreeStyle() {
        return yearFreeStyle;
    }

    public void setYearFreeStyle(String yearFreeStyle) {
        this.yearFreeStyle = yearFreeStyle;
    }

    public String getYearZeroing() {
        return yearZeroing;
    }

    public void setYearZeroing(String yearZeroing) {
        this.yearZeroing = yearZeroing;
    }

    public String getYearReaction() {
        return yearReaction;
    }

    public void setYearReaction(String yearReaction) {
        this.yearReaction = yearReaction;
    }

    public String getLocationFreeStyle() {
        return locationFreeStyle;
    }

    public void setLocationFreeStyle(String locationFreeStyle) {
        this.locationFreeStyle = locationFreeStyle;
    }

    public String getLocationZeroing() {
        return locationZeroing;
    }

    public void setLocationZeroing(String locationZeroing) {
        this.locationZeroing = locationZeroing;
    }

    public String getLocationReaction() {
        return locationReaction;
    }

    public void setLocationReaction(String locationReaction) {
        this.locationReaction = locationReaction;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSubUnit() {
        return subUnit;
    }

    public void setSubUnit(String subUnit) {
        this.subUnit = subUnit;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPlatoon() {
        return platoon;
    }

    public void setPlatoon(String platoon) {
        this.platoon = platoon;
    }
}

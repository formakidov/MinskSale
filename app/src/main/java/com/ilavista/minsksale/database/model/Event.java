package com.ilavista.minsksale.database.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Event extends RealmObject {
    public static final String FIELD_ID = "id";
    public static final String FIELD_ORGANIZER = "Organizer";
    public static final String FIELD_TYPE = "Type";

    @SerializedName("ID")
    @PrimaryKey
    public long id;
    @SerializedName("Type")
    public String type;
    @SerializedName("Rate")
    public String rate;
    @SerializedName("Name")
    public String name;
    @SerializedName("Organizer")
    public String organizer;
    @SerializedName("StartDate")
    public String startDate;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("FinishDate")
    public String finishDate;
    @SerializedName("FinishTime")
    public String finishTime;
    @SerializedName("ImageURL")
    public String imageURL;
    @SerializedName("ImageName")
    public String imageName;
    @SerializedName("Location")
    public String location;
    @SerializedName("Description")
    public String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}



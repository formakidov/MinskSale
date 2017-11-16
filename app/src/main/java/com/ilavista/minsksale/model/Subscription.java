package com.ilavista.minsksale.model;

import io.realm.RealmObject;

public class Subscription extends RealmObject {
    public static final String FIELD_ORGANIZER_NAME = "organizerName";

    private String organizerName;

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

}

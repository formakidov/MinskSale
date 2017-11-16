package com.ilavista.minsksale.model;

import io.realm.RealmObject;

public class FavoriteEvent extends RealmObject {
    public static final String FIELD_EVENT_ID = "eventId";

    private long eventId;

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}

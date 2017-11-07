package com.ilavista.minsksale;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

class FavoriteEvent extends RealmObject {
    public static final String FIELD_ID = "id";

    @PrimaryKey
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

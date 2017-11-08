package com.ilavista.minsksale.database;

import com.ilavista.minsksale.database.model.Event;
import com.ilavista.minsksale.database.model.FavoriteEvent;

import io.realm.annotations.RealmModule;

@RealmModule(classes = { Event.class, FavoriteEvent.class })
public class RealmSchemaModule {
}

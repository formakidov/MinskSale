package com.ilavista.minsksale.database;

import com.ilavista.minsksale.model.Event;
import com.ilavista.minsksale.model.FavoriteEvent;
import com.ilavista.minsksale.model.Subscription;

import io.realm.annotations.RealmModule;

@RealmModule(classes = { Event.class, FavoriteEvent.class, Subscription.class})
public class RealmSchemaModule {
}

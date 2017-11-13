package com.ilavista.minsksale.database;

import com.ilavista.minsksale.database.model.Event;
import com.ilavista.minsksale.database.model.FavoriteEvent;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

public class DBManager {

    public static void insert(final List<Event> events) {
        realm().executeTransactionAsync(realm -> realm.insertOrUpdate(events));
    }

    public static void insertFavorite(final Event event) {
        FavoriteEvent favoriteEvent = new FavoriteEvent();
        favoriteEvent.setId(event.getId());
        realm().insertOrUpdate(favoriteEvent);
    }

    public static void removeFavorite(final Event event) {
        FavoriteEvent ev = realm().where(FavoriteEvent.class).equalTo(FavoriteEvent.FIELD_ID, event.getId()).findFirst();
        if (ev != null) {
            ev.deleteFromRealm();
        }
    }

    public static Event getEvent(int id) {
        return realm().where(Event.class).equalTo(Event.FIELD_ID, id).findFirst();
    }

    public static boolean isFavorite(int id) {
        return realm().where(FavoriteEvent.class).equalTo(FavoriteEvent.FIELD_ID, id).findFirst() != null;
    }

    public static List<Event> loadEvents(String type) {
        return loadEvents(type, null);
    }

    public static List<Event> loadEvents(String type, String organizer) {
        RealmQuery<Event> query = realm().where(Event.class);
        if (organizer != null) {
            query = query.equalTo(Event.FIELD_ORGANIZER, organizer);
        } else if (!type.equals("All") && !type.equals("first") || !type.equals("selected")) {
            query = query.equalTo(Event.FIELD_TYPE, type);
        }
        return query.findAllSorted(Event.FIELD_ID, Sort.DESCENDING);
    }

    public static List<Event> loadFavorite(String type, String organizer) {
        RealmQuery<FavoriteEvent> query = realm().where(FavoriteEvent.class);
        if (organizer != null) {
            query = query.equalTo(Event.FIELD_ORGANIZER, organizer);
        } else if (!type.equals("All") && !type.equals("first") || !type.equals("selected")) {
            query = query.equalTo(Event.FIELD_TYPE, type);
        }
        List<FavoriteEvent> favoriteEvents = query.findAll();
        List<Long> eventsIds = new ArrayList<>();
        for (FavoriteEvent favoriteEvent : favoriteEvents) {
            eventsIds.add(favoriteEvent.getId());
        }
        return realm().where(Event.class)
                .in(Event.FIELD_ID, eventsIds.toArray(new Long[eventsIds.size()]))
                .findAllSorted(FavoriteEvent.FIELD_ID, Sort.DESCENDING);
    }

    public static Realm realm() {
        return Realm.getDefaultInstance();
    }
}

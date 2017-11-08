package com.ilavista.minsksale;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.Sort;

public class DBManager {

    public static void insert(final List<Event> events) {
        realm().executeTransactionAsync(realm -> realm.copyToRealm(events));
    }

    public static void insertFavorite(final Event event) {
        FavoriteEvent favoriteEvent = new FavoriteEvent();
        favoriteEvent.setId(event.getID());
        realm().copyToRealm(favoriteEvent);
    }

    public static void removeFavorite(final Event event) {
        FavoriteEvent ev = realm().where(FavoriteEvent.class).equalTo(FavoriteEvent.FIELD_ID, event.getID()).findFirst();
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

    public static void loadEvents(final List<Event> events, String type) {
        loadEvents(events, type, null);
    }

    public static void loadEvents(final List<Event> events, String type, String organizer) {
        RealmQuery<Event> query = realm().where(Event.class);
        events.clear();
        if (organizer != null) {
            query = query.equalTo(Event.FIELD_ORGANIZER, organizer);
        } else if (!type.equals("All") && !type.equals("first") || !type.equals("selected")) {
            query = query.equalTo(Event.FIELD_TYPE, type);
        }
        events.addAll(query.findAllSorted(Event.FIELD_ID, Sort.DESCENDING));
    }

    public static void loadFavorite(final List<Event> events, String type, String organizer) {
        RealmQuery<FavoriteEvent> query = realm().where(FavoriteEvent.class);
        events.clear();
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
        events.addAll(realm().where(Event.class)
                .in(Event.FIELD_ID, eventsIds.toArray(new Long[eventsIds.size()]))
                .findAllSorted(FavoriteEvent.FIELD_ID, Sort.DESCENDING));
    }

    public static Realm realm() {
        return Realm.getDefaultInstance();
    }
}

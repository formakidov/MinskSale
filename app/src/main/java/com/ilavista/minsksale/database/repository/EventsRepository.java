package com.ilavista.minsksale.database.repository;

import com.ilavista.minsksale.model.Event;
import com.ilavista.minsksale.model.FavoriteEvent;
import com.ilavista.minsksale.utils.RealmUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;

public class EventsRepository {

    public static void insert(final List<Event> events) {
        RealmUtils.executeTransaction(r -> r.insertOrUpdate(events));
    }

    public static void insertAsync(final List<Event> events) {
        RealmUtils.executeTransaction(r -> r.insertOrUpdate(events));
    }

    public static void insertFavorite(final Event event) {
        RealmUtils.executeTransaction(r -> {
            FavoriteEvent favoriteEvent = new FavoriteEvent();
            favoriteEvent.setEventId(event.getId());
            r.insertOrUpdate(favoriteEvent);
        });
    }

    public static void removeFavorite(final Event event) {
        RealmUtils.executeTransaction(r -> {
            FavoriteEvent ev = r.where(FavoriteEvent.class).equalTo(FavoriteEvent.FIELD_EVENT_ID, event.getId()).findFirst();
            if (ev != null) {
                ev.deleteFromRealm();
            }
        });
    }

    public static Event getEvent(int id) {
        return RealmUtils.executeForResult(r ->
                r.where(Event.class).equalTo(Event.FIELD_ID, id).findFirst()
        );
    }

    public static boolean isFavorite(int id) {
        return RealmUtils.executeForResult(r ->
                r.where(FavoriteEvent.class).equalTo(FavoriteEvent.FIELD_EVENT_ID, id).findFirst() != null
        );
    }

    public static List<Event> loadEvents(String type) {
        return loadEvents(type, null);
    }

    public static List<Event> loadAllEvents() {
        return loadEvents("All", null);
    }

    public static List<Event> loadEvents(String type, String organizer) {
        return RealmUtils.executeForResult(r -> {
            RealmQuery<Event> query = r.where(Event.class);
            if (organizer != null) {
                query = query.equalTo(Event.FIELD_ORGANIZER, organizer);
            } else if (!type.equals("All") && !type.equals("favorite")) {
                query = query.equalTo(Event.FIELD_TYPE, type);
            }
            return findSorted(query);
        });
    }

    public static List<Event> loadFavorite(String type, String organizer) {
        return RealmUtils.executeForResult(r -> {
            RealmQuery<FavoriteEvent> query = r.where(FavoriteEvent.class);
            if (organizer != null) {
                query = query.equalTo(Event.FIELD_ORGANIZER, organizer);
            } else if (!type.equals("All") && !type.equals("favorite")) {
                query = query.equalTo(Event.FIELD_TYPE, type);
            }
            List<FavoriteEvent> favoriteEvents = query.findAll();
            List<Long> eventsIds = new ArrayList<>();
            for (FavoriteEvent favoriteEvent : favoriteEvents) {
                eventsIds.add(favoriteEvent.getEventId());
            }
            return r.where(Event.class)
                    .in(Event.FIELD_ID, eventsIds.toArray(new Long[eventsIds.size()]))
                    .findAllSorted(FavoriteEvent.FIELD_EVENT_ID, Sort.DESCENDING);

        });
    }

    private static RealmResults<Event> findSorted(RealmQuery<Event> query) {
        return query.findAllSorted(new String[]{Event.FIELD_RATE, Event.FIELD_ID}, new Sort[]{Sort.DESCENDING, Sort.DESCENDING});
    }

}

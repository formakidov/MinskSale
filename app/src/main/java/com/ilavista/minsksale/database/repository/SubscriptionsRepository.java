package com.ilavista.minsksale.database.repository;

import com.ilavista.minsksale.model.Subscription;
import com.ilavista.minsksale.utils.RealmUtils;

import io.realm.RealmResults;

public class SubscriptionsRepository {

    public static boolean hasItem(String organizerName) {
        return RealmUtils.executeForResult(r ->
                r.where(Subscription.class).equalTo(Subscription.FIELD_ORGANIZER_NAME, organizerName).findFirst() != null
        );
    }

    public static void remove(String organizerName) {
        RealmUtils.executeAction(r -> {
            Subscription s = r.where(Subscription.class).equalTo(Subscription.FIELD_ORGANIZER_NAME, organizerName).findFirst();
            if (s != null) {
                s.deleteFromRealm();
            }
        });
    }

    public static RealmResults<Subscription> getAll() {
        return RealmUtils.executeForResult(r -> r.where(Subscription.class).findAll());
    }

    // TODO remove in future releases when db structure will be different (add organizerId)
    public static void insertOrgNamesIfNotExist(String orgName) {
        RealmUtils.executeTransactionAsync(r -> {
            Subscription sub = r.where(Subscription.class).equalTo(Subscription.FIELD_ORGANIZER_NAME, orgName).findFirst();
            if (sub == null) {
                Subscription newSub = new Subscription();
                newSub.setOrganizerName(orgName);
                r.insertOrUpdate(newSub);
            }
        });
    }
}
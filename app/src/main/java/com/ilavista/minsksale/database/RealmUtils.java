package com.ilavista.minsksale.database;

import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObject;

public class RealmUtils {
    private static final int SCHEMA_VERSION = 1;

    public static void init(Context context) {
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_VERSION)
                .build();
        Realm.setDefaultConfiguration(config);
    }

    public static void executeAction(RealmAction action) {
        Realm realm = Realm.getDefaultInstance();
        action.execute(realm);
        realm.close();
    }

    public static <T> T executeForResult(RealmActionForResult<T> action) {
        Realm realm = Realm.getDefaultInstance();
        T result = action.execute(realm);
        realm.close();
        return result;
    }

    public static void executeTransaction(RealmAction action) {
        executeAction(realm -> realm.executeTransaction(action::execute));
    }

    public static void insertOrUpdate(RealmObject entity) {
        executeTransaction(realm -> realm.copyToRealmOrUpdate(entity));
    }

    public static void insertOrUpdate(Iterable<RealmObject> entities) {
        executeTransaction(realm -> realm.copyToRealmOrUpdate(entities));
    }

    public interface RealmAction {
        void execute(Realm realm);
    }

    public interface RealmActionForResult<T> {
        T execute(Realm realm);
    }

    private static class MigrationImpl implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

        }
    }
}

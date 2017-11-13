package com.ilavista.minsksale;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CacheManager {
    @Inject
    public CacheManager() {
    }

//    public <T> Observable<Response<T>> cachedRequest(ConnectionManager connectionManager,
//                                                            String key,
//                                                            Observable<Response<T>> request,
//                                                            Class<T> cls) {
//        return cachedRequest(connectionManager, key, request, cls, null);
//    }
//
//    public <T> Single<Response<T>> cachedRequest(ConnectionManager connectionManager,
//                                                 String key,
//                                                 Observable<Response<T>> request,
//                                                 Class<T> cls,
//                                                 ExpirationResolver<T> resolver) {
//        Observable<Response<T>> cache = getEntityFromCache(connectionManager, key, cls);
//        Observable<Response<T>> network = putRequestToCache(key, request, resolver);
//        return Observable.concat(cache, network).first(null);
//    }
//
////    public void clearCache() {
////        RealmUtils.executeTransaction(realm ->
////                realm.where(CachedEntity.class).findAll().deleteAllFromRealm());
////    }
//
//    private <T> Observable<Response<T>> getEntityFromCache(ConnectionManager connectionManager,
//                                                                  String key,
//                                                                  Class<T> cls) {
//        return Observable.create(subscriber -> {
//            RealmUtils.executeAction(realm -> {
//                CachedEntity entity = realm
//                        .where(CachedEntity.class)
//                        .equalTo("name", key)
//                        .findFirst();
//                if (entity != null) {
//                    if (worthLoadCached(connectionManager, entity)) {
//                        T result = CommonGson.get().fromJson(entity.getEntity(), cls);
//                        subscriber.onNext(Response.success(result));
//                    } else {
//                        subscriber.onCompleted();
//                    }
//                } else {
//                    subscriber.onCompleted();
//                }
//            });
//        });
//    }
//
//    private <T> Observable<Response<T>> putRequestToCache(String key, Observable<Response<T>> request,
//                                                          ExpirationResolver<T> resolver) {
//        return request.doOnEach(notification -> {
//            @SuppressWarnings("unchecked") Response<T> response = (Response<T>) notification.getValue();
//            if (response == null || !response.isSuccessful()) return;
//            @SuppressWarnings("unchecked") T value = response.body();
//            CachedEntity entity = new CachedEntity();
//            entity.setName(key);
//            entity.setEntity(CommonGson.get().toJson(value));
//            if (resolver != null) {
//                Expiration expiration = resolver.resolveExpiration(value);
//                if (expiration != null) {
//                    entity.setTtl(expiration.getTtlTimestamp());
//                }
//            }
//            RealmUtils.insertOrUpdate(entity);
//        });
//    }
//
//    private boolean worthLoadCached(ConnectionManager connectionManager, CachedEntity entity) {
//        // cached entity is worth loading if its TTL hasn't expired
//        boolean fresh = entity.getTtl() == null || System.currentTimeMillis() <= entity.getTtl();
//        // if the network is not available, cache should be loaded anyway
//        boolean hasInternet = connectionManager.isNetworkAvailable();
//        return fresh || !hasInternet;
//    }
//
//    public interface ExpirationResolver<T> {
//        Expiration resolveExpiration(T response);
//    }
//
//    public static class Expiration {
//        private long ttlTimestamp;
//
//        public Expiration(long duration) {
//            this(duration, TimeUnit.MILLISECONDS);
//        }
//
//        public Expiration(long duration, TimeUnit timeUnit) {
//            ttlTimestamp = System.currentTimeMillis() + timeUnit.toMillis(duration);
//        }
//
//        public long getTtlTimestamp() {
//            return ttlTimestamp;
//        }
//    }
}

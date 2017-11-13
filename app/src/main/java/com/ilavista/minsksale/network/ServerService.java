package com.ilavista.minsksale.network;

import com.ilavista.minsksale.CacheManager;
import com.ilavista.minsksale.database.model.Event;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;

public class ServerService extends BaseService {
    private static final String EVENTS_RESPONSE_KEY = "getdata.php";

    private static final String MULTIPART_FORM_DATA = "multipart/form-data";

    private final ServerInterface serverInterface;
    private final CacheManager cache;

    public ServerService(ConnectionManager connectionManager,
                         ServerInterface serverInterface,
                         CacheManager cache) {
        super(connectionManager);
        this.serverInterface = serverInterface;
        this.cache = cache;
    }

    public Observable<Response<List<Event>>> events() {
        // TODO: 11/9/17 cache
        return serverInterface.events()
                .compose(this::async)
                .compose(this::backoff)
                .compose(this::checkNetwork)
                .compose(this::checkServerBusy);
    }

//    public Observable<Response<OffersResponse>> offers(String categoryName,
//                                                       Integer skip, Integer count) {
//        return offers(categoryName, skip, count, false);
//    }
//
//    public Observable<Response<OffersResponse>> offers(String categoryName,
//                                                       Integer skip, Integer count,
//                                                       boolean forceCache) {
//        String postalCode = userManager.getZipCode();
//        String key = String.format(OFFERS_RESPONSE_KEY, postalCode, categoryName, skip, count);
//        Observable<Response<OffersResponse>> request = serverInterface
//                .offers(postalCode, categoryName, skip, count, false)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//        CacheManager.ExpirationResolver<OffersResponse> resolver;
//        if (forceCache) {
//            resolver = response -> null;
//        } else {
//            resolver = response -> new CacheManager.Expiration(response.ttl, TimeUnit.SECONDS);
//        }
//        return cache.cachedRequest(connectionManager, key, request, OffersResponse.class,
//                resolver);
//    }
//
//    public Observable<Response<OfferResponse>> offer(int offerId) {
//        return offer(offerId, null);
//    }
//
//    public Observable<Response<OfferResponse>> offer(int offerId, String postalCode) {
//        String key = String.format(OFFER_RESPONSE_KEY, offerId, postalCode);
//        Observable<Response<OfferResponse>> request = serverInterface.offer(offerId, postalCode)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//        CacheManager.ExpirationResolver<OfferResponse> resolver =
//                response -> new CacheManager.Expiration(response.ttl, TimeUnit.SECONDS);
//        return cache.cachedRequest(connectionManager, key, request, OfferResponse.class,
//                resolver);
//    }
//
//    public Observable<Response<CategoriesResponse>> categories() {
//        String postalCode = userManager.getZipCode();
//        String key = String.format(CATEGORIES_RESPONSE_KEY, postalCode);
//        Observable<Response<CategoriesResponse>> request = serverInterface
//                .categories(postalCode, false)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//        return cache.cachedRequest(connectionManager,
//                key,
//                request,
//                CategoriesResponse.class,
//                response -> new CacheManager.Expiration(response.ttl, TimeUnit.SECONDS));
//    }
//
//    public Observable<Response<RetailStoreLocationsResponse>> stores(int offerId, Double radius, Integer maxResults) {
//        return stores(offerId, userManager.getZipCode(), radius, maxResults);
//    }
//
//    public Observable<Response<RetailStoreLocationsResponse>> stores(int offerId, String postalCode,
//                                                                     Double radius, Integer maxResults) {
//        Observable<Response<RetailStoreLocationsResponse>> request = serverInterface
//                .stores(offerId, postalCode, radius, maxResults)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//        String key = String.format(STORES_RESPONSE_KEY, offerId, postalCode, radius, maxResults);
//        return cache.cachedRequest(connectionManager,
//                key,
//                request,
//                RetailStoreLocationsResponse.class,
//                response -> new CacheManager.Expiration(response.ttl, TimeUnit.SECONDS));
//    }
//
//    public Observable<Response<RetailStoreLocationsResponse>> stores(
//            int offerId, double latitude,
//            double longitude, Double radius, Integer maxResults) {
//        return serverInterface.stores(offerId, latitude, longitude, radius, maxResults)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<ShareCodeResponse>> shareCode(int offerId) {
//        return serverInterface.shareCode(offerId)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<AuthResponse>> login(LoginRequest data) {
//        return serverInterface.login(data)
//                .compose(this::async)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<AuthResponse>> appOpen(LoginRequest data) {
//        // NOTE: this is meant to fail silently, so we shouldn't
//        // use backoff or check the network status beforehand.
//        return serverInterface.appOpen(data)
//                .compose(this::async);
//    }
//
//    public Observable<Response<AuthResponse>> signUp(SignUpRequest data) {
//        return serverInterface.signUp(data)
//                .compose(this::async)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<AuthResponse>> signUpFB(FBLoginRequest d) {
//        return serverInterface.signUpFB(d)
//                .compose(this::async)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<Void>> addSavedOffer(String offerId) {
//        return serverInterface.addSavedOffer(userManager.getZipCode(), offerId)
//                .compose(this::asyncAuth)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<Void>> removeSavedOffer(String offerId) {
//        return serverInterface.removeSavedOffer(offerId)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<OffersResponse>> savedOffers() {
//        return serverInterface.savedOffers(userManager.getZipCode())
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<Void>> resetPassword(String emailAddress, String code, String newPassword) {
//        return serverInterface.resetPassword(emailAddress, code, newPassword)
//                .compose(this::async)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<UserDataResponse>> getProfile() {
//        return asyncAuth(serverInterface.getProfile())
//                .compose(obs -> saveUserData(obs, false))
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<AuthResponse>> updateProfile(UserData data) {
//        return serverInterface.updateProfile(data)
//                .compose(this::asyncAuth)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<Response<List<Receipt>>> receipts() {
//        return serverInterface.receipts()
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<List<UploadReceiptImagesResponse>> uploadReceiptImages(List<String> imageFiles) {
//        List<MultipartBody.Part> parts = new ArrayList<>();
//        for (String image: imageFiles) {
//            RequestBody requestFile = RequestBody.create(MediaType.parse(MULTIPART_FORM_DATA), new File(image));
//            parts.add(MultipartBody.Part.createFormData(image, image, requestFile));
//        }
//        return Observable
//                .from(parts)
//                .buffer(Constants.REDEMPTION_PHOTO_PACK_SIZE)
//                .concatMap(serverInterface::uploadReceiptImages)
//                .compose(this::asyncAuth)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy)
//                .flatMap(response -> Observable.from(response.body()))
//                .toList();
//    }
//
//    public Observable<Response<Void>> addReceipt(PostReceiptRequest request) {
//        return serverInterface.addReceipt(request)
//                .compose(this::asyncAuth)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//    }
//
//    public Observable<ZipCode> validateZip(String zip) {
//        if (userManager.isLoggedIn()) {
//            return serverInterface.saveZip(zip)
//                    .compose(obs -> saveUserData(obs, true))
//                    .compose(this::asyncAuth)
//                    .compose(this::checkNetwork)
//                    .compose(this::checkBanned)
//                    .compose(this::checkServerBusy)
//                    .map(r -> new ZipCode(zip, r.isSuccessful(), r.isSuccessful() && r.body().userData.isLocationAllowed));
//        } else {
//            return serverInterface.validateZip(zip)
//                    .compose(this::asyncAuth)
//                    .compose(this::backoff)
//                    .compose(this::checkNetwork)
//                    .compose(this::checkBanned)
//                    .compose(this::checkServerBusy)
//                    .map(r -> new ZipCode(zip, r.body().isValid, r.body().isValid && r.body().isAllowedState));
//        }
//    }
//
//    public Observable<ZipCode> validateLocation(double latitude, double longitude) {
//        return serverInterface.validateLocation(latitude, longitude)
//                .compose(this::asyncAuth)
//                .compose(this::backoff)
//                .compose(this::checkNetwork)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy)
//                .map(r -> new ZipCode(r.body().postalCode, r.body().isValid, r.body().isValid && r.body().isAllowedState));
//    }
//
//    public Observable<Response<String>> getSupportHtml(String path) {
//        Observable<Response<String>> request = serverInterface
//                .getSupportHtml(path)
//                .compose(this::checkBanned)
//                .compose(this::checkServerBusy);
//        return cache.cachedRequest(connectionManager, path, request, String.class,
//                response -> new CacheManager.Expiration(0, TimeUnit.SECONDS));
//    }
//
//    public Observable<Response<Void>> updatePromoCode(String newPromoCode) {
//        return serverInterface.updatePromoCode(userManager.getPromoCode(), newPromoCode)
//                .compose(this::asyncAuth)
//                .compose(this::checkBanned)
//                .compose(this::checkNetwork)
//                .compose(this::checkServerBusy);
//
//    }
}

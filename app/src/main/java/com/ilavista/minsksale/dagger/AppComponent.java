package com.ilavista.minsksale.dagger;

import com.ilavista.minsksale.UpdateReceiver;
import com.ilavista.minsksale.activity.EventDetailsActivity;
import com.ilavista.minsksale.fragment.BrandsFragment;
import com.ilavista.minsksale.fragment.EventsListFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={NetModule.class})
public interface AppComponent {

    void inject(UpdateReceiver receiver);

    void inject(EventDetailsActivity activity);

    void inject(EventsListFragment fragment);

    void inject(BrandsFragment fragment);
}
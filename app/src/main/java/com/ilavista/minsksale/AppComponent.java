package com.ilavista.minsksale;

import com.ilavista.minsksale.fragment.EventsListFragment;
import com.ilavista.minsksale.network.NetModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules={NetModule.class})
public interface AppComponent {

    void inject(EventsListFragment fragment);

}
package com.ilavista.minsksale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ilavista.minsksale.UpdateReceiver;
import com.ilavista.minsksale.ProgramConfigs;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;
import com.ilavista.minsksale.fragment.BrandsFragment;
import com.ilavista.minsksale.fragment.EventsListFragment;
import com.ilavista.minsksale.fragment.SubscriptionFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {


    public final static String EXTRA_MESSAGE_TYPE = "com.ilavista.minsksale.TYPE";
    public final static String EXTRA_MESSAGE_OPTIONS = "com.ilavista.minsksale.OPTIONS";

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private String type = "All";
    private long extraID;
    private Menu menu;

    private Handler mHandlerLeft, mHandlerRight;


    public Handler getHandlerRight() {
        return mHandlerRight;
    }

    public Handler getHandlerLeft() {
        return mHandlerLeft;
    }

    public void setHandlerLeft(Handler mHandlerLeft) {
        this.mHandlerLeft = mHandlerLeft;
    }

    public void setHandlerRight(Handler mHandlerRight) {
        this.mHandlerRight = mHandlerRight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Log.d("logf", "Main Activity onCreate");

        SubscriptionManager.getInstance(this).setNotifications(ProgramConfigs.getInstance(this).getNotificationPeriod());

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        // TODO: 11/9/17 deep link: open details with backstack
        if (getIntent() != null) {
            extraID = getIntent().getLongExtra(UpdateReceiver.RECEIVER_MESSAGE_ID, -1);
        }

        replaceFragment(EventsListFragment.newInstance(type));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            Intent intent = new Intent(this, SingleFragmentActivity.class);
            intent.putExtra(EXTRA_MESSAGE_OPTIONS, "action_about");
            startActivity(intent);
            return true;
        } else if (id == R.id.action_post_avd) {
            Intent intent = new Intent(this, SingleFragmentActivity.class);
            intent.putExtra(EXTRA_MESSAGE_OPTIONS, "action_post_avd");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // TODO: 11/9/17 types to enum
        switch (item.getItemId()) {
            case R.id.nav_all:
                selectNavigationItemChecked(R.id.nav_all);
                type = "All";
                break;
            case R.id.nav_beauty:
                selectNavigationItemChecked(R.id.nav_beauty);
                type = "beauty";
                break;
            case R.id.nav_clothes:
                selectNavigationItemChecked(R.id.nav_clothes);
                type = "clothes";
                break;
            case R.id.nav_brand:
                selectNavigationItemChecked(R.id.nav_brand);
                type = "brands";
                break;
            case R.id.nav_jewellery:
                selectNavigationItemChecked(R.id.nav_jewellery);
                type = "jewellery";
                break;
            case R.id.nav_electronics:
                selectNavigationItemChecked(R.id.nav_electronics);
                type = "electronics";
                break;
            case R.id.nav_auto:
                selectNavigationItemChecked(R.id.nav_auto);
                type = "auto";
                break;
            case R.id.nav_others:
                selectNavigationItemChecked(R.id.nav_others);
                type = "others";
                break;
            case R.id.nav_cafe:
                selectNavigationItemChecked(R.id.nav_cafe);
                type = "cafe";
                break;
            case R.id.nav_sport:
                selectNavigationItemChecked(R.id.nav_sport);
                type = "sport";
                break;
            case R.id.nav_entertainment:
                selectNavigationItemChecked(R.id.nav_entertainment);
                type = "entertainment";
                break;
            case R.id.nav_selected:
                selectNavigationItemChecked(R.id.nav_selected);
                type = "favorite";
                break;
            case R.id.nav_subscription:
                selectNavigationItemChecked(R.id.nav_subscription);
                type = "subscription";
                break;
            default:
                selectNavigationItemChecked(R.id.nav_all);
                type = "All";
                break;
        }

        drawer.closeDrawer(GravityCompat.START);

        switch (type) {
            case "subscription":
                replaceFragment(SubscriptionFragment.newInstance());
                break;
            case "brands":
                replaceFragment(BrandsFragment.newInstance());
                break;
            default:
                replaceFragment(EventsListFragment.newInstance(type));
                break;
        }

        return true;
    }

    private void replaceFragment(Fragment fragment) {
        commit(getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment));
    }

    private void selectNavigationItemChecked(int id) {
        menu.findItem(R.id.nav_all).setChecked(false);
        menu.findItem(R.id.nav_brand).setChecked(false);
        menu.findItem(R.id.nav_clothes).setChecked(false);
        menu.findItem(R.id.nav_jewellery).setChecked(false);
        menu.findItem(R.id.nav_electronics).setChecked(false);
        menu.findItem(R.id.nav_others).setChecked(false);
        menu.findItem(R.id.nav_cafe).setChecked(false);
        menu.findItem(R.id.nav_sport).setChecked(false);
        menu.findItem(R.id.nav_entertainment).setChecked(false);
        menu.findItem(R.id.nav_subscription).setChecked(false);
        menu.findItem(R.id.nav_selected).setChecked(false);

        menu.findItem(id).setChecked(true);
    }

}

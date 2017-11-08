package com.ilavista.minsksale.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ilavista.minsksale.MyReceiver;
import com.ilavista.minsksale.ProgramConfigs;
import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;
import com.ilavista.minsksale.fragment.BrandsFragment;
import com.ilavista.minsksale.fragment.MainFragment;
import com.ilavista.minsksale.fragment.SubscriptionFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isEventOpen;
    private String type;
    private String organizer;
    private int listViewPosition;
    private ViewPager pager;
    private MyFragmentPagerAdapter pagerAdapter;
    private long extraID;
    private Menu menu;

    private Handler mHandlerLeft, mHandlerRight;

    public final static String EXTRA_MESSAGE_TYPE = "com.ilavista.minsksale.TYPE";
    public final static String EXTRA_MESSAGE_OPTIONS = "com.ilavista.minsksale.OPTIONS";
    public final static String EXTRA_MESSAGE_LIST_POSITION = "com.ilavista.minsksale.LIST_POSITION";

    public void setListViewPosition(int listViewPosition) {
        this.listViewPosition = listViewPosition;
    }

    public void setIsEventOpen(boolean isEventOpen) {
        this.isEventOpen = isEventOpen;
    }

    public boolean isEventOpen() {
        return isEventOpen;
    }

    public MyFragmentPagerAdapter getPagerAdapter() {
        return pagerAdapter;
    }

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

    public void setPage(int nmb) {
        pager.setCurrentItem(nmb);
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("logf", "Main Activity onCreate");
        listViewPosition = 0;

        SubscriptionManager.setNotifications(this, ProgramConfigs.getInstance(this).getNotificationPeriod());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();

        extraID = getIntent().getLongExtra(MyReceiver.RECEIVER_MESSAGE_ID, -1);

        pager = findViewById(R.id.pager);
        pagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                menu.findItem(R.id.nav_all).setChecked(false);
                menu.findItem(R.id.nav_brand).setChecked(false);
                menu.findItem(R.id.nav_clothes).setChecked(false);
                menu.findItem(R.id.nav_shoes).setChecked(false);
                menu.findItem(R.id.nav_jewellery).setChecked(false);
                menu.findItem(R.id.nav_electronics).setChecked(false);
                menu.findItem(R.id.nav_others).setChecked(false);
                menu.findItem(R.id.nav_cafe).setChecked(false);
                menu.findItem(R.id.nav_sport).setChecked(false);
                menu.findItem(R.id.nav_entertainment).setChecked(false);
                menu.findItem(R.id.nav_subscription).setChecked(false);
                menu.findItem(R.id.nav_selected).setChecked(false);
                if (position == 0) {
                    switch (type) {
                        case "clothes":
                            selectNavigationItemChecked(R.id.nav_clothes);
                            break;
                        case "shoes":
                            selectNavigationItemChecked(R.id.nav_shoes);
                            break;
                        case "jewellery":
                            selectNavigationItemChecked(R.id.nav_jewellery);
                            break;
                        case "electronics":
                            selectNavigationItemChecked(R.id.nav_electronics);
                            break;
                        case "others":
                            selectNavigationItemChecked(R.id.nav_others);
                            break;
                        case "cafe":
                            selectNavigationItemChecked(R.id.nav_cafe);
                            break;
                        case "sport":
                            selectNavigationItemChecked(R.id.nav_sport);
                            break;
                        case "entertainment":
                            selectNavigationItemChecked(R.id.nav_entertainment);
                            break;
                        case "selected":
                            selectNavigationItemChecked(R.id.nav_selected);
                            break;
                        case "subscription":
                            selectNavigationItemChecked(R.id.nav_subscription);
                            break;
                        default:
                            selectNavigationItemChecked(R.id.nav_all);
                    }
                } else if (position == 1) {
                    selectNavigationItemChecked(R.id.nav_brand);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isEventOpen) {
            if (type == null) {
                type = "All";
            }
            isEventOpen = false;
            Fragment fragment = new MainFragment();
            Bundle arg = new Bundle();
            arg.putString(EXTRA_MESSAGE_TYPE, type);
            if (type.equals("by_organizer")) {
                arg.putString(MainFragment.EXTRA_MESSAGE_ORGANIZER, organizer);
            }
            arg.putInt(EXTRA_MESSAGE_LIST_POSITION, listViewPosition);
            fragment.setArguments(arg);
            pagerAdapter.setFragmentLeft(fragment);
        } else {
            super.onBackPressed();
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
        type = "All";
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_all:
                selectNavigationItemChecked(R.id.nav_all);
                type = "All";
                break;
            case R.id.nav_clothes:
                selectNavigationItemChecked(R.id.nav_clothes);
                type = "clothes";
                break;
            case R.id.nav_brand:
                selectNavigationItemChecked(R.id.nav_brand);
                type = "brands";
                break;
            case R.id.nav_shoes:
                selectNavigationItemChecked(R.id.nav_shoes);
                type = "shoes";
                break;
            case R.id.nav_jewellery:
                selectNavigationItemChecked(R.id.nav_jewellery);
                type = "jewellery";
                break;
            case R.id.nav_electronics:
                selectNavigationItemChecked(R.id.nav_electronics);
                type = "electronics";
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
                type = "selected";
                break;
            case R.id.nav_subscription:
                selectNavigationItemChecked(R.id.nav_subscription);
                type = "subscription";
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        switch (type) {
            case "subscription":
                pager.setCurrentItem(0);
                pagerAdapter.setFragmentLeft(new SubscriptionFragment());
                break;
            case "brands":
                pager.setCurrentItem(1);
                break;
            default:
                pager.setCurrentItem(0);
                Fragment fragment = new MainFragment();
                Bundle arg = new Bundle();
                arg.putString(EXTRA_MESSAGE_TYPE, type);
                fragment.setArguments(arg);
                pagerAdapter.setFragmentLeft(fragment);
                break;
        }

        return true;
    }

    public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private int PAGE_COUNT = 2;

        private Fragment fragmentLeft;
        private Fragment fragmentRight;
        private FragmentManager fragmentManager;

        public void setFragmentLeft(Fragment newFragment) {
            Fragment page = fragmentManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
            fragmentManager.beginTransaction().remove(page).commit();
            fragmentLeft = newFragment;
            notifyDataSetChanged();
        }

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            if (extraID != -1) {
//                isEventOpen = true;
//                fragmentLeft = new SingleFragment();
//                Bundle arg = new Bundle();
//                arg.putInt(MainFragment.EXTRA_MESSAGE_ID, (int) extraID);
//                fragmentLeft.setArguments(arg);
            } else {
                type = "All";
                fragmentLeft = new MainFragment();
                Bundle arg = new Bundle();
                arg.putString(EXTRA_MESSAGE_TYPE, type);
                fragmentLeft.setArguments(arg);
            }
            fragmentRight = new BrandsFragment();
            fragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fragmentLeft;
            }
            if (position == 1) {
                return fragmentRight;
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }

    private void selectNavigationItemChecked(int id) {
        menu.findItem(R.id.nav_all).setChecked(false);
        menu.findItem(R.id.nav_brand).setChecked(false);
        menu.findItem(R.id.nav_clothes).setChecked(false);
        menu.findItem(R.id.nav_shoes).setChecked(false);
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

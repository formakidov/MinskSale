package com.ilavista.minsksale;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private boolean isEventOpen;
    private String type;
    private String organizer;
    private int ListViewPosition;
    private ViewPager pager;
    private MyFragmentPagerAdapter pagerAdapter;
    private long extraID;
    private Menu menu;

    private Handler mHandlerLeft, mHandlerRight;

    public final static String EXTRA_MESSAGE_TYPE = "com.ilavista.minsksale.TYPE";
    public final static String EXTRA_MESSAGE_OPTIONS = "com.ilavista.minsksale.OPTIONS";
    public final static String EXTRA_MESSAGE_LIST_POSITION = "com.ilavista.minsksale.LIST_POSITION";

    public void setListViewPosition(int listViewPosition) {
        ListViewPosition = listViewPosition;
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
        ListViewPosition = 0;

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
                            menu.findItem(R.id.nav_clothes).setChecked(true);
                            break;
                        case "shoes":
                            menu.findItem(R.id.nav_shoes).setChecked(true);
                            break;
                        case "jewellery":
                            menu.findItem(R.id.nav_jewellery).setChecked(true);
                            break;
                        case "electronics":
                            menu.findItem(R.id.nav_electronics).setChecked(true);
                            break;
                        case "others":
                            menu.findItem(R.id.nav_others).setChecked(true);
                            break;
                        case "cafe":
                            menu.findItem(R.id.nav_cafe).setChecked(true);
                            break;
                        case "sport":
                            menu.findItem(R.id.nav_sport).setChecked(true);
                            break;
                        case "entertainment":
                            menu.findItem(R.id.nav_entertainment).setChecked(true);
                            break;
                        case "selected":
                            menu.findItem(R.id.nav_selected).setChecked(true);
                            break;
                        case "subscription":
                            menu.findItem(R.id.nav_subscription).setChecked(true);
                            break;
                        default:
                            menu.findItem(R.id.nav_all).setChecked(true);
                    }
                } else if (position == 1)
                    menu.findItem(R.id.nav_brand).setChecked(true);

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
            if (type == null) type = "All";
            isEventOpen = false;
            Fragment fragment = new MainFragment();
            Bundle arg = new Bundle();
            arg.putString(EXTRA_MESSAGE_TYPE, type);
            if (type.equals("by_organizer"))
                arg.putString(MainFragment.EXTRA_MESSAGE_ORGANIZER, organizer);
            arg.putInt(EXTRA_MESSAGE_LIST_POSITION, ListViewPosition);
            fragment.setArguments(arg);
            pagerAdapter.setFragmentLeft(fragment);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
        type = "All";
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_all) {
            menu.findItem(R.id.nav_all).setChecked(true);
            type = "All";
        } else if (id == R.id.nav_clothes) {
            menu.findItem(R.id.nav_clothes).setChecked(true);
            type = "clothes";

        } else if (id == R.id.nav_brand) {
            menu.findItem(R.id.nav_brand).setChecked(true);
            type = "brands";
        } else if (id == R.id.nav_shoes) {
            menu.findItem(R.id.nav_shoes).setChecked(true);
            type = "shoes";
        } else if (id == R.id.nav_jewellery) {
            menu.findItem(R.id.nav_jewellery).setChecked(true);
            type = "jewellery";
        } else if (id == R.id.nav_electronics) {
            menu.findItem(R.id.nav_electronics).setChecked(true);
            type = "electronics";
        } else if (id == R.id.nav_others) {
            menu.findItem(R.id.nav_others).setChecked(true);
            type = "others";
        } else if (id == R.id.nav_cafe) {
            menu.findItem(R.id.nav_cafe).setChecked(true);
            type = "cafe";
        } else if (id == R.id.nav_sport) {
            menu.findItem(R.id.nav_sport).setChecked(true);
            type = "sport";
        } else if (id == R.id.nav_entertainment) {
            menu.findItem(R.id.nav_entertainment).setChecked(true);
            type = "entertainment";
        } else if (id == R.id.nav_selected) {
            menu.findItem(R.id.nav_selected).setChecked(true);
            type = "selected";
        } else if (id == R.id.nav_subscription) {
            menu.findItem(R.id.nav_subscription).setChecked(true);
            type = "subscription";
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
            Log.d("logf", "MyFragmentPagerAdapter created");

            if (extraID != -1) {
                isEventOpen = true;
                fragmentLeft = new SingleFragment();
                Bundle arg = new Bundle();
                arg.putInt(MainFragment.EXTRA_MESSAGE_ID, (int) extraID);
                fragmentLeft.setArguments(arg);
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
            if (position == 0)
                return fragmentLeft;
            else if (position == 1)
                return fragmentRight;
            else return null;

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

}

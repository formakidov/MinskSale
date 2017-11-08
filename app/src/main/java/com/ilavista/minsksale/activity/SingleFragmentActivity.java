package com.ilavista.minsksale.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ilavista.minsksale.fragment.HelpFragment;
import com.ilavista.minsksale.activity.MainActivity;
import com.ilavista.minsksale.fragment.PostAdFragment;
import com.ilavista.minsksale.R;

public class SingleFragmentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_keyboard_arrow_left_white_24dp));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        String options = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE_OPTIONS);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.single_fragment_container);
        if (fragment == null) {
            if (options.equals("action_about")) {
                toolbar.setSubtitle(R.string.about);
                fragment = new HelpFragment();
            } else {
                toolbar.setSubtitle(R.string.post_adv);
                fragment = new PostAdFragment();
            }
            fragment.setArguments(getIntent().getExtras());
            fm.beginTransaction().add(R.id.single_fragment_container, fragment).commit();
        }

    }

}

package com.ilavista.minsksale.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ilavista.minsksale.R;
import com.ilavista.minsksale.SubscriptionManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscriptionFragment extends Fragment {

    @BindView(R.id.subscriptionsLayout)
    LinearLayout layout;

    private SubscriptionManager subscriptionManager;
    private List<String> orgNames;

    public static SubscriptionFragment newInstance() {

        Bundle args = new Bundle();

        SubscriptionFragment fragment = new SubscriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_subscription, container, false);
        ButterKnife.bind(this, v);
        subscriptionManager = SubscriptionManager.getInstance(getActivity());
        orgNames = subscriptionManager.getAllOrganizersNames();
        drawSubs();
        return v;
    }

    private void drawSubs() {
        Context context = getContext();

        if (layout.getChildCount() > 0) {
            layout.removeAllViews();
        }

        TextView mTextView;
        LinearLayout mLayout, mLayoutLeft, mLayoutRight;
        ImageView mButton;

        for (String orgName : orgNames) {

            mLayout = new LinearLayout(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(40, 20, 20, 0);
            mLayout.setLayoutParams(params);
            mLayout.setOrientation(LinearLayout.HORIZONTAL);

            mLayoutLeft = new LinearLayout(context);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 3;
            mLayoutLeft.setLayoutParams(params);
            mLayoutLeft.setOrientation(LinearLayout.HORIZONTAL);
            mLayoutLeft.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

            mButton = new ImageView(context);
            mButton.setBackgroundResource(R.drawable.ic_clear_black_24dp);
            mButton.setOnClickListener(v -> {
                subscriptionManager.remove(orgName);
                orgNames.remove(orgName);
                Log.d("logf", "Remove " + orgName);
                drawSubs();
            });

            mLayoutLeft.addView(mButton);

            mLayoutRight = new LinearLayout(context);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.weight = 2;
            mLayoutRight.setLayoutParams(params);
            mLayoutRight.setOrientation(LinearLayout.HORIZONTAL);
            mLayoutRight.setGravity(Gravity.CENTER_VERTICAL);

            mTextView = new TextView(context);
            mTextView.setText(orgName);
            mTextView.setTextSize(17);

            mLayoutRight.addView(mTextView);

            mLayout.addView(mLayoutLeft);
            mLayout.addView(mLayoutRight);

            layout.addView(mLayout);
        }
    }
}

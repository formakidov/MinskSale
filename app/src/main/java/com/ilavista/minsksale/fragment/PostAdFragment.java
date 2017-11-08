package com.ilavista.minsksale.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilavista.minsksale.R;

public class PostAdFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post_ad, container, false);
        TextView mail = view.findViewById(R.id.mailTv);
        mail.setMovementMethod(LinkMovementMethod.getInstance());
        // TODO: 11/7/17 кнопку "написать нам" ?
        return view;
    }

}

package com.ilavista.minsksale.activity;

import android.os.Build;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected void commit(FragmentTransaction transaction) {
        if (canPerformTransactionAction()) {
            transaction.commitAllowingStateLoss();
        }
    }

    private boolean canPerformTransactionAction() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !(isFinishing() || isDestroyed());
        } else {
            return !isFinishing();
        }
    }
}

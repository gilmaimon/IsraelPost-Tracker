package com.gilmaimon.israelposttracker;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.gilmaimon.israelposttracker.AndroidUtils.Permissions;
import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;
import com.gilmaimon.israelposttracker.Balance.BalanceFragment;
import com.gilmaimon.israelposttracker.Balance.BalancePresenter;
import com.gilmaimon.israelposttracker.Balance.DynamicPostPacketsBalance;
import com.gilmaimon.israelposttracker.Balance.PacketsBalanceContract;
import com.gilmaimon.israelposttracker.Balance.PostPacketsBalance;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.SMS.NewSmsBroadcastListener;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.UserAppended.SQLiteUserAppendedActions;


public class MainActivity extends AppCompatActivity  {

    private Permissions.OnRequestPermissionHandler mSmsPermissionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        mSmsPermissionHandler = Permissions.RequirePermission(
                this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                new Permissions.PermissionCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void permissionGranted(String[] permissions) {
                        showBalanceFragment();
                    }

                    @Override
                    public void permissionDenied(String[] permissions) {
                        Toast.makeText(MainActivity.this, "App must have SMS permissions to operate.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showBalanceFragment() {
        NewSmsBroadcastListener newSmsBroadcastListener = new NewSmsBroadcastListener(this);

        BranchesProvider branchesProvider = new JsonBranches(new RawResource(this, R.raw.branches).readAll());
        PostPacketsBalance balance = new DynamicPostPacketsBalance(
                new SQLiteUserAppendedActions(this, false),
                SMSProvider.from(this, "%1111%"), // todo: change to "Israel Post" or "%1111% for debug
                branchesProvider,
                KeywordsMessagesSorter.getDefault(),
                new RegexPostMessageParser()
        );

        PacketsBalanceContract.View balanceFragment = new BalanceFragment();
        PacketsBalanceContract.Presenter balancePresenter = new BalancePresenter(
                balanceFragment,
                branchesProvider,
                balance,
                newSmsBroadcastListener
        );

        getSupportFragmentManager().beginTransaction().replace(R.id.contentPlaceholder, (Fragment) balanceFragment).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSmsPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

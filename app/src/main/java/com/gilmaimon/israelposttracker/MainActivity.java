package com.gilmaimon.israelposttracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.gilmaimon.israelposttracker.AndroidUtils.Permissions;
import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private Permissions.OnRequestPermissionHandler mSmsPermissionHandler;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSmsPermissionHandler = Permissions.RequirePermission(
                this,
                Manifest.permission.RECEIVE_SMS,
                new Permissions.PermissionCallback() {

            @Override
            public void permissionGranted(String permission) {
                Toast.makeText(MainActivity.this, "Got it, tnx!", Toast.LENGTH_LONG).show();
                try {
                    showAllPendingPackets();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void permissionDenied(String permission) {
                Toast.makeText(MainActivity.this, "Gotta have dat permission", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showAllPendingPackets() throws IOException {
        PostPacketsBalance balance = new DynamicPostPacketsBalance(
                SMSProvider.from(this, "Israel Post"),
                new JsonBranches(new RawResource(this, R.raw.branches).readAll()),
                KeywordsMessagesSorter.getDefault(),
                new RegexPostMessageParser()
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        BranchesAndPacketsAdapter adapter = new BranchesAndPacketsAdapter(
                this,
                balance.getAllPendingPackets()
        );
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSmsPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

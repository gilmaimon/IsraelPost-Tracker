package com.gilmaimon.israelposttracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.gilmaimon.israelposttracker.AndroidUtils.ItemClickedListener;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProxy;
import com.gilmaimon.israelposttracker.Branches.BranchesRecyclerViewAdapter;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Parsing.PostMessageParser;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.Parsing.UnknownMessageFormat;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.Sorting.PostMessageSorter;
import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        try {
            BranchesProxy branches = new JsonBranches(new RawResource(this, R.raw.branches).readAll());
            List<Branch> allBranches = branches.getAll();

            BranchesRecyclerViewAdapter adapter = new BranchesRecyclerViewAdapter(this, allBranches);
            adapter.setItemClickedListener(new ItemClickedListener<Branch>() {
                @Override
                public void itemClicked(Branch item, int position) {
                    Toast.makeText(MainActivity.this, item.getName(), Toast.LENGTH_SHORT).show();
                }
            });
            recyclerView.setAdapter(adapter);

        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            testParserSorter();
//        } catch (UnknownMessageFormat unknownMessageFormat) {
//            unknownMessageFormat.printStackTrace();
//        }
    }

    void testParserSorter() throws UnknownMessageFormat {
        String tnxMessage = "לקוח יקר, תודה שאספת " +
                "את דבר הדואר YY511283943IL בדואר ישראל.\nלצורך שיפ" +
                "ור השרות, נודה לך אם תוכל לענות על 3 שאלות קצרות ב" +
                "קישור הבא: https://postil.wizsupport.com/chat/m" +
                "/VTZVG92END\nלהבא, עם" +
                " שירות BOX2GO תוכל לאסוף את החבילה 24/7." +
                " להרשמה לחץ כאן: http://bit.ly/2lPkEEy " +
                "\n תודה רבה, דואר ישראל.\n";

        String reminderMsg = "להזכירך-דבר דואר YY511280400IL\nג 1497\n עדיין ממתין לך ביחידת הדואר - סופר סמדר (בית עסק) - כתובת-ישעיהו 20 , אופקים לשעות פתיחה לחץ  http://www.israelpost.co.il/m/2328  .";
        Log.v("MessageLogs", reminderMsg);
        PostMessageSorter sorter = KeywordsMessagesSorter.getDefault();
        PostMessageParser parser = new RegexPostMessageParser();

        PostMessageSorter.PostSMSType type = sorter.sortMessage(reminderMsg);
        if (type == PostMessageSorter.PostSMSType.AwaitingPickup) {
            PendingPacket packet = parser.parseAwaitingPacketMessage(reminderMsg);
        } else {
            Packet packet = parser.parsePickedUpMessage(reminderMsg);
        }
    }
}

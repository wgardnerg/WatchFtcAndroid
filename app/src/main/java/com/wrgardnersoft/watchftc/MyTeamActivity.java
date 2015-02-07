package com.wrgardnersoft.watchftc;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ExpandableListView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyTeamActivity extends ActionBarActivity {

    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Match>> listDataChild;

    public ArrayList<Team> myTeam;  // full team info from team server page
    public ArrayList<TeamFtcRanked> myTeamFtcRanked;
    public ArrayList<Match> myMatch;

    public MyTeamActivity() {
        this.myTeam = new ArrayList<>();
        this.myTeamFtcRanked = new ArrayList<>();
        this.myMatch = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        setTitle(" Team " + Integer.toString(myApp.currentTeamNumber));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        setContentView(R.layout.activity_my_team);

        inflateMeAll();

        setTitle(" Team " + Integer.toString(myApp.currentTeamNumber)+": "+myTeam.get(0).name);
    }

    private void inflateMeAll() {
        MyApp myApp = (MyApp) getApplication();

        for (Team t : myApp.team[myApp.division()]) {
            if (t.number == myApp.currentTeamNumber) {
                myTeam.add(t);
            }
        }
 //       inflateMeTeam();

        Log.i("FtcRankedSize: ", String.valueOf(myApp.teamFtcRanked[myApp.division()].size()));
        if (myApp.teamFtcRanked[myApp.division()].size() > 0) {
            for (TeamFtcRanked t : myApp.teamFtcRanked[myApp.division()]) {
                if (t.number == myApp.currentTeamNumber) {
                    myTeamFtcRanked.add(t);
                }
            }
            Log.i("FtcRankedSize: ", String.valueOf(myTeamFtcRanked.size()));
            if (myTeamFtcRanked.size() > 0) {
                inflateMeTeamFtcRanked();
            }
        }
        Log.i("MatchSize: ", String.valueOf(myApp.match[myApp.division()].size()));
        if (myApp.match[myApp.division()].size() > 0) {
            for (Match m : myApp.match[myApp.division()]) {
                if ((m.rTeam0 == myApp.currentTeamNumber) ||
                        (m.rTeam1 == myApp.currentTeamNumber) ||
                        (m.bTeam0 == myApp.currentTeamNumber) ||
                        (m.bTeam1 == myApp.currentTeamNumber)) {
                    myMatch.add(m);
                }
            }
            Log.i("MatchSize: ", String.valueOf(myMatch.size()));
            if (myMatch.size() > 0) {
                inflateMeMatch();
            }
        }
    }

    private void inflateMeTeamFtcRanked() {
 Log.i("Inflater", "got here");
        FtcRankingsListAdapter adapter = new FtcRankingsListAdapter(this,
                R.layout.list_item_ftc_ranking, myTeamFtcRanked);
        ListView listViewTeamFtcRanked;
        listViewTeamFtcRanked = (ListView) findViewById(R.id.ftc_rankings_list_view);
        listViewTeamFtcRanked.setAdapter(adapter);
    }
/*
    private void inflateMeTeam() {

        TeamListAdapter adapter = new TeamListAdapter(this,
                R.layout.list_item_team, myTeam);
       ListView listViewTeam = (ListView) findViewById(R.id.teams_list_view);
       listViewTeam.setAdapter(adapter);

    }
*/
    private void inflateMeMatch() {
        expListView = (ExpandableListView) findViewById(R.id.matches_expListView);
          Log.i("exp list view", expListView.toString());
        prepareListData();
        MatchesExpandableListAdapter listAdapter = new MatchesExpandableListAdapter(this,
                listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        // Adding child data
        listDataHeader.add("Qualifier");
        listDataHeader.add("Semi-Final");
        listDataHeader.add("Final");

        // Adding child data
        List<Match> qual = new ArrayList<>();
        List<Match> semi = new ArrayList<>();
        List<Match> finals = new ArrayList<>();

        for (int i = 0; i < myMatch.size(); i++) {
            if (myMatch.get(i).title.startsWith("Q")) {
                qual.add(myMatch.get(i));
            } else if (myMatch.get(i).title.startsWith("S")) {
                semi.add(myMatch.get(i));
            } else {
                finals.add(myMatch.get(i));
            }
        }

        listDataChild.put(listDataHeader.get(0), qual); // Header, Child data
        listDataChild.put(listDataHeader.get(1), semi);
        listDataChild.put(listDataHeader.get(2), finals);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_my_team);

        inflateMeAll();
    }

}


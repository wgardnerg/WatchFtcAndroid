package com.wrgardnersoft.watchftc.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.adapters.FtcRankingsListAdapter;
import com.wrgardnersoft.watchftc.adapters.MatchesExpandableListAdapter;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Team;
import com.wrgardnersoft.watchftc.models.TeamFtcRanked;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MyTeamActivity extends CommonMenuActivity implements AsyncResponse {

    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Match>> listDataChild;

    ClientTask clientTask;

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

   //     setTitle(" Team " + Integer.toString(myApp.currentTeamNumber));

        setContentView(R.layout.activity_my_team);

        inflateMeAll();

        setTitle(" Team " + Integer.toString(myApp.currentTeamNumber) + ": " + myTeam.get(0).name);


    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        myTeam.clear();
        myTeamFtcRanked.clear();
        myMatch.clear();
        inflateMeAll();

    }

    private void inflateMeAll() {
        MyApp myApp = (MyApp) getApplication();

        for (Team t : myApp.team[myApp.division()]) {
            if (t.number == myApp.currentTeamNumber) {
                myTeam.add(t);
            }
        }
        //       inflateMeTeam();

        //      Log.i("FtcRankedSize: ", String.valueOf(myApp.teamFtcRanked[myApp.division()].size()));
        if (myApp.teamFtcRanked[myApp.division()].size() > 0) {
            for (TeamFtcRanked t : myApp.teamFtcRanked[myApp.division()]) {
                if (t.number == myApp.currentTeamNumber) {
                    myTeamFtcRanked.add(t);
                }
            }
            //         Log.i("FtcRankedSize: ", String.valueOf(myTeamFtcRanked.size()));
            if (myTeamFtcRanked.size() > 0) {
                inflateMeTeamFtcRanked();
            }
        }
        //     Log.i("MatchSize: ", String.valueOf(myApp.match[myApp.division()].size()));
        if (myApp.match[myApp.division()].size() > 0) {
            for (Match m : myApp.match[myApp.division()]) {
                if ((m.rTeam0 == myApp.currentTeamNumber) ||
                        (m.rTeam1 == myApp.currentTeamNumber) ||
                        (m.bTeam0 == myApp.currentTeamNumber) ||
                        (m.bTeam1 == myApp.currentTeamNumber)) {
                    Match mm = new Match(m);
                    if ((m.rTot < 0) && (myApp.enableMatchPrediction)) {
                        mm.predicted = true;
                        mm.rTot = 0;
                        mm.bTot = 0;
                        for (TeamStatRanked t : myApp.teamStatRanked[myApp.division()]) {
                            if ((mm.rTeam0 == t.number) ||
                                    (mm.rTeam1 == t.number) ||
                                    (mm.rTeam2 == t.number)) {
                                mm.rTot += t.oprA;
                                mm.bTot -= t.dprA;
                            }
                            if ((mm.bTeam0 == t.number) ||
                                    (mm.bTeam1 == t.number) ||
                                    (mm.bTeam2 == t.number)) {
                                mm.bTot += t.oprA;
                                mm.rTot -= t.dprA;
                            }
                        }

                    }
                    myMatch.add(mm);
                }
            }
            //         Log.i("MatchSize: ", String.valueOf(myMatch.size()));
            if (myMatch.size() > 0) {
                inflateMeMatch();
            }
        }
    }

    private void inflateMeTeamFtcRanked() {
        //Log.i("Inflater", "got here");
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
        //        Log.i("exp list view", expListView.toString());
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        Match.prepareListData(myMatch, listDataHeader, listDataChild);
        MatchesExpandableListAdapter listAdapter = new MatchesExpandableListAdapter(this,
                listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {

                Match matchPicked = (Match) parent.getExpandableListAdapter().getChild(groupPosition, childPosition);

                MyApp myApp = (MyApp) getApplication();
                myApp.currentMatchNumber = matchPicked.number;

                Intent getNameScreenIntent = new Intent(view.getContext(), MyMatchActivity.class);
                startActivity(getNameScreenIntent);
                return true;

            }

        });
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_my_team);

        inflateMeAll();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        MyApp myApp = MyApp.getInstance();

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}


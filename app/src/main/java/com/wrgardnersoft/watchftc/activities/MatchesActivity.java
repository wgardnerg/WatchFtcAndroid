package com.wrgardnersoft.watchftc.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.adapters.MatchesExpandableListAdapter;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MatchesActivity extends CommonMenuActivity implements AsyncResponse {


    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<Match>> listDataChild;
    ClientTask clientTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        //     team = new ArrayList<Team>();

        if (myApp.dualDivision()) {
            setTitle(" " + getString(R.string.matches) + ", Division " + Integer.toString(myApp.division() + 1));
        } else {
            setTitle(" " + getString(R.string.matches));
        }

        setContentView(R.layout.activity_matches);

        if (myApp.match[myApp.division()].size() > 0) {
            inflateMe();
        } else {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
        }

    }

    private void inflateMe() {
        MyApp myApp = MyApp.getInstance();
        ArrayList<Match> localMatch = new ArrayList<>();

        for (Match m : myApp.match[myApp.division()]) {

            Match mm = new Match(m);
            //int teamsPerMatch = 2;
            if ((mm.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] < 0) && (myApp.enableMatchPrediction)) {
                mm.predicted = true;
                double sf=1.0;
                if (mm.teamNumber[MyApp.RED][2]>0) { // 3 team alliance so scale scores by 2/3
                    sf=2.0/3.0;
                }
                for (int i=0; i<MyApp.NUM_ALLIANCES; i++) {
                    for (int j=0; j<MyApp.NUM_SCORE_TYPES; j++) {
                        mm.score[i][j]=0;
                    }
                }
                for (int color=0; color<MyApp.NUM_ALLIANCES; color++) {
                    for (int j=0; j<MyApp.NUM_SCORE_TYPES-1; j++) {
                        mm.score[color][j]+=myApp.meanOffenseScoreTotal[myApp.division()][j]*MyApp.TEAMS_PER_MATCH;
                        //   Log.i("Mean "+String.valueOf(j), String.valueOf(mm.score[color][j]));
                    }
                    mm.score[1 - color][MyApp.ScoreType.PENALTY.ordinal()] -=
                            myApp.meanOffenseScoreTotal[myApp.division()][MyApp.ScoreType.PENALTY.ordinal()]*MyApp.TEAMS_PER_MATCH;
                    mm.score[1-color][MyApp.ScoreType.TOTAL.ordinal()] -=
                            myApp.meanOffenseScoreTotal[myApp.division()][MyApp.ScoreType.PENALTY.ordinal()]*MyApp.TEAMS_PER_MATCH;
                    mm.score[color][MyApp.ScoreType.TOTAL.ordinal()] -=
                            myApp.meanOffenseScoreTotal[myApp.division()][MyApp.ScoreType.PENALTY.ordinal()]*MyApp.TEAMS_PER_MATCH;
                    //   Log.i("Mean 0**", String.valueOf(mm.score[0][0]));
                    //   Log.i("Mean 1**", String.valueOf(mm.score[1][0]));
                }
                for (TeamStatRanked t : myApp.teamStatRanked[myApp.division()]) {
                    for (int color = 0; color< MyApp.NUM_ALLIANCES; color++) {

                        if ((mm.teamNumber[color][0] == t.number) ||
                                (mm.teamNumber[color][1] == t.number) ||
                                (mm.teamNumber[color][2] == t.number)) {

                            for (int i = 0; i < MyApp.NUM_SCORE_TYPES - 1; i++) {
                                mm.score[color][i] += sf*t.oprA[i];
                            }
                            mm.score[1 - color][MyApp.ScoreType.PENALTY.ordinal()] -=sf*
                                    t.oprA[MyApp.ScoreType.PENALTY.ordinal()];
                            mm.score[1 - color][MyApp.ScoreType.TOTAL.ordinal()] -=sf*
                                    t.oprA[MyApp.ScoreType.PENALTY.ordinal()];
                            mm.score[color][MyApp.ScoreType.TOTAL.ordinal()] -=sf*
                                    t.oprA[MyApp.ScoreType.PENALTY.ordinal()];

                            for (int i = 0; i < MyApp.NUM_SCORE_TYPES - 1; i++) {
                                mm.score[1 - color][i] -= sf*t.dprA[i];
                            }
                            mm.score[color][MyApp.ScoreType.PENALTY.ordinal()] +=sf*
                                    t.dprA[MyApp.ScoreType.PENALTY.ordinal()];
                            mm.score[color][MyApp.ScoreType.TOTAL.ordinal()] +=sf*
                                    t.dprA[MyApp.ScoreType.PENALTY.ordinal()];

                            mm.score[1-color][MyApp.ScoreType.TOTAL.ordinal()] +=sf*
                                    t.dprA[MyApp.ScoreType.PENALTY.ordinal()];

                        }
                    }
                }

            }
            localMatch.add(mm);

        }

        expListView = (ExpandableListView) findViewById(R.id.matches_expListView);
        //  Log.i("exp list view", expListView.toString());

        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();
        Match.prepareListData(localMatch, listDataHeader, listDataChild);
        MatchesExpandableListAdapter listAdapter = new MatchesExpandableListAdapter(this,
                listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
//Log.i("ELA Group", String.valueOf(groupPosition));
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
        setContentView(R.layout.activity_matches);

        MyApp myApp = (MyApp) getApplication();
        if (myApp.match[myApp.division()].size() > 0) {
            inflateMe();
        }
    }


    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = (MyApp) getApplication();

        if (myApp.match[myApp.division()].size() > 0) {
            inflateMe();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

       // MyApp myApp = MyApp.getInstance();

        MenuItem item = menu.findItem(R.id.action_matches);
        item.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        boolean saveReturn;

        //MyApp myApp = MyApp.getInstance();

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
            return true;
        }
        saveReturn =  super.onOptionsItemSelected(item);

        if ((id == R.id.action_load)&&saveReturn) { // just loaded data, so refresh
            processFinish(0);
        }

        return saveReturn;
    }
}


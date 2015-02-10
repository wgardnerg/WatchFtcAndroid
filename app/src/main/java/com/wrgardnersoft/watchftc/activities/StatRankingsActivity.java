package com.wrgardnersoft.watchftc.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.adapters.StatRankingsListAdapter;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;

import java.util.Collections;
import java.util.Comparator;


public class StatRankingsActivity extends CommonMenuActivity implements AsyncResponse {

    private ListView listView;
    ClientTask clientTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        //     team = new ArrayList<Team>();

        if (myApp.dualDivision()) {
            setTitle(" " + getString(R.string.statRankings) + ", Division " + Integer.toString(myApp.division() + 1));
        } else {
            setTitle(" " + getString(R.string.statRankings));
        }

        setContentView(R.layout.activity_stat_rankings);

        if (myApp.team[myApp.division()].size() > 0) {
            inflateMe();
        } else {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
        }
    }

    private void inflateMe() {
        MyApp myApp = MyApp.getInstance();

        StatRankingsListAdapter adapter = new StatRankingsListAdapter(this,
                R.layout.list_item_stat_ranking, myApp.teamStatRanked[myApp.division()]);
        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TeamStatRanked teamPicked = (TeamStatRanked) parent.getItemAtPosition(position);

                MyApp myApp = (MyApp) getApplication();

                if (myApp.selectedTeams.contains(teamPicked.number)) {
                    myApp.selectedTeams.remove(Integer.valueOf(teamPicked.number));
                } else {
                    myApp.selectedTeams.add(teamPicked.number);
                }
                listView.invalidateViews();
                return true;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TeamStatRanked teamPicked = (TeamStatRanked) parent.getItemAtPosition(position);

                MyApp myApp = (MyApp) getApplication();
                myApp.currentTeamNumber = teamPicked.number;

                Intent getNameScreenIntent = new Intent(view.getContext(), MyTeamActivity.class);
                startActivity(getNameScreenIntent);
            }
        });

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_stat_rankings);

        MyApp myApp = (MyApp) getApplication();
        if (myApp.teamStatRanked[myApp.division()].size() > 0) {
            inflateMe();
        }

    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = (MyApp) getApplication();
        if (myApp.team[myApp.division()].size() > 0) {
            inflateMe();
        }

    }

    public void onClickNumberTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct = TeamStatRanked.getComparator(TeamStatRanked.SortParameter.NUMBER_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    public void onClickFtcRankTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct = TeamStatRanked.getComparator(TeamStatRanked.SortParameter.FTCRANK_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    public void onClickWinPercentTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct =
                TeamStatRanked.getComparator(TeamStatRanked.SortParameter.WINPERCENT_SORT,
                        TeamStatRanked.SortParameter.OPR_SORT,
                        TeamStatRanked.SortParameter.CCWM_SORT,
                        TeamStatRanked.SortParameter.FTCRANK_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    public void onClickOprTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct = TeamStatRanked.getComparator(TeamStatRanked.SortParameter.OPR_SORT,
                TeamStatRanked.SortParameter.CCWM_SORT,
                TeamStatRanked.SortParameter.WINPERCENT_SORT,
                TeamStatRanked.SortParameter.FTCRANK_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    public void onClickDprTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct = TeamStatRanked.getComparator(TeamStatRanked.SortParameter.DPR_SORT,
                TeamStatRanked.SortParameter.OPR_SORT,
                TeamStatRanked.SortParameter.WINPERCENT_SORT,
                TeamStatRanked.SortParameter.FTCRANK_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    public void onClickCcwmTextView(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        Comparator<TeamStatRanked> ct = TeamStatRanked.getComparator(TeamStatRanked.SortParameter.CCWM_SORT,
                TeamStatRanked.SortParameter.OPR_SORT,
                TeamStatRanked.SortParameter.WINPERCENT_SORT,
                TeamStatRanked.SortParameter.FTCRANK_SORT);
        Collections.sort(myApp.teamStatRanked[myApp.division()], ct);

        listView = (ListView) findViewById(R.id.stat_rankings_list_view);
        listView.invalidateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

        MyApp myApp = MyApp.getInstance();
        MenuItem item = menu.findItem(R.id.action_stat_rankings);

        item.setVisible(false);

        return true;
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

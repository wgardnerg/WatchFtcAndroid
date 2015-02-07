package com.wrgardnersoft.watchftc.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.adapters.MatchesExpandableListAdapter;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MatchesActivity extends ActionBarActivity implements AsyncResponse {


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

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
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
        expListView = (ExpandableListView) findViewById(R.id.matches_expListView);
        //  Log.i("exp list view", expListView.toString());
        prepareListData();
        MatchesExpandableListAdapter listAdapter = new MatchesExpandableListAdapter(this,
                listDataHeader, listDataChild);
        expListView.setAdapter(listAdapter);
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

        MyApp myApp = (MyApp) getApplication();

        for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
            if (myApp.match[myApp.division()].get(i).title.startsWith("Q")) {
                qual.add(myApp.match[myApp.division()].get(i));
            } else if (myApp.match[myApp.division()].get(i).title.startsWith("S")) {
                semi.add(myApp.match[myApp.division()].get(i));
            } else {
                finals.add(myApp.match[myApp.division()].get(i));
            }
        }

        listDataChild.put(listDataHeader.get(0), qual); // Header, Child data
        listDataChild.put(listDataHeader.get(1), semi);
        listDataChild.put(listDataHeader.get(2), finals);
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
        MyApp myApp = MyApp.getInstance();

        if (myApp.dualDivision()) {
            getMenuInflater().inflate(R.menu.menu_main_change_div, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        MyApp myApp = MyApp.getInstance();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setup) {
            Intent getNameScreenIntent = new Intent(this, SetupActivity.class);
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id == R.id.action_ftc_rankings) {
            Intent getNameScreenIntent = new Intent(this, FtcRankingsActivity.class);
            startActivity(getNameScreenIntent);
            return true;
  /*      } else if (id == R.id.action_matches) {
            Intent getNameScreenIntent = new Intent(this, MatchesActivity.class);
            startActivity(getNameScreenIntent);
   //         finish();
            return true; */
        } else if (id == R.id.action_stat_rankings) {
            Intent getNameScreenIntent = new Intent(this, StatRankingsActivity.class);
            startActivity(getNameScreenIntent);
            return true;
        } else if (id == R.id.action_teams) {
            Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
            startActivity(getNameScreenIntent);
            return true;
        } else if (id == R.id.action_refresh) {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
            return true;
        } else if (myApp.dualDivision()) {
            if (id== R.id.action_change_division) {
                if (myApp.division() ==0) {
                    myApp.setDivision(1);
                } else {
                    myApp.setDivision(0);
                }
                // restart activity to load data from new division
                Intent intent = new Intent(this, TeamsActivity.class);
                finish();
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

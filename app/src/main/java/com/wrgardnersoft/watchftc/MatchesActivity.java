package com.wrgardnersoft.watchftc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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

        if (myApp.match.size() > 0) {
            inflateMe();
        } else {
            clientTask = new ClientTask();
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
        if (myApp.match.size() > 0) {
            inflateMe();
        }
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<Match>>();

        // Adding child data
        listDataHeader.add("Qualifier");
        listDataHeader.add("Semi-Final");
        listDataHeader.add("Final");

        // Adding child data
        List<Match> qual = new ArrayList<Match>();
        List<Match> semi = new ArrayList<Match>();
        List<Match> finals = new ArrayList<Match>();

        MyApp myApp = (MyApp) getApplication();

        for (int i = 0; i < myApp.match.size(); i++) {
            if (myApp.match.get(i).title.startsWith("Q")) {
                qual.add(myApp.match.get(i));
            } else if (myApp.match.get(i).title.startsWith("S")) {
                semi.add(myApp.match.get(i));
            } else {
                finals.add(myApp.match.get(i));
            }
        }

        listDataChild.put(listDataHeader.get(0), qual); // Header, Child data
        listDataChild.put(listDataHeader.get(1), semi);
        listDataChild.put(listDataHeader.get(2), finals);
    }


    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = (MyApp) getApplication();

        if (myApp.match.size() > 0) {
            inflateMe();
        }

    }

    // AsyncTask
    public class ClientTask extends AsyncTask<Void, Void, Void> {
        public AsyncResponse delegate;

        ProgressDialog mProgressDialog;

        boolean serverOK;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MatchesActivity.this);
            mProgressDialog.setTitle("Watch FTC");
            mProgressDialog.setMessage("Accessing Match Info...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String teamsUrl;
            String[] urlSuffix = {"", ":8080"};
            String pageSuffix = "MatchDetails";

            MyApp myApp = (MyApp) getApplication();

            serverOK = false;

            for (int i = 0; (i < urlSuffix.length) && (!serverOK); i++) {

                teamsUrl = "http://" + myApp.serverAddressString(myApp.division()) +
                        urlSuffix[i] + "/" + pageSuffix;
                try {
                    // Connect to the web site
                    Document document = Jsoup.connect(teamsUrl).get();
                    serverOK = true;

                    Element table = document.select("table").get(0); //select the first table.
                    Elements rows = table.select("tr");
                    Log.i("Match Rows", Integer.toString(rows.size()));

                    for (int j = 2; j < rows.size(); j++) { //first row is the col names so skip it.
                        Element row = rows.get(j);
                        Elements cols = row.select("td");

                        String redTeamString = cols.get(2).text();
                        String blueTeamString = cols.get(3).text();
                        String redTeam[] = redTeamString.split("\\s+");
                        String blueTeam[] = blueTeamString.split("\\s+");

                        myApp.match.add(new Match(j - 1,
                                cols.get(0).text(),
                                cols.get(1).text(),
                                redTeam[0],
                                redTeam[1],
                                blueTeam[0],
                                blueTeam[1],
                                cols.get(4).text(),
                                cols.get(5).text(),
                                cols.get(6).text(),
                                cols.get(7).text(),
                                cols.get(8).text(),
                                cols.get(9).text(),
                                cols.get(10).text(),
                                cols.get(11).text(),
                                cols.get(12).text(),
                                cols.get(13).text(),
                                cols.get(14).text(),
                                cols.get(15).text()
                        ));

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    serverOK = false;
                }
            }

            if (!serverOK) {
                Context context = getApplicationContext();
                CharSequence text = "Could not download data from server";
                int duration = Toast.LENGTH_LONG;

                Toast.makeText(context, text, duration).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {
                mProgressDialog.dismiss();
            } finally {

            }
            delegate.processFinish(RESULT_OK);
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
     /*   } else if (id == R.id.action_my_team) {
            Intent getNameScreenIntent = new Intent(this, MyTeamActivity.class);
            startActivity(getNameScreenIntent);
            finish();
            return true;*/
        } else if (id == R.id.action_ftc_rankings) {
            Intent getNameScreenIntent = new Intent(this, FtcRankingsActivity.class);
            startActivity(getNameScreenIntent);
  //          finish();
            return true;
  /*      } else if (id == R.id.action_matches) {
            Intent getNameScreenIntent = new Intent(this, MatchesActivity.class);
            startActivity(getNameScreenIntent);
   //         finish();
            return true; */
        } else if (id == R.id.action_stat_rankings) {
            Intent getNameScreenIntent = new Intent(this, StatRankingsActivity.class);
            startActivity(getNameScreenIntent);
    //        finish();
            return true;
        } else if (id == R.id.action_teams) {
            Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
            startActivity(getNameScreenIntent);
   //         finish();
            return true;
        } else if (id == R.id.action_refresh) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        } else if (myApp.dualDivision()) {
            if (id== R.id.action_change_division) {
                if (myApp.division() ==0) {
                    myApp.setDivision(1);
                } else {
                    myApp.setDivision(0);
                }
                // restart activity to load data from new division
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


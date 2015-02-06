package com.wrgardnersoft.watchftc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class TeamsActivity extends ActionBarActivity implements AsyncResponse {

    private ListView listView;
    ClientTask clientTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        //     team = new ArrayList<Team>();

        if (myApp.dualDivision()) {
            setTitle(" " + getString(R.string.teams) + ", Division " + Integer.toString(myApp.division() + 1));
        } else {
            setTitle(" " + getString(R.string.teams));
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        setContentView(R.layout.activity_teams);

        if (myApp.team.size() > 0) {
            inflateMe();
        } else {
            clientTask = new ClientTask();
            clientTask.delegate = this;
            clientTask.execute();
        }

    }

    private void inflateMe() {

        MyApp myApp = (MyApp) getApplication();

        TeamListAdapter adapter = new TeamListAdapter(this,
                R.layout.list_item_team, myApp.team);
        listView = (ListView) findViewById(R.id.teams_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Team teamPicked = (Team) parent.getItemAtPosition(position);

                MyApp myApp = (MyApp) getApplication();

                if (myApp.selectedTeams.contains(teamPicked.number)) {
                    myApp.selectedTeams.remove(Integer.valueOf(teamPicked.number));
                } else {
                    myApp.selectedTeams.add(teamPicked.number);
                }
                listView.invalidateViews();
            }
        });


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_teams);

        MyApp myApp = (MyApp) getApplication();
        if (myApp.team.size() > 0) {
            inflateMe();
        }
    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = (MyApp) getApplication();
        if (myApp.team.size() > 0) {
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
            mProgressDialog = new ProgressDialog(TeamsActivity.this);
            mProgressDialog.setTitle("Watch FTC");
            mProgressDialog.setMessage("Accessing Team Info...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String teamsUrl;
            String[] urlSuffix = {"", ":8080"};
            String pageSuffix = "TeamList";

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

                    myApp.team.clear(); // made it this far, so hopefully good data coming

                    for (int j = 1; j < rows.size(); j++) { //first row is the col names so skip it.
                        Element row = rows.get(j);
                        Elements cols = row.select("td");

                        myApp.team.add(new Team(Integer.parseInt(cols.get(0).text()),
                                        cols.get(1).text(),
                                        cols.get(2).text(),
                                        cols.get(3).text(),
                                        cols.get(4).text(),
                                        cols.get(5).text())
                        );
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

        MyApp myApp = MyApp.getInstance();

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setup) {
            Intent getNameScreenIntent = new Intent(this, SetupActivity.class);
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id == R.id.action_ftc_rankings) {
            Intent getNameScreenIntent = new Intent(this, FtcRankingsActivity.class);
            startActivity(getNameScreenIntent);
            //          finish();
            return true;
        } else if (id == R.id.action_matches) {
            Intent getNameScreenIntent = new Intent(this, MatchesActivity.class);
            startActivity(getNameScreenIntent);
            //         finish();
            return true;
        } else if (id == R.id.action_stat_rankings) {
            Intent getNameScreenIntent = new Intent(this, StatRankingsActivity.class);
            startActivity(getNameScreenIntent);
            //        finish();
            return true;
  /*      } else if (id == R.id.action_teams) {
            Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
            startActivity(getNameScreenIntent);
            //         finish();
            return true;*/
        } else if (id == R.id.action_refresh) {
            myApp.team.clear();
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

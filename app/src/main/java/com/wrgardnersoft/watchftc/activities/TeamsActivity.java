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
import com.wrgardnersoft.watchftc.adapters.TeamListAdapter;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Team;


public class TeamsActivity extends CommonMenuActivity implements AsyncResponse {

    private ListView listView;
    ClientTask clientTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        if (myApp.dualDivision()) {
            setTitle(" " + getString(R.string.teams) + ", Division " + Integer.toString(myApp.division() + 1));
        } else {
            setTitle(" " + getString(R.string.teams));
        }

        setContentView(R.layout.activity_teams);

        if (myApp.team[myApp.division()].size() > 0) {
            inflateMe();
        } else {
            clientTask = new ClientTask(this);
            clientTask.delegate = this;
            clientTask.execute();
        }

    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = (MyApp) getApplication();
        if (myApp.team[myApp.division()].size() > 0) {
            inflateMe();
        }

    }

    private void inflateMe() {

        MyApp myApp = (MyApp) getApplication();

        TeamListAdapter adapter = new TeamListAdapter(this,
                R.layout.list_item_team, myApp.team[myApp.division()]);
        listView = (ListView) findViewById(R.id.teams_list_view);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Team teamPicked = (Team) parent.getItemAtPosition(position);

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
                Team teamPicked = (Team) parent.getItemAtPosition(position);

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
        setContentView(R.layout.activity_teams);

        MyApp myApp = (MyApp) getApplication();
        if (myApp.team[myApp.division()].size() > 0) {
            inflateMe();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);

        MyApp myApp = MyApp.getInstance();

        MenuItem item = menu.findItem(R.id.action_teams);
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

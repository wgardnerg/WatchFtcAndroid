package com.wrgardnersoft.watchftc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class StatRankingsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        setTitle(" "+getString(R.string.statRankings) + ", Division " + Integer.toString(myApp.division() + 1));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        setContentView(R.layout.activity_stat_rankings);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_setup) {
            Intent getNameScreenIntent = new Intent(this, SetupActivity.class);
            startActivity(getNameScreenIntent);
            finish();
            return true;
     /*   } else if (id == R.id.action_my_team) {
            Intent getNameScreenIntent = new Intent(this, MyTeamActivity.class );
            startActivity(getNameScreenIntent);
            finish();
            return true;*/
        } else if (id == R.id.action_ftc_rankings) {
            Intent getNameScreenIntent = new Intent(this, FtcRankingsActivity.class );
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id == R.id.action_matches) {
            Intent getNameScreenIntent = new Intent(this, MatchesActivity.class );
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id == R.id.action_stat_rankings) {
            Intent getNameScreenIntent = new Intent(this, StatRankingsActivity.class );
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id == R.id.action_teams) {
            Intent getNameScreenIntent = new Intent(this, TeamsActivity.class );
            startActivity(getNameScreenIntent);
            finish();
            return true;
        } else if (id==R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

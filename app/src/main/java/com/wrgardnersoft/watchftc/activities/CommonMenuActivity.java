package com.wrgardnersoft.watchftc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Team;
import com.wrgardnersoft.watchftc.models.TeamFtcRanked;

/**
 * Created by Bill on 2/10/2015.
 */
public class CommonMenuActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MyApp myApp = MyApp.getInstance();
        if (!myApp.dualDivision()) {
            MenuItem item = menu.findItem(R.id.action_change_division);
            item.setVisible(false);
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
        } else if (id == R.id.action_teams) {
            Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
            startActivity(getNameScreenIntent);
            //         finish();
            return true;
        } else if (id == R.id.action_share) {
            String shareOutput = "";
            if (myApp.team[0].size() > 0) {
                shareOutput = shareOutput + "Division,0" + System.getProperty("line.separator");
                shareOutput = shareOutput + "Teams," + myApp.team[0].size() + System.getProperty("line.separator");
                shareOutput = shareOutput + Team.shareHeader();
                for (Team t : myApp.team[0]) {
                    shareOutput = shareOutput + t;
                }
                shareOutput = shareOutput + TeamFtcRanked.shareHeader();
                for (TeamFtcRanked t : myApp.teamFtcRanked[0]) {
                    shareOutput = shareOutput + t;
                }
                shareOutput = shareOutput + Match.shareHeader();
                for (Match m : myApp.match[0]) {
                    shareOutput = shareOutput + m;
                }
            }
            if (myApp.team[1].size() > 0) {
                shareOutput = shareOutput + "Division,1" + System.getProperty("line.separator");
                shareOutput = shareOutput + "Teams," + myApp.team[1].size() + System.getProperty("line.separator");
                shareOutput = shareOutput + Team.shareHeader();
                for (Team t : myApp.team[1]) {
                    shareOutput = shareOutput + t;
                }
                shareOutput = shareOutput + TeamFtcRanked.shareHeader();
                for (TeamFtcRanked t : myApp.teamFtcRanked[1]) {
                    shareOutput = shareOutput + t;
                }
                shareOutput = shareOutput + Match.shareHeader();
                for (Match m : myApp.match[1]) {
                    shareOutput = shareOutput + m;
                }
            }
            if ((myApp.team[0].size() > 0) || (myApp.team[1].size() > 0)) {
                Intent intent = new Intent(android.content.Intent.ACTION_SEND);
                intent.setType("message/rfc822");

// Add data to the intent, the receiving app will decide what to do with it.
                intent.putExtra(Intent.EXTRA_SUBJECT, "Watch FTC Data");
                intent.putExtra(Intent.EXTRA_TEXT, shareOutput);
                startActivity(Intent.createChooser(intent, "Select share option"));
            }
            return true;

        } else if (myApp.dualDivision()) {
            if (id == R.id.action_change_division) {
                if (myApp.division() == 0) {
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


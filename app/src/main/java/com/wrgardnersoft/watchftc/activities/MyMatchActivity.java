package com.wrgardnersoft.watchftc.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;

import java.util.ArrayList;


public class MyMatchActivity extends CommonMenuActivity implements AsyncResponse {

    ClientTask clientTask;

    public ArrayList<Match> myMatch;

    public MyMatchActivity() {
        this.myMatch = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        for (Match m : myApp.match[myApp.division()]) {
            if (m.number == myApp.currentMatchNumber) {
                myMatch.add(m);
            }
            //          Log.i("Size of match find", String.valueOf(myMatch.size()));
        }


        setTitle(" Match: " + myMatch.get(0).title);
        setContentView(R.layout.activity_my_match);

        inflateMe();
    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = MyApp.getInstance();

        myMatch.clear();
        
        for (Match m : myApp.match[myApp.division()]) {
            if (m.number == myApp.currentMatchNumber) {
                myMatch.add(m);
            }
            //          Log.i("Size of match find", String.valueOf(myMatch.size()));
        }
        inflateMe();

    }

    private void inflateMe() {
        TextView tv;

        MyApp myApp = (MyApp) getApplication();

        tv = (TextView) findViewById(R.id.mm_rT0);
        tv.setText(String.valueOf(myMatch.get(0).rTeam0));
        if (myApp.selectedTeams.contains(myMatch.get(0).rTeam0)) {
            tv.setBackgroundResource(R.color.yellow);
        } else {
            tv.setBackgroundResource(R.color.lighter_red);
        }
        tv = (TextView) findViewById(R.id.mm_rT1);
        tv.setText(String.valueOf(myMatch.get(0).rTeam1));
        if (myApp.selectedTeams.contains(myMatch.get(0).rTeam1)) {
            tv.setBackgroundResource(R.color.yellow);
        } else {
            tv.setBackgroundResource(R.color.lighter_red);
        }
        tv = (TextView) findViewById(R.id.mm_rT2);
        if (myMatch.get(0).rTeam2 > 0) {
            tv.setText(String.valueOf(myMatch.get(0).rTeam2));
            if (myApp.selectedTeams.contains(myMatch.get(0).rTeam2)) {
                tv.setBackgroundResource(R.color.yellow);
            } else {
                tv.setBackgroundResource(R.color.lighter_red);
            }
            TextView tv2 = (TextView) findViewById(R.id.mm_rT0);
            LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams) tv2.getLayoutParams();
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tv.getLayoutParams();
            param.height = param2.height;
            tv.setLayoutParams(param);
        } else {
            tv.setText("");
            tv.setBackgroundResource(R.color.lighter_red);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tv.getLayoutParams();
            param.height = 0;
            tv.setLayoutParams(param);
        }
        if (myApp.selectedTeams.contains(myMatch.get(0).rTeam0)) {
            tv.setBackgroundResource(R.color.yellow);
        } else {
            tv.setBackgroundResource(R.color.lighter_red);
        }

        tv = (TextView) findViewById(R.id.mm_bT0);
        tv.setText(String.valueOf(myMatch.get(0).bTeam0));
        if (myApp.selectedTeams.contains(myMatch.get(0).bTeam0)) {
            tv.setBackgroundResource(R.color.yellow);
        } else {
            tv.setBackgroundResource(R.color.lighter_blue);
        }
        tv = (TextView) findViewById(R.id.mm_bT1);
        tv.setText(String.valueOf(myMatch.get(0).bTeam1));
        if (myApp.selectedTeams.contains(myMatch.get(0).bTeam1)) {
            tv.setBackgroundResource(R.color.yellow);
        } else {
            tv.setBackgroundResource(R.color.lighter_blue);
        }
        tv = (TextView) findViewById(R.id.mm_bT2);
        if (myMatch.get(0).bTeam2 > 0) {
            tv.setText(String.valueOf(myMatch.get(0).bTeam2));
            if (myApp.selectedTeams.contains(myMatch.get(0).bTeam2)) {
                tv.setBackgroundResource(R.color.yellow);
            } else {
                tv.setBackgroundResource(R.color.lighter_blue);
            }
            TextView tv2 = (TextView) findViewById(R.id.mm_bT0);
            LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams) tv2.getLayoutParams();
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tv.getLayoutParams();
            param.height = param2.height;
            tv.setLayoutParams(param);
        } else {
            tv.setText("");
            tv.setBackgroundResource(R.color.lighter_blue);
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) tv.getLayoutParams();
            param.height = 0;
            tv.setLayoutParams(param);
        }


        if (myMatch.get(0).rTot >= 0) {

            if (myMatch.get(0).rTot > myMatch.get(0).bTot) {
                tv = (TextView) findViewById(R.id.mm_rTot);
                tv.setBackgroundResource(R.drawable.red_border);
            } else {
                tv = (TextView) findViewById(R.id.mm_rTot);
                tv.setBackgroundResource(R.drawable.no_border_red);
            }
            if (myMatch.get(0).rTot < myMatch.get(0).bTot) {
                tv = (TextView) findViewById(R.id.mm_bTot);
                tv.setBackgroundResource(R.drawable.blue_border);
            } else {
                tv = (TextView) findViewById(R.id.mm_bTot);
                tv.setBackgroundResource(R.drawable.no_border_blue);
            }
            tv = (TextView) findViewById(R.id.mm_rTot);
            tv.setText(String.valueOf(myMatch.get(0).rTot));
            tv = (TextView) findViewById(R.id.mm_rAuto);
            tv.setText(String.valueOf(myMatch.get(0).rAuto));
            tv = (TextView) findViewById(R.id.mm_rAutoB);
            tv.setText(String.valueOf(myMatch.get(0).rAutoB));
            tv = (TextView) findViewById(R.id.mm_rTele);
            tv.setText(String.valueOf(myMatch.get(0).rTele));
            tv = (TextView) findViewById(R.id.mm_rEndG);
            tv.setText(String.valueOf(myMatch.get(0).rEndG));
            tv = (TextView) findViewById(R.id.mm_rPen);
            tv.setText(String.valueOf(myMatch.get(0).rPen));

            tv = (TextView) findViewById(R.id.mm_bTot);
            tv.setText(String.valueOf(myMatch.get(0).bTot));
            tv = (TextView) findViewById(R.id.mm_bAuto);
            tv.setText(String.valueOf(myMatch.get(0).bAuto));
            tv = (TextView) findViewById(R.id.mm_bAutoB);
            tv.setText(String.valueOf(myMatch.get(0).bAutoB));
            tv = (TextView) findViewById(R.id.mm_bTele);
            tv.setText(String.valueOf(myMatch.get(0).bTele));
            tv = (TextView) findViewById(R.id.mm_bEndG);
            tv.setText(String.valueOf(myMatch.get(0).bEndG));
            tv = (TextView) findViewById(R.id.mm_bPen);
            tv.setText(String.valueOf(myMatch.get(0).bPen));
        } else {
            tv = (TextView) findViewById(R.id.mm_rTot);
            tv.setBackgroundResource(R.drawable.no_border_red);

            tv = (TextView) findViewById(R.id.mm_bTot);
            tv.setBackgroundResource(R.drawable.no_border_blue);

            tv = (TextView) findViewById(R.id.mm_rTot);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_rAuto);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_rAutoB);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_rTele);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_rEndG);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_rPen);
            tv.setText("");

            tv = (TextView) findViewById(R.id.mm_bTot);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_bAuto);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_bAutoB);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_bTele);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_bEndG);
            tv.setText("");
            tv = (TextView) findViewById(R.id.mm_bPen);
            tv.setText("");
        }

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_my_match);

        inflateMe();
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


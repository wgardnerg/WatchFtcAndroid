package com.wrgardnersoft.watchftc.activities;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;

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
                Match mm = new Match(m);
                myMatch.add(mm);
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

        if ((myMatch.get(0).rTot < 0) && (myApp.enableMatchPrediction)) {
            myMatch.get(0).predicted = true;
            myMatch.get(0).rTot = 0;
            myMatch.get(0).bTot = 0;
            myMatch.get(0).rAuto = 0;
            myMatch.get(0).rAutoB = 0;
            myMatch.get(0).rTele = 0;
            myMatch.get(0).rEndG = 0;
            myMatch.get(0).rPen = 0;
            myMatch.get(0).bAuto = 0;
            myMatch.get(0).bAutoB = 0;
            myMatch.get(0).bTele = 0;
            myMatch.get(0).bEndG = 0;
            myMatch.get(0).bPen = 0;
            for (TeamStatRanked t : myApp.teamStatRanked[myApp.division()]) {
                if ((myMatch.get(0).rTeam0 == t.number) ||
                        (myMatch.get(0).rTeam1 == t.number) ||
                        (myMatch.get(0).rTeam2 == t.number)) {
                    myMatch.get(0).rAuto += t.oprA[TeamStatRanked.StatType.AUTONOMOUS.ordinal()];
                    myMatch.get(0).rAutoB += t.oprA[TeamStatRanked.StatType.AUTO_BONUS.ordinal()];
                    myMatch.get(0).rTele += t.oprA[TeamStatRanked.StatType.TELEOP.ordinal()];
                    myMatch.get(0).rEndG += t.oprA[TeamStatRanked.StatType.END_GAME.ordinal()];
                    myMatch.get(0).bPen -= t.oprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                    myMatch.get(0).rTot += t.oprA[TeamStatRanked.StatType.TOTAL.ordinal()];
                    myMatch.get(0).bTot -= t.oprA[TeamStatRanked.StatType.PENALTY.ordinal()];

                    myMatch.get(0).bAuto -= t.dprA[TeamStatRanked.StatType.AUTONOMOUS.ordinal()];
                    myMatch.get(0).bAutoB -= t.dprA[TeamStatRanked.StatType.AUTO_BONUS.ordinal()];
                    myMatch.get(0).bTele -= t.dprA[TeamStatRanked.StatType.TELEOP.ordinal()];
                    myMatch.get(0).bEndG -= t.dprA[TeamStatRanked.StatType.END_GAME.ordinal()];
                    myMatch.get(0).rPen += t.dprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                    myMatch.get(0).bTot -= t.dprA[TeamStatRanked.StatType.TOTAL.ordinal()];
                    myMatch.get(0).rTot += t.dprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                }
                if ((myMatch.get(0).bTeam0 == t.number) ||
                        (myMatch.get(0).bTeam1 == t.number) ||
                        (myMatch.get(0).bTeam2 == t.number)) {
                    myMatch.get(0).bAuto += t.oprA[TeamStatRanked.StatType.AUTONOMOUS.ordinal()];
                    myMatch.get(0).bAutoB += t.oprA[TeamStatRanked.StatType.AUTO_BONUS.ordinal()];
                    myMatch.get(0).bTele += t.oprA[TeamStatRanked.StatType.TELEOP.ordinal()];
                    myMatch.get(0).bEndG += t.oprA[TeamStatRanked.StatType.END_GAME.ordinal()];
                    myMatch.get(0).rPen -= t.oprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                    myMatch.get(0).bTot += t.oprA[TeamStatRanked.StatType.TOTAL.ordinal()];
                    myMatch.get(0).rTot -= t.oprA[TeamStatRanked.StatType.PENALTY.ordinal()];

                    myMatch.get(0).rAuto -= t.dprA[TeamStatRanked.StatType.AUTONOMOUS.ordinal()];
                    myMatch.get(0).rAutoB -= t.dprA[TeamStatRanked.StatType.AUTO_BONUS.ordinal()];
                    myMatch.get(0).rTele -= t.dprA[TeamStatRanked.StatType.TELEOP.ordinal()];
                    myMatch.get(0).rEndG -= t.dprA[TeamStatRanked.StatType.END_GAME.ordinal()];
                    myMatch.get(0).bPen += t.dprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                    myMatch.get(0).rTot -= t.dprA[TeamStatRanked.StatType.TOTAL.ordinal()];
                    myMatch.get(0).bTot += t.dprA[TeamStatRanked.StatType.PENALTY.ordinal()];
                }
            }


            myMatch.get(0).rAuto = Math.round(myMatch.get(0).rAuto);
            myMatch.get(0).rAutoB = Math.round(myMatch.get(0).rAutoB);
            myMatch.get(0).rTele = Math.round(myMatch.get(0).rTele);
            myMatch.get(0).rEndG = Math.round(myMatch.get(0).rEndG);
            myMatch.get(0).rPen = Math.round(myMatch.get(0).rPen);
            myMatch.get(0).rTot = Math.round(myMatch.get(0).rTot);

            myMatch.get(0).bAuto = Math.round(myMatch.get(0).bAuto);
            myMatch.get(0).bAutoB = Math.round(myMatch.get(0).bAutoB);
            myMatch.get(0).bTele = Math.round(myMatch.get(0).bTele);
            myMatch.get(0).bEndG = Math.round(myMatch.get(0).bEndG);
            myMatch.get(0).bPen = Math.round(myMatch.get(0).bPen);
            myMatch.get(0).bTot = Math.round(myMatch.get(0).bTot);

        }

        if (myMatch.get(0).predicted) {
            tv = (TextView) findViewById(R.id.my_match_title);
            tv.setText(getString(R.string.predictedResult) + " ");
            tv.setTypeface(null, Typeface.BOLD_ITALIC);
        } else if (myMatch.get(0).rTot < 0) {
            tv = (TextView) findViewById(R.id.my_match_title);
            tv.setText(getString(R.string.noResultYet));
            tv.setTypeface(null, Typeface.NORMAL);
        }

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
            if (myMatch.get(0).predicted) {
                tv = (TextView) findViewById(R.id.mm_rTot);
                tv.setTypeface(null, Typeface.ITALIC);
                tv = (TextView) findViewById(R.id.mm_bTot);
                tv.setTypeface(null, Typeface.ITALIC);
            } else {
                tv = (TextView) findViewById(R.id.mm_rTot);
                tv.setTypeface(null, Typeface.BOLD);
                tv = (TextView) findViewById(R.id.mm_bTot);
                tv.setTypeface(null, Typeface.BOLD);
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
            }

            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }

            tv = (TextView) findViewById(R.id.mm_rTot);
            tv.setText(String.format("%.0f", myMatch.get(0).rTot));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_rAuto);
            tv.setText(String.format("%.0f", myMatch.get(0).rAuto));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_rAutoB);
            tv.setText(String.format("%.0f", myMatch.get(0).rAutoB));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_rTele);
            tv.setText(String.format("%.0f", myMatch.get(0).rTele));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_rEndG);
            tv.setText(String.format("%.0f", myMatch.get(0).rEndG));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_rPen);
            tv.setText(String.format("%.0f", myMatch.get(0).rPen));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }

            tv = (TextView) findViewById(R.id.mm_bTot);
            tv.setText(String.format("%.0f", myMatch.get(0).bTot));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_bAuto);
            tv.setText(String.format("%.0f", myMatch.get(0).bAuto));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_bAutoB);
            tv.setText(String.format("%.0f", myMatch.get(0).bAutoB));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_bTele);
            tv.setText(String.format("%.0f", myMatch.get(0).bTele));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_bEndG);
            tv.setText(String.format("%.0f", myMatch.get(0).bEndG));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
            tv = (TextView) findViewById(R.id.mm_bPen);
            tv.setText(String.format("%.0f", myMatch.get(0).bPen));
            if (myMatch.get(0).predicted) {
                tv.setTypeface(null, Typeface.ITALIC);
            }
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


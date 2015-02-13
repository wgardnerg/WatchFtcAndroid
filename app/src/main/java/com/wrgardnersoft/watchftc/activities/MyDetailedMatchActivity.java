package com.wrgardnersoft.watchftc.activities;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.internet.ClientTask;
import com.wrgardnersoft.watchftc.models.DetailedMatch;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;
import com.wrgardnersoft.watchftc.views.VerticalTextView;

import java.util.ArrayList;

/**
 * Created by Bill on 2/11/2015.
 */
public class MyDetailedMatchActivity extends CommonMenuActivity implements AsyncResponse {

    ClientTask clientTask;

    public ArrayList<Match> myMatch;
    public ArrayList<DetailedMatch> myDetailedMatch;
    public Match mmPartial[];

    enum DisplayType {OPR, DPR, CCWM}

    String displayString[] = {"OPR", "DPR", "CCWM"};

    DisplayType displayType;

    public MyDetailedMatchActivity() {

        this.myMatch = new ArrayList<>();
        this.myDetailedMatch = new ArrayList<>();
        this.mmPartial = new Match[MyApp.TEAMS_PER_ALLIANCE];
        for (int i = 0; i < MyApp.TEAMS_PER_ALLIANCE; i++) {
            this.mmPartial[i] = new Match();
        }
        this.displayType = DisplayType.OPR;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        MyApp myApp = (MyApp) getApplication();

        for (Match m : myApp.match[myApp.division()]) {

            if (m.number == myApp.currentMatchNumber) {
                Match mm = new Match(m);
                myMatch.add(mm);
                for (int i = 0; i < MyApp.TEAMS_PER_ALLIANCE; i++) {
                    this.mmPartial[i] = mm;
                }
                myDetailedMatch.add(new DetailedMatch(this.mmPartial));
            }
            //          Log.i("Size of match find", String.valueOf(myMatch.size()));
        }


        setTitle(" Match: " + myMatch.get(0).title);
        setContentView(R.layout.activity_my_detailed_match);

        inflateMe();
    }

    public void processFinish(int result) {
        //this you will received result fired from async class of onPostExecute(result) method.
        MyApp myApp = MyApp.getInstance();

        myMatch.clear();
        myDetailedMatch.clear();

        for (Match mm : myApp.match[myApp.division()]) {
            if (mm.number == myApp.currentMatchNumber) {
                myMatch.add(mm);
                for (int i = 0; i < MyApp.TEAMS_PER_ALLIANCE; i++) {
                    this.mmPartial[i] = mm;
                }
                myDetailedMatch.add(new DetailedMatch(this.mmPartial));
            }
            //          Log.i("Size of match find", String.valueOf(myMatch.size()));
        }
        inflateMe();

    }

    private void inflateMe() {
        TextView tv;

        MyApp myApp = (MyApp) getApplication();

        Match mm = myMatch.get(0);
        DetailedMatch mdm = myDetailedMatch.get(0);

        int tvTeamId[][] = {
                {R.id.mdm_rT0, R.id.mdm_rT1, R.id.mdm_rT2},
                {R.id.mdm_bT0, R.id.mdm_bT1, R.id.mdm_bT2}
        };
        int tvScoreId[][] = {
                {R.id.mdm_r0_s0, R.id.mdm_r1_s0, R.id.mdm_r2_s0, R.id.mdm_b0_s0, R.id.mdm_b1_s0, R.id.mdm_b2_s0},
                {R.id.mdm_r0_s1, R.id.mdm_r1_s1, R.id.mdm_r2_s1, R.id.mdm_b0_s1, R.id.mdm_b1_s1, R.id.mdm_b2_s1},
                {R.id.mdm_r0_s2, R.id.mdm_r1_s2, R.id.mdm_r2_s2, R.id.mdm_b0_s2, R.id.mdm_b1_s2, R.id.mdm_b2_s2},
                {R.id.mdm_r0_s3, R.id.mdm_r1_s3, R.id.mdm_r2_s3, R.id.mdm_b0_s3, R.id.mdm_b1_s3, R.id.mdm_b2_s3},
                {R.id.mdm_r0_s4, R.id.mdm_r1_s4, R.id.mdm_r2_s4, R.id.mdm_b0_s4, R.id.mdm_b1_s4, R.id.mdm_b2_s4},
                {R.id.mdm_r0_s5, R.id.mdm_r1_s5, R.id.mdm_r2_s5, R.id.mdm_b0_s5, R.id.mdm_b1_s5, R.id.mdm_b2_s5}
        };

        mm.predicted = true;
        int teamsPerAllianceInThisMatch = 3;
        if (mm.teamNumber[MyApp.RED][2] <= 0) {
            teamsPerAllianceInThisMatch = 2;
        }
        for (int k = 0; k < teamsPerAllianceInThisMatch; k++) {
            for (int i = 0; i < MyApp.NUM_ALLIANCES; i++) {
                for (int j = 0; j < MyApp.NUM_SCORE_TYPES; j++) {
                    mdm.m[k].score[i][j] = 0;
                }
            }
        }
        for (TeamStatRanked t : myApp.teamStatRanked[myApp.division()]) {
            for (int color = 0; color < MyApp.NUM_ALLIANCES; color++) {
                for (int k = 0; k < teamsPerAllianceInThisMatch; k++) {

                    if ((mm.teamNumber[color][k] == t.number)) {

                        for (int i = 0; i < MyApp.NUM_SCORE_TYPES; i++) {
                            if (this.displayType == DisplayType.OPR) {
                                mdm.m[k].score[color][i] = t.oprA[i];
                            } else if (this.displayType == DisplayType.DPR) {
                                mdm.m[k].score[color][i] = t.dprA[i];
                            } else if (this.displayType == DisplayType.CCWM) {
                                mdm.m[k].score[color][i] = t.ccwmA[i];
                            }

                        }
                        /*
                        if (this.displayType == DisplayType.OPR) {
                            mdm.m[k].score[color][MyApp.ScoreType.TOTAL.ordinal()] +=
                                    t.oprA[MyApp.ScoreType.PENALTY.ordinal()];
                        } else if (this.displayType == DisplayType.DPR) {
                            mdm.m[k].score[color][MyApp.ScoreType.TOTAL.ordinal()] +=
                                    t.dprA[MyApp.ScoreType.PENALTY.ordinal()];
                        } else if (this.displayType == DisplayType.CCWM) {
                            mdm.m[k].score[color][MyApp.ScoreType.TOTAL.ordinal()] +=
                                    t.ccwmA[MyApp.ScoreType.PENALTY.ordinal()];
                        }*/

                    }
                }
            }
        }

        tv = (TextView) findViewById(R.id.my_detailed_match_title);
        tv.setText(getString(R.string.predictedBreakdown) + ": " + displayString[displayType.ordinal()] + " ");
        tv.setTypeface(null, Typeface.BOLD_ITALIC);

        for (int k = 0; k < teamsPerAllianceInThisMatch; k++) {
            for (int i = 0; i < MyApp.NUM_ALLIANCES; i++) {
                TextView vtv = (TextView) findViewById(tvTeamId[i][k]);

                vtv.setText(String.valueOf(mm.teamNumber[i][k]));
                if (myApp.selectedTeams.contains(mm.teamNumber[i][k])) {
                    vtv.setBackgroundResource(R.color.yellow);
                } else {
                    if (i == MyApp.RED) {
                        vtv.setBackgroundResource(R.color.lighter_red);
                    } else {
                        vtv.setBackgroundResource(R.color.lighter_blue);
                    }
                }
                VerticalTextView vtv2 = (VerticalTextView) findViewById(tvTeamId[i][0]);
                LinearLayout.LayoutParams param2 = (LinearLayout.LayoutParams) vtv2.getLayoutParams();
                LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) vtv.getLayoutParams();
                param.height = param2.height;
                vtv.setLayoutParams(param);
            }
        }

        for (int k = 0; k < teamsPerAllianceInThisMatch; k++) {
            for (int i = 0; i < MyApp.NUM_ALLIANCES; i++) {
                for (int j = 0; j < MyApp.NUM_SCORE_TYPES; j++) {
                    tv = (TextView) findViewById(tvScoreId[j][i * MyApp.TEAMS_PER_ALLIANCE + k]);
                    tv.setText(String.format("%.0f", mdm.m[k].score[i][j]));
                }
            }
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.activity_my_detailed_match);

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

    public void onClickMdmOprButton(View view) {

        displayType = DisplayType.OPR;
        inflateMe();
    }

    public void onClickMdmDprButton(View view) {

        displayType = DisplayType.DPR;
        inflateMe();
    }

    public void onClickMdmCcwmButton(View view) {

        displayType = DisplayType.CCWM;
        inflateMe();
    }


}



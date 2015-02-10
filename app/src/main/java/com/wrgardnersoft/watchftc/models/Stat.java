package com.wrgardnersoft.watchftc.models;


import android.util.Log;

import java.util.ArrayList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Created by Bill on 2/8/2015.
 */
public class Stat {

    public static void computeAll(int division) {
        MyApp myApp = MyApp.getInstance();

        myApp.teamStatRanked[division].clear();

        for (TeamFtcRanked t : myApp.teamFtcRanked[division]) {
            myApp.teamStatRanked[division].add(new TeamStatRanked());
            TeamStatRanked tStat = myApp.teamStatRanked[division].get(myApp.teamStatRanked[division].size() - 1);
            tStat.number = t.number;
            tStat.ftcRank = t.rank;
            /////////////////////////////////////////
            // win percentage is normal percentage of wins of matches played.
            // if no matches played, return 50% (best guess)
            // Also, add eps for each match if win percent is >=50%
            //   and subtract eps for each match if win percent is <50%
            // This will make teams that are 5-0 have a higher win percentage than 4-0 teams,
            //   and similarly make teams that are 0-5 have a lower win percentage than 0-4 teams.
            if (t.matches > 0) {
                tStat.winPercent = (float) t.qp / (float) t.matches * (float) 100.0 / (float) (2.0) + 0.0005;
                if (tStat.winPercent >= 50.0) {
                    tStat.winPercent += 0.00005 * t.matches; // reward more wins
                } else {
                    tStat.winPercent -= 0.00005 * t.matches; // penalize more losses
                }
            } else {
                tStat.winPercent = 50; // best guess if no matches played
            }
        }

        ArrayList<TeamStatRanked> ts = myApp.teamStatRanked[myApp.division()];
        ArrayList<Match> ms = myApp.match[myApp.division()];
        ArrayList<Integer> atn = new ArrayList<>();

        int numTeams = ts.size();
        for (int i = 0; i < numTeams; i++) {
            atn.add(ts.get(i).number);
        }

        // only count SCORED QUALIFYING matches
        int numMatches = 0;
        for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
            if ((ms.get(i).rTot >= 0) &&
                    (ms.get(i).title.substring(0, 1).matches("Q"))) {
                numMatches++;
            }
        }

/* ORIGINAL OPR, CCWM, DPR
        //////////////////////////////////////////
        // Matrix based stats: opr, dpr, ccwm
        // For these stats, counting penalties as negative for the alliance incurring the penalty!
        // So one alliance isn't rewarded if their opponent happens to get a lot of penalties.

        Matrix A = new Matrix(2*numMatches, numTeams);
        Matrix Bopr = new Matrix(2*numMatches, 1);
        Matrix Bdpr = new Matrix(2*numMatches, 1);
        Matrix Bccwm = new Matrix(2*numMatches, 1);

        int iM = 0;
        for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
            Match m = ms.get(i);

            if ((m.rTot >= 0) &&
                    (m.title.substring(0, 1).matches("Q"))) {

                A.set(iM, atn.indexOf(m.rTeam0), 1.0);
                A.set(iM, atn.indexOf(m.rTeam1), 1.0);
                Bopr.set(iM, 0, m.rTot - m.rPen - m.bPen);
                Bdpr.set(iM, 0, m.bTot - m.bPen - m.rPen);
                Bccwm.set(iM, 0, m.rTot - m.bTot);
                iM++;
                A.set(iM, atn.indexOf(m.bTeam0), 1.0);
                A.set(iM, atn.indexOf(m.bTeam1), 1.0);
                Bopr.set(iM, 0, m.bTot - m.bPen - m.rPen);
                Bdpr.set(iM, 0, m.rTot - m.rPen - m.bPen);
                Bccwm.set(iM, 0, m.bTot - m.rTot);
                iM++;
            }
        }


        // using SVD to solve so get result even when underdetermined!
        SingularValueDecomposition svd = new SingularValueDecomposition(A.transpose().times(A));
        Log.i("SVDRank", String.valueOf(svd.rank()));
        Matrix s = svd.getS();
        Matrix v = svd.getV();
        Matrix sInv = new Matrix(v.getColumnDimension(), v.getColumnDimension());
        for (int i = 0; i < svd.rank(); i++) {
            sInv.set(i, i, 1.0 / s.get(i, i));
        }
        Matrix AtAinv = v.times(sInv.times(v.transpose()));

        ////////////////////////
        // OPR:
        //
        // Compute A topr = B
        //   where A = rows with two 1s representing which teams were in each alliance
        //       A has 2 rows per match.
        //   and B is the offensive score minus penalties for that alliance.
        // Then, least squares topr solution is, solve A' A topr = A' b
        //   A' A is positive semidef, so use Cholesky decomposition to solve it.


        Matrix topr = new Matrix(numTeams, 1);

        topr = AtAinv.times(A.transpose().times(Bopr));

        ////////////////////////
        // CCWM:
        //
        // Same as OPR, but use team score minus opponent score for B

        Matrix tccwm = new Matrix(numTeams, 1);
        tccwm = AtAinv.times(A.transpose().times(Bccwm));

        ////////////////////////
        // CCWM = OPR - DPR, so DPR = OPR - CCWM
        //
        // = -(OPR - CCWM)

        Matrix tdpr= new Matrix(numTeams, 1);
        tdpr = AtAinv.times(A.transpose().times(Bdpr));

        for (int i=0; i< numTeams; i++) {
            myApp.teamStatRanked[division].get(i).ccwm = tccwm.get(i,0);
            myApp.teamStatRanked[division].get(i).opr = topr.get(i,0);
            myApp.teamStatRanked[division].get(i).dpr = tdpr.get(i,0);

        }
*/

// New and improved OPR, DPR, CCWM
        //////////////////////////////////////////
        // Matrix based stats: opr, dpr, ccwm
        // For these stats, counting penalties as negative for the alliance incurring the penalty!
        // So one alliance isn't rewarded if their opponent happens to get a lot of penalties.

        double dprPreWeight;

        if ( numMatches <= numTeams) {
            dprPreWeight = 0;
        } else {
            dprPreWeight = 0.5;
        }

        Matrix AA = new Matrix(2 * numMatches, 2 * numTeams);
        Matrix BoprA = new Matrix(2 * numMatches, 1);
        Matrix BccwmA = new Matrix(2 * numMatches, 1);

        double meanOffense = 0;

        int iM = 0;
        for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
            Match m = ms.get(i);

            if ((m.rTot >= 0) &&
                    (m.title.substring(0, 1).matches("Q"))) {

                AA.set(iM, atn.indexOf(m.rTeam0), 1.0);
                AA.set(iM, atn.indexOf(m.rTeam1), 1.0);
                AA.set(iM, numTeams + atn.indexOf(m.bTeam0), -2.0 * (dprPreWeight));
                AA.set(iM, numTeams + atn.indexOf(m.bTeam1), -2.0 * (dprPreWeight));
                BoprA.set(iM, 0, m.rTot - m.rPen - m.bPen);
                meanOffense += m.rTot - m.rPen - m.bPen;
                iM++;
                AA.set(iM, atn.indexOf(m.bTeam0), 1.0);
                AA.set(iM, atn.indexOf(m.bTeam1), 1.0);
                AA.set(iM, numTeams + atn.indexOf(m.rTeam0), -2.0 * (dprPreWeight));
                AA.set(iM, numTeams + atn.indexOf(m.rTeam1), -2.0 * (dprPreWeight));
                BoprA.set(iM, 0, m.bTot - m.bPen - m.rPen);
                meanOffense += m.bTot - m.bPen - m.rPen;
                iM++;
            }
        }
        meanOffense /= 2 * numMatches * 2; // 2 for red/blue, 2 for 2 teams per alliance
        Log.i("Mean Offense ", String.valueOf(meanOffense));
        for (int i = 0; i < 2 * numMatches; i++) {

            BoprA.set(i, 0, BoprA.get(i, 0) - 2 * meanOffense);

        }


        // using SVD to solve so get result even when underdetermined!
        SingularValueDecomposition svdA = new SingularValueDecomposition(AA.transpose().times(AA));
        Matrix sA = svdA.getS();
        Matrix vA = svdA.getV();
        Matrix sInvA = new Matrix(vA.getColumnDimension(), vA.getColumnDimension());
        for (int i = 0; i < svdA.rank(); i++) {
            sInvA.set(i, i, 1.0 / sA.get(i, i));
        }
        Matrix AtAinvA = vA.times(sInvA.times(vA.transpose()));

        ////////////////////////
        // OPR:
        //
        // Compute A topr = B
        //   where A = rows with two 1s representing which teams were in each alliance
        //       A has 2 rows per match.
        //   and B is the offensive score minus penalties for that alliance.
        // Then, least squares topr solution is, solve A' A topr = A' b
        //   A' A is positive semidef, so use Cholesky decomposition to solve it.


        Matrix toprA = new Matrix(2 * numTeams, 1);

 /*       Log.i("AtAinvr", String.valueOf(AtAinv.getRowDimension()));
        Log.i("AtAinvr", String.valueOf(AtAinv.getColumnDimension()));
        Log.i("At", String.valueOf(A.transpose().getRowDimension()));
        Log.i("At", String.valueOf(A.transpose().getColumnDimension()));
        Log.i("B", String.valueOf(Bopr.getRowDimension()));
        Log.i("B", String.valueOf(Bopr.getColumnDimension()));*/

        toprA = AtAinvA.times(AA.transpose().times(BoprA));

        ////////////////////////
        // CCWM:

        ////////////////////////
        // CCWM = OPR - DPR
        for (int i = 0; i < 6; i++) {
            String msg = "Team " + atn.get(i) + ", OPR " + toprA.get(i, 0) + ", DPR " + toprA.get(i + 6, 0);
            Log.i(msg, String.valueOf(0));

        }
        for (int i = 0; i < numTeams; i++) {
            if (dprPreWeight != 0) {
                toprA.set(i + numTeams, 0, toprA.get(i + numTeams, 0) * (2.0 * (dprPreWeight)));
            }
            myApp.teamStatRanked[division].get(i).ccwmA = toprA.get(i, 0) + toprA.get(i + numTeams, 0);
            myApp.teamStatRanked[division].get(i).oprA = toprA.get(i, 0);
            myApp.teamStatRanked[division].get(i).dprA = toprA.get(i + numTeams, 0);

        }

        // Normalize for average defense and offense
        double meanDpr = 0;
        for (int i = 0; i < numTeams; i++) {
            meanDpr += myApp.teamStatRanked[division].get(i).dprA;
        }
        meanDpr /= numTeams;

        for (int i = 0; i < numTeams; i++) {
            myApp.teamStatRanked[division].get(i).oprA += meanDpr + meanOffense;
            myApp.teamStatRanked[division].get(i).dprA -= meanDpr;
        }

        //       if (myApp.useAdvancedStats[myApp.division()]) {
        for (int i = 0; i < numTeams; i++) {
            myApp.teamStatRanked[division].get(i).ccwm = myApp.teamStatRanked[division].get(i).ccwmA;
            myApp.teamStatRanked[division].get(i).opr = myApp.teamStatRanked[division].get(i).oprA;
            myApp.teamStatRanked[division].get(i).dpr = myApp.teamStatRanked[division].get(i).dprA;
        }
        //       }



 /*       } else {
            // estimate OPR by average offensive score in matches played

            // estimate CCWM by average WM in matches played
        } */


    }

}

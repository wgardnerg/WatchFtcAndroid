package com.wrgardnersoft.watchftc.models;


import android.util.Log;

import java.util.ArrayList;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/**
 * Created by Bill on 2/8/2015.
 */
public class Stat {

    private static double OprComponent(Match m, int color, MyApp.ScoreType type) {
        double retVal = 0;

        if (type == MyApp.ScoreType.TOTAL) {
            retVal = m.score[color][type.ordinal()] - m.score[color][MyApp.ScoreType.PENALTY.ordinal()] - m.score[1-color][MyApp.ScoreType.PENALTY.ordinal()];
        } else if (type == MyApp.ScoreType.PENALTY){
            retVal = -m.score[1-color][type.ordinal()];
        } else {
            retVal = m.score[color][type.ordinal()];
        }
        return retVal;
    }

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
            if ((ms.get(i).score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                    (ms.get(i).title.substring(0, 1).matches("Q"))) {
                numMatches++;
            }
        }

        // New and improved OPR, DPR, CCWM
        //////////////////////////////////////////
        // Matrix based stats: opr, dpr, ccwm
        // For these stats, counting penalties as negative for the alliance incurring the penalty!
        // So one alliance isn't rewarded if their opponent happens to get a lot of penalties.

        Matrix AA = new Matrix(2 * numMatches, 2 * numTeams);
        Matrix AtAinvA = new Matrix(2 * numTeams, 2 * numTeams);
        double dprPreWeight = 0;
        Matrix toprA = new Matrix(2 * numTeams, 1);
        double meanOffense[] = new double[MyApp.ScoreType.values().length];

        if (numMatches > 0) {

            if (numMatches <= numTeams) {
                dprPreWeight = 0;
            } else {
                dprPreWeight = 0.5;
            }

            int iM = 0;
            for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
                Match m = ms.get(i);

                if ((m.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                        (m.title.substring(0, 1).matches("Q"))) {

                    AA.set(iM, atn.indexOf(m.teamNumber[MyApp.RED][0]), 1.0);
                    AA.set(iM, atn.indexOf(m.teamNumber[MyApp.RED][1]), 1.0);
                    AA.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.BLUE][0]), -2.0 * (dprPreWeight));
                    AA.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.BLUE][1]), -2.0 * (dprPreWeight));
                    iM++;
                    AA.set(iM, atn.indexOf(m.teamNumber[MyApp.BLUE][0]), 1.0);
                    AA.set(iM, atn.indexOf(m.teamNumber[MyApp.BLUE][1]), 1.0);
                    AA.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.RED][0]), -2.0 * (dprPreWeight));
                    AA.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.RED][1]), -2.0 * (dprPreWeight));
                    iM++;
                }
            }

            // using SVD to solve so get result even when underdetermined!
            SingularValueDecomposition svdA = new SingularValueDecomposition(AA.transpose().times(AA));
            Matrix sA = svdA.getS();
            Matrix vA = svdA.getV();
            Matrix sInvA = new Matrix(vA.getColumnDimension(), vA.getColumnDimension());
            for (int i = 0; i < svdA.rank(); i++) {
                sInvA.set(i, i, 1.0 / sA.get(i, i));
            }
            AtAinvA = vA.times(sInvA.times(vA.transpose()));

            ////////////////////////
            // OPR:
            //
            // Compute A topr = B
            //   where A = rows with two 1s representing which teams were in each alliance
            //       A has 2 rows per match.
            //   and B is the offensive score minus penalties for that alliance.
            // Then, least squares topr solution is, solve A' A topr = A' b
            //   A' A is positive semidef, so use Cholesky decomposition to solve it.


            for (MyApp.ScoreType type : MyApp.ScoreType.values()) {
                //       int type = 0; type < TeamStatRanked.StatType.values().length; type++) {

                Matrix BoprA = new Matrix(2 * numMatches, 1);

                iM = 0;
                for (int i = 0; i < myApp.match[myApp.division()].size(); i++) {
                    Match m = ms.get(i);

                    if ((m.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                            (m.title.substring(0, 1).matches("Q"))) {

                        BoprA.set(iM, 0, Stat.OprComponent(m, MyApp.RED, type));
                        meanOffense[type.ordinal()] += BoprA.get(iM, 0);
                        iM++;

                        BoprA.set(iM, 0, Stat.OprComponent(m, MyApp.BLUE, type));
                        meanOffense[type.ordinal()] += BoprA.get(iM, 0);
                        iM++;
                    }
                }
                meanOffense[type.ordinal()] /= 2 * numMatches * 2; // per team, 2 for red/blue, 2 for 2 teams per alliance
 Log.i("MO", String.valueOf(meanOffense[type.ordinal()]));
                for (int i = 0; i < 2 * numMatches; i++) {
                    BoprA.set(i, 0, BoprA.get(i, 0) - 2 * meanOffense[type.ordinal()]);
                }

                ////////////////////////
                // OPR:
                //
                // Compute A topr = B
                //   where A = rows with two 1s representing which teams were in each alliance
                //       A has 2 rows per match.
                //   and B is the offensive score minus penalties for that alliance.
                // Then, least squares topr solution is, solve A' A topr = A' b
                //   A' A is positive semidef, so use Cholesky decomposition to solve it.

                toprA = AtAinvA.times(AA.transpose().times(BoprA));

                for (int i = 0; i < numTeams; i++) {
                    if (dprPreWeight != 0) {
                        toprA.set(i + numTeams, 0, toprA.get(i + numTeams, 0) * (2.0 * (dprPreWeight)));
                    }
                }

                for (int i = 0; i < numTeams; i++) {
                    myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = toprA.get(i, 0);
                    Log.i("O", String.valueOf(toprA.get(i, 0)));
                    myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = toprA.get(i + numTeams, 0);
                    Log.i("D", String.valueOf(toprA.get(i+numTeams, 0)));
                    myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = toprA.get(i, 0) + toprA.get(i + numTeams, 0);
                    Log.i("C", String.valueOf(toprA.get(i, 0) + toprA.get(i + numTeams, 0)));
                }

                // Normalize for average defense and offense
                double meanDpr = 0;
                for (int i = 0; i < numTeams; i++) {
                    meanDpr += myApp.teamStatRanked[division].get(i).dprA[type.ordinal()];
                }
                meanDpr /= numTeams;

                for (int i = 0; i < numTeams; i++) {
                    myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] += meanDpr + meanOffense[type.ordinal()];
                    myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] -= meanDpr;
            //        if (type == MyApp.ScoreType.PENALTY) {
            //            Log.i("Pen OPR"+i, String.valueOf(myApp.teamStatRanked[division].get(i).oprA[type.ordinal()]));
             //       }
                }
            }


        }

    }
}

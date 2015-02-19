package com.wrgardnersoft.watchftc.models;


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
            retVal = m.score[color][type.ordinal()] - m.score[color][MyApp.ScoreType.PENALTY.ordinal()] - m.score[1 - color][MyApp.ScoreType.PENALTY.ordinal()];
        } else if (type == MyApp.ScoreType.PENALTY) {
            retVal = -m.score[1 - color][type.ordinal()];
        } else {
            retVal = m.score[color][type.ordinal()];
        }
        return retVal;
    }

    public static void computeAll(int division) {
        MyApp myApp = MyApp.getInstance();

        myApp.teamStatRanked[division].clear();

        if (myApp.team[division].size() > 0) {
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

            ArrayList<TeamStatRanked> ts = myApp.teamStatRanked[division];
            ArrayList<Match> ms = myApp.match[division];
            ArrayList<Integer> atn = new ArrayList<>();

            int numTeams = ts.size();
            for (int i = 0; i < numTeams; i++) {
                atn.add(ts.get(i).number);
            }

            // only count SCORED QUALIFYING matches
            int numMatches = 0;
            for (int i = 0; i < myApp.match[division].size(); i++) {
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

            Matrix A2 = new Matrix(2 * numMatches, 2 * numTeams);
            Matrix AtAinvA2 = new Matrix(2 * numTeams, 2 * numTeams);
            Matrix toprA2 = new Matrix(2 * numTeams, 1);

            Matrix A1 = new Matrix(2 * numMatches, numTeams);
            Matrix AtAinvA1 = new Matrix(numTeams, numTeams);
            Matrix toprA1 = new Matrix(numTeams, 1);

            Matrix A1d = new Matrix(2 * numMatches, numTeams);
            Matrix AtAinvA1d = new Matrix(numTeams, numTeams);
            Matrix tdprA1 = new Matrix(numTeams, 1);

            //        int meanOffenseMatchCount[] = new int[numTeams];
            for (int i = 0; i < MyApp.NUM_SCORE_TYPES; i++) {
                myApp.meanOffenseScoreTotal[division][i] = 0;
            }

            double dprPreWeight = 0;

            if (numMatches > 0) {

                int matchesPerTeam[] = new int[numTeams];

                int iM = 0;
                for (int i = 0; i < myApp.match[division].size(); i++) {
                    Match m = ms.get(i);

                    if ((m.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                            (m.title.substring(0, 1).matches("Q"))) {

                        A2.set(iM, atn.indexOf(m.teamNumber[MyApp.RED][0]), 1.0);
                        A2.set(iM, atn.indexOf(m.teamNumber[MyApp.RED][1]), 1.0);
                        A2.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.BLUE][0]), -1);
                        A2.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.BLUE][1]), -1);

                        iM++;
                        A2.set(iM, atn.indexOf(m.teamNumber[MyApp.BLUE][0]), 1.0);
                        A2.set(iM, atn.indexOf(m.teamNumber[MyApp.BLUE][1]), 1.0);
                        A2.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.RED][0]), -1);
                        A2.set(iM, numTeams + atn.indexOf(m.teamNumber[MyApp.RED][1]), -1);

                        iM++;

                        matchesPerTeam[atn.indexOf(m.teamNumber[MyApp.RED][0])]++;
                        matchesPerTeam[atn.indexOf(m.teamNumber[MyApp.RED][1])]++;
                        matchesPerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][0])]++;
                        matchesPerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][1])]++;

                    }
                }


                // using SVD to solve so get result even when under-determined!
                SingularValueDecomposition svdA2 = new SingularValueDecomposition(A2.transpose().times(A2));
                Matrix sA2 = svdA2.getS();
                Matrix vA2 = svdA2.getV();
           //     Log.i("SVD2 RankA2", String.valueOf(A2.rank()));
            //    Log.i("SVD2 Rank", String.valueOf(svdA2.rank()));
                Matrix sInvA2 = new Matrix(vA2.getColumnDimension(), vA2.getColumnDimension());
                for (int i = 0; i < svdA2.rank(); i++) {
                    sInvA2.set(i, i, 1.0 / sA2.get(i, i));
                }
                AtAinvA2 = vA2.times(sInvA2.times(vA2.transpose()));
            //    Log.i("SVD2 Rank", String.valueOf(AtAinvA2.rank()));


                A1 = A2.getMatrix(0, 2 * numMatches - 1, 0, numTeams - 1); // only offense, left of AA2

                SingularValueDecomposition svdA1 = new SingularValueDecomposition(A1.transpose().times(A1));
                Matrix sA1 = svdA1.getS();
                Matrix vA1 = svdA1.getV();
           //     Log.i("SVD1 RankA1", String.valueOf(A1.rank()));
           //     Log.i("SVD1 Rank", String.valueOf(svdA1.rank()));
                Matrix sInvA1 = new Matrix(vA1.getColumnDimension(), vA1.getColumnDimension());
                for (int i = 0; i < svdA1.rank(); i++) {
                    sInvA1.set(i, i, 1.0 / sA1.get(i, i));
                }
                AtAinvA1 = vA1.times(sInvA1.times(vA1.transpose()));


                A1d = A2.getMatrix(0, 2 * numMatches - 1, numTeams, 2 * numTeams - 1); // only defense, right

                SingularValueDecomposition svdA1d = new SingularValueDecomposition(A1d.transpose().times(A1d));
                Matrix sA1d = svdA1d.getS();
                Matrix vA1d = svdA1d.getV();
          //      Log.i("SVD1d Rank", String.valueOf(svdA1d.rank()));
                Matrix sInvA1d = new Matrix(vA1d.getColumnDimension(), vA1d.getColumnDimension());
                for (int i = 0; i < svdA1d.rank(); i++) {
                    sInvA1d.set(i, i, 1.0 / sA1d.get(i, i));
                }
                AtAinvA1d = vA1d.times(sInvA1d.times(vA1d.transpose()));

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

                    double offensePerTeam[] = new double[numTeams];
                    double defensePerTeam[] = new double[numTeams];

                    Matrix BoprA = new Matrix(2 * numMatches, 1);

                    iM = 0;
                    for (int i = 0; i < myApp.match[division].size(); i++) {
                        Match m = ms.get(i);

                        if ((m.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                                (m.title.substring(0, 1).matches("Q"))) {

                            BoprA.set(iM, 0, Stat.OprComponent(m, MyApp.RED, type));

                            offensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][0])] += BoprA.get(iM, 0);
                            offensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][1])] += BoprA.get(iM, 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][0])] += BoprA.get(iM, 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][1])] += BoprA.get(iM, 0);

                            myApp.meanOffenseScoreTotal[division][type.ordinal()] += BoprA.get(iM, 0);
                            iM++;

                            BoprA.set(iM, 0, Stat.OprComponent(m, MyApp.BLUE, type));

                            offensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][0])] += BoprA.get(iM, 0);
                            offensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][1])] += BoprA.get(iM, 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][0])] += BoprA.get(iM, 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][1])] += BoprA.get(iM, 0);

                            myApp.meanOffenseScoreTotal[division][type.ordinal()] += BoprA.get(iM, 0);
                            iM++;
                        }
                    }
                //    Log.i("SumMean " + String.valueOf(type.ordinal()), String.valueOf((myApp.meanOffenseScoreTotal[division][type.ordinal()])));

                    myApp.meanOffenseScoreTotal[division][type.ordinal()] /= 2 * numMatches * 2; // per team, 2 for red/blue, 2 for 2 teams per alliance
               //    Log.i("Mean " + String.valueOf(type.ordinal()), String.valueOf((myApp.meanOffenseScoreTotal[division][type.ordinal()])));
                    //Log.i("MO", String.valueOf(meanOffense[type.ordinal()]));
                    for (int i = 0; i < 2 * numMatches; i++) {
                        BoprA.set(i, 0, BoprA.get(i, 0) - 2 * myApp.meanOffenseScoreTotal[division][type.ordinal()]);
                        //       Log.i("BoprA", String.valueOf(BoprA.get(i,0)));
                    }
//Log.i("MeanOff", String.valueOf(myApp.meanOffenseScoreTotal[division][type.ordinal()]));
                    for (int i = 0; i < numTeams; i++) {
                        defensePerTeam[i] /= 2;
                        //       Log.i("DpT", String.valueOf(defensePerTeam[i]));
                        defensePerTeam[i] -= myApp.meanOffenseScoreTotal[division][type.ordinal()] * matchesPerTeam[i];
                        //       Log.i("DpT", String.valueOf(defensePerTeam[i]));
                    }

                    ////////////////////////
                    // OPR:
                    //
                    // Compute A topr = B
                    //   where A = rows with two 1s representing which teams were in each alliance
                    //       A has 2 rows per match.
                    //   and B is the offensive score minus penalties for that alliance.
                    // Then, least squares topr solution is, solve A' A topr = A' b

                    toprA2 = AtAinvA2.times(A2.transpose().times(BoprA));


                    //     Log.i(String.valueOf(AtAinvA1.getRowDimension()),String.valueOf(AtAinvA1.getColumnDimension()));
                    //     Log.i(String.valueOf(A1.transpose().getRowDimension()),String.valueOf(A1.transpose().getColumnDimension()));
                    //     Log.i(String.valueOf(BoprA.getRowDimension()),String.valueOf(BoprA.getColumnDimension()));

                    toprA1 = AtAinvA1.times(A1.transpose().times(BoprA));
                    tdprA1 = AtAinvA1d.times(A1d.transpose().times(BoprA));


                    iM = 0;
                    for (int i = 0; i < myApp.match[division].size(); i++) {
                        Match m = ms.get(i);

                        if ((m.score[MyApp.RED][MyApp.ScoreType.TOTAL.ordinal()] >= 0) &&
                                (m.title.substring(0, 1).matches("Q"))) {

                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][0])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.RED][0]), 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][0])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.RED][1]), 0);

                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][1])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.RED][0]), 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.BLUE][1])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.RED][1]), 0);

                            iM++;

                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][0])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.BLUE][0]), 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][0])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.BLUE][1]), 0);

                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][1])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.BLUE][0]), 0);
                            defensePerTeam[atn.indexOf(m.teamNumber[MyApp.RED][1])] -= toprA1.get(atn.indexOf(m.teamNumber[MyApp.BLUE][1]), 0);

                            iM++;
                        }
                    }
                    for (int i = 0; i < numTeams; i++) {
                        //        Log.i("DpT", String.valueOf(defensePerTeam[i] / matchesPerTeam[i]));
                    }

 /*                   if (numMatches>numTeams) { //if (svdA2.rank() !=2*numTeams) {
                        for (int i = 0; i < numTeams; i++) {
                            myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = toprA2.get(i, 0);
                            //    Log.i("O", String.valueOf(toprA.get(i, 0)));
                            myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = toprA2.get(i+numTeams, 0);
                            //    Log.i("D", String.valueOf(toprA.get(i+numTeams, 0)));
                            myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = toprA2.get(i, 0) + toprA2.get(i + numTeams, 0);
                            //    Log.i("C", String.valueOf(toprA.get(i, 0) + toprA.get(i + numTeams, 0)));
                        }
                    } else */
                    if (svdA1.rank() == numTeams) {
                        for (int i = 0; i < numTeams; i++) {
                            myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = toprA1.get(i, 0);
                            if (numMatches*4/numTeams >=5) { // if all teams have played at least 5 matches
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = -0.25*(defensePerTeam[i] / matchesPerTeam[i]);
                            } else {
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = 0;
                            }
                            myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] +
                                    myApp.teamStatRanked[division].get(i).dprA[type.ordinal()];


                            /* //////////// GOOD VERSION HERE /////////////////////
                            myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = toprA1.get(i, 0);
                            //    Log.i("O", String.valueOf(toprA.get(i, 0)));
                            if (matchesPerTeam[i]>0) {
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = 0;//-(defensePerTeam[i] / matchesPerTeam[i]);
                            } else {
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] =0;
                            }
                            //    Log.i("D", String.valueOf(toprA.get(i+numTeams, 0)));
                            myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = toprA1.get(i, 0); //0.5*(toprA1.get(i, 0) + tdprA1.get(i, 0));
                            //    Log.i("C", String.valueOf(toprA.get(i, 0) + toprA.get(i + numTeams, 0)));
                            */
                        }
                    } else { // just use averages
                        for (int i = 0; i < numTeams; i++) {
                            if (matchesPerTeam[i] > 0) {
                                myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = offensePerTeam[i] / (double) (matchesPerTeam[i]) / 2 - myApp.meanOffenseScoreTotal[division][type.ordinal()];
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = 0; //-(defensePerTeam[i] / (double) (matchesPerTeam[i]) - myApp.meanOffenseScoreTotal[division][type.ordinal()]);
                                myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = offensePerTeam[i] / (double) (matchesPerTeam[i]) / 2 - myApp.meanOffenseScoreTotal[division][type.ordinal()];
                            } else {
                                myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] = 0;
                                //    Log.i("O", String.valueOf(toprA.get(i, 0)));
                                myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] = 0;
                                //    Log.i("D", String.valueOf(toprA.get(i+numTeams, 0)));
                                myApp.teamStatRanked[division].get(i).ccwmA[type.ordinal()] = 0;
                                //    Log.i("C", String.valueOf(toprA.get(i, 0) + toprA.get(i + numTeams, 0)));
                            }
                        }
                    }

                    // Normalize for average defense and offense
                    double meanDpr = 0;
                    for (int i = 0; i < numTeams; i++) {
                        meanDpr += myApp.teamStatRanked[division].get(i).dprA[type.ordinal()];
                    }
                    meanDpr /= numTeams;

                    // correct for bias due to not all teams having played the same number of matches!


                    for (int i = 0; i < numTeams; i++) {
                        myApp.teamStatRanked[division].get(i).oprA[type.ordinal()] += meanDpr; // + myApp.Log[division][type.ordinal()];
                        myApp.teamStatRanked[division].get(i).dprA[type.ordinal()] -= meanDpr;
                        //        if (type == MyApp.ScoreType.PENALTY) {
                        //            Log.i("Pen OPR"+i, String.valueOf(myApp.teamStatRanked[division].get(i).oprA[type.ordinal()]));
                        //       }
                    }
                }

            }
        }

    }
}

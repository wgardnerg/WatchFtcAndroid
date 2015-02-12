package com.wrgardnersoft.watchftc.models;

import java.util.Comparator;

/**
 * Created by Bill on 2/3/2015.
 */
public class TeamStatRanked {

    public int number;
    public int ftcRank;
    public double winPercent;
    public double oprA[];
    public double dprA[];
    public double ccwmA[];


    public TeamStatRanked() {
        this.number = 0;
        this.ftcRank = 0;
        this.winPercent = 0;
        this.oprA = new double[MyApp.ScoreType.values().length];
        this.dprA = new double[MyApp.ScoreType.values().length];
        this.ccwmA = new double[MyApp.ScoreType.values().length];
    }

    public TeamStatRanked(int num) {
        this.number = num;
        this.ftcRank = 0;
        this.winPercent = 0;
        this.oprA = new double[MyApp.ScoreType.values().length];
        this.dprA = new double[MyApp.ScoreType.values().length];
        this.ccwmA = new double[MyApp.ScoreType.values().length];
    }

    public String toString() {
        String output;
        output = this.number + "," + this.ftcRank + "," + this.winPercent;
        output = output + "," + this.ccwmA[MyApp.ScoreType.TOTAL.ordinal()] + "," + this.oprA[MyApp.ScoreType.TOTAL.ordinal()]
                + "," + this.dprA[MyApp.ScoreType.TOTAL.ordinal()];
        output = output + System.getProperty("line.separator");
        return output;
    }

    public static Comparator<TeamStatRanked> getComparator(SortParameter... sortParameters) {
        return new TeamStatRankedComparator(sortParameters);
    }

    public enum SortParameter {
        NUMBER_SORT, FTCRANK_SORT, WINPERCENT_SORT, OPR_SORT, DPR_SORT, CCWM_SORT
    }

    private static class TeamStatRankedComparator implements Comparator<TeamStatRanked> {
        private SortParameter[] parameters;

        private TeamStatRankedComparator(SortParameter[] parameters) {
            this.parameters = parameters;
        }

        public int compare(TeamStatRanked o1, TeamStatRanked o2) {
            int comparison;
            for (SortParameter parameter: parameters) {
                switch (parameter) {
                    case NUMBER_SORT:
                        comparison = o1.number - o2.number;
                        if (comparison!=0) return comparison;
                        break;
                    case FTCRANK_SORT:
                        comparison = o1.ftcRank - o2.ftcRank;
                        if (comparison!=0) return comparison;
                        break;
                    case WINPERCENT_SORT:
                        comparison = (int)(-100000 * (o1.winPercent - o2.winPercent));
                        if (comparison!=0) return comparison;
                        break;
                    case OPR_SORT:
                        comparison = (int)(-100000 * (o1.oprA[MyApp.ScoreType.TOTAL.ordinal()] - o2.oprA[MyApp.ScoreType.TOTAL.ordinal()]));
                        if (comparison!=0) return comparison;
                        break;
                    case DPR_SORT:
                        comparison = (int)(-100000 * (o1.dprA[MyApp.ScoreType.TOTAL.ordinal()] - o2.dprA[MyApp.ScoreType.TOTAL.ordinal()]));
                        if (comparison!=0) return comparison;
                        break;
                    case CCWM_SORT:
                        comparison = (int)(-100000 * (o1.ccwmA[MyApp.ScoreType.TOTAL.ordinal()] - o2.ccwmA[MyApp.ScoreType.TOTAL.ordinal()]));
                        if (comparison!=0) return comparison;
                        break;
                }
            }
            return 0;
        }
    }

}


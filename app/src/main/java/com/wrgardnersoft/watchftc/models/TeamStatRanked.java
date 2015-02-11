package com.wrgardnersoft.watchftc.models;

import java.util.Comparator;

/**
 * Created by Bill on 2/3/2015.
 */
public class TeamStatRanked {
    public int number;
    public int ftcRank;
    public double winPercent;
    public double opr, oprA;
    public double dpr, dprA;
    public double ccwm, ccwmA;

    public TeamStatRanked() {
        this.number = 0;
        this.ftcRank = 0;
        this.winPercent = 0;
        this.opr = 0; this.oprA=0;
        this.dpr = 0; this.dprA=0;
        this.ccwm = 0; this.ccwmA=0;
    }

    public TeamStatRanked(int num) {
        this.number = num;
        this.ftcRank = 0;
        this.winPercent = 0;
        this.opr = 0; this.oprA=0;
        this.dpr = 0; this.dprA=0;
        this.ccwm = 0; this.ccwmA=0;
    }

    public String toString() {
        String output;
        output = this.number + "," + this.ftcRank + "," + this.winPercent;
        output = output + "," + this.ccwm + this.opr + "," + this.dpr;
        output = output + "," + this.ccwmA + this.oprA + "," + this.dprA;
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
                        comparison = (int)(-100000 * (o1.opr - o2.opr));
                        if (comparison!=0) return comparison;
                        break;
                    case DPR_SORT:
                        comparison = (int)(-100000 * (o1.dpr - o2.dpr));
                        if (comparison!=0) return comparison;
                        break;
                    case CCWM_SORT:
                        comparison = (int)(-100000 * (o1.ccwm - o2.ccwm));
                        if (comparison!=0) return comparison;
                        break;
                }
            }
            return 0;
        }
    }

}


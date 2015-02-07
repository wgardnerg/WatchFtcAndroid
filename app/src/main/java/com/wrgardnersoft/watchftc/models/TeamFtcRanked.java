package com.wrgardnersoft.watchftc.models;

/**
 * Created by Bill on 2/3/2015.
 */
public class TeamFtcRanked {
    public int rank, number;
    public String name;
    public int qp, rp, highest, matches;

    public TeamFtcRanked() {
        this.rank=0;
        this.number=0;
        this.name="";
        this.qp=0;
        this.rp=0;
        this.highest=0;
        this.matches=0;
    }
    public TeamFtcRanked(int rank, int num, String sName, int qp, int rp, int highest, int matches) {
        this.rank=rank;
        this.number=num;
        this.name=sName;
        this.qp=qp;
        this.rp=rp;
        this.highest=highest;
        this.matches=matches;
    }
}


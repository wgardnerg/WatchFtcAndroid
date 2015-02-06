package com.wrgardnersoft.watchftc;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by Bill on 2/2/2015.
 */
public class MyApp extends Application {
    static MyApp myAppInstance;
 /*   public MyApp() {
        myAppInstance = this;
    }*/


//public class MyApp extends Application {
    private String[] serverAddressString= new String[2];
    private boolean dualDivision;
    private int division;
    public ArrayList<Integer> selectedTeams;

    public ArrayList<Team> team;  // full team info from team server page

    public ArrayList<TeamFtcRanked> teamFtcRanked;

    public ArrayList<Match> match;

    public static MyApp getInstance() {
        return myAppInstance;
    }

    public MyApp() {

        myAppInstance = this;

        this.division = 0;
        this.dualDivision=true;
        this.serverAddressString[0]="192.168.11.131";
        this.serverAddressString[1]="192.168.11.135";
        this.selectedTeams = new ArrayList<Integer>();

        this.team = new ArrayList<Team>();
        this.teamFtcRanked = new ArrayList<TeamFtcRanked>();
        this.match = new ArrayList<Match>();
    }

    public int division() {
        return this.division;
    }

    public void setDivision(int div) {
        this.division = div;
    }

    public String serverAddressString(int i) {
        return serverAddressString[i];
    }
    public void setServerAddressString(int i, String addressString) {
        this.serverAddressString[i] = addressString;
    }

    public boolean dualDivision() {
        return this.dualDivision;
    }
    public void setDualDivision(boolean dualDiv) {
        this.dualDivision = dualDiv;
    }
}

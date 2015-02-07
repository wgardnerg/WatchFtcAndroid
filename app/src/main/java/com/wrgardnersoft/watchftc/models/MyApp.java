package com.wrgardnersoft.watchftc.models;

import android.app.Application;

import java.util.ArrayList;

/**
 *   Created by Bill on 2/2/2015.
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

    public ArrayList<com.wrgardnersoft.watchftc.models.Team>[] team = (ArrayList<Team>[])new ArrayList[2];  // full team info from team server page

    public ArrayList<TeamFtcRanked>[] teamFtcRanked = (ArrayList<TeamFtcRanked>[])new ArrayList[2];

    public ArrayList<Match>[] match = (ArrayList<Match>[])new ArrayList[2];

    public int currentTeamNumber;

    public static MyApp getInstance() {
        return myAppInstance;
    }

    public MyApp() {

        myAppInstance = this;

        this.division = 0;
        this.dualDivision=true;
        this.serverAddressString[0]="192.168.11.131";
        this.serverAddressString[1]="192.168.11.135";
        this.selectedTeams = new ArrayList<>();

        this.team[0] = new ArrayList<>();
        this.team[1] = new ArrayList<>();

        this.teamFtcRanked[0] = new ArrayList<>();
        this.teamFtcRanked[1] = new ArrayList<>();

        this.match[0] = new ArrayList<>();
        this.match[1] = new ArrayList<>();

        this.currentTeamNumber=-1;
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

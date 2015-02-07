package com.wrgardnersoft.watchftc.models;

/**
 * Created by Bill on 2/3/2015.
 */
public class Team {
    public int number;
    public String name, school, city, state, country;

    public Team() {
        this.number=0;
        this.name="";
        this.school="";
        this.city="";
        this.state="";
        this.country="";
    }
    public Team(int num, String sName, String sSchool, String sCity, String sState, String sCountry) {
        this.number=num;
        this.name=sName;
        this.school=sSchool;
        this.city=sCity;
        this.state=sState;
        this.country=sCountry;
    }
}


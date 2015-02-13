package com.wrgardnersoft.watchftc.models;

import android.util.Log;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.List;

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

    public static String shareHeader() {
        String output;
        output = "Number,Name,School,City,State,Country"+ System.getProperty("line.separator");
        return output;
    }
    public String toString() {
        String output;
        output = this.number + "," + this.name + "," + this.school + "," + this.city + "," + this.state + "," + this.country + System.getProperty("line.separator");
        return output;
    }
    public static Team readFromBR(BufferedReader fr) {
        Team t = new Team();
        try {
            String input = fr.readLine();
            List<String> param = Arrays.asList(input.split(","));
            t.number = Integer.valueOf(param.get(0));
            t.name = param.get(1);
            t.school = param.get(2);
            t.city = param.get(3);
            t.state = param.get(4);
            t.country = param.get(5);
        } catch (Exception e) {
            Log.i("Team.readFromBR", "Error reading");
        }
        return t;
    }
}


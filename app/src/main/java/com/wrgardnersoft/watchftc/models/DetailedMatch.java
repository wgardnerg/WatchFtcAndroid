package com.wrgardnersoft.watchftc.models;

/**
 * Created by Bill on 2/11/2015.
 */
public class DetailedMatch {


    public double[][][] score;

    public DetailedMatch() {
       this.score = new double[2][3][MyApp.ScoreType.values().length];
    }

}

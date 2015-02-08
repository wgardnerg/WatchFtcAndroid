package com.wrgardnersoft.watchftc.models;

/**
 * Created by Bill on 2/3/2015.
 */
public class Match {
    public int number;
    public int rTeam0, rTeam1, rTeam2;
    public int bTeam0, bTeam1, bTeam2;
    public String title, resultStr;
    public int rTot, rAuto, rAutoB, rTele, rEndG, rPen;
    public int bTot, bAuto, bAutoB, bTele, bEndG, bPen;

    public Match(int num, String sName, String sResult,
                 int rT0,
                 int rT1,
                 int bT0,
                 int bT1,
                 int rTot,
                 int rAuto,
                 int rAutoB,
                 int rTele,
                 int rEndG,
                 int rPen,
                 int bTot,
                 int bAuto,
                 int bAutoB,
                 int bTele,
                 int bEndG,
                 int bPen) {

        this.number = num;
        this.title = sName;
        this.resultStr = sResult;
        this.rTeam0 = rT0;
        this.rTeam1 = rT1;
        this.rTeam2 = -1;
        this.bTeam0 = bT0;
        this.bTeam1 = bT1;
        this.bTeam2=-1;
        this.rTot = rTot;
        this.rAuto = rAuto;
        this.rAutoB = rAutoB;
        this.rTele = rTele;
        this.rEndG = rEndG;
        this.rPen = rPen;
        this.bTot = bTot;
        this.bAuto = bAuto;
        this.bAutoB = bAutoB;
        this.bTele = bTele;
        this.bEndG = bEndG;
        this.bPen = bPen;
    }


    public Match(int num, String sName, String sResult,
                 String rT0,
                 String rT1,
                 String bT0,
                 String bT1,
                 String rTot,
                 String rAuto,
                 String rAutoB,
                 String rTele,
                 String rEndG,
                 String rPen,
                 String bTot,
                 String bAuto,
                 String bAutoB,
                 String bTele,
                 String bEndG,
                 String bPen) {

        this.number = num;
        this.title = sName;

        try {
            this.rTeam0 = Integer.parseInt(rT0);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.rTeam0 = Integer.parseInt(rT0.substring(0,rT0.length()-1));
        }
        try {
            this.rTeam1 = Integer.parseInt(rT1);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.rTeam1 = Integer.parseInt(rT1.substring(0,rT1.length()-1));
        }
        this.rTeam2=0;
        try {
            this.bTeam0 = Integer.parseInt(bT0);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.bTeam0 = Integer.parseInt(bT0.substring(0,bT0.length()-1));
        }
        try {
            this.bTeam1 = Integer.parseInt(bT1);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.bTeam1 = Integer.parseInt(bT1.substring(0,bT1.length()-1));
        }
        this.bTeam2=0;

        if ((sResult == null) || (sResult.startsWith(" "))) {
            this.resultStr = "notEnteredYet";
        } else {
            this.resultStr = sResult;
            try {
                if (rTot != null)
                    this.rTot = Integer.parseInt(rTot);
            } catch (NumberFormatException e) {
                this.rTot = -1;
            }
            try {
                if (rAuto != null)
                    this.rAuto = Integer.parseInt(rAuto);
            } catch (NumberFormatException e) {
                this.rAuto = -1;
            }
            try {
                if (rAutoB != null)
                    this.rAutoB = Integer.parseInt(rAutoB);
            } catch (NumberFormatException e) {
                this.rAutoB = -1;
            }
            try {
                if (rTele != null)
                    this.rTele = Integer.parseInt(rTele);
            } catch (NumberFormatException e) {
                this.rTele = -1;
            }
            try {
                if (rEndG != null)
                    this.rEndG = Integer.parseInt(rEndG);
            } catch (NumberFormatException e) {
                this.rEndG = -1;
            }
            try {
                if (rPen != null)
                    this.rPen = Integer.parseInt(rPen);
            } catch (NumberFormatException e) {
                this.rPen = -1;
            }
            try {
                if (bTot != null)
                    this.bTot = Integer.parseInt(bTot);
            } catch (NumberFormatException e) {
                this.bTot = -1;
            }
            try {
                if (bAuto != null)
                    this.bAuto = Integer.parseInt(bAuto);
            } catch (NumberFormatException e) {
                this.bAuto = -1;
            }
            try {
                if (bAutoB != null)
                    this.bAutoB = Integer.parseInt(bAutoB);
            } catch (NumberFormatException e) {
                this.bAutoB = -1;
            }
            try {
                if (bTele != null)
                    this.bTele = Integer.parseInt(bTele);
            } catch (NumberFormatException e) {
                this.bTele = -1;
            }
            try {
                if (bEndG != null)
                    this.bEndG = Integer.parseInt(bEndG);
            } catch (NumberFormatException e) {
                this.bEndG = -1;
            }
            try {
                if (bPen != null)
                    this.bPen = Integer.parseInt(bPen);
            } catch (NumberFormatException e) {
                this.bPen = -1;
            }
        }
    }


    public Match(int num, String sName, String sResult,
                 String rT0,
                 String rT1,
                 String rT2,
                 String bT0,
                 String bT1,
                 String bT2,
                 String rTot,
                 String rAuto,
                 String rAutoB,
                 String rTele,
                 String rEndG,
                 String rPen,
                 String bTot,
                 String bAuto,
                 String bAutoB,
                 String bTele,
                 String bEndG,
                 String bPen) {
        this.number = num;
        this.title = sName;

        try {
            this.rTeam0 = Integer.parseInt(rT0);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.rTeam0 = Integer.parseInt(rT0.substring(0,rT0.length()-1));
        }
        try {
            this.rTeam1 = Integer.parseInt(rT1);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.rTeam1 = Integer.parseInt(rT1.substring(0,rT1.length()-1));
        }
        this.rTeam2 = Integer.parseInt(rT2); // can't be surrogate

        try {
            this.bTeam0 = Integer.parseInt(bT0);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.bTeam0 = Integer.parseInt(bT0.substring(0,bT0.length()-1));
        }
        try {
            this.bTeam1 = Integer.parseInt(bT1);
        } catch (NumberFormatException e) { // catch surrogate match *
            this.bTeam1 = Integer.parseInt(bT1.substring(0,bT1.length()-1));
        }
        this.bTeam2 = Integer.parseInt(bT2); // can't be surrogate

        if ((sResult == null) || (sResult.startsWith(" "))) {
            this.resultStr = "notEnteredYet";
        } else {
            this.resultStr = sResult;
            try {
                if (rTot != null)
                    this.rTot = Integer.parseInt(rTot);
            } catch (NumberFormatException e) {
                this.rTot = -1;
            }
            try {
                if (rAuto != null)
                    this.rAuto = Integer.parseInt(rAuto);
            } catch (NumberFormatException e) {
                this.rAuto = -1;
            }
            try {
                if (rAutoB != null)
                    this.rAutoB = Integer.parseInt(rAutoB);
            } catch (NumberFormatException e) {
                this.rAutoB = -1;
            }
            try {
                if (rTele != null)
                    this.rTele = Integer.parseInt(rTele);
            } catch (NumberFormatException e) {
                this.rTele = -1;
            }
            try {
                if (rEndG != null)
                    this.rEndG = Integer.parseInt(rEndG);
            } catch (NumberFormatException e) {
                this.rEndG = -1;
            }
            try {
                if (rPen != null)
                    this.rPen = Integer.parseInt(rPen);
            } catch (NumberFormatException e) {
                this.rPen = -1;
            }
            try {
                if (bTot != null)
                    this.bTot = Integer.parseInt(bTot);
            } catch (NumberFormatException e) {
                this.bTot = -1;
            }
            try {
                if (bAuto != null)
                    this.bAuto = Integer.parseInt(bAuto);
            } catch (NumberFormatException e) {
                this.bAuto = -1;
            }
            try {
                if (bAutoB != null)
                    this.bAutoB = Integer.parseInt(bAutoB);
            } catch (NumberFormatException e) {
                this.bAutoB = -1;
            }
            try {
                if (bTele != null)
                    this.bTele = Integer.parseInt(bTele);
            } catch (NumberFormatException e) {
                this.bTele = -1;
            }
            try {
                if (bEndG != null)
                    this.bEndG = Integer.parseInt(bEndG);
            } catch (NumberFormatException e) {
                this.bEndG = -1;
            }
            try {
                if (bPen != null)
                    this.bPen = Integer.parseInt(bPen);
            } catch (NumberFormatException e) {
                this.bPen = -1;
            }
        }


    }


}

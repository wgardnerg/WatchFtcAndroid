package com.wrgardnersoft.watchftc.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Stat;
import com.wrgardnersoft.watchftc.models.TeamStatRanked;

import java.util.ArrayList;

/**
 *   Created by Bill on 2/3/2015.
 */
public class StatDetailsListAdapter extends ArrayAdapter<TeamStatRanked> {
    Context context;
    int layoutResourceId;
    ArrayList<TeamStatRanked> team;

    public StatDetailsListAdapter(Context context, int resource, ArrayList<TeamStatRanked> teamData) {
        super(context, resource, teamData);
        this.layoutResourceId = resource;
        this.context = context;
        this.team = teamData;
    }

    /**
     * ***** What is the size of Passed Arraylist Size ***********
     */
    public int getCount() {

        if (team.size() <= 0)
            return 1;
        return team.size();
    }

    public TeamStatRanked getItem(int position) {
        return team.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TeamHolder holder;


        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TeamHolder();
            holder.numView = (TextView) row.findViewById(R.id.number);
            holder.autoView = (TextView) row.findViewById(R.id.auto);
            holder.teleView = (TextView) row.findViewById(R.id.tele);
            holder.endgView = (TextView) row.findViewById(R.id.endg);
            holder.penView = (TextView) row.findViewById(R.id.pen);
            holder.totView = (TextView) row.findViewById(R.id.tot);
            holder.nameView = (TextView) row.findViewById(R.id.name);

            row.setTag(holder);
        } else {
            holder = (TeamHolder) row.getTag();
        }

        TeamStatRanked thisTeam = team.get(position);

        MyApp myApp = MyApp.getInstance();

        if (myApp.selectedTeams.contains(thisTeam.number)) {
            row.setBackgroundColor(Color.YELLOW);
        } else {
            row.setBackgroundColor(Color.WHITE);
        }
   //     Log.i("Ftc adapter", "got here");
        holder.numView.setText(String.format("%5d",thisTeam.number));
        if (myApp.detailType == Stat.Type.OPR) {
            holder.autoView.setText(String.format("%3d", Math.round(thisTeam.oprA[MyApp.ScoreType.AUTONOMOUS.ordinal()])));
            holder.teleView.setText(String.format("%3d", Math.round(thisTeam.oprA[MyApp.ScoreType.TELEOP.ordinal()])));
            holder.endgView.setText(String.format("%3d", Math.round(thisTeam.oprA[MyApp.ScoreType.END_GAME.ordinal()])));
            holder.penView.setText(String.format("%3d", Math.round(thisTeam.oprA[MyApp.ScoreType.PENALTY.ordinal()])));
            holder.totView.setText(String.format("%3d", Math.round(thisTeam.oprA[MyApp.ScoreType.TOTAL.ordinal()])));
        }
        else if (myApp.detailType == Stat.Type.DPR) {
            holder.autoView.setText(String.format("%3d", Math.round(thisTeam.dprA[MyApp.ScoreType.AUTONOMOUS.ordinal()])));
            holder.teleView.setText(String.format("%3d", Math.round(thisTeam.dprA[MyApp.ScoreType.TELEOP.ordinal()])));
            holder.endgView.setText(String.format("%3d", Math.round(thisTeam.dprA[MyApp.ScoreType.END_GAME.ordinal()])));
            holder.penView.setText(String.format("%3d", Math.round(thisTeam.dprA[MyApp.ScoreType.PENALTY.ordinal()])));
            holder.totView.setText(String.format("%3d", Math.round(thisTeam.dprA[MyApp.ScoreType.TOTAL.ordinal()])));
        }
        else  {
            holder.autoView.setText(String.format("%3d", Math.round(thisTeam.ccwmA[MyApp.ScoreType.AUTONOMOUS.ordinal()])));
            holder.teleView.setText(String.format("%3d", Math.round(thisTeam.ccwmA[MyApp.ScoreType.TELEOP.ordinal()])));
            holder.endgView.setText(String.format("%3d", Math.round(thisTeam.ccwmA[MyApp.ScoreType.END_GAME.ordinal()])));
            holder.penView.setText(String.format("%3d", Math.round(thisTeam.ccwmA[MyApp.ScoreType.PENALTY.ordinal()])));
            holder.totView.setText(String.format("%3d", Math.round(thisTeam.ccwmA[MyApp.ScoreType.TOTAL.ordinal()])));
        }
        holder.nameView.setText(thisTeam.name);

        return row;

    }

    static class TeamHolder {
        TextView numView, autoView, teleView, endgView, penView, totView, nameView;
    }
}


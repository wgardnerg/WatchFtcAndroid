package com.wrgardnersoft.watchftc.internet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.wrgardnersoft.watchftc.R;
import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Stat;
import com.wrgardnersoft.watchftc.models.Team;
import com.wrgardnersoft.watchftc.models.TeamFtcRanked;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Bill on 2/7/2015.
 */
// AsyncTask
public class ClientTask extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    public AsyncResponse delegate;
    ProgressDialog mProgressDialog;
    boolean serverOK;

    public ClientTask(Context context) {
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.setTitle("Watch FTC");
        mProgressDialog.setMessage("Loading data...");
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        String teamsUrl;
        String[] urlSuffix = {""}; //, ":8080"};

        MyApp myApp = MyApp.getInstance();
        Context context = myApp.getApplicationContext();

        String lastDataFileName = context.getString(R.string.lastReceivedData);

        serverOK = false;

        for (int i = 0; (i < urlSuffix.length) && (!serverOK); i++) {

            String teamsUrlHeader = "http://" + myApp.serverAddressString(myApp.division()) +
                    urlSuffix[i] + "/";
            try {
                teamsUrl = teamsUrlHeader + "TeamList";
                Document document = Jsoup.connect(teamsUrl).get();

                Elements hTags = document.select("h2");
                String info = hTags.get(0).text();
                //    Log.i("H2 Tag", hTags.get(0).text());
                info = info.replace(" Team Information", "");
                String[] parts = info.split(" Division: ");
                myApp.tournamentName = parts[0];
                if (parts.length > 1) {
                    myApp.divisionName[myApp.division()] = parts[1];
                }
                //   Log.i("Tname", myApp.tournamentName);
                //   Log.i("Dname", myApp.divisionName[myApp.division()]);

                Element table = document.select("table").get(0); //select the first table.
                Elements rows = table.select("tr");

                myApp.team[myApp.division()].clear(); // made it this far, so hopefully good data coming
                myApp.teamFtcRanked[myApp.division()].clear();
                myApp.match[myApp.division()].clear();
                myApp.teamStatRanked[myApp.division()].clear();

                for (int j = 1; j < rows.size(); j++) { //first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    myApp.team[myApp.division()].add(new Team(Integer.parseInt(cols.get(0).text()),
                                    cols.get(1).text(),
                                    cols.get(2).text(),
                                    cols.get(3).text(),
                                    cols.get(4).text(),
                                    cols.get(5).text())
                    );
                }

                Comparator<Team> ct = Team.getComparator(Team.SortParameter.NUMBER_SORT);
                Collections.sort(myApp.team[myApp.division()], ct);

                teamsUrl = teamsUrlHeader + "Rankings";
                document = Jsoup.connect(teamsUrl).get();

                table = document.select("table").get(0); //select the first table.
                rows = table.select("tr");

                for (int j = 1; j < rows.size(); j++) { //first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    myApp.teamFtcRanked[myApp.division()].add(new TeamFtcRanked(Integer.parseInt(cols.get(0).text()),
                            Integer.parseInt(cols.get(1).text()),
                            cols.get(2).text(),
                            Integer.parseInt(cols.get(3).text()),
                            Integer.parseInt(cols.get(4).text()),
                            Integer.parseInt(cols.get(5).text()),
                            Integer.parseInt(cols.get(6).text())
                    ));
                }

                // Clear out extra teams that might be in the team list but not in this division
                if (myApp.teamFtcRanked[myApp.division()].size()>0) {
                    ArrayList<Team> teamsToDelete = new ArrayList<Team>();

                    for (Team t : myApp.team[myApp.division()]) {
                        if (myApp.teamFtcRanked[myApp.division()].contains(t) == false) {
                     //       Log.i("Num to delete", String.valueOf(t.number));
                            teamsToDelete.add(t);
                        }
                    }
                    for (Team t : teamsToDelete) {
                  //      Log.i("Deleting ", String.valueOf(t.number));
                        myApp.team[myApp.division()].remove(t);
                    }
                }

                teamsUrl = teamsUrlHeader + "MatchDetails";
                document = Jsoup.connect(teamsUrl).get();

                table = document.select("table").get(0); //select the first table.
                rows = table.select("tr");

                for (int j = 2; j < rows.size(); j++) { //first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    // special handling for 2 OR 3 team alliances
                    String redTeam[] = {"0", "0", "0"};
                    String blueTeam[] = {"0", "0", "0"};

                    String redTeamString = cols.get(2).text();
                    String blueTeamString = cols.get(3).text();
                    String redTeamResult[] = redTeamString.split("\\s+");
                    String blueTeamResult[] = blueTeamString.split("\\s+");

                    int k = 0;
                    for (String team : redTeamResult) {
                        redTeam[k] = redTeamResult[k];
                        k++;
                    }
                    k = 0;
                    for (String team : blueTeamResult) {
                        blueTeam[k] = blueTeamResult[k];
                        k++;
                    }

                    myApp.match[myApp.division()].add(new Match(j - 1,
                            cols.get(0).text(),
                            cols.get(1).text(),
                            redTeam[0],
                            redTeam[1],
                            redTeam[2],
                            blueTeam[0],
                            blueTeam[1],
                            blueTeam[2],
                            cols.get(4).text(),
                            cols.get(5).text(),
                            cols.get(6).text(),
                            cols.get(7).text(),
                            cols.get(8).text(),
                            cols.get(9).text(),
                            cols.get(10).text(),
                            cols.get(11).text(),
                            cols.get(12).text(),
                            cols.get(13).text(),
                            cols.get(14).text(),
                            cols.get(15).text(),
                            false // predicted = false: real match!
                    ));

                }
                Stat.computeAll(myApp.division());
                serverOK = true;

                try {
                    context.deleteFile(lastDataFileName);
                } catch (Exception e) {
                    Log.i("No file to delete", "OK");
                }
                try {
                    FileOutputStream fOut = context.openFileOutput(lastDataFileName, Context.MODE_PRIVATE);
                    OutputStreamWriter fw = new OutputStreamWriter(fOut);

                    MyApp.saveTournamentData(fw);

                    fw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (serverOK == false) {
                    if (myApp.team[myApp.division()].size() == 0) {
                        try {
                            FileInputStream fi = context.openFileInput(lastDataFileName);
                            InputStreamReader fr = new InputStreamReader(fi);

                            BufferedReader br = new BufferedReader(fr);

                            MyApp.loadTournamentData(br);

                            fr.close();
                        } catch (Exception f) {
                            Log.i("Error", "Can't open saved tournament data");
                        }
                    }
                }

                serverOK = false;
            }
        }


        return null;
    }


    @Override
    protected void onPostExecute(Void result) {

        mProgressDialog.dismiss();
        delegate.processFinish(Activity.RESULT_OK);

        if (!serverOK) {
            CharSequence text = "Error downloading data.\nTry tapping refresh.";
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(mContext, text, duration).show();


        }
    }
}

package com.wrgardnersoft.watchftc.internet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.wrgardnersoft.watchftc.interfaces.AsyncResponse;
import com.wrgardnersoft.watchftc.models.Match;
import com.wrgardnersoft.watchftc.models.MyApp;
import com.wrgardnersoft.watchftc.models.Team;
import com.wrgardnersoft.watchftc.models.TeamFtcRanked;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

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
        mContext=context;
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
        String[] urlSuffix = {"", ":8080"};

        MyApp myApp = MyApp.getInstance();

        serverOK = false;

        for (int i = 0; (i < urlSuffix.length) && (!serverOK); i++) {

            String teamsUrlHeader = "http://" + myApp.serverAddressString(myApp.division()) +
                    urlSuffix[i] + "/";
            try {
                teamsUrl = teamsUrlHeader + "TeamList";
                Document document = Jsoup.connect(teamsUrl).get();

                Element table = document.select("table").get(0); //select the first table.
                Elements rows = table.select("tr");

                myApp.team[myApp.division()].clear(); // made it this far, so hopefully good data coming
                myApp.teamFtcRanked[myApp.division()].clear();
                myApp.match[myApp.division()].clear();

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

                teamsUrl = teamsUrlHeader + "MatchDetails";
                document = Jsoup.connect(teamsUrl).get();

                table = document.select("table").get(0); //select the first table.
                rows = table.select("tr");

                for (int j = 2; j < rows.size(); j++) { //first row is the col names so skip it.
                    Element row = rows.get(j);
                    Elements cols = row.select("td");

                    // special handling for 2 OR 3 team alliances
                    String redTeam[] = {"0","0","0"};
                    String blueTeam[] = {"0","0","0"};

                    String redTeamString = cols.get(2).text();
                    String blueTeamString = cols.get(3).text();
                    String redTeamResult[] = redTeamString.split("\\s+");
                    String blueTeamResult[] = blueTeamString.split("\\s+");

                    int k=0;
                    for (String team: redTeamResult) {
                        redTeam[k]=redTeamResult[k];
                        k++;
                    }
                    k=0;
                    for (String team: blueTeamResult) {
                        blueTeam[k]=blueTeamResult[k];
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
                            cols.get(15).text()
                    ));

                }

                serverOK = true;
            } catch (IOException e) {
                e.printStackTrace();
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
            CharSequence text = "Error downloading data.";
            int duration = Toast.LENGTH_LONG;
            Toast.makeText(mContext, text, duration).show();
        }
    }
}

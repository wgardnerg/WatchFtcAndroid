package com.wrgardnersoft.watchftctournament.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.wrgardnersoft.watchftctournament.R;
import com.wrgardnersoft.watchftctournament.models.MyApp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class SetupActivity extends ActionBarActivity {

    private EditText[] serverAddressEditText = new EditText[2];
    private CheckBox dualDivisionCheckBox;
    private String setupFileName = "WatchFtcSetupInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(" " + getString(R.string.setup));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        setContentView(R.layout.activity_setup);

        dualDivisionCheckBox = (CheckBox) findViewById(R.id.dualDivisionCheckBox);

        serverAddressEditText[0] = (EditText) findViewById(R.id.div0ServerAddressEditText);
        serverAddressEditText[1] = (EditText) findViewById(R.id.div1ServerAddressEditText);

        MyApp myApp = (MyApp) getApplication();

        try {
            FileInputStream fi = openFileInput(setupFileName);
            InputStreamReader fr = new InputStreamReader(fi);
            BufferedReader br = new BufferedReader(fr);

            serverAddressEditText[0].setText(br.readLine());
            serverAddressEditText[1].setText(br.readLine());

            boolean dd;
            dd = Boolean.parseBoolean(br.readLine());
            dualDivisionCheckBox.setChecked(dd);

            fr.close();
        } catch (IOException e) {
 //           Log.i("Setup Activity", "Exception reading setup data");
            dualDivisionCheckBox.setChecked(myApp.dualDivision());
            serverAddressEditText[0].setText(myApp.serverAddressString[0], TextView.BufferType.EDITABLE);
            serverAddressEditText[1].setText(myApp.serverAddressString[1], TextView.BufferType.EDITABLE);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_exit_only, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onClickSetupDoneButton(View view) {

        // save all setup info to globals in myApp class
        MyApp myApp = (MyApp) getApplication();

        myApp.setDualDivision(dualDivisionCheckBox.isChecked());

        myApp.setDivision(0);

        myApp.setServerAddressString(0, String.valueOf(serverAddressEditText[0].getText()));
        myApp.setServerAddressString(1, String.valueOf(serverAddressEditText[1].getText()));

        try {
            this.deleteFile(setupFileName);
            FileOutputStream fOut = openFileOutput(setupFileName, MODE_PRIVATE);
            OutputStreamWriter fw = new OutputStreamWriter(fOut);

            fw.write(serverAddressEditText[0].getText().toString() + "\n");
            fw.write(serverAddressEditText[1].getText().toString() + "\n");

            //        fw.write(myApp.serverAddressString[0] + "\n");
            //        fw.write(myApp.serverAddressString[1] + "\n");
            fw.write(String.valueOf(myApp.dualDivision()));
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // jump to next activity
        Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
        startActivity(getNameScreenIntent);
        //     finish();
    }
}

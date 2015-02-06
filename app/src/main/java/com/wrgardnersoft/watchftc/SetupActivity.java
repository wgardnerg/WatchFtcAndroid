package com.wrgardnersoft.watchftc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class SetupActivity extends ActionBarActivity {


    private Button setupDoneButton;
    private EditText[] serverAddressEditText = new EditText[2];
    private EditText[] myTeamEditText = new EditText[2];
    private CheckBox dualDivisionCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(" " + getString(R.string.setup));
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        setContentView(R.layout.activity_setup);

        setupDoneButton = (Button) findViewById(R.id.setupDoneButton);

        dualDivisionCheckBox = (CheckBox) findViewById(R.id.dualDivisionCheckBox);

        serverAddressEditText[0] = (EditText) findViewById(R.id.div0ServerAddressEditText);
        serverAddressEditText[1] = (EditText) findViewById(R.id.div1ServerAddressEditText);

        MyApp myApp = (MyApp) getApplication();

        try {
            FileReader fr = new FileReader("WatchFtcSetupInfo");
            BufferedReader br = new BufferedReader(fr);
            dualDivisionCheckBox.setChecked(Boolean.parseBoolean(br.readLine()));
            serverAddressEditText[0].setText(br.readLine());
            serverAddressEditText[1].setText(br.readLine());
            fr.close();
        } catch (IOException e) {
            dualDivisionCheckBox.setChecked(myApp.dualDivision());
            serverAddressEditText[0].setText(myApp.serverAddressString(0));
            serverAddressEditText[1].setText(myApp.serverAddressString(1));
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
            FileOutputStream fOut = openFileOutput("WatchFtcSetupInfo", MODE_PRIVATE);
            fOut.write(String.valueOf(myApp.dualDivision()).getBytes());
            fOut.write(String.valueOf(serverAddressEditText[0].getText()).getBytes());
            fOut.write(String.valueOf(serverAddressEditText[1].getText()).getBytes());
            fOut.close();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // jump to next activity
        Intent getNameScreenIntent = new Intent(this, TeamsActivity.class);
        startActivity(getNameScreenIntent);
        //     finish();
    }
}

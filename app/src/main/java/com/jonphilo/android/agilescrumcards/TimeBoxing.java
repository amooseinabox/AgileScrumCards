package com.jonphilo.android.agilescrumcards;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;


public class TimeBoxing extends Activity {

    TextView textView;
    CountDownTimer timer;
    Boolean isTimerRunning;
    Button startTimer;
    Menu actionBar;
    Spinner spinner;
    private static final String FORMAT = "%d min, %d sec";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_boxing);
        textView = (TextView)findViewById(R.id.time_boxing_text);
        spinner = (Spinner)findViewById(R.id.time_boxing_drop_down);
        textView.setGravity(17);
        textView.setTextColor(-1);
        textView.setTextSize(100);
        textView.setText("");
        setTimer(300000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_boxing, menu);

        actionBar = menu;
        actionBar.findItem(R.id.action_restart_time).setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        String text = spinner.getSelectedItem().toString();
        long time = getTime(text);
        setTimer(time);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_restart_time) {
            if(isTimerRunning)
            {
                timer.cancel();

            }
            timer.start();
            return true;
        }
        if(id == R.id.action_start)
        {
            timer.start();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setTimer(long miliseconds)
    {
        if(timer != null)
        {
            timer.cancel();
        }
        timer = new CountDownTimer(miliseconds, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {

                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String prefix = "";
                if(seconds < 10)
                {
                    prefix = "0";
                }
                String secondsString = prefix + seconds;
                textView.setText("" + String.format("%d:%s", minutes
                        , secondsString
                ));
                if(actionBar != null)
                {
                    actionBar.findItem(R.id.action_start).setEnabled(false);
                    actionBar.findItem(R.id.action_restart_time).setEnabled(true);
                }

                isTimerRunning = true;
            }

            public void onFinish() {
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(1000);
                textView.setText("0:00");
                if(actionBar != null)
                {
                    actionBar.findItem(R.id.action_restart_time).setEnabled(false);
                    actionBar.findItem(R.id.action_start).setEnabled(true);
                }

                isTimerRunning = false;
            }
        };
    }

    private long getTime(String text)
    {
        if(text.equals("5 Minutes"))
        {
            return 300000;
        }
        else if(text.equals("10 Minutes"))
        {
            return 600000;
        }
        else{
            return 900000;
        }
    }


}

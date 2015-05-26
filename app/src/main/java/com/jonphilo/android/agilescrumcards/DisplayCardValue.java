package com.jonphilo.android.agilescrumcards;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jonphilo.android.agilescrumcards.models.CardListModel;


public class DisplayCardValue extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String scrumvalue  = intent.getStringExtra(AgileScrumCardList.CARD_VALUE);
        CardView card = new CardView(this);
        card.setCardElevation(25);
        card.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));

        ActionBar a = getActionBar();
        a.setTitle("");


        // Create the text view
        TextView textView = new TextView(this);
        if(scrumvalue.length() > 4)
        {
            textView.setTextSize(150);
        }
        else if(scrumvalue.length() > 7){
            textView.setTextSize(100);
        }
        else{
            textView.setTextSize(200);
        }
        textView.setText(scrumvalue);
        textView.setGravity(17);
        textView.setTextColor(-1);

        card.addView(textView);

        setContentView(card);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_display_card_value, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
}

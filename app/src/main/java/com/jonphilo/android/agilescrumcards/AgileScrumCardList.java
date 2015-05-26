package com.jonphilo.android.agilescrumcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.text.InputFilter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.view.View;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.support.v7.widget.CardView;
import android.app.ActionBar;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;

import com.jonphilo.android.agilescrumcards.data.CardListsContract;
import com.jonphilo.android.agilescrumcards.data.CardListsDbHelper;
import com.jonphilo.android.agilescrumcards.models.CardListModel;


public class AgileScrumCardList extends Activity {

    final public static String CARD_VALUE = "com.android.agilescrumcards.CARDVALUE";
    private CardListModel backingModel;
    private ArrayList<String> cards;
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agile_scrum_card_list);
        Intent intent = getIntent();
        backingModel = (CardListModel) intent.getSerializableExtra(AgileScrumCardsHome.CARD_LIST);
        ActionBar a = getActionBar();
        a.setTitle(backingModel.getTitle());
        backingModel = GetMostRecentModel(backingModel);
        cards = new ArrayList<>();
        PopulateList(cards, backingModel.getContent());

        UpdateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agile_scrum_card_list, menu);
        return true;
    }

    private CardListModel GetMostRecentModel(CardListModel model)
    {
        CardListsDbHelper cardListsDbHelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = cardListsDbHelper.getReadableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT, model.getContent());
        String query = "SELECT * FROM " + CardListsContract.CardLists.TABLE_NAME + " WHERE _id=" + model.getID();
        Cursor cursor = db.rawQuery(query, new String[]{});
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            CardListModel card = new CardListModel(
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists.COLUMN_NAME_LIST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT))
            );
            model = card;
            cursor.moveToLast();
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        cardListsDbHelper.close();
        return model;

    }

    private void PopulateList(ArrayList<String> list, String values)
    {
        if(values.equals(""))
        {
            return;
        }
        String[] arrValues = values.split(",");
        for(String val : arrValues)
        {
            list.add(val);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select Action");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        //  info.position will give the index of selected item
        int IndexSelected = info.position;
        if (item.getTitle() == "Delete")
        {
            final GridView list = (GridView) findViewById(R.id.mainGrid);
            String card = (String) list.getItemAtPosition(IndexSelected);
            cards.remove(card);
            backingModel.setContent(updateCardListContent(cards));
            updateCardList(backingModel);
            UpdateList();
        }
        else
        {
            return false;
        }
        return true;
    }

    private void UpdateList()
    {
        final GridView gridView = (GridView) findViewById(R.id.mainGrid);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.card_set_template, R.id.label, cards);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                String text = ((TextView) ((LinearLayout) ((CardView) v).getChildAt(0)).getChildAt(0)).getText().toString();
                Intent intent = new Intent(getApplicationContext(), DisplayCardValue.class);
                intent.putExtra(CARD_VALUE, text);
                intent.putExtra(AgileScrumCardsHome.CARD_LIST, backingModel);
                startActivity(intent);
            }
        });
        registerForContextMenu(gridView);
    }

    private void updateCardList(CardListModel model)
    {
        CardListsDbHelper cardListsDbHelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = cardListsDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT, model.getContent());
        String strFilter = "_id=" + model.getID();
        db.update(CardListsContract.CardLists.TABLE_NAME, contentValues,strFilter, new String[]{});
        db.close();
        cardListsDbHelper.close();
    }

    private String updateCardListContent(ArrayList<String> values)
    {
        StringBuilder sb = new StringBuilder();
        for(String val : values)
        {
            sb.append(val);
            sb.append(",");
        }
        return sb.toString();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new_card) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Card");
            builder.setIcon(R.drawable.ic_action_content_add_circle);

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            int maxLength = 10;
            InputFilter[] fArray = new InputFilter[1];
            fArray[0] = new InputFilter.LengthFilter(maxLength);
            input.setTextColor(-1);
            input.setFilters(fArray);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    if (m_Text.trim().equals(""))
                    {
                        Toast t = Toast.makeText(getApplicationContext(), "Card Text Must Be Supplied.", Toast.LENGTH_LONG);
                        t.show();
                        onOptionsItemSelected(item);
                    }
                    else
                    {
                        cards.add(m_Text);
                        backingModel.setContent(updateCardListContent(cards));
                        updateCardList(backingModel);
                        UpdateList();
                    }

                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

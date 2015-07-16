package com.jonphilo.android.agilescrumcards;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.text.InputType;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.view.ContextMenu.ContextMenuInfo;
import com.jonphilo.android.agilescrumcards.data.CardListsContract;
import com.jonphilo.android.agilescrumcards.data.CardListsDbHelper;
import com.jonphilo.android.agilescrumcards.models.CardListModel;
import java.io.File;
import java.util.ArrayList;


public class AgileScrumCardsHome extends Activity {

    public final static String CARD_LIST = "com.android.agilescrumcards.CARDLIST";
    private String m_Text = "";
    private ArrayList<CardListModel> cardLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agile_scrum_cards_home);

        String[] values = new String[] { "0", "1", "2",
                "3", "5", "8", "13", "20",
                "40", "100", "?" };
        CardListModel model = new CardListModel("0", "Fibonacci", JoinValues(values));
        if(!doesDatabaseExist(CardListsDbHelper.DATABASE_NAME))
        {
            createDatabase(model);
        }
        cardLists = getCardLists();

        UpdateList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agile_scrum_cards_home, menu);
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
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
            final ListView list = (ListView) findViewById(R.id.list_view_main);
            CardListModel cardListModel = (CardListModel) list.getItemAtPosition(IndexSelected);
            cardLists.remove(cardListModel);
            CardListsDbHelper cardListsDbHelper = new CardListsDbHelper(getApplicationContext());
            SQLiteDatabase db = cardListsDbHelper.getWritableDatabase();
            String where = "_id=" + cardListModel.getID();
            db.delete(CardListsContract.CardLists.TABLE_NAME, where, new String[]{});
            db.close();
            cardListsDbHelper.close();
            UpdateList();
        }
        else
        {
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_new) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("New Card List");
            builder.setIcon(R.drawable.ic_action_content_add_circle);

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_CLASS_TEXT);
            input.setTextColor(-1);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    m_Text = input.getText().toString();
                    CardListModel cardListModel = new CardListModel("", input.getText().toString(), "");
                    cardLists.add(cardListModel);
                    InsertList(cardListModel);
                    UpdateList();
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

        if(id == R.id.action_time_boxing)
        {
            Intent intent = new Intent(getApplicationContext(), TimeBoxing.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void UpdateList()
    {
        final ListView list = (ListView) findViewById(R.id.list_view_main);
        ArrayAdapter<CardListModel> adapter = new ArrayAdapter<>(this,
                R.layout.main_list_row_layout, R.id.label, cardLists);

        list.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getApplicationContext(), AgileScrumCardList.class);
                CardListModel cardList = (CardListModel) list.getItemAtPosition(position);
                intent.putExtra(CARD_LIST, cardList);
                startActivity(intent);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                return false;
            }
        });
        list.setAdapter(adapter);
        registerForContextMenu(list);
    }

    private void createDatabase(CardListModel initialValue)
    {
        CardListsDbHelper clDbHelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = clDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_ID, initialValue.getID());
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_NAME, initialValue.getTitle());
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT, initialValue.getContent());
        long newRowId;

        newRowId = db.insert(CardListsContract.CardLists.TABLE_NAME,
                "null",
                values);
        db.close();
        clDbHelper.close();
    }

    private boolean doesDatabaseExist( String dbName) {
        File dbFile = getApplicationContext().getDatabasePath(dbName);
        return dbFile.exists();
    }

    private void ClearDatabase()
    {
        CardListsDbHelper clDbHelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = clDbHelper.getWritableDatabase();
        db.delete(CardListsContract.CardLists.TABLE_NAME, null, null);
        db.close();
        clDbHelper.close();
        getApplicationContext().deleteDatabase(CardListsDbHelper.DATABASE_NAME);
    }

    private void InsertList(CardListModel cardListModel)
    {
        CardListsDbHelper clDbhelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = clDbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_ID, cardListModel.getID());
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_NAME, cardListModel.getTitle());
        values.put(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT, cardListModel.getContent());
        long newID = db.insert(CardListsContract.CardLists.TABLE_NAME, null, values);
        db.close();
        clDbhelper.close();
        StringBuilder sb = new StringBuilder();
        sb.append(newID);
        cardListModel.setID(sb.toString());

    }


    private ArrayList<CardListModel> getCardLists()
    {
        ArrayList<CardListModel> ScrumCards = new ArrayList<CardListModel>();
        CardListsDbHelper clDbHelper = new CardListsDbHelper(getApplicationContext());
        SQLiteDatabase db = clDbHelper.getReadableDatabase();

        String[] projection = {
                CardListsContract.CardLists._ID,
                CardListsContract.CardLists.COLUMN_NAME_LIST_ID,
                CardListsContract.CardLists.COLUMN_NAME_LIST_NAME,
                CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT
        };
        String selectionQuery = "SELECT * FROM " + CardListsContract.CardLists.TABLE_NAME;
        String[] queryArgs = {};
        Cursor cursor = db.rawQuery(selectionQuery, queryArgs);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            CardListModel card = new CardListModel(
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists.COLUMN_NAME_LIST_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(CardListsContract.CardLists.COLUMN_NAME_LIST_CONTENT))
            );
            ScrumCards.add(card);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        clDbHelper.close();

        return ScrumCards;
    }

    public static String JoinValues(String[] values)
    {
        if(values == null)
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for(String val : values)
        {
            sb.append(val);
            sb.append(",");
        }
        return sb.toString();
    }
}

package com.jonphilo.android.agilescrumcards.data;

import android.provider.BaseColumns;

/**
 * Created by jonathanphilo on 5/21/15.
 */
public final class CardListsContract {
    public CardListsContract() {}

    public static abstract class CardLists implements BaseColumns
    {
        public static final String TABLE_NAME = "cardlists";
        public static final String COLUMN_NAME_LIST_ID = "cardlistid";
        public static final String COLUMN_NAME_LIST_NAME = "cardlistname";
        public static final String COLUMN_NAME_LIST_CONTENT = "cardlistcontent";
    }
}

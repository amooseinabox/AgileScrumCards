package com.jonphilo.android.agilescrumcards.models;
import java.io.Serializable;
/**
 * Created by jonathanphilo on 5/21/15.
 */
@SuppressWarnings("serial")
public class CardListModel implements Serializable{
    private String _id;
    private String _title;
    private String _content;

    public CardListModel(){}

    public CardListModel(String id, String title, String content)
    {
        _id = id;
        _title = title;
        _content = content;
    }

    public String getID()
    {
        return _id;
    }

    public void setID(String id)
    {
        _id = id;
    }

    public String getTitle()
    {
        return _title;
    }

    public void setTitle(String title)
    {
        _title = title;
    }

    public String getContent()
    {
        return _content;
    }

    public void setContent(String content)
    {
        _content = content;
    }

    @Override
    public String toString()
    {
        return _title;
    }
}

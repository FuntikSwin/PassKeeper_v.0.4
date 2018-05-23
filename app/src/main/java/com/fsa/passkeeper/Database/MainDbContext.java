package com.fsa.passkeeper.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.fsa.passkeeper.Model.Card;
import com.fsa.passkeeper.Model.CardField;
import com.fsa.passkeeper.Model.CardFieldValueType;
import com.fsa.passkeeper.Model.CardGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainDbContext implements IDatabaseContext {

    private SQLiteHelper mDbHelper;
    private SQLiteDatabase mDb;

    public MainDbContext(Context context) {
        mDbHelper = new SQLiteHelper(context);
        try {
            mDbHelper.updateDatabase();
        } catch (IOException ex) {
            throw new Error("UnableToUpdateDatabase");
        }
        mDb = mDbHelper.getWritableDatabase();
    }

    @Override
    public List<Card> getCards() {
        List<Card> cards = new ArrayList<>();

        Cursor cursor = mDb.rawQuery(
                "select c.Id as CardId, c.Caption as CardCaption, c.CardGroupId, cs.GroupName " +
                        "from Card c " +
                        "inner join CardGroup cs ON c.CardGroupId = cs.Id", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Card card = new Card(
                    cursor.getInt(0),
                    cursor.getString(1),
                    !cursor.isNull(2)
                            ? new CardGroup(cursor.getInt(2), cursor.getString(3))
                            : null,
                    null);
            cards.add(card);
            cursor.moveToNext();
        }
        cursor.close();

        return cards;
    }

    @Override
    public boolean removeCard(Card card) {
        int cnt;
        cnt = mDb.delete("CardField", "CardId = " + card.getId(), null);
        int cntCard = mDb.delete("Card", "Id = " + card.getId(), null);
        cnt = cnt + cntCard;
        return cnt > 0;
    }

    @Override
    public void updateCard(Card card) {
        ContentValues cv = new ContentValues();
        if (card.getCardFields() == null || card.getCardFields().size() == 0) {
            mDb.delete("CardField", "CardId = " + card.getId(), null);
        } else {
            List<CardField> dbCardFields = getCardFields(card);
            boolean isFound = false;
            for (CardField dbCf : dbCardFields) {
                isFound = false;
                for (CardField cf : card.getCardFields()) {
                    if (cf.getId() == dbCf.getId()) {
                        isFound = true;

                        cv.clear();
                        cv.put("Caption", cf.getCaption());
                        cv.put("FieldValue", cf.getValue());
                        cv.put("ValueTypeId", cf.getCardFieldValueType().getId());
                        mDb.update("CardField", cv, "Id = " + cf.getId(), null);

                        break;
                    }
                }

                if (!isFound) {
                    mDb.delete("CardField", "Id = " + dbCf.getId(), null);
                }
            }

            for (CardField cf : card.getCardFields()) {
                isFound = false;
                for (CardField dbCf : dbCardFields) {
                    if (dbCf.getId() == cf.getId()) {
                        isFound = true;
                    }
                }

                if (!isFound) {
                    cv.clear();
                    cv.put("Caption", cf.getCaption());
                    cv.put("FieldValue", cf.getValue());
                    cv.put("ValueTypeId", cf.getCardFieldValueType().getId());
                    mDb.insert("CardField", null, cv);
                }
            }
        }

        cv.clear();
        cv.put("Caption", card.getCaption());
        cv.put("CardGroupId", card.getCardGroup() != null ? card.getCardGroup().getId() : null);
        mDb.update("Card", cv, "Id = " + card.getId(), null);
    }

    @Override
    public boolean addCard(Card card) {
        ContentValues cv = new ContentValues();
        cv.put("Caption", card.getCaption());
        cv.put("CardGroupId", card.getCardGroup().getId());
        long cardId = mDb.insert("Card", null, cv);

        if (cardId == 0) {
            return false;
        }
        for (CardField field : card.getCardFields()) {
            cv.clear();
            cv.put("Caption", field.getCaption());
            cv.put("FieldValue", field.getValue());
            cv.put("ValueTypeId", field.getCardFieldValueType().getId());
            cv.put("CardId", card.getId());
            mDb.insert("CardField", null, cv);
        }

        return true;
    }

    @Override
    public List<CardGroup> getCardGroups() {
        List<CardGroup> cardGroups = new ArrayList<>();

        Cursor cursor = mDb.rawQuery("select cg.Id, cg.GroupName from CardGroup cg", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cardGroups.add(new CardGroup(cursor.getLong(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();

        return cardGroups;
    }

    @Override
    public List<CardField> getCardFields(Card card) {
        List<CardField> cardFields = new ArrayList<>();

        Cursor cursor = mDb.rawQuery(
                "select cf.Id, cf.Caption, cf.FieldValue, cf.ValueTypeId, ft.TypeName " +
                        "from CardField cf " +
                        "left join CardFieldValueType ft ON ft.Id = cf.ValueTypeId " +
                        "where cf.CardId = " + Long.toString(card.getId()), null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cardFields.add(new CardField(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    !cursor.isNull(3)
                            ? new CardFieldValueType(cursor.getInt(3), cursor.getString(4))
                            : null));
            cursor.moveToNext();
        }
        cursor.close();

        return cardFields;
    }

    @Override
    public List<CardFieldValueType> getCardFieldValueTypes() {
        List<CardFieldValueType> valueTypes = new ArrayList<>();

        Cursor cursor = mDb.rawQuery("select t.Id, t.TypeName from CardFieldValueType t", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            valueTypes.add(new CardFieldValueType(cursor.getLong(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();

        return valueTypes;
    }
}

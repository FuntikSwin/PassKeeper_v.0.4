package com.fsa.passkeeper.Model;

import java.io.Serializable;

public class CardField implements Serializable {

    private long id;
    private String caption;
    private String value;
    private CardFieldValueType cardFieldValueType;

    public CardField(long id, String caption, String value, CardFieldValueType cardFieldValueType) {
        this.id = id;
        this.caption = caption;
        this.value = value;
        this.cardFieldValueType = cardFieldValueType;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public CardFieldValueType getCardFieldValueType() {
        return cardFieldValueType;
    }

    public void setCardFieldValueType(CardFieldValueType cardFieldValueType) {
        this.cardFieldValueType = cardFieldValueType;
    }

}

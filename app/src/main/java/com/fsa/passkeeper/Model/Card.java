package com.fsa.passkeeper.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {

    private long id;
    private String caption;
    private CardGroup cardGroup;
    private List<CardField> cardFields;

    public Card() {
    }

    public Card(int id, String caption, CardGroup cardGroup, List<CardField> cardFields) {
        this.id = id;
        this.caption = caption;
        this.cardGroup = cardGroup;
        this.cardFields = cardFields;
        if (this.cardFields == null) {
            this.cardFields = new ArrayList<>();
        }
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

    public CardGroup getCardGroup() {
        return cardGroup;
    }

    public void setCardGroup(CardGroup cardGroup) {
        this.cardGroup = cardGroup;
    }

    public List<CardField> getCardFields() {
        return cardFields;
    }

    public void setCardFields(List<CardField> cardFields) {
        this.cardFields = cardFields;
    }

}

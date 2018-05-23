package com.fsa.passkeeper.Database;

import com.fsa.passkeeper.Model.Card;
import com.fsa.passkeeper.Model.CardField;
import com.fsa.passkeeper.Model.CardFieldValueType;
import com.fsa.passkeeper.Model.CardGroup;

import java.util.List;

public interface IDatabaseContext {

    List<Card> getCards();
    boolean removeCard(Card card);
    void updateCard(Card card);
    boolean addCard(Card card);

    List<CardGroup> getCardGroups();

    List<CardField> getCardFields(Card card);

    List<CardFieldValueType> getCardFieldValueTypes();

}
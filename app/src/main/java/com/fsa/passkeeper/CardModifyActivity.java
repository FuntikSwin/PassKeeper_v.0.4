package com.fsa.passkeeper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;

import com.fsa.passkeeper.Adapter.CardModifyFieldsAdapter;
import com.fsa.passkeeper.Database.IDatabaseContext;
import com.fsa.passkeeper.Model.Card;
import com.fsa.passkeeper.Model.CardField;
import com.fsa.passkeeper.Model.CardFieldValueType;
import com.fsa.passkeeper.Model.CardGroup;

import java.util.ArrayList;
import java.util.List;

public class CardModifyActivity extends AppCompatActivity {

    private IDatabaseContext mDbContext;

    private boolean isAddNewCard;
    private Card mCard;

    private EditText etCardCaption;
    private Spinner spinnerCardGroup;
    private List<CardGroup> cardGroups;
    private ArrayAdapter<String> adapterSpinner;
    private ListView lvFields;
    private CardModifyFieldsAdapter adapterModifyFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_modify);

        mDbContext = App.getDatabaseContext();

        Intent intent = getIntent();
        String intentAction = intent.getAction();
        if (intentAction.isEmpty()) {
            finish();
            return;
        }
        isAddNewCard = intentAction == "com.fsa.passkeeper.addcard";

        mCard = (Card) intent.getSerializableExtra("Card");

        Toolbar toolbar = findViewById(R.id.ToolbarCardModify);
        setSupportActionBar(toolbar);

        spinnerCardGroup = findViewById(R.id.spinnerCardGroup);
        etCardCaption = findViewById(R.id.etCardCaption);

        cardGroups = mDbContext.getCardGroups();
        List<String> dataSpinner = new ArrayList<>();
        dataSpinner.add("Без группы");
        for (int i = 0; i < cardGroups.size(); i++) {
            String groupName = cardGroups.get(i).getGroupName();
            dataSpinner.add(groupName);
        }

        adapterSpinner = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                dataSpinner);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCardGroup.setAdapter(adapterSpinner);

        lvFields = findViewById(R.id.lvCardModifyFields);
        adapterModifyFields = new CardModifyFieldsAdapter(this, mCard.getCardFields());
        lvFields.setAdapter(adapterModifyFields);

        Button btnAddField = findViewById(R.id.btnAddField);
        btnAddField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(CardModifyActivity.this, v);
                Menu menu = popupMenu.getMenu();

                final List<CardFieldValueType> valueTypes = mDbContext.getCardFieldValueTypes();
                for (CardFieldValueType vType : valueTypes) {
                    menu.add(vType.getTypeName());
                }

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        CardFieldValueType currFieldType = null;
                        for (CardFieldValueType vType : valueTypes) {
                            if (vType.getTypeName().equals(item.getTitle().toString())) {
                                currFieldType = vType;
                                break;
                            }
                        }
                        if (currFieldType != null) {
                            LayoutInflater layoutInflater = LayoutInflater.from(CardModifyActivity.this);
                            final View dlgView = layoutInflater.inflate(R.layout.dialog_add_card_field, null);

                            final EditText etFieldCaption = dlgView.findViewById(R.id.etFieldCaption);
                            final CardFieldValueType finalFieldType = currFieldType;
                            AlertDialog dialog = new AlertDialog.Builder(CardModifyActivity.this)
                                    .setTitle("Add " + finalFieldType.getTypeName() + " field")
                                    .setView(dlgView)
                                    .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            addCardField(finalFieldType, etFieldCaption.getText().toString());
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            return;
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }

                        return true;
                    }
                });

                popupMenu.show();
            }
        });

        prepareModifyData();
    }

    private void addCardField(CardFieldValueType cardFieldValueType, String fieldCaption) {
        CardField cardField = new CardField(0, fieldCaption, "", cardFieldValueType);
        //mCard.getCardFields().add(cardField);
        adapterModifyFields.addCardField(cardField);
        //adapterModifyFields.notifyDataSetChanged();
    }

    private void prepareModifyData() {
        etCardCaption.setText(mCard.getCaption());

        spinnerCardGroup.setSelection(0);
        for (int i = 0; i < adapterSpinner.getCount(); i++) {
            String spinnerItem = adapterSpinner.getItem(i);
            if (mCard.getCardGroup() != null && mCard.getCardGroup().getGroupName().equals(spinnerItem)) {
                spinnerCardGroup.setSelection(i);
                break;
            }
        }
    }

    private void updateCard() {
        long cardId = mCard != null ? mCard.getId() : 0;
        mCard = new Card();
        mCard.setId(cardId);
        mCard.setCaption(etCardCaption.getText().toString());
        String groupName = (String) spinnerCardGroup.getSelectedItem();
        mCard.setCardGroup(null);
        for (CardGroup cg : cardGroups) {
            if (cg.getGroupName().equals(groupName)) {
                mCard.setCardGroup(cg);
                break;
            }
        }

        List<CardField> cardFields = new ArrayList<>();
        for (int i = 0; i < lvFields.getCount(); i++) {
            View v = getViewByPosition(i, lvFields);
            EditText etFieldValue = v.findViewById(R.id.etFieldValue);
            CardField cf = (CardField) adapterModifyFields.getItem(i);
            cf.setValue(etFieldValue.getText().toString());
            cardFields.add(cf);
        }
        mCard.setCardFields(cardFields);

        if (isAddNewCard) {
            mDbContext.addCard(mCard);
        } else {
            //mCard.setId(cardId);
            mDbContext.updateCard(mCard);
        }
    }

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition =firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, listView.getChildAt(pos), listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_modify, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_save_card:
                updateCard();
                if (!isAddNewCard) {
                    Intent intent = new Intent(CardModifyActivity.this, CardInfoActivity.class);
                    intent.putExtra("Card", mCard);
                    startActivityForResult(intent, 1);
                    break;
                }
                finish();
                break;
            case R.id.action_cancel:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

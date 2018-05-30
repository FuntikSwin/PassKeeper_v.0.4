package com.fsa.passkeeper;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fsa.passkeeper.Adapter.CardInfoFieldsAdapter;
import com.fsa.passkeeper.Database.IDatabaseContext;
import com.fsa.passkeeper.Model.Card;
import com.fsa.passkeeper.Model.CardField;

import java.util.List;

public class CardInfoActivity extends AppCompatActivity {

    private Card mCard;
    private IDatabaseContext mDbContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info);

        mDbContext = PassKeeperApp.getDatabaseContext();

        Toolbar toolbar = findViewById(R.id.ToolbarCardInfo);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mCard = (Card) intent.getSerializableExtra("Card");

        TextView tvCardCaption = findViewById(R.id.tvCardCaption);
        tvCardCaption.setText(mCard.getCaption());

        TextView tvGroupName = findViewById(R.id.tvCardGroupName);
        tvGroupName.setText(
                mCard.getCardGroup() != null
                        ? mCard.getCardGroup().getGroupName()
                        : "Без группы");

        List<CardField> cardFields = mDbContext.getCardFields(mCard);
        mCard.setCardFields(cardFields);

        ListView lvCardFields = findViewById(R.id.lvCardInfoFields);
        CardInfoFieldsAdapter adapter = new CardInfoFieldsAdapter(this, mCard.getCardFields());
        lvCardFields.setAdapter(adapter);

        lvCardFields.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvValue = view.findViewById(R.id.tvFieldValue);
                if (tvValue.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                    tvValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else if (tvValue.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)) {
                    tvValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
            }
        });

        lvCardFields.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tvValue = view.findViewById(R.id.tvFieldValue);

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("FIeldValue", tvValue.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                Snackbar.make(view, "Data copy to clipboard", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_card_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_edit_card) {
            Intent intent = new Intent("com.fsa.passkeeper.editcard");
            intent.putExtra("Card", mCard);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}

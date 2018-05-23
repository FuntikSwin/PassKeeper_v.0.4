package com.fsa.passkeeper;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fsa.passkeeper.Adapter.CardsAdapter;
import com.fsa.passkeeper.Database.IDatabaseContext;
import com.fsa.passkeeper.Model.Card;

import java.util.ArrayList;
import java.util.List;

public class CardsActivity extends AppCompatActivity {

    private IDatabaseContext mDbContext;

    private ListView lvCards;
    private List<Card> cards;
    private CardsAdapter adapterCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Card newCard = new Card(0,"", null, null);
                Intent intent = new Intent("com.fsa.passkeeper.addcard");
                intent.putExtra("Card", newCard);
                startActivity(intent);
            }
        });

        mDbContext = App.getDatabaseContext();

        lvCards = findViewById(R.id.lvCards);
        cards = mDbContext.getCards();
        adapterCards = new CardsAdapter(this, cards);
        lvCards.setAdapter(adapterCards);
        lvCards.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Card card = (Card) parent.getItemAtPosition(position);
                Intent intent = new Intent(CardsActivity.this, CardInfoActivity.class);
                intent.putExtra("Card", card);
                startActivity(intent);
            }
        });

        registerForContextMenu(lvCards);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        cards = mDbContext.getCards();
        adapterCards.updateData(cards);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cards, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.lvCards) {
            ListView lv = (ListView) v;
            Card cardItem = (Card) lv.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);

            menu.add("Удалить");
            MenuItem mi = menu.getItem(0);
            mi.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    Card currCard = (Card) adapterCards.getItem(info.position);
                    if (mDbContext.removeCard(currCard)) {
                        adapterCards.remove(currCard);
                    }

                    return true;
                }
            });
        }
    }
}

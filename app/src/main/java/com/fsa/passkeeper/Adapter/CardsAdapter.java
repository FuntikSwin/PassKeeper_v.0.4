package com.fsa.passkeeper.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fsa.passkeeper.Model.Card;
import com.fsa.passkeeper.R;

import java.util.ArrayList;
import java.util.List;

public class CardsAdapter extends BaseAdapter {
    private Context mContext;
    private List<Card> mCards;

    public CardsAdapter(Context mContext, List<Card> mCards) {
        this.mContext = mContext;
        this.mCards = mCards;
        if (this.mCards == null) {
            this.mCards = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return mCards != null ? mCards.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        Card card = mCards != null ? mCards.get(position) : null;
        return card != null ? card : null;
    }

    @Override
    public long getItemId(int position) {
        Card card = mCards != null ? mCards.get(position) : null;
        return card != null ? card.getId() : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(mContext, R.layout.card_listview_item, null);

        TextView tvCaption = view.findViewById(R.id.tvCardCaption);
        TextView tvCardGroupName = view.findViewById(R.id.tvCardGroupName);

        Card card = mCards != null ? mCards.get(position) : null;

        if (card != null) {
            tvCaption.setText(card.getCaption());
            tvCardGroupName.setText(card.getCardGroup() != null ? card.getCardGroup().getGroupName() : "");
        }

        return view;
    }

    public void remove(Card card) {
        mCards.remove(card);
        notifyDataSetChanged();
    }
}

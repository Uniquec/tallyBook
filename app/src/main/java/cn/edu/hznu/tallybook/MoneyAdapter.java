package cn.edu.hznu.tallybook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MoneyAdapter extends ArrayAdapter<Money>{
    private int resourceId;

    public MoneyAdapter(Context context, int textViewResourceId, List<Money> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Money money = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView moneySource = (TextView)view.findViewById(R.id.money_sourcce);
        TextView price = (TextView)view.findViewById(R.id.price);
        TextView date = (TextView)view.findViewById(R.id.date);
        TextView type = (TextView)view.findViewById(R.id.type);
        moneySource.setText(money.getSource());
        price.setText(money.getPrice());
        date.setText(money.getDate());
        type.setText(money.getType());
        return view;
    }
}
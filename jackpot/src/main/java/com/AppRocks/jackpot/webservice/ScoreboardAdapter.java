package com.AppRocks.jackpot.webservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.AppRocks.jackpot.JackpotApplication;
import com.AppRocks.jackpot.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ScoreboardAdapter extends BaseAdapter {

    Context context;
    ArrayList<HashMap<String, String>> scoreList;
    LayoutInflater inflator;
    private TextView txtName;
    private TextView txtNumber;
    private TextView txtScore;
    private HashMap<String, String> hMap;

    public ScoreboardAdapter(Context c, ArrayList<HashMap<String, String>> arrayList) {
        context = c;
        scoreList = arrayList;
        inflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return scoreList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View v = convertView;
        if (convertView == null)
            v = inflator.inflate(R.layout.score_row, null);

        txtName = (TextView) v.findViewById(R.id.txtName);
        txtNumber = (TextView) v.findViewById(R.id.txtNumber);
        txtScore = (TextView) v.findViewById(R.id.txtScore);

        hMap = new HashMap<String, String>();
        hMap = scoreList.get(position);
        if (hMap.get(JackpotApplication.TAG_NICKNAME) == null || hMap.get(JackpotApplication.TAG_NICKNAME).isEmpty()) {
            txtName.setText(hMap.get(JackpotApplication.TAG_Mail));
        } else {
            txtName.setText(hMap.get(JackpotApplication.TAG_NICKNAME));
        }
        txtNumber.setText("" + (position + 1));
        txtScore.setText(hMap.get(JackpotApplication.TAG_TOTAL_SCORE));

        return v;
    }

}

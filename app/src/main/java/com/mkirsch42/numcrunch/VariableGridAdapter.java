package com.mkirsch42.numcrunch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Mathew on 5/11/2017.
 */

public class VariableGridAdapter extends BaseAdapter {
    private LinkedHashMap<String, RPNValue> data;
    private Context context;
    private static LayoutInflater inflater = null;

    public VariableGridAdapter(Context context, LinkedHashMap<String, RPNValue> data) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.entrySet().toArray()[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        HashMap.Entry<String, RPNValue> entry = (HashMap.Entry<String, RPNValue>)getItem(i);
        ViewGroup v = (ViewGroup)inflater.inflate(R.layout.variable_button, null);
        String key = entry.getKey();
        RPNValue value = entry.getValue();
        ((Button)v.getChildAt(0)).setText(key);
        v.getChildAt(0).setTag("V."+key + " S."+key);
        ((TextView)v.getChildAt(1)).setText(new Term(value.get()).toString());

        return v;
    }
}

package com.mkirsch42.numcrunch;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class KeyPadLayout extends LinearLayout implements Button.OnClickListener, Button.OnLongClickListener {

    private ArrayList<Button> buttons = new ArrayList<>();
    private KeyPadListener listener;

    public KeyPadLayout(Context context) {
        super(context);
    }

    public KeyPadLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KeyPadLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        Context context = getContext();
        updateButtons();
        if(context instanceof KeyPadListener) {
            listener = (KeyPadListener)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement KeyPadListener");
        }
    }

    private void findAllButtons(ViewGroup viewGroup) {
        int count = viewGroup.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup)
                findAllButtons((ViewGroup) view);
            else if (view instanceof Button) {
                Button btn = (Button) view;
                buttons.add(btn);
            }
        }

    }

    public void updateButtons() {
        findAllButtons(this);
        for(Button b : buttons) {
            b.setText(Html.fromHtml(b.getText().toString(), Html.FROM_HTML_MODE_LEGACY));
            b.setOnClickListener(this);
            b.setOnLongClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        listener.keyPressed(RPNAction.parse(v.getTag().toString().split(" ")[0]));
    }

    @Override
    public boolean onLongClick(View v) {
        String op;
        try {
            op = v.getTag().toString().split(" ")[1];
        } catch(ArrayIndexOutOfBoundsException e) {
            return false;
        }
        listener.keyPressed(RPNAction.parse(op));
        return true;
    }

    public interface KeyPadListener {
        void keyPressed(RPNAction op);
    }
}

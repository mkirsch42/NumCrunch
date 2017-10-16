package com.mkirsch42.numcrunch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.LinkedList;
import java.util.function.Consumer;

public class MainActivity extends Activity implements KeyPadLayout.KeyPadListener {

    private static MainActivity current;

    private final int DEFAULT_KEY_PANE_NAV = R.id.navigation_basic;

    private int current_pane_index = -1;
    private RPNStack stack;
    private RPNFunction history;
    private RPNStack stackOld;
    private RPNFunction historyOld;
    private Variables vars;
    private Functions funs;
    private boolean radians;
    private boolean inMenu;
    private boolean creatingFunction = false;

    private BottomNavigationView.OnNavigationItemSelectedListener keyNavListener = item -> {
            int index = -1;
            switch (item.getItemId()) {
                case R.id.navigation_adv:
                    index = 0;
                    break;
                case R.id.navigation_basic:
                    index = 1;
                    break;
                case R.id.navigation_values:
                    index = 2;
                    break;
                case R.id.navigation_vars:
                    index = 3;
                    break;
                case R.id.navigation_fun:
                    index = 4;
                    break;
            }
            if(current_pane_index == index) {
                return false;
            }
            if(creatingFunction && index == 3) {
                return false;
            }
            if (current_pane_index == -1) {
                ((FrameLayout) findViewById(R.id.key_wrapper)).getChildAt(index).setVisibility(View.VISIBLE);
            } else {
                crossfade(current_pane_index, index, 500);
            }
            if(index == 3 || index == 4) {
                findViewById(R.id.new_var).setVisibility(View.VISIBLE);
                findViewById(R.id.new_var).animate().scaleX(1).scaleY(1).setDuration(250).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (creatingFunction) {
                            findViewById(R.id.end_fun).setVisibility(View.VISIBLE);
                            findViewById(R.id.end_fun).animate().scaleX(1).scaleY(1).setDuration(250).setListener(null);
                        }
                    }
                });
            } else {
                findViewById(R.id.new_var).animate().scaleX(0).scaleY(0).setDuration(250).setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation){
                        findViewById(R.id.new_var).setVisibility(View.GONE);
                    }
                });
                findViewById(R.id.end_fun).animate().scaleX(0).scaleY(0).setDuration(250).setListener(new AnimatorListenerAdapter(){
                    @Override
                    public void onAnimationEnd(Animator animation){
                        findViewById(R.id.end_fun).setVisibility(View.GONE);
                    }
                });
            }
            current_pane_index = index;
            return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        current = this;
        stack = new RPNStack();
        history = new RPNFunction();
        funs = new Functions();
        vars = new Variables();
        addVariable("A");
        addVariable("B");
        addVariable("C");
        radians = true;

        findViewById(R.id.list_wrapper).setVisibility(View.INVISIBLE);
        inMenu = false;

        findViewById(R.id.btn_trigMode).setOnClickListener(e->flipTrig());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.key_nav);
        navigation.setOnNavigationItemSelectedListener(keyNavListener);
        navigation.setSelectedItemId(DEFAULT_KEY_PANE_NAV);

        findViewById(R.id.btn_history).setOnClickListener(e->createDropdown(history.getOps(), (p, v, i, id)->{
            Log.d("HISTORY_NAV", ""+i);
            stack = new RPNStack();
            history = history.getThrough(i);
            stack.applyAction(history);
            updateDisplay();
            exitMenu();
        }));
        findViewById(R.id.btn_stack).setOnClickListener(e->{
            LinkedList s = stack.getStack();
            Collections.reverse(s);
            createDropdown(s, (p, v, i, id)->{
                int si = stack.getStack().size()-1-i;
                history.addAction(new Getter(si));
                stack.applyAction(new Getter(si));
                //stack.push(new Term(((TextView)v).getText().toString()));
                updateDisplay();
                exitMenu();
            });
        });
        findViewById(R.id.exit_menu).setOnClickListener((e)->exitMenu());
        findViewById(R.id.new_var).setOnClickListener((e)->fab());
        findViewById(R.id.end_fun).setOnClickListener((e)->endFun());
        updateDisplay();
    }

    private void fab() {
        if(current_pane_index == 3) {
            createVar();
        } else if(current_pane_index == 4) {
            if(creatingFunction) {
                addFun();
            } else {
                startFun();
            }
        }
    }

    @Override
    public void keyPressed(RPNAction op) {
        Log.d("ARGC", op.argc()+"");
        stack.applyAction(op);
        history.addAction(op);
        updateDisplay();
    }

    public void updateDisplay() {
        ((TextView)findViewById(R.id.textX)).setText(stack.getX().toString());
        ((TextView)findViewById(R.id.textY)).setText(stack.getY().toString());
        ((TextView)findViewById(R.id.textZ)).setText(stack.getZ().toString());
        findViewById(R.id.scrollX).post(()->
                ((HorizontalScrollView)findViewById(R.id.scrollX)).fullScroll(View.FOCUS_RIGHT)
        );
        updateVars();
        checkEasterEgg();
    }

    public void updateVars() {
        GridLayout varsGrid = (GridLayout)findViewById(R.id.varsGrid);
        for(int i=0; i < varsGrid.getChildCount(); i++) {
            ViewGroup current = (ViewGroup)varsGrid.getChildAt(i);
            ((TextView)current.getChildAt(1)).setText(new Term(vars.get(((Button)current.getChildAt(0)).getText().toString()).get()).toString());
        }
        varsGrid.post(()->((KeyPadLayout)varsGrid.getParent()).updateButtons());
        GridLayout funGrid = (GridLayout)findViewById(R.id.funGrid);
        funGrid.post(()->((KeyPadLayout)funGrid.getParent()).updateButtons());
    }

    public Variables getVars() {
        return vars;
    }

    public Functions getFuns() {
        return funs;
    }

    public RPNStack getStack() {
        return stack;
    }

    private void flipTrig() {
        radians = !radians;
        ((Button)findViewById(R.id.btn_trigMode)).setText(radians?"R":"D");
    }

    public boolean rad() {
        return radians;
    }

    public static MainActivity getCurrent() {
        return current;
    }

    private void crossfade(int oldI, int newI, int duration) {

        FrameLayout wrapper = (FrameLayout) findViewById(R.id.key_wrapper);

        View oldV = wrapper.getChildAt(oldI);
        View newV = wrapper.getChildAt(newI);

        // Set the content view to 0% opacity but visible, so that it is visible
        // (but fully transparent) during the animation.
        newV.animate().cancel();
        newV.setAlpha(0f);
        newV.setVisibility(View.VISIBLE);

        // Animate the content view to 100% opacity, and clear any animation
        // listener set on the view.
        newV.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(duration)
                .setListener(null);

        // Animate the loading view to 0% opacity. After the animation ends,
        // set its visibility to GONE as an optimization step (it won't
        // participate in layout passes, etc.)
        oldV.animate()
                .alpha(0f)
                .translationY(56)
                .setDuration(duration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        oldV.setTranslationY(0);
                        oldV.setVisibility(View.GONE);
                    }
                });
    }

    private void createDropdown(LinkedList members, AdapterView.OnItemClickListener listener) {
        ViewGroup wrapper = (ViewGroup)findViewById(R.id.list_wrapper);
        ListView v = (ListView)findViewById(R.id.list);
        v.setAdapter(new ArrayAdapter<Object>(this, R.layout.list_item, members));
        v.setOnItemClickListener(listener);
        v.post(()->{
            wrapper.setVisibility(View.VISIBLE);
            wrapper.setTranslationY(-wrapper.getHeight());
            wrapper.animate().translationY(0).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    findViewById(R.id.exit_menu).animate().scaleX(1).scaleY(1).setDuration(250);
                }
            });
        });
        inMenu = true;
    }

    public void exitMenu() {
        if(inMenu) {
            ViewGroup wrapper = (ViewGroup)findViewById(R.id.list_wrapper);
            findViewById(R.id.exit_menu).animate().scaleX(0).scaleY(0).setDuration(250);
            wrapper.animate().translationY(-wrapper.getHeight()).setDuration(500).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    wrapper.setVisibility(View.INVISIBLE);
                }
            });
            inMenu = false;
        }
    }

    private void createVar() {
        openDialog("New Variable Name", s->addVariable(s));
    }

    private void openDialog(String msg, Consumer<String> c){
        final EditText editText = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(msg);
        builder.setView(editText);
        AlertDialog alertDialog = builder.create();

        builder.setPositiveButton("OK", (dialog, which)->{
            c.accept(editText.getText().toString());
        });

        builder.setNegativeButton("Cancel", (d,w)->{});

        builder.show();
    }

    private void addVariable(String s) {
        vars.add(s);
        GridLayout varsGrid = (GridLayout)findViewById(R.id.varsGrid);
        ViewGroup add = (ViewGroup)((ViewGroup)getLayoutInflater().inflate(R.layout.variable_button, varsGrid)).getChildAt(varsGrid.getChildCount()-1);
        ((Button)add.getChildAt(0)).setText(s);
        add.getChildAt(0).setTag("V."+s + " S."+s);
        ((TextView)add.getChildAt(1)).setText(new Term(0).toString());
        updateVars();
    }

    private void startFun() {
        historyOld = history;
        stackOld = new RPNStack(stack);
        history = new RPNFunction();
        creatingFunction = true;
        findViewById(R.id.end_fun).setVisibility(View.VISIBLE);
        findViewById(R.id.end_fun).animate().scaleX(1).scaleY(1).setDuration(250).setListener(null);
    }

    private void addFun() {
        openDialog("Function Name", s->{
            GridLayout funGrid = (GridLayout)findViewById(R.id.funGrid);
            Button add = (Button)((ViewGroup)getLayoutInflater().inflate(R.layout.function_button, funGrid)).getChildAt(funGrid.getChildCount()-1);
            add.setText(s);
            s = s.replaceAll("\\s", "");
            add.setTag("F."+s);
            history.setName("F."+s);
            funs.add(s, history);
            updateVars();
            endFun();
        });
    }

    private void endFun() {
        history = historyOld;
        stack = stackOld;
        creatingFunction = false;
        findViewById(R.id.end_fun).animate().scaleX(0).scaleY(0).setDuration(250).setListener(new AnimatorListenerAdapter(){
            @Override
            public void onAnimationEnd(Animator animation){
                findViewById(R.id.end_fun).setVisibility(View.GONE);
            }
        });
        updateDisplay();
    }

    @Override
    public void onBackPressed() {
        if(inMenu) {
            exitMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void checkEasterEgg() {
        Term x = stack.getX();
        if(x.appendable && x.toString().equals("01189998819991197253")) {
            ViewGroup parent = (ViewGroup)findViewById(R.id.disp_parent);
            parent.setBackgroundColor(Color.BLACK);
            int orange = Color.parseColor("#F76D2E");
            ((Button)findViewById(R.id.btn_trigMode)).setTextColor(orange);
            ((TextView)findViewById(R.id.textX)).setTextColor(orange);
            ((TextView)findViewById(R.id.textY)).setTextColor(orange);
            ((TextView)findViewById(R.id.textZ)).setTextColor(orange);
            Typeface face = Typeface.createFromAsset(getAssets(),"fonts/VT323-Regular.ttf");
            ((TextView)findViewById(R.id.textX)).setTypeface(face);
            ((TextView)findViewById(R.id.textY)).setTypeface(face);
            ((TextView)findViewById(R.id.textZ)).setTypeface(face);
            ((ImageButton)findViewById(R.id.btn_stack)).setColorFilter(orange);
            ((ImageButton)findViewById(R.id.btn_history)).setColorFilter(orange);
        }
    }
}

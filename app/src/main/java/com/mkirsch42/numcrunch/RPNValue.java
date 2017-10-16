package com.mkirsch42.numcrunch;

public class RPNValue implements RPNAction {
    private double val;
    private String s;
    private int argc = 1;

    public RPNValue(String s) {
        this(s, 0d);
    }

    public RPNValue(String s, double val) {
        this.s = s;
        this.val = val;
    }

    private RPNValue(RPNValue clone, int argc) {
        val = clone.get();
        s = clone.toString();
        this.argc = argc;
    }

    public RPNValue appender() {
        return new RPNValue(this, 0);
    }

    @Override
    public int argc() {
        return argc;
    }

    @Override
    public Term[] apply(Term[] in) {
        return new Term[]{new Term(val)};
    }

    public double get() {
        return val;
    }

    @Override
    public String toString() {
        return s;
    }

}

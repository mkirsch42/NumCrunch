package com.mkirsch42.numcrunch;
import java.util.Arrays;
import java.util.LinkedList;

public class Getter implements RPNAction {
    private int i;

    public Getter(int i) {
        this.i = i;
    }

    @Override
    public int argc() {
        return i+1;
    }

    @Override
    public Term[] apply(Term[] in) {
        LinkedList<Term> l = new LinkedList<>(Arrays.asList(in));
        l.add(0, in[i]);
        return l.toArray(new Term[]{});
    }

    @Override
    public String toString() {
        return "N."+i;
    }
}
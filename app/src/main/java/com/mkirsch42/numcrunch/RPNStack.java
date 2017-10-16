package com.mkirsch42.numcrunch;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Stream;

public class RPNStack {

    LinkedList<Term> stack;

    public RPNStack() {
        this(new LinkedList<>());
    }

    public RPNStack(LinkedList<Term> stack) {
        this.stack = stack;
    }

    public RPNStack(RPNStack clone) {
        stack = clone.getStack();
    }

    public void applyAction(RPNAction op) {
        stack.addAll(0, Arrays.asList(op.apply(Stream.generate(()->stack.size()==0?new Term("0"):stack.pop()).limit(op.argc()<0?stack.size()+1+op.argc():op.argc()).toArray(Term[]::new))));
        stack.removeIf(t->t.s.equals(""));
    }

    public LinkedList<Term> getStack() {
        return new LinkedList<>(stack);
    }

    public Term getX() {
        try {
            return stack.get(0);
        } catch(Exception e) {
            return new Term("0");
        }
    }

    public Term getY() {
        try {
            return stack.get(1);
        } catch(Exception e) {
            return new Term("0");
        }
    }

    public Term getZ() {
        try {
            return stack.get(2);
        } catch(Exception e) {
            return new Term("0");
        }
    }

    public void push(Term t) {
        stack.push(t);
    }



}

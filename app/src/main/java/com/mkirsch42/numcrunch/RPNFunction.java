package com.mkirsch42.numcrunch;

import java.util.Arrays;
import java.util.LinkedList;

public class RPNFunction implements RPNAction {

    LinkedList<RPNAction> ops;
    String name = "";


    public RPNFunction() {
        this(new LinkedList<>());
    }

    public RPNFunction(LinkedList<RPNAction> ops) {
        this.ops = ops;
    }

    public LinkedList<RPNAction> getOps() {
        return new LinkedList<>(ops);
    }

    public void addAction(RPNAction o) {
        ops.add(o);
    }

    public void execute(RPNStack in) {
        for(RPNAction o : ops) {
            in.applyAction(o);
        }
    }

    public RPNFunction getThrough(int i) {
        if(i == 0) {
            return new RPNFunction();
        }
        return new RPNFunction(new LinkedList<>(ops.subList(0, i+1)));
    }

    public void setName(String s) {
        name = s;
    }

    public int argc() {
        return -1;
    }

    public Term[] apply(Term[] in) {
        RPNStack s = new RPNStack(new LinkedList<>(Arrays.asList(in)));
        execute(s);
        return s.getStack().toArray(new Term[]{});
    }

    @Override
    public String toString() {
        return name;
    }
}

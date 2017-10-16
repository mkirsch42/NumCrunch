package com.mkirsch42.numcrunch;

import java.util.LinkedHashMap;

public class Functions {

    private LinkedHashMap<String, RPNFunction> vars;

    public Functions() {
        vars = new LinkedHashMap<>();
    }

    public boolean add(String s, RPNFunction f) {
        if(vars.containsKey(s)) {
            return false;
        }
        vars.put(s, f);
        return true;
    }

    public RPNFunction get(String s) {
        return vars.get(s);
    }

    public LinkedHashMap<String, RPNFunction> getFuns() {
        return new LinkedHashMap<>(vars);
    }

}

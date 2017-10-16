package com.mkirsch42.numcrunch;

import java.util.LinkedHashMap;

public class Variables {

    private LinkedHashMap<String, RPNValue> vars;

    public Variables() {
        vars = new LinkedHashMap<>();
    }

    public boolean add(String s) {
        if(vars.containsKey(s)) {
            return false;
        }
        set(s, 0);
        return true;
    }

    public void set(String s, double d) {
        vars.put(s, new RPNValue("V."+s, d));
    }

    public RPNValue get(String s) {
        return vars.get(s);
    }

    public Setter setter(String s) {
        return new Setter(s);
    }

    public LinkedHashMap<String, RPNValue> getVars() {
        return new LinkedHashMap<>(vars);
    }

    private class Setter implements RPNAction {

        private String s;

        public int argc() {
            return 1;
        }
        public Term[] apply(Term[] in) {
            set(s, in[0].n());
            return new Term[]{in[0]};
        }

        public Setter(String s) {
            this.s = s;
        }

        @Override
        public String toString() {
            return "S." + s;
        }
    }

}

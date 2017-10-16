package com.mkirsch42.numcrunch;

public interface RPNAction {
    Term[] apply(Term[] in);
    int argc();

    static RPNAction parse(String s) {
        String name = s.substring(2);
        switch(s.charAt(0)) {
            case 'O':
                return Operation.valueOf(name);
            case 'V':
                return MainActivity.getCurrent().getVars().get(name);
            case 'C':
                return Constants.valueOf(name).get();
            case 'S':
                return MainActivity.getCurrent().getVars().setter(name);
            case 'F':
                return MainActivity.getCurrent().getFuns().get(name);
            case 'N':
                return new Getter(Integer.parseInt(name));
        }
        throw new IllegalArgumentException("Invalid action prefix: " + s.charAt(0));
    }
}

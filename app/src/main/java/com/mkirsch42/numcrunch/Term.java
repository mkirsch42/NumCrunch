package com.mkirsch42.numcrunch;

public class Term {

    public final boolean appendable;

    public final String s;

    public Term(String s) {
        this(s, false);
    }

    public Term(double d) {
        this(""+d, false);
    }

    public Term(Term t) {
        appendable = t.appendable;
        s = t.s;
    }

    public Term(String s, boolean a) {
        this.s = s;
        appendable = a;
    }

    public Term a(String digit) {
        if(appendable) {
            try {
                Double.parseDouble(s + digit);
                return new Term(s + digit, true);
            } catch(Exception e) {
                return new Term(s, true);
            }
        } else {
            return new Term(digit, true);
        }
    }

    public double n() {
        try {
            return Double.parseDouble(s);
        } catch(NumberFormatException e) {
            return Double.parseDouble(s + "0");
        }
    }

    public Term neg() {
        return new Term(("-"+s).replaceFirst("--", ""), appendable);
    }

    public Term bspace() {
        if(appendable) {
            return new Term(s.substring(0, s.length()-1), true);
        } else {
            if(s.equals("0")) {
                return new Term("");
            }
            return new Term("0");
        }
    }

    @Override
    public String toString() {
        if(appendable) {
            return s;
        }
        String format = String.format("%,.10G", n());
        String part1 = format.split("E")[0].replaceFirst("(\\.\\d*?)0*$", "$1").replaceAll("\\.$", "");
        String part2;
        try {
            part2 = "E" + format.split("E")[1];
        } catch(Exception e) {
            part2 = "";
        }
        return (part1 + part2).replaceAll("(?i)infinity", "∞").replaceAll("(?i)nan", "NaN");
    }

}

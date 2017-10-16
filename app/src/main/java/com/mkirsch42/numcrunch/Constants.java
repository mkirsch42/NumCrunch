package com.mkirsch42.numcrunch;

public enum Constants {

    PI(Math.PI),
    PHI((1+Math.sqrt(5))/2),
    EPFS(8.854187817E-12),
    MPFS(Math.PI*4E-7),
    G(6.67408E-11),
    E(Math.E),
    CHARGE(1.60217662E-19),
    C(2.998E8),
    H(6.626E-34);

    private final RPNValue d;
    Constants(double d){
        this.d = new RPNValue("C."+name(), d);
    }

    public RPNValue get() {
        return d;
    }

}

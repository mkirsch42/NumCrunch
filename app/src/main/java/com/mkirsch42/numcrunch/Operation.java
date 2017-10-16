package com.mkirsch42.numcrunch;

import org.apache.commons.math3.special.Gamma;
import org.apache.commons.math3.util.ArithmeticUtils;

import java.util.function.Function;

public enum Operation implements RPNAction {
    NUM_1(1, s->new Term[]{s[0].a("1")}),
    NUM_2(1, s->new Term[]{s[0].a("2")}),
    NUM_3(1, s->new Term[]{s[0].a("3")}),
    NUM_4(1, s->new Term[]{s[0].a("4")}),
    NUM_5(1, s->new Term[]{s[0].a("5")}),
    NUM_6(1, s->new Term[]{s[0].a("6")}),
    NUM_7(1, s->new Term[]{s[0].a("7")}),
    NUM_8(1, s->new Term[]{s[0].a("8")}),
    NUM_9(1, s->new Term[]{s[0].a("9")}),
    NUM_0(1, s->new Term[]{s[0].a("0")}),
    ENTER(1, s->new Term[]{new Term(s[0].s, false), new Term(s[0].s, false)}),
    DECIMAL(1, s->new Term[]{s[0].a(".")}),

    PLUS(2, s->new Term[]{new Term(s[0].n() + s[1].n())}),
    MINUS(2, s->new Term[]{new Term(s[1].n() - s[0].n())}),
    MULT(2, s->new Term[]{new Term(s[0].n() * s[1].n())}),
    DIVIDE(2, s->new Term[]{new Term(s[1].n() / s[0].n())}),
    MOD(2, s->new Term[]{new Term(s[1].n() % s[0].n())}),
    NEG(1, s->new Term[]{s[0].neg()}),
    DEL(2, s->new Term[]{new Term(s[1].s, false)}),
    BSPC(1, s->new Term[]{s[0].bspace()}),

    SIN(1, s->new Term[]{new Term(Math.sin(RPNUtil.trigIn(s[0].n())))}),
    COS(1, s->new Term[]{new Term(Math.cos(RPNUtil.trigIn(s[0].n())))}),
    TAN(1, s->new Term[]{new Term(Math.tan(RPNUtil.trigIn(s[0].n())))}),
    SQRT(1, s->new Term[]{new Term(Math.sqrt(s[0].n()))}),
    LN(1, s->new Term[]{new Term(Math.log(s[0].n()))}),
    LOG(1, s->new Term[]{new Term(Math.log10(s[0].n()))}),
    EXP(2, s->new Term[]{new Term(Math.pow(s[1].n(), s[0].n()))}),

    ARCSIN(1, s->new Term[]{new Term(RPNUtil.trigOut(Math.asin(s[0].n())))}),
    ARCCOS(1, s->new Term[]{new Term(RPNUtil.trigOut(Math.acos(s[0].n())))}),
    ARCTAN(1, s->new Term[]{new Term(RPNUtil.trigOut(Math.atan(s[0].n())))}),
    FAC(1, s->new Term[]{new Term(Gamma.gamma(s[0].n() + 1))}),
    GMA(1, s->new Term[]{new Term(Gamma.gamma(s[0].n()))}),
    E10(2, s->new Term[]{new Term(s[1].n()*Math.pow(10,s[0].n()))}),
    SWAP(2, s->new Term[]{new Term(s[1].s, false), new Term(s[0].s, false)}),
    R_P(2, s->new Term[]{new Term(Math.hypot(s[0].n(), s[1].n())), new Term(Math.atan2(s[1].n(), s[0].n()))}),
    P_R(2, s->new Term[]{new Term(s[0].n()*Math.cos(s[1].n())), new Term(s[0].n()*Math.sin(s[1].n()))}),
    GCD(2, s->new Term[]{new Term(ArithmeticUtils.gcd((long)s[0].n(), (long)s[1].n()))}),
    RND(1, s->new Term[]{new Term(Math.round(s[0].n()))}),
    FLOOR(1, s->new Term[]{new Term((long)s[0].n())});

    @Override
    public int argc() {
        return argc;
    }

    @Override
    public Term[] apply(Term[] in) {
        return fn.apply(in);
    }

    private final int argc;
    private Function<Term[], Term[]> fn;
    Operation(int argc, Function<Term[], Term[]> fn) {
        this.argc = argc;
        this.fn = fn;
    }

    @Override
    public String toString() {
        return "O." + name();
    }
}

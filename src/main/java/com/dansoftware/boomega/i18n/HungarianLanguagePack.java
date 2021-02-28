package com.dansoftware.boomega.i18n;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

/**
 * A {@link HungarianLanguagePack} is a {@link LanguagePack} that provides translation for the Hungarian language.
 *
 * @author Daniel Gyorffy
 */
public class HungarianLanguagePack extends InternalLanguagePack {

    private static final Locale LOCALE = new Locale("hu", "HU");

    static {
        registerLanguagePack(LOCALE, new HungarianLanguagePack());
    }

    public HungarianLanguagePack() {
        super(LOCALE);
    }

    private Collator buildAbcCollator() {
        try {
            return new RuleBasedCollator(
                    " < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS" +
                            " < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J" +
                            " < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó" +
                            " < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T" +
                            " < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS");
        } catch (ParseException e) {
            return null;
        }
    }

    @Override
    protected Collator getABCCollator() {
        return buildAbcCollator();
    }

    @Override
    protected boolean isRTL() {
        return false;
    }
}
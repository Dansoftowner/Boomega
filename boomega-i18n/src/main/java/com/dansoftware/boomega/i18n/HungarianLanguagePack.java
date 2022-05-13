/*
 * Boomega - A modern book explorer & catalog application
 * Copyright (C) 2020-2022  Daniel Gyoerffy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.dansoftware.boomega.i18n;

import com.dansoftware.boomega.i18n.api.LanguagePack;
import com.dansoftware.boomega.util.Person;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.dansoftware.boomega.i18n.DefaultResourceBundle.DEFAULT_RESOURCE_BUNDLE_NAME;

/**
 * A {@link HungarianLanguagePack} is a {@link LanguagePack} that provides translation for the Hungarian language.
 *
 * @author Daniel Gyorffy
 */
public class HungarianLanguagePack extends LanguagePack {

    private static final Locale LOCALE = new Locale("hu", "HU");

    public HungarianLanguagePack() {
        super(LOCALE);
    }

    private Collator buildAbcCollator() {
        try {
            return new NullHandlingCollator(new ABCCollator());
        } catch (ParseException e) {
            return Collator.getInstance(LOCALE);
        }
    }

    @Nullable
    @Override
    public Person getTranslator() {
        return new Person("Györffy", "Dániel", "dansoftwareowner@gmail.com");
    }

    @Override
    public @NotNull ResourceBundle getValues() {
        return ResourceBundle.getBundle(DEFAULT_RESOURCE_BUNDLE_NAME);
    }

    @Override
    public @NotNull Collator getABCCollator() {
        return buildAbcCollator();
    }

    @Override
    protected boolean isRTL() {
        return false;
    }

    @Override
    public @NotNull String displayPersonName(@Nullable String firstName, @Nullable String lastName) {
        return String.format("%s %s", lastName, firstName);
    }

    private static final class ABCCollator extends RuleBasedCollator {
        ABCCollator() throws ParseException {
            super(
                    """
                            < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS \
                            < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J \
                            < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó \
                            < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T \
                            < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS\
                            """
            );
        }
    }
}

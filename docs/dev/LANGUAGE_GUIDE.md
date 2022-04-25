# Adding custom languages to Boomega

This page will guide you how to **contribute languages** to the app, and also gives you a basis for developing
**language-plugins**.

## Prerequisites

* Understand the concept
  of [ResourceBundle](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ResourceBundle.html)s in
  java (this article might help you in [this](https://www.baeldung.com/java-resourcebundle))

**These apply to you only if you're contributing to the core project:**

* Make sure you've read the [contribution guideline](/CONTRIBUTING.md)
* Work in the [`boomega-i18n`](/boomega-i18n) subproject

## Property files

Firstly, create your own `.properties` file that contains the translations. View
the [default resource file](/boomega-i18n/src/main/resources/com/dansoftware/boomega/i18n/Values.properties) to have an
idea.

**These apply to you only if you're contributing to the core project:**

* Name your property files according to this pattern: `Values<postfix>.properties`
* Make sure you add the locale-specific postfix to your file (e.g `Values_en_US.properties`)
* Place your [property files](#property-files) to
  the [`resources/com/dansoftware/boomega/i18n`](/boomega-i18n/src/main/resources/com/dansoftware/boomega/i18n)
  directory

## The `LanguagePack` class

After you've created the properties-file, you have to create
your [LanguagePack](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n/api/LanguagePack.java). A `LanguagePack` in
Boomega provides the `ResourceBundle` (representing the .properties file)
and other things needed for defining a language.

A simple example:

<table>

<tr>
<th>Kotlin</th>
<th>Java</th>
</tr>

<tr>

<td>

```kotlin
class PortugueseLanguagePack : LanguagePack(Locale("pt"), AUTHOR) {

    // The com/dansoftware/boomega/i18n/MyValues_pt.properties file
    override fun getValues(): ResourceBundle = super.getBundle("com.dansoftware.boomega.i18n.MyValues")

    override fun isRTL(): Boolean = false // Portuguese is not a right-to-left language

    companion object {
        // represents the person who translated the language
        private val AUTHOR = Person(
            lastName = "LastName",
            firstName = "FirstName",
            email = "myemail@example.com"
        )
    }
}
```

</td>

<td>

```java
public class PortugueseLanguagePack extends LanguagePack {

    // the Locale representing the language we want to translate to (in this case Portuguese)
    private static final Locale LOCALE = new Locale("pt");

    // represents the person who translated the language
    private static final Person AUTHOR = new Person("LastName", "FirstName", "myemail@example.com");

    protected PortugueseLanguagePack() {
        super(LOCALE, AUTHOR);
    }

    @Override
    public @NotNull
    ResourceBundle getValues() {
        //The com/dansoftware/boomega/i18n/MyValues_pt.properties file
        return super.getBundle("com.dansoftware.boomega.i18n.MyValues");
    }

    @Override
    protected boolean isRTL() {
        return false; // Portuguese is not a right-to-left language
    }
}
```

</td>

</tr>
</table>

**This applies to you only if you're contributing to the core project:**

* Locate your LanguagePack to
  the [`com.dansofware.boomega.i18n`](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n) package

### Specifying the alphabetical order

Knowing the alphabetical order for Boomega is crucial for several features (e.g. sorting records in a table-view).  
Defining `ABC`s in Java is possible with the help of
[Collators](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/Collator.html). By overriding
the `LanguagePack.getABCCollator()` method you can specify the `Collator` for your language-pack.  
*If you don't specify any collator for your pack, the default collator will be used which is `Collator.getInstance()`.*

Look at this simplified snippet from the
internal [HungarianLanguagePack](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n/HungarianLanguagePack.java):

```java
public class HungarianLanguagePack extends LanguagePack {

  ...

    @Override
    public @NotNull
    Collator getABCCollator() {
        return new NullHandlingCollator(new ABCCollator());
    }

  ...

    private static final class ABCCollator extends RuleBasedCollator {
        ABCCollator() throws ParseException {
            super("""
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
```

Notice that it wraps the base collator into
a [NullHandlingCollator](/boomega-i18n/src/main/kotlin/com/dansoftware/boomega/i18n/NullHandlingCollator.kt)
for preventing possible null-pointer exceptions when comparing `null` values with the collator in the future. You should
also follow this practice with your own collator.

### Registering your language-pack

**This page applies to you only if you're contributing to the core project.**

After you've created your pack implementation you have to register it in
the [internal_lang_packs.json](/boomega-i18n/src/main/resources/com/dansoftware/boomega/i18n/api/internal_lang_packs.json)
config file.

Like this:

```json
{
  ...,
  "classNames": [
    ...
    "com.dansofware.boomega.i18n.PortugueseLanguagePack"
  ]
}
```

#### Other examples

You can view the internal LanguagePack implementations (for understanding the concepts better)
in the [`com.dansoftware.boomega.i18n`](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n) package e.g:

* [EnglishLanguagePack](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n/EnglishLanguagePack.java)
* [HungarianLanguagePack](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n/HungarianLanguagePack.java)
* [TurkishLanguagePack](/boomega-i18n/src/main/java/com/dansoftware/boomega/i18n/TurkishLanguagePack.java)

---

Now, if you want to develop a **language-plugin** with this knowledge, check out
the [plugin guide](../PLUGIN_GUIDE.md#language-plugins) for further instructions.
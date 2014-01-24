package org.unicode.cldr.unittest;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.unicode.cldr.unittest.TestAll.TestInfo;
import org.unicode.cldr.util.SupplementalDataInfo;
import org.unicode.cldr.util.SupplementalDataInfo.PluralInfo;
import org.unicode.cldr.util.SupplementalDataInfo.PluralType;

import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.PluralRules.SampleType;

public class TestPluralRuleGeneration extends TestFmwkPlus {
    public static void main(String[] args) {
        new TestPluralRuleGeneration().run(args);
    }

    public void TestGeneration() {
        /*
        <pluralRules locales="am bn fa gu hi kn mr zu">
            <pluralRule count="one">i = 0 or n = 1 @integer 0, 1 @decimal 0.0~1.0, 0.00~0.04</pluralRule>
            <pluralRule count="other"> @integer 2~17, 100, 1000, 10000, 100000, 1000000, … @decimal 1.1~2.6, 10.0, 100.0, 1000.0, 10000.0, 100000.0, 1000000.0, …</pluralRule>
        </pluralRules>
         */
        PluralRules rules = PluralRules.createRules("one:i = 0 or n = 1");
        boolean i = rules.computeLimited("one", SampleType.INTEGER);
        boolean d = rules.computeLimited("one", SampleType.DECIMAL);
        System.out.println(i + ", " + d);
    }

    public void TestAtoms() {
        TestInfo testInfo = TestAll.TestInfo.getInstance();
        SupplementalDataInfo supp = testInfo.getSupplementalDataInfo();
        Set<PluralRules> seen = new HashSet();
        Set<String> atoms = new TreeSet();
        Set<String> limitedSet = new TreeSet();
        for (PluralType type : SupplementalDataInfo.PluralType.values()) {
            for (String locale : supp.getPluralLocales(type)) {
                PluralInfo info = supp.getPlurals(locale, false);
                if (info == null) {
                    continue;
                }
                PluralRules rules = info.getPluralRules();
                if (seen.contains(rules)) {
                    continue;
                }
                seen.add(rules);
                for (String keyword : rules.getKeywords()) {
                    String rulesString = rules.getRules(keyword);
                    if (rulesString.isEmpty()) {
                        continue;
                    }

                    for (String orPart : rulesString.split("\\s*or\\s*")) {
                        for (String andPart : orPart.split("\\s*and\\s*")) {
                            PluralRules rules2 = PluralRules.createRules("one: " + andPart);
                            boolean i = rules.computeLimited("one", SampleType.INTEGER);
                            boolean d = rules.computeLimited("one", SampleType.DECIMAL);
                            limitedSet.add(
                                (i ? "in" : "i∞")
                                + " " + (d ? "dn" : "d∞")
                                + " : " + andPart);
                        }
                    }
                }
            }
        }
        for (String item : limitedSet) {
            System.out.println(item);
        }

    }
}
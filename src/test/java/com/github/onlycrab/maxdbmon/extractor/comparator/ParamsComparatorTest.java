package com.github.onlycrab.maxdbmon.extractor.comparator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link ParamsComparator}.
 *
 * @author Roman Rynkovich
 * @version 0.9
 */
public class ParamsComparatorTest extends ParamsComparator{
    /**
     * Test for {@link ParamsComparator#getDifference()}.
     */
    @Test
    public void getDifferenceTest(){
        ParamsComparator comparator = new ParamsComparator();
        comparator.previous.put("description", "value");
        comparator.previous.put("maxlogwritertasks", "0");
        comparator.previous.put("_max_backup_tasks", "0");
        comparator.previous.put("kernelversion", "kernel 7.9.08 build 008-123-247-140");

        comparator.current.put("description", "value");
        comparator.current.put("maxlogwritertasks", "0");
        comparator.current.put("_max_backup_tasks", "0");
        comparator.current.put("kernelversion", "kernel 7.9.08 build 008-123-247-140");

        //same pairs
        Assert.assertEquals("", comparator.getDifference());

        //change values
        comparator.current.put("maxlogwritertasks", "22");
        Assert.assertEquals(printDifference("maxlogwritertasks", "0", "22"), comparator.getDifference());
        comparator.current.put("kernelversion", "kernel  7.9.08 build 008-123-247-140");
        Assert.assertEquals(printDifference("maxlogwritertasks", "0", "22") +
                printDifference("kernelversion", "kernel 7.9.08 build 008-123-247-140",
                        "kernel  7.9.08 build 008-123-247-140"), comparator.getDifference());

        //back same values
        comparator.current.put("maxlogwritertasks", "0");
        comparator.current.put("kernelversion", "kernel 7.9.08 build 008-123-247-140");

        //key-value pair exist only in previous or current data map
        comparator.previous.put("enablevariableoutput", "0");
        Assert.assertEquals("", comparator.getDifference());
        comparator.current.put("enablevariableinput", "1");
        Assert.assertEquals("", comparator.getDifference());
    }
}

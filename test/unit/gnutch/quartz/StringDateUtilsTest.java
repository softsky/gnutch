package gnutch.quartz;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for quartz-app.
 */
public class StringDateUtilsTest
    extends TestCase
{

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( StringDateUtilsTest.class );
    }

    /**
     *
     */
    public void testStringDateUtils()
    {
        try { // null argument
            StringDateUtils.fromStringToIntervalMs(null);
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // negative value
            StringDateUtils.fromStringToIntervalMs("-1 second");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // missing unit
            StringDateUtils.fromStringToIntervalMs("0");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // wrong delimiter
            StringDateUtils.fromStringToIntervalMs("0 second, 1 minute, 5 hour");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // number > max Long, parsing error
            StringDateUtils.fromStringToIntervalMs(String.valueOf(Long.MAX_VALUE) + "1 minute");
            fail();
        } catch (NumberFormatException e) {
            // Note that NumberFormatException derives from IllegalArgumentException
        }
        try { // number = max Long, overflow in multiplication
            StringDateUtils.fromStringToIntervalMs(String.valueOf(Long.MAX_VALUE) + " second");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // sum > max Long, overflow in accumulation
            StringDateUtils.fromStringToIntervalMs(String.valueOf(Long.MAX_VALUE) + " second + 1 second");
            fail();
        } catch (IllegalArgumentException e) {
        }
        try { // maximum number of groups (14) exceeded
            StringDateUtils.fromStringToIntervalMs("0 second 1 minute 5 hour 0 second 1 minute 5 hour 0 second 1 minute 5 hour 0 second 1 minute 5 hour 0 second 1 minute 5 hour");
            fail();
        } catch (IllegalArgumentException e) {
        }
        // maximum number of groups (14) processed correctly
        assertTrue( (5l * 18060000l) == StringDateUtils.fromStringToIntervalMs("0 second 1 minute 5 hour 0 second 1 minute 5 hour 0 second 1 minute 5 hour 0 second 1 minute 5 hour 1 minute 5 hour") );
        //
        assertTrue( StringDateUtils.fromStringToIntervalMs("") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 second") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 minute") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 hour") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 day") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 week") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 month") == 0 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 year") == 0 );
        //
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 second") == 1000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 minute") == 60000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 hour") == 3600000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 day") == 24L * 3600000L );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 week") == 7L * 24L * 3600000L );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 month") == 30L * 24L * 3600000L );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 year") == 12L * 30L * 24L * 3600000L );
        //
        assertTrue( StringDateUtils.fromStringToIntervalMs("0 second 1 minute 5 hour") == 18060000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 minute 5 hour 0 second") == 18060000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("5 hour 0 second 1 minute") == 18060000 );
        assertTrue( StringDateUtils.fromStringToIntervalMs("5 hour 1 minute 0 second") == 18060000 );
        // repeated groups
        assertTrue( StringDateUtils.fromStringToIntervalMs("1 minute 1 minute") == 120000 );
    }
}

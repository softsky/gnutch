package gnutch.quartz;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class to perform conversion string literal => milliseconds time. <br/>
 * <br/>
 * @author rumen.dimov.mail@gmail.com
 * @version %I%, %G%
 */
public class StringDateUtils {

    /** Maximum number of entries in the following pattern */
    private static int MAX_GROUPS = 14;
    private static String INTERVAL_PATTERN_AS_STRONG = 
    	"(\\s*\\d+\\s*(?:seconds?|minutes?|hours?|days?|weeks?|months?|years?))+";
    private static Pattern INTERVAL_PATTERN =
        Pattern.compile(INTERVAL_PATTERN_AS_STRONG);
	
    /**
     * Converts string literal => milliseconds time.<br>
     * <br>
     * Samples:<br>
     * - 5 minutes<br>
     * - 1 hour 5 minutes 10 seconds<br>
     * - 2 months<br>
     * <br>
     * Available words are second(s) minute(s) hour(s) day(s) week(s) month(s) year(s).<br>
     * To simplify the task we won't make difference between correct word end.<br>
     * So these strings are equal: 5 hours 10 minutes 2 seconds = 5 hour 10 minute 2 second, however we should recognize words with both w/ and w/o `s` end. 
     */
    public static Long fromStringToIntervalMs(String str) {
        if(str == null) {
            throw new IllegalArgumentException("Null argument");
        }
        // Normalize space and convert to lower-case before parsing
        return sum((long)0, str.trim().replaceAll("\\s+", " ").toLowerCase(), 0);
    }
	
    private static Long sum(long v1, String v2, int invokationNumber) {
        if(v2.length() > 0) {
            return sum(v1, v2, INTERVAL_PATTERN.matcher(v2), invokationNumber);
        }
        return v1;
    }

    private static Long sum(long v1, String v2, Matcher m, int invokationNumber) {
        if(m.matches() && invokationNumber < MAX_GROUPS) { 
            // The Matcher captures the last occurrence of the group, so
            // cut it and continue with the preceding part of the string
            return sum( addSafe(v1, toMillis( m.group(1).trim().replaceFirst("(.*)s$", "$1").split("\\s+")))
                        , v2.substring(0, v2.length() - m.group(1).length()).trim()
                        , 1 + invokationNumber); 
        } else {
            throw new IllegalArgumentException("Invalid syntax. Please, follow this regex: " + INTERVAL_PATTERN_AS_STRONG);
        }
    }

    /**
     * 
     * @param time { number, unit }
     * @return
     */
    private static long toMillis(String[] time) {
        //		System.out.println(time[0] + " " + time[1]);
        return multiplySafe( Long.parseLong(time[0]),
                             "second".equals(time[1])?1000L:
                             "minute".equals(time[1])?60L * 1000L:
                             "hour".equals(time[1])?60L * 60L * 1000L:
                             "day".equals(time[1])?24L * 60L * 60L * 1000L:
                             "week".equals(time[1])?7L * 24L * 60L * 60L * 1000L:
                             // ambiguous, months have various lengths
                             "month".equals(time[1])?30L * 24L * 60L * 60L * 1000L:
                             // ambiguous, years have various lengths
                             "year".equals(time[1])?12L * 30L * 24L * 60L * 60L * 1000L:
                             1); // this will never be reached because of all the previous validations
    }

    /**
     * Checks for and overflow before multiplication
     * 
     * @param time
     * @param factor
     * @return
     */
    private static long multiplySafe(long time, long factor) {
        if(time != 0 && factor != 1) {
            if(time > Long.MAX_VALUE / factor) {
                throw new IllegalArgumentException("Overflow: (" + time + " * " + factor + ") > " + Long.MAX_VALUE);
            }
            return time * factor;
        }
        return time;
    }

    /**
     * Checks for and overflow before addition
     * 
     * @param v1
     * @param v2
     * @return
     */
    private static long addSafe(long v1, long v2) {
    	if(v1 > Long.MAX_VALUE - v2) {
            throw new IllegalArgumentException("Overflow: (" + v1 + " + " + v2 + ") > " + Long.MAX_VALUE);
    	}
    	return  v1 + v2;
    }
}

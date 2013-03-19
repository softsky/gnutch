package gnutch.quartz;

/**
 * Simple interface with a single method onTimeout, which is triggered upon scheduled event.
 * 
 * @author rumen.dimov.mail@gmail.com
 * @version %I%, %G%
 */
public interface TimeoutListener {
    public void onTimeout(String key);
}

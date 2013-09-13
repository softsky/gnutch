package gnutch.quartz;

/**
 * onTimeout is triggered upon scheduled event.
 *
 * @author rumen.dimov.mail@gmail.com
 * @version %I%, %G%
 */
public interface TimeoutListener {
    void onTimeout(String key);
}

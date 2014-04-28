package gnutch.urls;

import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.concurrent.atomic.AtomicReference;

public class PatternService {
    AtomicReference<List<Pattern>> allowedPatternList = new AtomicReference<List<Pattern>>(new LinkedList<Pattern>());
    AtomicReference<List<Pattern>> ignoredPatternList = new AtomicReference<List<Pattern>>(new LinkedList<Pattern>());

    public List<Pattern> getAllowedPatterns() {
        return allowedPatternList.get();
    }

    public List<Pattern> getIgnoredPatterns() {
        return ignoredPatternList.get();
    }

    public synchronized void addAllowedPattern(Pattern p) {
        List<Pattern> newPatterns = new LinkedList<Pattern>(allowedPatternList.get());
        newPatterns.add(p);
        allowedPatternList.set(Collections.unmodifiableList(newPatterns)); // or ImmutableList
    }

    public synchronized void addIgnoredPattern(Pattern p) {
        List<Pattern> newPatterns = new LinkedList<Pattern>(ignoredPatternList.get());
        newPatterns.add(p);
        ignoredPatternList.set(Collections.unmodifiableList(newPatterns)); // or ImmutableList
    }
}

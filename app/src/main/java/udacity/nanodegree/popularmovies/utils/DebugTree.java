package udacity.nanodegree.popularmovies.utils;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class DebugTree extends Timber.DebugTree {

    private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
    private final String mTag;

    public DebugTree(String tag) {

        mTag = tag;
    }

    private static String createPrefix() {

        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if (stackTrace.length < 7) {
            throw new IllegalStateException(
                    "Synthetic stacktrace didn't have enough elements: are you using proguard?");
        }

        String className = stackTrace[6].getClassName();
        final Matcher m = ANONYMOUS_CLASS.matcher(className);
        if (m.find()) {
            className = m.replaceAll("");
        }
        className = className.substring(className.lastIndexOf('.') + 1);

        final String fileName = stackTrace[6].getFileName();
        final String methodName = stackTrace[6].getMethodName();
        final int lineNumber = stackTrace[6].getLineNumber();

        return String.format(Locale.ENGLISH, "%s.%s(%s:%,d)", className, methodName, fileName, lineNumber);
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        final String prefix = createPrefix();
        message = String.format(Locale.ENGLISH, "%s   %s", prefix, message);

        super.log(priority, mTag, message, t);
    }
}

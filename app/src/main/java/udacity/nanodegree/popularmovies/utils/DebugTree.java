package udacity.nanodegree.popularmovies.utils;

import android.os.Looper;

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

        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(className);
        stringBuilder.append('.');
        stringBuilder.append(methodName);

        if (Looper.myLooper() != Looper.getMainLooper()) {
            stringBuilder.append(" [");
            stringBuilder.append(Thread.currentThread().getName());
            stringBuilder.append(']');
        }

        stringBuilder.append(" (");
        stringBuilder.append(fileName);
        stringBuilder.append(':');
        stringBuilder.append(lineNumber);
        stringBuilder.append(')');

        return stringBuilder.toString();
    }

    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        final String prefix = createPrefix();
        message = new StringBuilder()
            .append(prefix)
            .append("  ")
            .append(message)
            .toString();

        super.log(priority, mTag, message, t);
    }
}

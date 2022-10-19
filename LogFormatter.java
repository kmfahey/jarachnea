package jarachnea;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    @Override
    public String format(final LogRecord record) {
        return "[" + new Date(record.getMillis()) + "] {" + record.getThreadID() + "} " + record.getLevel().getName() + ": " + record.getMessage() + "\n";
    }
}

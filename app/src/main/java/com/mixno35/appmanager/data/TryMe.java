package com.mixno35.appmanager.data;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

public class TryMe {

    public String getStackTrace(Throwable th) {
        final Writer result = new StringWriter();

        final PrintWriter printWriter = new PrintWriter(result);
        Throwable cause = th;

        while(cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        final String stacktraceAsString = result.toString();
        printWriter.close();

        return stacktraceAsString;
    }
}

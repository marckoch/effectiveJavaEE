package com.airhacks.doit.business.logging.boundary;

/**
 *
 * @author marckoch
 */
@FunctionalInterface
public interface LogSink {
    void log(String msg);
}

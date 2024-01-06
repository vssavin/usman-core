package com.github.vssavin.usmancore.config;

/**
 * An interface for notifying that argument processing has completed.
 *
 * @author vssavin on 21.12.2023.
 */
public interface ArgumentsProcessedNotifier {

    void notifyArgumentsProcessed(Class<?> aClass);

}

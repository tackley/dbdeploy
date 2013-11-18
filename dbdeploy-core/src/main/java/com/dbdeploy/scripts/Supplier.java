package com.dbdeploy.scripts;

/**
 * Delayed instantiation of T
 * @param <T> the thing to supply
 * @author alanraison
 */
public interface Supplier<T> {
    /**
     * @return a &lt;T&gt;
     */
    T get() throws Exception;
}

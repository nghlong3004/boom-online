package vn.nghlong3004.boom.online.client.util;

/**
 * Project: boom-online-client
 *
 * @author nghlong3004
 * @since 1/7/2026
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {
    void accept(T t, U u, V v);
}

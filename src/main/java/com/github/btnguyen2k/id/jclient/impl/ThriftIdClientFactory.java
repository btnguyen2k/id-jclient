package com.github.btnguyen2k.id.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link ThriftIdClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftIdClientFactory {
    private static LoadingCache<String, ThriftIdClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, ThriftIdClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, ThriftIdClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, ThriftIdClient>() {
                @Override
                public ThriftIdClient load(String hostAndPort) throws Exception {
                    ThriftIdClient idClient = new ThriftIdClient(hostAndPort);
                    idClient.init();
                    return idClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link ThriftIdClient} instance.
     * 
     * @param hostAndPort
     *            Thrift host & port of id-server, format {@code host:port}
     * @return
     */
    public static ThriftIdClient newIdClient(String hostAndPort) {
        try {
            return cache.get(hostAndPort);
        } catch (ExecutionException e) {
            return null;
        }
    }

    /**
     * Helper method to create a new {@link ThriftIdClient} instance.
     * 
     * @param idServerHost
     *            Thrift host of id-server
     * @param idServerPort
     *            Thrift port of id-server
     * @return
     */
    public static ThriftIdClient newIdClient(String idServerHost, int idServerPort) {
        return newIdClient(idServerHost + ":" + idServerPort);
    }
}

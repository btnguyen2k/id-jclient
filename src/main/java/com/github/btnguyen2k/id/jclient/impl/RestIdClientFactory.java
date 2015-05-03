package com.github.btnguyen2k.id.jclient.impl;

import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

/**
 * Factory to create {@link RestIdClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestIdClientFactory {
    private static LoadingCache<String, RestIdClient> cache = CacheBuilder.newBuilder()
            .removalListener(new RemovalListener<String, RestIdClient>() {
                @Override
                public void onRemoval(RemovalNotification<String, RestIdClient> notification) {
                    notification.getValue().destroy();
                }
            }).build(new CacheLoader<String, RestIdClient>() {
                @Override
                public RestIdClient load(String idServerUrl) throws Exception {
                    RestIdClient idClient = new RestIdClient();
                    idClient.setIdServerUrl(idServerUrl).init();
                    return idClient;
                }
            });

    public static void cleanup() {
        cache.invalidateAll();
    }

    /**
     * Helper method to create a new {@link RestIdClient} instance.
     * 
     * @param idServerUrl
     * @return
     */
    public static RestIdClient newIdClient(String idServerUrl) {
        try {
            return cache.get(idServerUrl);
        } catch (ExecutionException e) {
            return null;
        }
    }
}

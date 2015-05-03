package com.github.btnguyen2k.id.jclient;

/**
 * Client API to interact with id-server.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public interface IIdClient {
    /**
     * Generates next ID for a namespace, using default engine.
     * 
     * @param namespace
     * @return
     */
    public long nextId(String namespace);

    /**
     * Generates next ID for a namespace, using specified engine.
     * 
     * @param namespace
     * @param engine
     * @return
     */
    public long nextId(String namespace, EngineType engine);
}

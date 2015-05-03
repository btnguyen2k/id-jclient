package com.github.btnguyen2k.id.jclient.impl;

/**
 * Abstract implementation of {@link IIdClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
import com.github.btnguyen2k.id.jclient.IIdClient;

public abstract class AbstractIdClient implements IIdClient {

    public AbstractIdClient init() {
        return this;
    }

    public void destroy() {
    }

}

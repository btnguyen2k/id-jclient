package com.github.ddth.id.jclient.thrift;

import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.github.btnguyen2k.idserver.thrift.TIdService;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Factory to create id-server Thrift clients.
 * 
 * @author ThanhNB
 * @since 0.1.0
 */
public class ThriftClientFactory {

    /**
     * Helper method to create a new {@link ITProtocolFactory} for id-server
     * Thrift client.
     * 
     * @param host
     * @param port
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String host, final int port) {
        ITProtocolFactory protocolFactory = new ITProtocolFactory() {
            @Override
            public TProtocol create() {
                TSocket socket = new TSocket(host, port);
                socket.setTimeout(10000);
                TTransport transport = new TFramedTransport(socket);
                TProtocol protocol = new TCompactProtocol(transport);
                return protocol;
            }
        };
        return protocolFactory;
    }

    /**
     * Helper method to create a new client-pool with default settings.
     * 
     * @param host
     * @param port
     * @return
     */
    public static ThriftClientPool<TIdService.Client, TIdService.Iface> newDefaultClientPool(
            final String host, final int port) {
        final ThriftClientPool<TIdService.Client, TIdService.Iface> pool = new ThriftClientPool<TIdService.Client, TIdService.Iface>();
        pool.setClientClass(TIdService.Client.class).setClientInterface(TIdService.Iface.class);
        pool.setTProtocolFactory(protocolFactory(host, port));
        pool.setPoolConfig(new PoolConfig().setMaxActive(32).setMaxWaitTime(10000));
        pool.init();
        return pool;
    }
}

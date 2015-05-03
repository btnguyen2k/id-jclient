package com.github.btnguyen2k.id.jclient.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.id.jclient.EngineType;
import com.github.btnguyen2k.id.jclient.IIdClient;
import com.github.btnguyen2k.idserver.thrift.TIdResponse;
import com.github.btnguyen2k.idserver.thrift.TIdService;
import com.github.ddth.thriftpool.ITProtocolFactory;
import com.github.ddth.thriftpool.PoolConfig;
import com.github.ddth.thriftpool.ThriftClientPool;

/**
 * Thrift-implementation of {@link IIdClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class ThriftIdClient extends AbstractIdClient {

    private Logger LOGGER = LoggerFactory.getLogger(ThriftIdClient.class);

    private String idServerHost = "localhost";
    private int idServerPort = 9090;
    private ThriftClientPool<TIdService.Client, TIdService.Iface> thriftClientPool;

    /**
     * Constructs a new {@link ThriftIdClient} object.
     */
    public ThriftIdClient() {
    }

    /**
     * Constructs a new {@link ThriftIdClient} object.
     * 
     * @param idServerHost
     *            Thrift host of id-server
     * @param idServerPort
     *            Thrift port of id-server
     */
    public ThriftIdClient(String idServerHost, int idServerPort) {
        setIdServerHost(idServerHost);
        setIdServerPort(idServerPort);
    }

    /**
     * Constructs a new {@link ThriftIdClient} object.
     * 
     * @param hostAndPort
     *            Thrift host & port of id-server, format {@code host:port}
     */
    public ThriftIdClient(String hostAndPort) {
        String[] tokens = StringUtils.split(hostAndPort, ':');
        setIdServerHost(tokens[0]);
        if (tokens.length > 1) {
            setIdServerPort(Integer.parseInt(tokens[1]));
        }
    }

    public String getIdServerHost() {
        return idServerHost;
    }

    public ThriftIdClient setIdServerHost(String idServerHost) {
        this.idServerHost = idServerHost;
        return this;
    }

    public int getIdServerPort() {
        return idServerPort;
    }

    public ThriftIdClient setIdServerPort(int idServerPort) {
        this.idServerPort = idServerPort;
        return this;
    }

    /**
     * Helper method to create a new {@link ITProtocolFactory} for id-server
     * Thrift client.
     * 
     * @param host
     * @param port
     * @return
     */
    public static ITProtocolFactory protocolFactory(final String host, final int port,
            final int soTimeout) {
        ITProtocolFactory protocolFactory = new ITProtocolFactory() {
            @Override
            public TProtocol create() {
                TSocket socket = new TSocket(host, port);
                socket.setTimeout(soTimeout);
                TTransport transport = new TFramedTransport(socket);
                TProtocol protocol = new TCompactProtocol(transport);
                return protocol;
            }
        };
        return protocolFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThriftIdClient init() {
        super.init();

        final int timeout = 10000;
        thriftClientPool = new ThriftClientPool<TIdService.Client, TIdService.Iface>();
        thriftClientPool.setClientClass(TIdService.Client.class).setClientInterface(
                TIdService.Iface.class);
        thriftClientPool.setTProtocolFactory(protocolFactory(idServerHost, idServerPort, timeout));
        thriftClientPool.setPoolConfig(new PoolConfig().setMaxActive(32).setMaxWaitTime(timeout));
        thriftClientPool.init();

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        if (thriftClientPool != null) {
            try {
                thriftClientPool.destroy();
            } catch (Exception e) {
            }
        }

        super.destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextId(String namespace) {
        return nextId(namespace, EngineType.DEFAULT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long nextId(String namespace, EngineType engine) {
        long id = -1;
        try {
            TIdService.Iface idClient = thriftClientPool.borrowObject();
            if (idClient != null) {
                try {
                    String _engine = "default";
                    if (engine != null) {
                        switch (engine) {
                        case JDBC:
                            _engine = "jdbc";
                            break;
                        case REDIS:
                            _engine = "redis";
                            break;
                        case SNOWFLAKE:
                            _engine = "snowflake";
                            break;
                        case ZOOKEEPER:
                            _engine = "zookeeper";
                            break;
                        default:
                        }
                    }
                    TIdResponse response = idClient.nextId(namespace, _engine);
                    id = response != null && response.status == 200 ? response.id : -1;
                } finally {
                    thriftClientPool.returnObject(idClient);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return id;
    }
}

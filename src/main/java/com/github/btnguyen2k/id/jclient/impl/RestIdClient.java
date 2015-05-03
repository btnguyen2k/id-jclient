package com.github.btnguyen2k.id.jclient.impl;

import java.util.Map;

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.btnguyen2k.id.jclient.EngineType;
import com.github.btnguyen2k.id.jclient.IIdClient;
import com.github.ddth.commons.utils.DPathUtils;
import com.github.ddth.commons.utils.SerializationUtils;

/**
 * REST-implementation of {@link IIdClient}.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.1.0
 */
public class RestIdClient extends AbstractIdClient {

    private Logger LOGGER = LoggerFactory.getLogger(RestIdClient.class);

    private String idServerUrl;

    public RestIdClient() {
    }

    public RestIdClient(String idServerUrl) {
        setIdServerUrl(idServerUrl);
    }

    public String getIdServerUrl() {
        return idServerUrl;
    }

    public RestIdClient setIdServerUrl(String idServerUrl) {
        this.idServerUrl = idServerUrl;
        if (this.idServerUrl.endsWith("/")) {
            this.idServerUrl = this.idServerUrl.substring(0, this.idServerUrl.length() - 1);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callApi(String url) {
        try {
            HttpResponse httpResponse = HttpRequest.get(url).timeout(5000).send();
            try {
                if (httpResponse.statusCode() != 200) {
                    return null;
                }

                int contentLength = Integer.parseInt(httpResponse.contentLength());
                if (contentLength == 0 || contentLength > 1024) {
                    LOGGER.warn("Invalid response length: " + contentLength);
                    return null;
                }

                return SerializationUtils.fromJsonString(httpResponse.body(), Map.class);
            } finally {
                httpResponse.close();
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
            return null;
        }
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
        String apiUri = "/nextId/" + namespace;
        if (engine != null) {
            switch (engine) {
            case JDBC:
                apiUri = "/nextId/" + namespace + "/jdbc";
                break;
            case REDIS:
                apiUri = "/nextId/" + namespace + "/redis";
                break;
            case SNOWFLAKE:
                apiUri = "/nextId/" + namespace + "/snowflake";
                break;
            case ZOOKEEPER:
                apiUri = "/nextId/" + namespace + "/zookeeper";
                break;
            default:
            }
        }
        Map<String, Object> apiResult = callApi(idServerUrl + apiUri);
        if (apiResult == null) {
            return -1;
        }
        Integer status = DPathUtils.getValue(apiResult, "status", Integer.class);
        Long id = DPathUtils.getValue(apiResult, "id", Long.class);
        return status != null && status.intValue() == 200 ? (id != null ? id.longValue() : -1) : -1;
    }
}

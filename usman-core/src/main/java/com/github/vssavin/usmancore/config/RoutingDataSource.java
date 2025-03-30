package com.github.vssavin.usmancore.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.NonNull;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Routes connection switching between data sources based on a lookup key.
 *
 * @author vssavin on 28.11.2023.
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    enum DATASOURCE_TYPE {

        UM_DATASOURCE, APPLICATION_DATASOURCE

    }

    private DATASOURCE_TYPE datasourceKey = DATASOURCE_TYPE.APPLICATION_DATASOURCE;

    private final Map<Object, Object> dataSources = new HashMap<>();

    void setKey(DATASOURCE_TYPE key) {
        this.datasourceKey = key;
    }

    public DATASOURCE_TYPE getDatasourceKey() {
        return datasourceKey;
    }

    public RoutingDataSource() {
        setTargetDataSources(dataSources);
    }

    void addDataSource(DATASOURCE_TYPE key, DataSource dataSource) {
        dataSources.put(key, dataSource);
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return datasourceKey;
    }

    @NonNull
    @Override
    protected DataSource determineTargetDataSource() {
        return (DataSource) dataSources.get(datasourceKey);
    }

}

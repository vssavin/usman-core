package com.github.vssavin.usmancore.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Deque;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Wrapper to provide switching between user management datasource and main datasource.
 *
 * @author vssavin on 28.11.2023.
 */
@Component
public class DataSourceSwitcher {

	private final AbstractRoutingDataSource routingDataSource;

	private final Deque<RoutingDataSource.DATASOURCE_TYPE> datasourceStack = new ConcurrentLinkedDeque<>();

	public DataSourceSwitcher(AbstractRoutingDataSource routingDataSource) {
		this.routingDataSource = routingDataSource;
	}

	public void switchToUmDataSource() {
		datasourceStack.push(((RoutingDataSource) routingDataSource).getDatasourceKey());
		((RoutingDataSource) routingDataSource).setKey(RoutingDataSource.DATASOURCE_TYPE.UM_DATASOURCE);
	}

	public void switchToApplicationDataSource() {
		datasourceStack.push(((RoutingDataSource) routingDataSource).getDatasourceKey());
		((RoutingDataSource) routingDataSource).setKey(RoutingDataSource.DATASOURCE_TYPE.APPLICATION_DATASOURCE);
	}

	public void switchToPreviousDataSource() {
		try {
			RoutingDataSource.DATASOURCE_TYPE dsType = datasourceStack.pop();
			((RoutingDataSource) routingDataSource).setKey(dsType);
		}
		catch (NoSuchElementException e) {
			((RoutingDataSource) routingDataSource).setKey(RoutingDataSource.DATASOURCE_TYPE.APPLICATION_DATASOURCE);
		}
	}

	public DataSource getCurrentDataSource() {
		return ((RoutingDataSource) routingDataSource).determineTargetDataSource();
	}

}

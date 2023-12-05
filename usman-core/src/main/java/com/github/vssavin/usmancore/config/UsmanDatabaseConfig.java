package com.github.vssavin.usmancore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Configuration of user management database params.
 *
 * @author vssavin on 28.11.2023.
 */
@Configuration
@PropertySource(value = "classpath:" + UsmanDatabaseConfig.CONFIG_FILE)
public class UsmanDatabaseConfig {

	static final String CONFIG_FILE = "usman_db_conf.properties";

	@Value("${usman.db.url}")
	private String url;

	@Value("${usman.db.driverClass}")
	private String driverClass;

	@Value("${usman.db.dialect}")
	private String dialect;

	@Value("${usman.db.name}")
	private String name;

	@Value("${usman.db.user}")
	private String user;

	@Value("${usman.db.password}")
	private String password;

	@Value("${usman.db.additionalParams}")
	private String additionalParams;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAdditionalParams() {
		return additionalParams;
	}

	public void setAdditionalParams(String additionalParams) {
		this.additionalParams = additionalParams;
	}

}

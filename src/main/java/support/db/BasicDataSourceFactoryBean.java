package support.db;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

public class BasicDataSourceFactoryBean implements FactoryBean<BasicDataSource>, DisposableBean {
	private DataSourceConfig config;

	private BasicDataSource datasource;

	private String username = null;

	private String password = null;

	private String driverClassName = null;

	private String url = null;
	
	private String validationString = null;

	public void setConfig(DataSourceConfig config) {
		this.config = config;
	}

	public DataSourceConfig getConfig() {
		if (config == null) {
			config = new DataSourceConfig();
		}
		return config;
	}

	@Override
	public BasicDataSource getObject() {
		if (datasource == null) {
			datasource = new BasicDataSource();
			if(driverClassName == null || driverClassName.equals(""))
				datasource.setDriverClassName("com.mysql.jdbc.Driver");
			else {
				datasource.setDriverClassName(driverClassName);
				datasource.setValidationQuery(validationString);
			}
			datasource.setUsername(username);
			datasource.setPassword(password);
			datasource.setUrl(url);
			getConfig().configure(datasource);
		}
		return datasource;
	}

	public Class<?> getObjectType() {
		return BasicDataSource.class;
	}

	public boolean isSingleton() {
		return true;
	}

	@Override
	public void destroy() throws Exception {
		if (datasource != null) {
			datasource.close();
		}
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getValidationString() {
		return validationString;
	}

	public void setValidationString(String validationString) {
		this.validationString = validationString;
	}
}

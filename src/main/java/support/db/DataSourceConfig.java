package support.db;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PropertiesLoaderSupport;

public class DataSourceConfig extends PropertiesLoaderSupport implements BeanFactoryPostProcessor, ResourceLoaderAware {

	protected String driverClassName = "com.mysql.jdbc.Driver";

	protected int initialSize = 0;

	protected int maxActive = GenericObjectPool.DEFAULT_MAX_ACTIVE;

	protected int maxIdle = GenericObjectPool.DEFAULT_MAX_IDLE;

	protected int minIdle = GenericObjectPool.DEFAULT_MIN_IDLE;

	protected long maxWait = GenericObjectPool.DEFAULT_MAX_WAIT;

	protected long minEvictableIdleTimeMillis = GenericObjectPool.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

	protected long timeBetweenEvictionRunsMillis = 180000;//GenericObjectPool.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

	protected int numTestsPerEvictionRun = GenericObjectPool.DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

	protected boolean testOnBorrow = true;

	protected boolean testOnReturn = true;

	protected boolean testWhileIdle = true;

	protected String validationQuery = "SELECT 1";

	protected String validationQueryOracle = "SELECT 1 FROM DUAL";

	protected int validationQueryTimeout = -1;

	protected boolean removeAbandoned = false;

	protected int removeAbandonedTimeout = 300;

	protected String connectionProperties = "useUnicode=true;characterEncoding=UTF-8";

	private String propertyFileLocation = "dbcp.properties";

	private ResourceLoader resourceLoader;

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		try {
			// Resource location =
			// ResourceLoaderUtil.getResource(this.resourceLoader,
			// propertyFileLocation);
			Resource location = this.resourceLoader.getResource(propertyFileLocation);
			this.setLocation(location);

			Properties properties = this.mergeProperties();
			if (properties.getProperty("dbcp.initialSize") != null) {
				initialSize = Integer.valueOf(properties.getProperty("dbcp.initialSize"));
			}
			if (properties.getProperty("dbcp.maxActive") != null) {
				maxActive = Integer.valueOf(properties.getProperty("dbcp.maxActive"));
			}
			if (properties.getProperty("dbcp.maxIdle") != null) {
				maxIdle = Integer.valueOf(properties.getProperty("dbcp.maxIdle"));
			}
			if (properties.getProperty("dbcp.maxWait") != null) {
				maxWait = Long.valueOf(properties.getProperty("dbcp.maxWait"));
			}
			if (properties.getProperty("dbcp.minEvictableIdleTimeMillis") != null) {
				minEvictableIdleTimeMillis = Long.valueOf(properties.getProperty("dbcp.minEvictableIdleTimeMillis"));
			}
//			if (properties.getProperty("dbcp.timeBetweenEvictionRunsMillis") != null) {
//				timeBetweenEvictionRunsMillis = Long.valueOf(properties.getProperty("dbcp.timeBetweenEvictionRunsMillis"));
//			}
			if (properties.getProperty("dbcp.numTestsPerEvictionRun") != null) {
				numTestsPerEvictionRun = Integer.valueOf(properties.getProperty("dbcp.numTestsPerEvictionRun"));
			}
			// if(properties.getProperty("dbcp.testOnBorrow") != null){
			// testOnBorrow =
			// Boolean.valueOf(properties.getProperty("dbcp.testOnBorrow"));
			// }
			if (properties.getProperty("dbcp.testOnReturn") != null) {
				testOnReturn = Boolean.valueOf(properties.getProperty("dbcp.testOnReturn"));
			}
			if (properties.getProperty("dbcp.testWhileIdle") != null) {
				testWhileIdle = Boolean.valueOf(properties.getProperty("dbcp.testWhileIdle"));
			}
			// if(properties.getProperty("dbcp.validationQuery") != null){
			// validationQuery = properties.getProperty("dbcp.validationQuery");
			// }
			if (properties.getProperty("dbcp.validationQueryTimeout") != null) {
				validationQueryTimeout = Integer.valueOf(properties.getProperty("dbcp.validationQueryTimeout"));
			}
			if (properties.getProperty("dbcp.removeAbandoned") != null) {
				removeAbandoned = Boolean.valueOf(properties.getProperty("dbcp.removeAbandoned"));
			}
			if (properties.getProperty("dbcp.removeAbandonedTimeout") != null) {
				removeAbandonedTimeout = Integer.valueOf(properties.getProperty("dbcp.removeAbandonedTimeout"));
			}
		} catch (IOException e) {
			logger.error("load dbcp property error", e);
		}
	}

	public void configure(BasicDataSource datasource) {
		if (datasource == null) {
			return;
		}
		// datasource.setDriverClassName(driverClassName);
		datasource.setInitialSize(initialSize);
		datasource.setMaxActive(maxActive);
		datasource.setMaxIdle(maxIdle);
		datasource.setMaxWait(maxWait);
		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		datasource.setMinIdle(minIdle);
		datasource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
		datasource.setRemoveAbandoned(removeAbandoned);
		datasource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		datasource.setTestOnBorrow(testOnBorrow);
		datasource.setTestOnReturn(testOnReturn);
		datasource.setTestWhileIdle(testWhileIdle);
		datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
		// if(datasource.getValidationQuery() == null ||
		// datasource.getValidationQuery() == "")
		datasource.setValidationQuery(validationQuery);
		datasource.setValidationQueryTimeout(validationQueryTimeout);
		datasource.setConnectionProperties(connectionProperties);
	}
	

	public void setPropertyFileLocation(String propertyFileLocation) {
		this.propertyFileLocation = propertyFileLocation;
	}

	public String getPropertyFileLocation() {
		return propertyFileLocation;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public int getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}
	
	
	
	public int getInitialSize(){
		return this.initialSize;
	}
	
	public int getMaxIdle(){
		return this.maxIdle;
	}
	
	public int getMinIdle(){
		return this.minIdle;
	}
	
	public long getMaxWait(){
		return this.maxWait;
	}
	
	public long getMinEvictableIdleTimeMillis(){
		return this.minEvictableIdleTimeMillis;
	}
	
	public long getTimeBetweenEvictionRunsMillis(){
		return this.timeBetweenEvictionRunsMillis;
	}
	
	public long getNumTestsPerEvictionRun(){
		return this.numTestsPerEvictionRun;
	}
	
	public boolean getTestOnBorrow(){
		return this.testOnBorrow;
	}
	
	public boolean getTestWhileIdle(){
		return this.testWhileIdle;
	}
	
	public boolean getTestOnReturn(){
		return this.testOnReturn;
	}
	
	public String getValidationQuery(){
		return this.validationQuery;
	}
	
	public int getRemoveAbandonedTimeout(){
		return this.removeAbandonedTimeout;
	} 

}

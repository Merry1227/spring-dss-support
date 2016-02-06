package support.datasource;


import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/**
 * support both spring inject instantiation and common jave bean instantiation
 * use AtomikosDataSource and pool
 */


public class ShardDataSource2 extends AtomikosDataSourceBean implements InitializingBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7280839509354091251L;

	public static final String XADSCLASSNAME_MYSQL="com.mysql.jdbc.jdbc2.optional.MysqlXADataSource";
	
	public static final String CHARACTERENCODEING="UTF-8";
		
	private String id;
	
	private String type;
	
	private transient DataSourceConfig poolConfig;

	private transient boolean resetPool=false;

	public ShardDataSource2(){
		if(!resetPool){
			afterPropertiesSet();	
		}
	}

	/**
	 * create instance out of spring.
	 * @param index
	 * @param dsGroup
	 * @param dbConfig
	 * @param poolConfig
	 */
	public ShardDataSource2(int index,DataSourceGroup dsGroup,DatabaseConnectionConfiguration dbConfig,DataSourceConfig poolConfig) {
		this.setId(index,dsGroup);
		this.setXaDataSourceClassName(XADSCLASSNAME_MYSQL);
        this.setXaProperties(generateXaProperties(dbConfig));
		this.setUniqueResourceName(id);
		this.setPoolConfig(poolConfig);
		afterPropertiesSet();	
	}
	
	
	public void setId(int index,DataSourceGroup dsGroup){
		type=dsGroup.name();
		id=getDataSourceId(index,dsGroup);
	}
	
	public String getId(){
		return id;
	}
	
	public String getType(){
		return type;
	}	
	
	public void setPoolConfig(DataSourceConfig poolConfig){
		this.poolConfig=poolConfig;
	}
	
	public DataSourceConfig getPoolConfig(){
		  return this.poolConfig;
	}
	
	public static String getDataSourceId(int index,DataSourceGroup dsGroup){
		return dsGroup.name()+"_"+index;
	}

	@Override
	public void afterPropertiesSet() {
		if(poolConfig!=null){
			configPoolProperty();
			resetPool=true;
		}
	}
	
	private void configPoolProperty(){
		super.setMaxIdleTime(this.poolConfig.getMaxIdle());
		super.setBorrowConnectionTimeout((int)this.poolConfig.getMaxWait());
		super.setMaxPoolSize(this.poolConfig.getMaxActive());
		super.setMinPoolSize(this.poolConfig.getMinIdle());
		super.setTestQuery(this.poolConfig.getValidationQuery());		
		super.setMaintenanceInterval((int)this.poolConfig.getTimeBetweenEvictionRunsMillis());
		super.setReapTimeout(this.poolConfig.getRemoveAbandonedTimeout());
	}
	
	private Properties generateXaProperties(DatabaseConnectionConfiguration dbConfig){
		Properties dsProperties=new Properties();		
		dsProperties.setProperty("url",dbConfig.getUrl());
		dsProperties.setProperty("user",dbConfig.getUsername());
		dsProperties.setProperty("password",dbConfig.getPassword());
		dsProperties.setProperty("useUnicode","true");
		dsProperties.setProperty("characterEncoding",CHARACTERENCODEING);
		return dsProperties;
	}
	
	public String toString(){
		return this.id;
	}
	
}

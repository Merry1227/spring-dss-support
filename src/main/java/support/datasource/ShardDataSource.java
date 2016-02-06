package support.datasource;


import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;

import support.dao.DataSourceGroup;
import support.db.DataSourceConfig;
import support.db.DatabaseConnectionConfiguration;



/**
 * support both spring inject instantiation and common jave bean instantiation
 * use dbcp data source and pool strategy.
 */


public class ShardDataSource extends BasicDataSource implements InitializingBean{
	
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

	public ShardDataSource(){
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
	public ShardDataSource(int index,DataSourceGroup dsGroup,DatabaseConnectionConfiguration dbConfig,DataSourceConfig poolConfig) {
		this.setId(index,dsGroup);
		//this.setConnectionProperties(connectionProperties);
		//this.setXaDataSourceClassName(XADSCLASSNAME_MYSQL);
       // this.setXaProperties(generateXaProperties(dbConfig));
		this.setUrl(dbConfig.getUrl());
		this.setPassword(dbConfig.getPassword());
		this.setDriverClassName("com.mysql.jdbc.Driver");
		this.setUsername(dbConfig.getUsername());
        this.setConnectionProperties("useUnicode=true;characterEncoding=UTF-8");
	   // this.setUniqueResourceName(id);
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
		this.setInitialSize(this.poolConfig.getInitialSize());
		this.setMaxActive(this.poolConfig.getMaxActive());
		this.setMaxIdle(this.poolConfig.getMaxIdle());
		this.setMaxWait(this.poolConfig.getMaxWait());
		this.setMinEvictableIdleTimeMillis(this.poolConfig.getMinEvictableIdleTimeMillis());
		this.setMinIdle(this.poolConfig.getMinIdle());
		this.setNumTestsPerEvictionRun((int)this.poolConfig.getNumTestsPerEvictionRun());
		this.setRemoveAbandonedTimeout(this.poolConfig.getRemoveAbandonedTimeout());
		this.setTestOnBorrow(this.poolConfig.getTestOnBorrow());
		this.setTestOnReturn(this.poolConfig.getTestOnReturn());
		this.setTestWhileIdle(this.poolConfig.getTestWhileIdle());
		this.setTimeBetweenEvictionRunsMillis(this.poolConfig.getTimeBetweenEvictionRunsMillis());
		this.setMinEvictableIdleTimeMillis(this.poolConfig.getMinEvictableIdleTimeMillis());
		this.setValidationQuery(this.poolConfig.getValidationQuery());
	}
	
//	private Properties generateXaProperties(DatabaseConnectionConfiguration dbConfig){
//		Properties dsProperties=new Properties();		
//		dsProperties.setProperty("url",dbConfig.getUrl());
//		dsProperties.setProperty("user",dbConfig.getUsername());
//		dsProperties.setProperty("password",dbConfig.getPassword());
//		dsProperties.setProperty("useUnicode","true");
//		dsProperties.setProperty("characterEncoding",CHARACTERENCODEING);
//		return dsProperties;
//	}
	
	public String toString(){
		return this.id;
	}
	
}


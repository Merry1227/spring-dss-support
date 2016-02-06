package support.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;

import org.mybatis.spring.SqlSessionFactoryBean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * To sync transaction at dynamic data source, creating separate <code>SqlSessionFactory</code> for each data source is necessary.
 * This class 's will create SqlSessionFactory dynamically and store them by cloning <code>Configure</code> from defaultSqlSessionFactory.
 * Note that please set <code>DataSourceContextHolder</code> before using transaction.
 * @author cmei
 *
 */
public class DynamicSqlSessionFactoryBean extends SqlSessionFactoryBean implements InitializingBean{
	
	private static Logger logger=(Logger) LoggerFactory.getLogger(DynamicSqlSessionFactoryBean.class);
	
	private Map<String,SqlSessionFactory> targetSqlSessionFactorys;
	
	private SqlSessionFactory defaultTargetSqlSessionFactory;
	
	private Resource[] mapperLocations;
	
	private String typeAliasesPackage;
	
	private IDynamicDataSourceManager dynamicDataSourceManager;
	
	
	public void setDynamicDataSourceManager(IDynamicDataSourceManager dynamicDataSourceManager){
		this.dynamicDataSourceManager=dynamicDataSourceManager;
	}
	
	public void setMapperLocations(Resource[] mapperLocations){
		this.mapperLocations=mapperLocations;
	}

	public Resource[] getMapperLocations( ) {
	     return  this.mapperLocations;
	}
	
	public void setTypeAliasesPackage(String typeAliasesPackage){
		this.typeAliasesPackage=typeAliasesPackage;
	}
	
	public String getTypeAliasesPackage( ) {
	     return this.typeAliasesPackage;
	}
	

	// Be careful to avoid recursion call.
	public void afterPropertiesSet() throws Exception{
		super.setMapperLocations(mapperLocations);
		super.setTypeAliasesPackage(typeAliasesPackage);
		if(dynamicDataSourceManager!=null){
		    //Assert.notNull(, "Property 'dataSource' is required");
			this.setDataSource(dynamicDataSourceManager.getDefaultDataSource());
		}
		super.afterPropertiesSet();
		this.defaultTargetSqlSessionFactory=super.getObject();
		if(dynamicDataSourceManager==null){
			//use it as SqlSessionFactoryBean
			logger.info("property 'dynamicDataSourceManager' is unset,will use dynamicDataSource if set");
			Assert.notNull(defaultTargetSqlSessionFactory,"Property defaultTargetSqlSessionFactory is Null");
		}else{
			buildTargetSqlSessionFactorys(); 	
		}	
	}
	
	

	private void buildTargetSqlSessionFactorys() throws Exception{
		targetSqlSessionFactorys=new HashMap<String,SqlSessionFactory>();
		for(ShardDataSource ds:dynamicDataSourceManager.getAllDataSources()){
			SqlSessionFactoryBean fb=new SqlSessionFactoryBean();
			fb.setMapperLocations(this.getMapperLocations());
			fb.setTypeAliasesPackage(this.getTypeAliasesPackage());
			fb.setDataSource(ds);
			targetSqlSessionFactorys.put(ds.getId(),fb.getObject());
		}
		this.defaultTargetSqlSessionFactory=targetSqlSessionFactorys.get(dynamicDataSourceManager.getDefaultDataSource().getId());

	}
	
	public void setDefaultTargetSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		this.defaultTargetSqlSessionFactory=sqlSessionFactory;
	}
	
	public SqlSessionFactory getDefaultTargetSqlSessionFactory(){
		return this.defaultTargetSqlSessionFactory;
	}
	
	public Map<String,SqlSessionFactory> getTargetSqlSessionFactorys(){
		return this.targetSqlSessionFactorys;
	}
}

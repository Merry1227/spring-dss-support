package support.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import support.datasource.IDynamicDataSourceManager;
import support.datasource.ShardDataSource;

import com.microstrategy.alert.ecommerce.db.DataSourceConfig;
import com.microstrategy.alert.ns.enums.DataSourceGroup;
import com.microstrategy.alert.ns.guid.GUIDPropertyLoader;

@Component("dynamicDataSourceManager")
public class DynamicDataSourceManager implements IDynamicDataSourceManager, InitializingBean{
	
	@Autowired
	@Qualifier("guidPropertyLoader")
	private GUIDPropertyLoader guidPropertyLoader;
	
	@Autowired
	@Qualifier("guidDSConfig")
	private DataSourceConfig guidPoolConfig;
	
	@Autowired
	@Qualifier("shardDSConfig")
	private DataSourceConfig shardPoolConfig;
	
	private Map<String,ShardDataSource> allDataSources=new ConcurrentHashMap<String,ShardDataSource>();
	
	private static int shardServerNum=0;
	


	@Override
	public void afterPropertiesSet() throws Exception {
		setDataSources();
	}
	
	private void setDataSources(){
		Assert.notNull(guidPropertyLoader,"Property 'guidPropertyLoader' is empty!");
		
		List<DatabaseConnectionConfiguration> generatorConnectionConfigurationList=guidPropertyLoader.getGeneratorConnectionConfigurationList();	
		List<DatabaseConnectionConfiguration> hostConnectionConfigurationList=guidPropertyLoader.getHostConnectionConfigurationList();
		
		Assert.notEmpty(generatorConnectionConfigurationList,"Property generatorConnectionConfigurationList in GUIDPropertyLoader is empty!");
		Assert.notEmpty(hostConnectionConfigurationList,"Property hostConnectionConfigurationList in GUIDPropertyLoader is empty!");
		
		
		for(int i=0;i<generatorConnectionConfigurationList.size();i++){
			DatabaseConnectionConfiguration config=generatorConnectionConfigurationList.get(i);
			ShardDataSource sds=new ShardDataSource(i,DataSourceGroup.GUIDGENERATOR,config,guidPoolConfig);
			allDataSources.put(sds.getId(),sds);
		}
		
		setShardServerNum(hostConnectionConfigurationList.size());
		for(int i=0;i<getShardServerNum();i++){
			DatabaseConnectionConfiguration config=hostConnectionConfigurationList.get(i);
			ShardDataSource sds=new ShardDataSource(i,DataSourceGroup.SHARDDBSERVER,config,shardPoolConfig);
			allDataSources.put(sds.getId(),sds);
		}
	}

	@Override
	public ShardDataSource getDataSource(int index, DataSourceGroup dsGroup) {
		return allDataSources.get(ShardDataSource.getDataSourceId(index, dsGroup));
	}
	
	public Collection<String> getAllDataSourceIDs(){
		return allDataSources.keySet();
	}
	
	public Collection<ShardDataSource> getAllDataSources(){
		return allDataSources.values();
	}

	@Override
	public ShardDataSource getDataSource(String dataSourceId) {
		return allDataSources.get(dataSourceId);
	}
	
	/**
	 * the first data source from <code>allDataSources</code> as default.
	 */
	@Override
	public ShardDataSource getDefaultDataSource(){
		 ShardDataSource ds=getAllDataSources().iterator().next();
		 return ds;
	}

	public static int getShardServerNum() {
		return shardServerNum;
	}

	private static void setShardServerNum(int shardServerNum) {
		DynamicDataSourceManager.shardServerNum = shardServerNum;
	}
	
	

}

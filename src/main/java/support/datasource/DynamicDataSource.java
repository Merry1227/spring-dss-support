package support.datasource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component("dynamicDataSource")
public class DynamicDataSource extends AbstractRoutingDataSource implements InitializingBean{
	
	@Autowired
	@Qualifier("dynamicDataSourceManager")
	IDynamicDataSourceManager dynamicDataSourceManager;
	
	@Override
	protected Object determineCurrentLookupKey() {		
		return DataSourceContextHolder.getDataSourceType();
	}
	
	public void afterPropertiesSet() {
		
		if(dynamicDataSourceManager==null){
			logger.info("property dynamicDataSourceManager is unset.");
			return ;
		}
		Assert.notNull(dynamicDataSourceManager,"Property 'dynamicDataSourceManager' is Null");
		Map<Object,Object> targetDS=new HashMap<Object,Object>();
		for(ShardDataSource ds:dynamicDataSourceManager.getAllDataSources()){
			targetDS.put(ds.getId(),ds);
		}
		this.setTargetDataSources(targetDS);
		this.setDefaultTargetDataSource(dynamicDataSourceManager.getDefaultDataSource());

		super.afterPropertiesSet();
	}
}

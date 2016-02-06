package support.datasource;

import java.util.Collection;

import javax.sql.DataSource;

import support.db.DataSourceConfig;

import com.microstrategy.alert.ns.enums.DataSourceGroup;

public interface IDynamicDataSourceManager {
	
	ShardDataSource getDataSource(int index,DataSourceConfig dsGroup);
	
	ShardDataSource getDataSource(String dataSourceId);
	
	Collection<String> getAllDataSourceIDs();
	
	Collection<ShardDataSource> getAllDataSources();

	ShardDataSource getDefaultDataSource();

}

package support.datasource;

import support.dao.DataSourceGroup;



public class DataSourceContextHolder {
	
	private static final ThreadLocal<String> contextHolder=new ThreadLocal<String>();
	static{
		contextHolder.set(ShardDataSource.getDataSourceId(0, DataSourceGroup.SHARDDBSERVER));
	}

	/**
	 * @param dsIndex
	 */
	public static void setDataSourceType(int dsIndex,DataSourceGroup dsGroup){
		contextHolder.set(ShardDataSource.getDataSourceId(dsIndex, dsGroup));
	}
	
	public static String getDataSourceType(){
		return contextHolder.get();
	}
	
	public static void clearDataSourceType(){
		contextHolder.remove();
	}

}

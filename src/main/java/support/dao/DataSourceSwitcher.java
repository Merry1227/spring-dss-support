package support.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import support.datasource.DataSourceContextHolder;

import com.microstrategy.alert.ns.enums.DataSourceGroup;
import com.microstrategy.alert.ns.enums.DistributePolicy;
import com.microstrategy.alert.ns.guid.GUIDHelper;

@Component("dataSourceSwitcher")
public class DataSourceSwitcher {

	@Autowired
	private GUIDHelper guidHelper;
	
	/**
	 * @deprecated
	 * By default,the data source will be switch to shard data sources by <code>DistributePolicy.GROUPBYORG</code>
	 * use swithDataSouceBasedOnOrgID instead.
	 * @param key
	 */
	public void switchDataSourceBasedOnKey(Long key){
		switchDataSourceBasedOnKey(key,DistributePolicy.GROUPBYORG);
	}
	
	public void swithDataSouceBasedOnOrgID(Long orgID){
		switchDataSourceBasedOnKey(orgID,DistributePolicy.GROUPBYORG);
	}
	
	public void switchDataSourceBasedOnCustomerID(Long uid){
		if(uid==null){
			switchDataSourceBasedOnKey(0l,DistributePolicy.GROUPBYUSER);
		}else{
			switchDataSourceBasedOnKey(uid,DistributePolicy.GROUPBYUSER);
		}
		
		
	}
	
	public void switchDataSourceBasedOnKey(Long key,DistributePolicy policy){
		int hostIndex = guidHelper.getHostIndex(key, policy);
//		nsTransactionManager.setDataSource(dynamicDataSourceManager.getDataSource(hostIndex, DataSourceGroup.SHARDDBSERVER));
		DataSourceContextHolder.setDataSourceType(hostIndex,DataSourceGroup.SHARDDBSERVER);
	}
	
	public void switchDataSourceByHostIndex(int dsIndex){
		DataSourceContextHolder.setDataSourceType(dsIndex,DataSourceGroup.SHARDDBSERVER);
	}
	
	

	
	/**
	 * 0:master
	 * 1:slave
	 * @param index
	 */
	public void switchGeneratorDataSource(int index){
		switchGeneratorDataSource(index,DistributePolicy.FIXED);
	}
	
	private void switchGeneratorDataSource(int index,DistributePolicy policy){
		if(DistributePolicy.FIXED.equals(policy)){
		int hostIndex = index==0?0:1;
		DataSourceContextHolder.setDataSourceType(hostIndex,DataSourceGroup.GUIDGENERATOR);
		}
	}
	
	


	
}

package support.dao;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import support.datasource.DynamicSqlSessionDaoSupport;


/**
 * 
 * @author cmei
 * One or two search parameters are very common in DAOs, hence encapsulate them in BaseDAO. 
 * @param <T>
 */
public abstract class BaseDAO<T> extends DynamicSqlSessionDaoSupport {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private DataSourceSwitcher datasourceSwitcher;
	
	protected static final String VALUE_ASC="ASC";
	protected static final String VALUE_DESC="DESC";
	
	private static final String KEY_OBJLIST="list";
	private static final String KEY_PARAM1="param1";
	private static final String KEY_PARAM2="param2";
	private static final String KEY_PARAM3="param3";
	
	public DataSourceSwitcher getDataSourceSwitcher(){
		return this.datasourceSwitcher;
	}
	
	public void setDataSourceSwitcher(DataSourceSwitcher dataSourceSwitcher){
		this.datasourceSwitcher=dataSourceSwitcher;
	}
	
	
	@SuppressWarnings("unchecked")
	protected T selectOne(Object key) {
		return (T) getSqlSession().selectOne(getStatement("selectOne"), key);
	}
	
	/**
	 * Select by two parameters.
	 * Note: the parameters name should be param1 and param2 In mybatis Mapper.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T selectOne(Object key1,Object key2) {
		return this.selectOne(compositeParams(key1,key2));
		//return (T) getSqlSession().selectOne(getStatement("selectOne2"),compositeParams(key1,key2));
	}

	/**
	 * Select by three parameters.
	 * Note: the parameters name should be param1, param2 and param3 In mybatis Mapper.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected T selectOne(Object key1,Object key2,Object key3) {
		return (T) getSqlSession().selectOne(getStatement("selectOne3"),compositeParams(key1,key2,key3));
	}

	
	@SuppressWarnings("unchecked")
	protected List<T> selectListByFK1(Object fkValue) {
		return (List<T>) getSqlSession().selectList(getStatement("selectListByFK1"),
				fkValue);
	}
	
	/**
	 * Select by two parameters.
	 * Note: the parameters name should be param1 and param2 In mybatis Mapper.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> selectListByFK2(Object fkValue1,Object fkValue2) {
		return (List<T>) getSqlSession().selectList(getStatement("selectListByFK2"),
				compositeParams(fkValue1,fkValue2));
	}

	/**
	 * Select by two parameters.
	 * Note: the parameters name should be param1 and param2 In mybatis Mapper.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected List<T> selectListByFK3(Object fkValue1,Object fkValue2,Object fkValue3) {
		return (List<T>) getSqlSession().selectList(getStatement("selectListByFK3"),
				compositeParams(fkValue1,fkValue2,fkValue3));
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> selectListByFieldValue(String fieldName, Long fieldValue) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fieldName", fieldName);
		map.put("fieldValue", fieldValue);
		return (List<T>) getSqlSession().selectList(getStatement("selectListByFieldValue"),
				map);
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> selectListByFieldValueSet(String fieldName, Collection<Long> fieldValueSet) {
		if(fieldValueSet==null||fieldValueSet.isEmpty()){
			return new ArrayList<T>(0);
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("fieldName", fieldName);
		map.put("fieldValueSet", fieldValueSet);
		return (List<T>) getSqlSession().selectList(getStatement("selectListByFieldValueSet"),
				map);
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> selectList(Collection<Long> idList) {
		if(idList==null||idList.isEmpty()){
			return new ArrayList<T>(0);
		}
		return (List<T>) getSqlSession().selectList(getStatement("selectListByIDs"),
				compositeParams(idList));
	}
	
	@SuppressWarnings("unchecked")
	protected List<T> selectList(Collection<Long> idList,Long key) {
		if(idList==null||idList.isEmpty()){
			return new ArrayList<T>(0);
		}
		return (List<T>) getSqlSession().selectList(getStatement("selectListByIDs2"),
				compositeParams(key,idList));
	}

	protected boolean insert(Object parameter) {
		logger.debug("Get sql session.");
		SqlSession sqlSession = getSqlSession();
		logger.debug("Get sql session return.");
		logger.debug("Start insert.");
		int rows = sqlSession.insert(getStatement("insert"), parameter);
		logger.debug("Insert return");
		return rows != 0;
	}
	
	@SuppressWarnings("unchecked")
	protected List<T>  selectAll(Object key,int page,int pageSize) {
		if( page <= 0 ) page =1;
		if(pageSize<=0) pageSize=Integer.MAX_VALUE;
		int from =(page -1) * pageSize;
		return (List<T>) getSqlSession().selectList(getStatement("selectAll"),compositeParams(key,from,pageSize));
	}
	
	protected boolean insert(Long orgID,T entity) {
		return this.insert(compositeParams(orgID,entity));
	}
	
	protected boolean batchInsert(List<T> entityList){
		if(entityList.isEmpty()){
			return true;
		}
		int rows=getSqlSession().insert(getStatement("batchInsert"),entityList);
		return rows != 0;
	}
	
	protected boolean batchInsert(Long orgID,List<T> entityList){
		if(entityList.isEmpty()){
			return true;
		}
		int rows=getSqlSession().insert(getStatement("batchInsert"),compositeParams(orgID,entityList));
		return rows != 0;
	}
	

	protected boolean update(T entity) {
		int rows = getSqlSession().update(getStatement("update"), entity);
		return rows != 0;
	}

	protected boolean delete(Object key) {
		int rows = getSqlSession().update(getStatement("delete"), key);
		return rows != 0;
	}
	
	protected boolean delete(Object key1,Object key2){
		return this.delete(compositeParams(key1,key2));
	}
	
	protected boolean delete(Object key1,Object key2,Object key3){
		return this.delete(compositeParams(key1,key2,key3));
	}
	
	protected boolean batchDelete(Collection entityList){
		if(entityList.isEmpty()){
			return true;
		}
		int rows=getSqlSession().delete(getStatement("batchDelete"),entityList);
		return rows != 0;
	}
	
	protected boolean recover(Object key) {
		int rows = getSqlSession().update(getStatement("recover"), key);
		return rows != 0;
	}

	protected String getStatement(String statement) {
		StringBuilder builder = new StringBuilder();
		builder.append(getNamespace());
		builder.append('.');
		builder.append(statement);
		return builder.toString();
	}
	
	@SuppressWarnings("rawtypes")
	protected Map<String,Object> compositeParams(Collection paramList){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_OBJLIST,paramList);
		return map;
	}
	
	
	@SuppressWarnings("rawtypes")
	protected Map<String,Object> compositeParams(Object param1,Collection paramList){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_PARAM1,param1);
		map.put(KEY_OBJLIST,paramList);
		return map;
	}
	
	protected Map<String,Object> compositeParams(Object keyValue1,Object keyValue2){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_PARAM1,keyValue1);
		map.put(KEY_PARAM2,keyValue2);
		return map;
	}
	
	protected Map<String,Object> compositeParams(Object keyValue1,Object keyValue2,Object keyValue3){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(KEY_PARAM1,keyValue1);
		map.put(KEY_PARAM2,keyValue2);
		map.put(KEY_PARAM3,keyValue3);
		return map;
	}

	protected String getNamespace(){
		return this.getClass().getSimpleName();
	}
}

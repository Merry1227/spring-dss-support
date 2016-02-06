package support.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.util.Assert;

/**
 * @see org.mybatis.spring.support.SqlSessionDaoSupport
 * rewrite for transaction
 * TODO getSqlSession() 
 * @author cmei
 *
 */


public class DynamicSqlSessionDaoSupport extends JdbcDaoSupport implements InitializingBean{
	
	private static Logger logger=(Logger) LoggerFactory.getLogger(DynamicSqlSessionDaoSupport.class);
	
	private Map<String,SqlSessionFactory> targetSqlSessionFactorys;
	
	private SqlSessionFactory defaultTargetSqlSessionFactory;
	
	private SqlSession sqlSession;
	
	private boolean externalSqlSession;
	
	@Autowired
	@Qualifier("nsDefaultSqlSessionFactory")
	private DynamicSqlSessionFactoryBean dynamicFactoryBean;
	
	
	public final void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
		if(!this.externalSqlSession){
			this.sqlSession=new SqlSessionTemplate(sqlSessionFactory);
		}
	}
	
	public final void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate){
		this.sqlSession=sqlSessionTemplate;
		this.externalSqlSession=true;
	}
	
	
	 /**
     * Users should use this method to get a SqlSession to call its statement methods
     * This is SqlSession is managed by spring. Users should not commit/rollback/close it
     * because it will be automatically done.
     * Note:Mybatis use SqlSessionFacory as Lock key @cmei
     * @return Spring managed thread safe SqlSession 
     */
    public final SqlSession getSqlSession() {;
     	if(targetSqlSessionFactorys!=null){
     		String dataSourceID = DataSourceContextHolder.getDataSourceType();
    		SqlSessionFactory  targetSqlSessionFactory=targetSqlSessionFactorys.get(dataSourceID);		
    		if(targetSqlSessionFactory==null){
    			logger.warn("Default sqlSessionFactory is used!");
    			setSqlSessionFactory(defaultTargetSqlSessionFactory);
    		}else{
    			setSqlSessionFactory(targetSqlSessionFactory);		
    		}
    		
    	}else{
    		setSqlSessionFactory(defaultTargetSqlSessionFactory);	
    	}
    	return this.sqlSession;
    }

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		Assert.notNull(this.dynamicFactoryBean, "Property 'dynamicFactoryBean' is required");
		targetSqlSessionFactorys=this.dynamicFactoryBean.getTargetSqlSessionFactorys();
		this.defaultTargetSqlSessionFactory=this.dynamicFactoryBean.getDefaultTargetSqlSessionFactory();
	}


	public void setTargetSqlSessionFactorys(Map<String,SqlSessionFactory> targetSqlSessionFactorys){
		this.targetSqlSessionFactorys=targetSqlSessionFactorys;
	}
	
	public void setDefaultTargetSqlSessionFactory(SqlSessionFactory defaultTargetSqlSessionFactory){
		this.defaultTargetSqlSessionFactory=defaultTargetSqlSessionFactory;
	}
	
	public SqlSessionFactory getDefaultTargetSqlSessionFactory(){
		return this.defaultTargetSqlSessionFactory;
	}

}

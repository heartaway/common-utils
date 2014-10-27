package com.alibaba.gear.dao.impl;

import com.alibaba.gear.dao.BaseDao;
import com.alibaba.gear.dao.domain.BaseQuery;
import com.alibaba.gear.dao.domain.PageResult;
import com.alibaba.gear.dao.domain.UpdateByQuery;
import com.alibaba.gear.dao.exception.DataAccessException;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import javax.annotation.Resource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/10/10
 * Time: 下午4:04
 */
public abstract class BaseDaoImpl<DO extends Serializable,PK> implements BaseDao<DO,PK> {

    protected Class<DO> entityClass;

    public BaseDaoImpl() {
        super();
        Type genType = getClass().getGenericSuperclass();
        //相对子类，获取超类的泛型参数的实际类型
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.entityClass = (Class<DO>) params[0];
    }

    @Resource
    protected SqlMapClientTemplate sqlMapClientTemplate;

    public SqlMapClientTemplate getSqlMapClientTemplate() {
        return sqlMapClientTemplate;
    }

    public void setSqlMapClientTemplate(SqlMapClientTemplate sqlMapClientTemplate) {
        this.sqlMapClientTemplate = sqlMapClientTemplate;
    }

    @Override
    public PK insert(DO data) throws DataAccessException {
        try {
            PK back = (PK) this.sqlMapClientTemplate.insert(sqlIdName("insert"), data);
            return back;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int insertOrUpdate(DO data) throws DataAccessException {
        try {
            int back = this.sqlMapClientTemplate.update(sqlIdName("insertOrUpdate"), data);
            return back;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int update(DO data) throws DataAccessException {
        try {
            return this.sqlMapClientTemplate.update(sqlIdName("update"), data);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public DO getById(PK id) throws DataAccessException {
        if (id == null) {
            return null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        try {
            return (DO) this.sqlMapClientTemplate.queryForObject(sqlIdName("getById"), params);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public List<DO> query(BaseQuery<DO> query) throws DataAccessException {
        try {
            List<DO> lists = this.sqlMapClientTemplate.queryForList(sqlIdName("query"), query);
            return lists;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    private List<DO> query4page(BaseQuery<DO> query) throws DataAccessException {
        try {
            List<DO> lists = this.sqlMapClientTemplate.queryForList(sqlIdName("query4page"), query);
            return lists;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public PageResult<DO> query4Page(BaseQuery<DO> query) throws DataAccessException {
        PageResult<DO> pResult = new PageResult<DO>();
        pResult.setCurrentPage(query.getCurrentPage());
        pResult.setPageSize(query.getPageSize());
        try {
            long count = this.count(query);
            List<DO> result = this.query4page(query);
            pResult.setTotalItem((int) count);
            pResult.setResult(result);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
        return pResult;
    }

    @Override
    public int deleteById(PK id) throws DataAccessException {
        try {
            return this.sqlMapClientTemplate.delete(sqlIdName("deleteById"), id);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int delete(BaseQuery<DO> query) throws DataAccessException {
        try {
            return getInt(this.sqlMapClientTemplate.delete(sqlIdName("delete"), query));
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public long count(BaseQuery<DO> query) throws DataAccessException {
        try {
            return getLong(this.sqlMapClientTemplate.queryForObject(sqlIdName("count"), query));
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    @Override
    public int executeInsertBatch(final List<DO> ts) throws DataAccessException {
        if (ts == null || ts.size() == 0) {
            return 0;
        }
        try {
            Integer back = (Integer) this.sqlMapClientTemplate.execute(new SqlMapClientCallback() {
                public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                    executor.startBatch();
                    for (DO t : ts) {
                        executor.insert(sqlIdName("insert"), t);
                    }
                    return executor.executeBatch();
                }
            });
            return null == back ? 0 : back;
        } catch (Exception dae) {
            throw new DataAccessException(dae);
        }
    }

    @Override
    public int executeUpdateBatch(final List<DO> ts) throws DataAccessException {
        if (ts == null || ts.size() == 0) {
            return 0;
        }
        try {
            Integer back = (Integer) this.sqlMapClientTemplate.execute(new SqlMapClientCallback() {
                public Object doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
                    executor.startBatch();
                    for (DO t : ts) {
                        executor.update(sqlIdName("update"), t);
                    }
                    return executor.executeBatch();
                }
            });
            return null == back ? 0 : back;
        } catch (Exception dae) {
            throw new DataAccessException(dae);
        }
    }

    @Override
    public int updateByQuery(DO d, BaseQuery<DO> query) throws DataAccessException {
        try {
            UpdateByQuery<DO> uDO = new UpdateByQuery<DO>(query, d);
            return this.sqlMapClientTemplate.update(sqlIdName("updateByQuery"), uDO);
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }


    private int getInt(Object o) {
        if (o == null) {
            return 0;
        }
        return (Integer) o;
    }

    protected long getLong(Object o) {
        if (o == null) {
            return 0;
        }
        return (Long) o;
    }


    protected String sqlIdName(String sqlId) {
        return StringUtils.uncapitalize(entityClass.getSimpleName()) + "." + sqlId;
    }

}

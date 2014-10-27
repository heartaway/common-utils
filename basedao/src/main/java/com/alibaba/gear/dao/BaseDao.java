package com.alibaba.gear.dao;

import com.alibaba.gear.dao.domain.BaseQuery;
import com.alibaba.gear.dao.domain.PageResult;
import com.alibaba.gear.dao.exception.DataAccessException;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p></p>
 * User: <a href="mailto:xinyuan.ymm@alibaba-inc.com">心远</a>
 * Date: 14/10/10
 * Time: 下午3:10
 */
public interface BaseDao<DO extends Serializable, PK> {

    /**
     * 添加实体
     *
     * @param entity 要添加的实体对象
     * @return
     */
    public PK insert(DO entity) throws DataAccessException;


    /**
     * 新建对象或更新对象
     *
     * @param entity 要保存的实体对象
     * @return
     */
    public int insertOrUpdate(DO entity) throws DataAccessException;


    /**
     * 保存实体
     *
     * @param entity 要保存的实体对象
     * @return
     */
    public int update(DO entity) throws DataAccessException;

    /**
     * 根据sql mapping中定义的sql名称，以及传入的参数来更新实体
     *
     * @param sqlName sql名称
     * @param parameters 要保存的实体对象属性map
     * @return
     */
    public boolean update(String sqlName, Map parameters) throws DataAccessException;

    /**
     * 更新对象
     *
     * @param entity 对象, 需要更新成什么样子，只有非空字段会被更新
     * @param query 通过查询对象判断更新的范围
     * @return
     * @throws
     */
    public int updateByQuery(DO entity, BaseQuery<DO> query) throws DataAccessException;


    /**
     * 根据主键删除实体
     *
     * @param pk 主键
     * @return
     */
    public int deleteById(PK pk) throws DataAccessException;

    /**
     * 根据主键删除实体
     *
     * @param query 参数
     * @return
     */
    public int delete(BaseQuery<DO> query) throws DataAccessException;

    /**
     * 根据主键返回指定的实体对象
     *
     * @param pk 主键
     * @return
     */
    public DO getById(PK pk) throws DataAccessException;

    /**
     * 根据sql mapping中定义的sql名称，以及传入的参数来查找实体集
     *
     * @param query 参数
     * @return
     */
    public List<DO> query(BaseQuery<DO> query) throws DataAccessException;

    /**
     * 分页查询
     * 根据sql mapping中定义的sql名称，以及传入的参数来查找实体集
     *
     * @param query 参数
     * @return
     */
    public PageResult<DO> query4Page(BaseQuery<DO> query) throws DataAccessException;

    /**
     * 根据sql mapping中定义的sql名称，以及传入的参数来查找统计集
     *
     * @param query 参数
     * @return
     */
    public long count(BaseQuery<DO> query) throws DataAccessException;

    /**
     * 执行批处理插入任务
     *
     * @param tasks 任务集合
     * @return
     * @throws DataAccessException
     */
    public int executeInsertBatch(List<DO> tasks) throws DataAccessException;

    /**
     * 执行批处理更新任务
     *
     * @param tasks 任务集合
     * @return
     * @throws DataAccessException
     */
    public int executeUpdateBatch(List<DO> tasks) throws DataAccessException;
}

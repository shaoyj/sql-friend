package com.mylomen.core;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterDisposer;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;

/**
 * 简单封装
 *
 * @author ShaoYongJun
 */
public class JdbcTemplateWrapper extends JdbcTemplate {

    public JdbcTemplateWrapper(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return super.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException var4) {
            return null;
        } catch (DataAccessException var5) {
            throw var5;
        }
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, int[] argTypes, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return super.queryForObject(sql, args, argTypes, rowMapper);
        } catch (EmptyResultDataAccessException var6) {
            return null;
        } catch (DataAccessException var7) {
            throw var7;
        }
    }

    @Override
    public <T> T queryForObject(String sql, Object[] args, RowMapper<T> rowMapper) throws DataAccessException {
        try {
            return super.queryForObject(sql, args, rowMapper);
        } catch (EmptyResultDataAccessException var5) {
            return null;
        } catch (DataAccessException var6) {
            throw var6;
        }
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        try {
            return super.queryForObject(sql, rowMapper, args);
        } catch (EmptyResultDataAccessException var5) {
            return null;
        } catch (DataAccessException var6) {
            throw var6;
        }
    }


    /**
     * 仅支持Integer / Long 类型
     *
     * @param clazz
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public <T> T insertAndGetKeyForCommon(final Class<T> clazz, final String sql, final Object... params) {
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        this.update((con) -> {
            PreparedStatement ps = con.prepareStatement(sql, 1);
            PreparedStatementSetter pss = JdbcTemplateWrapper.this.newArgPreparedStatementSetter(params);
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
            } finally {
                if (pss instanceof ParameterDisposer) {
                    ((ParameterDisposer) pss).cleanupParameters();
                }
            }
            return ps;
        }, key);

        //判断返回对象类型
        if (Long.class == clazz) {
            Long l = key.getKey().longValue();
            return (T) l;
        }
        if (Integer.class == clazz) {
            Integer i = key.getKey().intValue();
            return (T) i;
        }

        return (T) key.getKey();
    }


    public int queryForInt(String sql) throws DataAccessException {
        Number number = this.queryForObject(sql, Integer.class);
        return number != null ? number.intValue() : 0;
    }

    public int queryForInt(String sql, Object... args) throws DataAccessException {
        Number number = (Number) this.queryForObject(sql, args, Integer.class);
        return number != null ? number.intValue() : 0;
    }

}


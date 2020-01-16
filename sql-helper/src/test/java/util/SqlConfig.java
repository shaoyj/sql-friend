package util;

import com.mylomen.dao.impl.DaoUtilImpl;
import com.mylomen.dao.impl.NamedParameterDaoUtilImpl;
import com.mylomen.core.JdbcTemplateWrapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/26
 * @time: 上午9:28
 */
public class SqlConfig {


    /**
     * 用于实际查询的sql工具。
     *
     * @param dataSource
     * @return
     */
    @Bean(name = "jdbcTemplate")
    public JdbcTemplateWrapper getJdbcTemplate(DataSource dataSource) {
        return new JdbcTemplateWrapper(dataSource);
    }


    @Bean(name = "namedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate(@Qualifier(value = "jdbcTemplate") JdbcTemplateWrapper jdbcTemplate) {
        return new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    @Bean
    public DaoUtilImpl getDaoUtilImpl(DataSource dataSource) {
        JdbcTemplateWrapper jdbcTemplateWrapper = new JdbcTemplateWrapper(dataSource);
        DaoUtilImpl daoUtil = new DaoUtilImpl(jdbcTemplateWrapper);
        return daoUtil;
    }


    @Bean
    public NamedParameterDaoUtilImpl getNamedParameterDaoUtilImpl(@Qualifier(value = "jdbcTemplate") JdbcTemplateWrapper jdbcTemplate) {
        NamedParameterDaoUtilImpl daoUtil = new NamedParameterDaoUtilImpl(jdbcTemplate);
        return daoUtil;
    }
}

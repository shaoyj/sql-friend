package com.mylomen.core;

import com.mylomen.core.cache.EntityCache;
import com.mylomen.exception.AssemblyEntityException;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 3:59 PM
 */
public class BeanRowMapperHelper<T> implements RowMapper<T> {


    @Nullable
    private Class<T> mappedClass;

    /**
     * key->colName
     * value->bean PropertyDescriptor
     */
    @Nullable
    private Map<String, PropertyDescriptor> mappedFields;


    public BeanRowMapperHelper(Class<T> mappedClass) {
        this.initialize(mappedClass);
    }

    private void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;

        //初始化 mappedFields
        mappedFields = new HashMap<>();
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(this.mappedClass, Object.class);
        } catch (IntrospectionException e) {
            throw new AssemblyEntityException(e.getMessage());
        }

        Map<String, String> beanNameMap = EntityCache.beanNameConversion(this.mappedClass);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        //遍历组装 mappedClass自身 成员变量名称对应的 PropertyDescriptor
        for (PropertyDescriptor pd : propertyDescriptors) {
            if (pd.getWriteMethod() != null) {
                mappedFields.put(beanNameMap.get(pd.getName()), pd);
            }
        }
    }


    @Override
    public T mapRow(ResultSet resultSet, int i) throws SQLException {
        assert this.mappedClass != null;
        T resultObj = BeanUtils.instantiateClass(this.mappedClass);
        ResultSetMetaData rsmd = resultSet.getMetaData();
        for (int index = 1; index <= rsmd.getColumnCount(); ++index) {
            String columnName = JdbcUtils.lookupColumnName(rsmd, index);

            PropertyDescriptor property = mappedFields.get(columnName);
            if (property != null) {
                Object value = JdbcUtils.getResultSetValue(resultSet, index, property.getPropertyType());
                if (value != null) {
                    Method setter = property.getWriteMethod();
                    if (setter != null) {
                        try {
                            setter.invoke(resultObj, value);
                        } catch (Exception e) {
                            throw new AssemblyEntityException(e.getMessage());
                        }
                    }
                }
            }
        }
        return resultObj;
    }
}

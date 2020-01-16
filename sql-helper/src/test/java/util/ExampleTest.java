package util;

import com.mylomen.core.BeanRowMapperHelper;
import com.mylomen.core.NameParamSqlHelper;
import com.mylomen.core.bo.ConditionBO;
import com.mylomen.core.bo.SqlArgsMapBO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/26
 * @time: 上午9:19
 */
@SpringBootTest(classes = {LocationApplication.class, SqlConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExampleTest {


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Test
    public void TestInQuery() {
        Map<String, Object> queryMap = new LinkedHashMap<>(4);
        queryMap.put("sn", "222");
        queryMap.put("test_name", Arrays.asList("1", "2"));
        BeanPropertyRowMapper<VendorDO> rowMapper = new BeanPropertyRowMapper<VendorDO>(VendorDO.class);
        List<VendorDO> maps = namedParameterJdbcTemplate.query(
                "SELECT *  FROM test WHERE 1 = 1  AND sn = :sn  AND test_name in (:test_name) ", queryMap, rowMapper);
        System.out.println(maps);


    }

    @Test
    public void TestAdd() {
        //add
        VendorDO addData = new VendorDO();
        addData.setTest_name("\"it's a dog.\"");
        addData.setId(6L);

        SqlArgsMapBO insertSql = NameParamSqlHelper.getMergeSql(addData, null);
        int update = namedParameterJdbcTemplate.update(insertSql.getSql(), insertSql.getParamsMap());
        System.out.println(update);
    }

    @Test
    public void TestUpdate() {
        VendorDO updateData = new VendorDO();
        updateData.setSn("update");
        updateData.setTest_name("test update");
        updateData.setId(4L);
        SqlArgsMapBO insertSql = NameParamSqlHelper.getUpdateMapOrPKSql(new ConditionBO(updateData));
        System.out.println(insertSql);

        int update = namedParameterJdbcTemplate.update(insertSql.getSql(), insertSql.getParamsMap());
        System.out.println(update);
    }


    @Test
    public void TestGetUpdateMapOrPKSql_in() {
        VendorDO updateData4In = new VendorDO();
        updateData4In.setTest_name("TestGetUpdateMapOrPKSql_in_condition");


        ConditionBO condition = new ConditionBO(updateData4In);
        condition.setIn("sn", Arrays.asList("add", "update"));

        SqlArgsMapBO updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(condition);
        System.out.println(updateSql);

        int update = namedParameterJdbcTemplate.update(updateSql.getSql(), updateSql.getParamsMap());
        System.out.println(update);
    }


    @Test
    public void testDel() {
        VendorDO delData = new VendorDO();

        SqlArgsMapBO delBO = NameParamSqlHelper.getDeleteSqlByParams(
                new ConditionBO(delData).setGe("status", 1));
        System.out.println(delBO);


        int update = namedParameterJdbcTemplate.update(delBO.getSql(), delBO.getParamsMap());
        System.out.println(update);
    }


    @Test
    public void TestFindOne() {
        VendorDO findOneData = new VendorDO();
        findOneData.setId(1L);

        ConditionBO conditionBO = new ConditionBO(findOneData).initResultArgs("*");
        conditionBO.setIn("sn", Arrays.asList("1", "2"));
//        conditionBO.setGroupSql("status");

        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(conditionBO.exceptOne());
        System.out.println(oneSql);
        Map<String, Object> stringObjectMap = namedParameterJdbcTemplate.queryForMap(oneSql.getSql(), oneSql.getParamsMap());
        System.out.println(stringObjectMap);


        BeanRowMapperHelper<VendorDO> beanRowMapperHelper = new BeanRowMapperHelper<>(VendorDO.class);

        VendorDO vendorDO = namedParameterJdbcTemplate.queryForObject(oneSql.getSql(), oneSql.getParamsMap(), beanRowMapperHelper);
        System.out.println(vendorDO);
    }


    @Test
    public void TestQueryList() {

        //1
        VendorDO queryListData = new VendorDO();
        ConditionBO conditionBO = new ConditionBO(queryListData).initResultArgs("*");
        conditionBO.setIn("sn", Arrays.asList("1", "2"));
        SqlArgsMapBO queryListSql = NameParamSqlHelper.querySql(conditionBO);
        List<VendorDO> query = namedParameterJdbcTemplate.query(queryListSql.getSql(), queryListSql.getParamsMap(), new BeanRowMapperHelper<>(VendorDO.class));
        System.out.println(query);
    }


    @Test
    public void TestCompatible() {
        SqlArgsMapBO updateSql = NameParamSqlHelper.querySql(
                new ConditionBO(new VendorDO()).setGe("status", 1)
                        .set("version", 1)
                        .setGe("age", 2)
                        .setLe("age", 8)
                        //6
                        .setNe("age", 6)
                        .setIn("gender", Arrays.asList("1", "2"))
                        .setNotIn("gender", Arrays.asList("3")));
        System.out.println(updateSql);


        List<VendorDO> query = namedParameterJdbcTemplate.query(
                updateSql.getSql(),
                updateSql.getParamsMap(),
                new BeanRowMapperHelper<>(VendorDO.class)
        );

        System.out.println(query);


        Map<String, Object> whereMap = new HashMap<>();
        whereMap.put("version", 1);
        whereMap.put("age__>=", 2);
        whereMap.put("age__<=", 8);
        whereMap.put("age__!=", 6);
        whereMap.put("gender__in", Arrays.asList("1", "2"));
        whereMap.put("gender__not_in", Arrays.asList("3"));
        updateSql = NameParamSqlHelper.querySql(new ConditionBO(new VendorDO()).initWhereMap(whereMap));
        query = namedParameterJdbcTemplate.query(updateSql.getSql(),
                updateSql.getParamsMap(),
                new BeanRowMapperHelper<>(VendorDO.class));
        System.out.println(query);
    }
}

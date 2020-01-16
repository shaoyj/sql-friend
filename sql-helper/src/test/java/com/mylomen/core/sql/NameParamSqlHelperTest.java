package com.mylomen.core.sql;

import com.mylomen.core.NameParamSqlHelper;
import com.mylomen.core.bo.ConditionBO;
import com.mylomen.core.bo.SqlArgsMapBO;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import util.VendorDO;

import java.time.Instant;
import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/10
 * @time: 10:53 AM
 */

public class NameParamSqlHelperTest {


    @Test
    public void getInsertSql() {
        VendorDO addData = new VendorDO();
        addData.setSn("add");
        addData.setTest_name("test add");
        SqlArgsMapBO insertSql = NameParamSqlHelper.getMergeSql(addData, null);
        System.out.println(insertSql);
    }

    @Test
    public void TestMergeSql() {
        VendorDO addData = new VendorDO();
        addData.setSn("add");
        addData.setTest_name("test add");
        SqlArgsMapBO insertSql = NameParamSqlHelper.getMergeSql(addData, Collections.singletonList("id"));
        System.out.println(insertSql);

        insertSql = NameParamSqlHelper.getMergeSql(addData, null);
        System.out.println(insertSql);
    }

    @Test
    public void getTabName() {
        Map<String, Object> whereMap = new HashMap<>();
        whereMap.putAll(null);

    }


    @Test
    public void TestGetUpdateMapOrPKSql_pk_no() {
        VendorDO updateData1 = new VendorDO();
        updateData1.setId(1L);
        updateData1.setSn("add");
        updateData1.setTest_name("test update__in");
        SqlArgsMapBO updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(new ConditionBO(updateData1));
        System.out.println(updateSql);
    }


    @Test
    public void TestGetUpdateMapOrPKSql() {
        VendorDO updateData1 = new VendorDO();
        updateData1.setTest_name("test update__in");
        updateData1.setId(1L);
        SqlArgsMapBO updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(new ConditionBO(updateData1));
        System.out.println(updateSql);


        ConditionBO condition = new ConditionBO(updateData1);
        condition.set("id", 1L);

        updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(condition);
        System.out.println(updateSql);


        condition.set("sn", "TestGetUpdateMapOrPKSql");
        updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(condition);
        System.out.println(updateSql);
    }

    @Test
    public void TestGetUpdateMapOrPKSql_in() {
        VendorDO updateData4In = new VendorDO();
        updateData4In.setTest_name("TestGetUpdateMapOrPKSql_in_condition");


        SqlArgsMapBO updateSql = NameParamSqlHelper.getUpdateMapOrPKSql(
                new ConditionBO(updateData4In).setIn("sn", Arrays.asList("add", "update")));
        System.out.println(updateSql);
    }


    @Test
    public void testDelSql() {
        SqlArgsMapBO updateSql = NameParamSqlHelper.getDeleteSqlByParams(
                new ConditionBO(new VendorDO()).setGe("status", 1)
                        .set("status", 1)
                        .setGt("status", 2)
                        .setLt("status", 3)
                        .setLe("status", 4)
                        .setNe("status", 5)
                        .setIn("status", Arrays.asList("66", "666"))
                        .setNotIn("status", Arrays.asList("77", "777")));
        System.out.println(updateSql);
    }


    @Test
    public void TestFindOneSql() {
        VendorDO findOneData = new VendorDO();
        findOneData.setId(1L);

        ConditionBO conditionBO = new ConditionBO(new VendorDO())
                .setIn("sn", Arrays.asList("1", "2"))
                .initResultArgs("status,count(*)");
        conditionBO.initGroupSql("status");

        SqlArgsMapBO oneSql = NameParamSqlHelper.querySql(conditionBO);
        System.out.println(oneSql);
    }


    @Test
    public void TestFindListSql() {

        VendorDO findOneData = new VendorDO();
        ConditionBO conditionBO = new ConditionBO(new VendorDO());
        SqlArgsMapBO argsMapBO = NameParamSqlHelper.querySql(conditionBO);
        System.out.println(argsMapBO);

        conditionBO.setIn("sn", Arrays.asList("1", "2"));


    }




}

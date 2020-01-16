package com.mylomen.dao.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylomen.core.bo.ConditionBO;
import com.mylomen.core.sql.LiveRoomRankingDO;
import com.mylomen.domain.PageView;
import com.mylomen.domain.ResultHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import util.LocationApplication;
import util.SliceDO;
import util.SqlConfig;
import util.VendorDO;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2019/10/11
 * @time: 12:08 PM
 */
@SpringBootTest(classes = {LocationApplication.class, SqlConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class NamedParameterDaoUtilImplTest {
    @Autowired
    private NamedParameterDaoUtilImpl namedParameterDaoUtil;

    @Test
    public void getNamedParameterJdbcTemplate() {
    }

    @Test
    public void getTabName() {
        String tabName = namedParameterDaoUtil.getTabName(new VendorDO());
        System.out.println(tabName);
    }

    @Test
    public void insertEntity() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        vendorDO.setSn("10-11 test_insert");
        ResultHandler resultHandler = namedParameterDaoUtil.insertEntity(vendorDO);
        System.out.println(resultHandler);
    }

    @Test
    public void insertAndGetKey() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setSn("10-11 test_insertAndGetKey");
        Long id = namedParameterDaoUtil.insertAndGetKey(vendorDO);
        System.out.println(id);
    }

    @Test
    public void deleteByPrimaryKey() {
        VendorDO vendorDO = new VendorDO();
//        vendorDO.setId(7L);
        ResultHandler resultHandler = namedParameterDaoUtil.deleteByPrimaryKey(vendorDO);
        System.out.println(resultHandler);
    }

    @Test
    public void deleteByEntityOrMap() {
        VendorDO vendorDO = new VendorDO();
        Map<String, Object> queryMap = new HashMap<>();

        queryMap.put("sn", "10-11 test_insert");
        ResultHandler resultHandler = namedParameterDaoUtil.deleteByEntityOrMap(vendorDO, queryMap);
        System.out.println(resultHandler);
    }

    @Test
    public void updateEntityOrMap() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setTest_name("updateEntityOrMap_by_map");
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("sn", "10-11 test_insert");
        ResultHandler resultHandler = namedParameterDaoUtil.updateEntityOrMap(vendorDO, queryMap);
        Assert.assertTrue(resultHandler.hasSuccess());


        vendorDO.setId(7L);
        vendorDO.setTest_name("updateEntityOrMap_by_id");
        resultHandler = namedParameterDaoUtil.updateEntityOrMap(vendorDO, queryMap);
        Assert.assertTrue(resultHandler.hasSuccess());
    }

    @Test
    public void updateByPrimaryKey() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        vendorDO.setTest_name("updateByPrimaryKey_by_id");
        ResultHandler resultHandler = namedParameterDaoUtil.updateByPrimaryKey(vendorDO);
        Assert.assertTrue(resultHandler.hasSuccess());
    }

    @Test
    public void saveOrUpdate() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        vendorDO.setTest_name("test_saveOrUpdate");
        ResultHandler resultHandler = namedParameterDaoUtil.saveOrUpdate(vendorDO, Arrays.asList("id"));
        Assert.assertTrue(resultHandler.hasSuccess());
    }

    @Test
    public void queryForList() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("status__>=", 3);

        List<VendorDO> vendorDOS = namedParameterDaoUtil.queryForList(new VendorDO(), queryMap);
        System.out.println(vendorDOS);
    }

    @Test
    public void queryForPage() {
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("status__>=", 3);

        PageView<VendorDO> vendorDOPageView = namedParameterDaoUtil.queryForPage(new VendorDO(), queryMap, null);
        System.out.println(vendorDOPageView);
    }

    @Test
    public void queryForPage1() {
        VendorDO vendorDO = new VendorDO();
        PageView<VendorDO> vendorDOPageView = namedParameterDaoUtil.queryForPage(vendorDO, null, null);
        System.out.println(vendorDOPageView);
    }

    @Test
    public void queryCountForInt() {
        VendorDO vendorDO = new VendorDO();
        Map<String, Object> queryMap = new HashMap<>();
        queryMap.put("status__>=", 1);
        Integer integer = namedParameterDaoUtil.queryCountForInt(vendorDO, queryMap);
        System.out.println(integer);

        vendorDO.setId(7L);
        integer = namedParameterDaoUtil.queryCountForInt(vendorDO, null);
        System.out.println(integer);


        queryMap.remove("status__>=");
        queryMap.put("status__ge", 2);
        integer = namedParameterDaoUtil.queryCountForInt(vendorDO, queryMap);
        System.out.println(integer);
    }

    @Test
    public void findEntityByParams() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        VendorDO entityByParams = namedParameterDaoUtil.findEntityByParams(vendorDO, null);
        System.out.println(entityByParams);
    }

    @Test
    public void findByPrimaryKey() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        VendorDO entityByParams = namedParameterDaoUtil.findByPrimaryKey(vendorDO);
        System.out.println(entityByParams);
    }

    @Test
    public void find() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(7L);
        VendorDO entityByParams = namedParameterDaoUtil.find(vendorDO, new StringBuilder("id,sn,status"), null);
        System.out.println(entityByParams);
    }

    @Test
    public void executeQuerySql() {
        StringBuilder sql = new StringBuilder("select max(id) from test ");
        Object obj = namedParameterDaoUtil.executeQuerySql(sql);
        System.out.println(obj);
    }

    @Test
    public void executeQuerySqlForMap() {
        StringBuilder sql = new StringBuilder("select * from test limit 1 ");
        Map<String, Object> map = namedParameterDaoUtil.executeQuerySqlForMap(sql);
        System.out.println(map);
    }

    @Test
    public void executeQuerySqlForList() {
        StringBuilder sql = new StringBuilder("select * from test");
        List<Map<String, Object>> maps = namedParameterDaoUtil.executeQuerySqlForList(sql);
        System.out.println(maps);
    }

    @Test
    public void executeSql() {
        StringBuilder sql = new StringBuilder("update test set sn='executeSql' where id=6");
        Object obj = namedParameterDaoUtil.executeSql(sql);
        System.out.println(obj);
    }

    @Test
    public void getJdbcTemplate() {
    }


    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void TestQuery() throws Exception {
        SliceDO sliceDO = new SliceDO();
        sliceDO.setId("0083952a-35bb-43d9-ba71-6bbe46a0cfcd");
        SliceDO byPrimaryKey = namedParameterDaoUtil.findByPrimaryKey(sliceDO);
        System.out.println(byPrimaryKey);


        JsonNode json = objectMapper.readTree(byPrimaryKey.getTargeting().getBinaryStream());
        System.out.println(json);
    }

    @Test
    public void deleteByEntityOrMap1() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(9L);
        ResultHandler resultHandler = namedParameterDaoUtil.deleteByEntityOrMap(new ConditionBO(vendorDO));
        System.out.println(resultHandler);
    }

    @Test
    public void updateEntityOrMap1() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(9L);
        vendorDO.setSn("test_updateEntityOrMap1_condition");
        ConditionBO conditionBO = new ConditionBO(vendorDO);
        ResultHandler resultHandler = namedParameterDaoUtil.updateEntityOrMap(conditionBO);
        System.out.println(resultHandler);

        vendorDO.setSn("test_updateEntityOrMap1_condition_two");
        vendorDO.setStatus(21);
        conditionBO.set("id", 9L);
        resultHandler = namedParameterDaoUtil.updateEntityOrMap(conditionBO);
        System.out.println(resultHandler);
    }

    @Test
    public void queryForList1() {
        VendorDO vendorDO = new VendorDO();
        ConditionBO conditionBO = new ConditionBO(vendorDO);
        conditionBO.set("status", 1);
        List<VendorDO> list = namedParameterDaoUtil.queryForList(conditionBO);
        System.out.println(list);

        conditionBO.setGe("age", 2);
        conditionBO.setLe("age", 8);
        conditionBO.setIn("score", Arrays.asList("70", "60"));
        conditionBO.setNe("gender", 1);
        list = namedParameterDaoUtil.queryForList(conditionBO);
        System.out.println(list);


        conditionBO = new ConditionBO(vendorDO);
        conditionBO.setLike("test_name", "test%");
        list = namedParameterDaoUtil.queryForList(conditionBO);
        System.out.println(list);

    }

    @Test
    public void queryForPage2() {
        VendorDO vendorDO = new VendorDO();
        ConditionBO conditionBO = new ConditionBO(vendorDO);
//        conditionBO.set("status", 1);
        conditionBO.setPageView(new PageView());
        PageView<VendorDO> page = namedParameterDaoUtil.queryForPage(conditionBO);
        System.out.println(page);
    }

    @Test
    public void queryCountForInt1() {
        VendorDO vendorDO = new VendorDO();
        ConditionBO conditionBO = new ConditionBO(vendorDO);
        conditionBO.set("status", 1);
        Integer integer = namedParameterDaoUtil.queryCountForInt(conditionBO);
        System.out.println(integer);
    }

    @Test
    public void find1() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(8L);
        ConditionBO conditionBO = new ConditionBO(vendorDO);
//        conditionBO.set("id", 9L);
        VendorDO data = namedParameterDaoUtil.find(conditionBO);
        System.out.println(data);
    }



    @Test
    public void voiceRanking() {

        List<LiveRoomRankingDO> list = new ArrayList<>(1000);
        for (int i = 0; i < 1000; i++) {
            LiveRoomRankingDO data = new LiveRoomRankingDO();
            data.setDotTime((long) i);
            data.setCategoryId((long) i);
            data.setLiveType(i);
            data.setLiveRoomId(12313L);
            data.setHostName("test hostName is batch insert");
            list.add(data);
        }

        Instant now = Instant.now();
        //批量转数组
        SqlParameterSource[] beanSources = SqlParameterSourceUtils.createBatch(list.toArray());
        String sql = "INSERT INTO live_room_ranking_syj(dot_time,category_id,live_type,live_room_id,host_name) " +
                "VALUES (:dotTime,:categoryId,:liveType,:liveRoomId,:hostName)";
        namedParameterJdbcTemplate.batchUpdate(sql, beanSources);

        System.out.println(Instant.now().toEpochMilli() - now.toEpochMilli());

    }

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
}

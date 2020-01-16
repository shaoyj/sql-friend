package util;

import com.mylomen.dao.DaoUtil;
import com.mylomen.domain.PageView;
import com.mylomen.domain.ResultHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author: Shaoyongjun
 * @date: 2018/6/26
 * @time: 上午9:19
 */
@SpringBootTest(classes = {LocationApplication.class, SqlConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ExampleForDaoUtilTest {


    @Autowired
    private DaoUtil daoUtil;

    @Test
    public void addData() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(1L);

        vendorDO.setStatus(1);
        vendorDO.setVersion(0);
        final ResultHandler resultHandler = daoUtil.insertEntity(vendorDO);

        Assert.isTrue(resultHandler.hasSuccess(), "save failed.");
    }

    @Test
    public void delData() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(1L);

        final HashMap<String, Object> map = new HashMap<>();
        map.put("status__>=", "0");
        ResultHandler resultHandler = daoUtil.deleteByEntityOrMap(vendorDO, map);
        Assert.isTrue(resultHandler.hasSuccess(), "delete failed.");

        //
        resultHandler = daoUtil.deleteByEntityOrMap(vendorDO, null);
        Assert.isTrue(resultHandler.hasSuccess(), "delete failed.");
    }


    @Test
    public void updateData() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(1L);

        vendorDO.setStatus(1);
        vendorDO.setVersion(0);

        ResultHandler resultHandler = daoUtil.updateByPrimaryKey(vendorDO);

        Assert.isTrue(resultHandler.hasSuccess(), "update failed.");

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("status", 3);
        resultHandler = daoUtil.updateEntityOrMap(vendorDO, updateMap);
        Assert.isTrue(resultHandler.hasSuccess(), "update failed.");
    }


    @Test
    public void TestFind(){
        Map<String, Object> updateMap = new HashMap<>(2);
        updateMap.put("status", 1);
        VendorDO entityByParams = daoUtil.findEntityByParams(new VendorDO(), updateMap);
        System.out.println(entityByParams);
    }

    @Test
    public void findData() {
        StringBuilder resultArgs = new StringBuilder("id");
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(1L);
        VendorDO entityByParams = daoUtil.findEntityByParams(vendorDO, null);
        System.out.println(entityByParams);
        System.out.println("########################################");
        entityByParams = daoUtil.find(vendorDO, resultArgs, null);
        System.out.println(entityByParams);
        System.out.println("########################################");


        Map<String, Object> updateMap = new HashMap<>(2);
        updateMap.put("status", 1);
        PageView<VendorDO> vendorDOPageView = daoUtil.queryForPage(vendorDO, updateMap, null);
        System.out.println(vendorDOPageView);

        System.out.println("########################################");
        vendorDOPageView = daoUtil.queryForPage(vendorDO, resultArgs, updateMap, null);
        System.out.println(vendorDOPageView);

    }

    @Test
    public void findDataPage() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setId(1L);
        final List<VendorDO> vendorDOS = daoUtil.queryForList(vendorDO, null);
        System.out.println(vendorDOS);

        StringBuilder resultArgs = new StringBuilder("id");
    }


    @Test
    public void saveOrUpdate() {
        VendorDO vendorDO = new VendorDO();
        vendorDO.setSn("syj-test-003");
        vendorDO.setStatus(1);
        vendorDO.setVersion(0);
        final ResultHandler resultHandler = daoUtil.saveOrUpdate(vendorDO, new ArrayList<String>() {
            private static final long serialVersionUID = -2429413389744741871L;

            {
                add("sn");
            }
        });

        Assert.isTrue(resultHandler.hasSuccess(), "save failed.");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void TestSaveOrUpdate() {
        String sql = "INSERT INTO test(sn,status,version)  VALUES (\"syj-test-003\",1,0)  on duplicate key update  status=9,version=0";
        jdbcTemplate.execute(sql);
    }


    @Test
    public void TestInQuery(){
        Map<String, Object> queryMap1 = new LinkedHashMap<>(4);
        queryMap1.put("sn","222");
        queryMap1.put("status__in",Arrays.asList("1","2"));
        List<VendorDO> vendorDOS = daoUtil.queryForList(new VendorDO(), queryMap1);System.out.println(vendorDOS);
        System.out.println(vendorDOS);



        StringBuilder sql=new StringBuilder("SELECT id ,sn ,status ,ctime ,mtime ,deleted ,version   FROM test WHERE 1 = 1  AND sn = ?  AND status in (?) ");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), new Object[]{"222"},Arrays.asList(1,2));
        System.out.println(maps);


        Map<String, Object> queryMap = new LinkedHashMap<>(4);
        queryMap.put("sn","222");
        queryMap.put("test_name",Arrays.asList("1","2"));
        NamedParameterJdbcTemplate namedParameterJdbcTemplate =
                new NamedParameterJdbcTemplate(jdbcTemplate);
        maps = namedParameterJdbcTemplate.queryForList("SELECT *  FROM test WHERE 1 = 1  AND sn = :sn  AND test_name in (:test_name) ", queryMap);
        System.out.println(maps);

        BeanPropertyRowMapper<VendorDO> rowMapper = new BeanPropertyRowMapper<VendorDO>(VendorDO.class);
        VendorDO user = jdbcTemplate.queryForObject("SELECT *  FROM test limit 1", rowMapper);
        System.out.println(user);
    }


}

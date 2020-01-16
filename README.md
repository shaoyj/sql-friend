# syj-sql
<h3>1.提供灵活的分表SQL封装功能</h3><br/>
<h3>2.基于注解即可实现分表的CURD功能</h3><br/>
<h3>3.分表规则可以自由扩展</h3><br/>
<br/>

部分实现参考了 BeanPropertyRowMapper <br/><br/>
<b>DEMO:</b><br/>
<h4>pom.xml</h4>

<dependency>
 &lt;dependency&gt;<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;mylomen&lt;/groupId&gt;<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;syj-sql&lt;/artifactId&gt;<br/>
  &nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;2.0.0-SNAPSHOT&lt;/version&gt;<br/>
        &lt;/dependency&gt;<br/>

<br/>
<br/>
<h4>MyloMenConfig</h4><br/>
import com.mylomen.druid.MyLoMenConfiguration;<br/>
import org.springframework.context.annotation.Configuration;<br/>
import org.springframework.context.annotation.Import;<br/>
<br/>
@Configuration<br/>
@Import({SyjSqlConfiguration.class})<br/>
public class SQLConfig {<br/>
}<br/>
<br/>

<h4>MylomenDateBO</h4>
@Table( name = "test", key = "id", tabSuffixPolicyProperty = "yyyy")<br/>
public class MylomenDateBO extends DateParserStrategy {<br/>

&nbsp;&nbsp;&nbsp;&nbsp;@Column(name = "id")<br/>
&nbsp;&nbsp;&nbsp;&nbsp;private Integer id;<br/>
<br/>
&nbsp;&nbsp;&nbsp;&nbsp;@Column(name = "age")<br/>
&nbsp;&nbsp;&nbsp;&nbsp;private Integer age;<br/>
<br/>
&nbsp;&nbsp;public Integer getId() {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;return id;<br/>
&nbsp;&nbsp;}<br/>
<br/>
&nbsp;&nbsp;public void setId(Integer id) {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;this.id = id;<br/>
&nbsp;&nbsp;}<br/>
<br/>
&nbsp;&nbsp;public Integer getAge() {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;return age;<br/>
&nbsp;&nbsp;}<br/>
<br/>
&nbsp;&nbsp;public void setAge(Integer age) {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;this.age = age;<br/>
&nbsp;&nbsp;}<br/>
<br/>


}<br/>
<br/>
<h4>HelloController</h4>
@SpringBootApplication<br/>
@RestController<br/>
public class HelloController {<br/>
<br/>
&nbsp;&nbsp; @Autowired<br/>
&nbsp;&nbsp; private NamedParameterDaoUtilImpl namedParameterDaoUtil;<br/>

<br/>
&nbsp;&nbsp;@RequestMapping(value = "/list")<br/>
&nbsp;&nbsp;public Object list() {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;MylomenDateBO mylomenDTO = new MylomenDateBO();<br/>
&nbsp;&nbsp;&nbsp;&nbsp;List&lt;MylomenBO&gt; objects = namedParameterDaoUtil.queryEntityForList(mylomenDTO, null);<br/>
&nbsp;&nbsp;&nbsp;&nbsp;return objects;<br/>
&nbsp;&nbsp;}<br/>
<br/>
<br/>
&nbsp;&nbsp;public static void main(String[] args) {<br/>
&nbsp;&nbsp;&nbsp;&nbsp;SpringApplication.run(LocalSpringApplication.class, args);<br/>
&nbsp;&nbsp;}<br/>
<br/>
 }<br/>
详细参照 com.mylomen.core.sql.NamedParameterDaoUtilImplTest
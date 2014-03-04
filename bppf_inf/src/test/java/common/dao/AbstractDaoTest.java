package common.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;



@ContextConfiguration(locations = {"/profile/applicationContext-dao.xml"})
public abstract class AbstractDaoTest extends AbstractJUnit4SpringContextTests {

}

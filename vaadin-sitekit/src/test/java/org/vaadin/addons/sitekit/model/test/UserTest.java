package org.vaadin.addons.sitekit.model.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.apache.log4j.BasicConfigurator;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Before;
import org.junit.Test;

import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.PostalAddress;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

/**
 * @author Tommi Laukkanen
 *
 */
public final class UserTest {
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "bare-site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = PROPERTIES_CATEGORY;
    /** The entity manager factory for test. */
    private static EntityManagerFactory entityManagerFactory;

    {
        BasicConfigurator.configure();
        @SuppressWarnings("rawtypes")
        final Map properties = new HashMap();
        properties.put(PersistenceUnitProperties.JDBC_DRIVER, PropertiesUtil.getProperty(PROPERTIES_CATEGORY, PersistenceUnitProperties.JDBC_DRIVER));
        properties.put(PersistenceUnitProperties.JDBC_URL, PropertiesUtil.getProperty(PROPERTIES_CATEGORY, PersistenceUnitProperties.JDBC_URL));
        properties.put(PersistenceUnitProperties.JDBC_USER, PropertiesUtil.getProperty(PROPERTIES_CATEGORY, PersistenceUnitProperties.JDBC_USER));
        properties.put(PersistenceUnitProperties.JDBC_PASSWORD, PropertiesUtil.getProperty(PROPERTIES_CATEGORY, PersistenceUnitProperties.JDBC_PASSWORD));
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PropertiesUtil.getProperty(PROPERTIES_CATEGORY, PersistenceUnitProperties.DDL_GENERATION));
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT, properties);
    }

    /** The entity manager for test. */
    private EntityManager entityManager;

    /**
     * @throws Exception if exception occurs in setup.
     */
    @Before
    public void setUp() throws Exception {

        entityManager = entityManagerFactory.createEntityManager();

    }

    /**
     * Tests User persistence.
     */
    @Test
    public void testPersistence() {
        final PostalAddress invoicingAddress = new PostalAddress("", "", "", "", "", "");
        final PostalAddress deliveryAddress = new PostalAddress("", "", "", "", "", "");
        entityManager.persist(invoicingAddress);
        entityManager.persist(deliveryAddress);

        final Company owner = new Company("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", invoicingAddress, deliveryAddress);
        entityManager.getTransaction().begin();
        entityManager.persist(owner);
        entityManager.getTransaction().commit();
        final Company loadedCompany = entityManager.find(Company.class, owner.getCompanyId());
        Assert.assertNotNull(loadedCompany);

        final Group group = new Group(owner, "test-group", "Test Group");
        UserDao.addGroup(entityManager, group);
        Assert.assertNotNull(entityManager.find(Group.class, group.getGroupId()));
        final Group loadedGroup = UserDao.getGroup(entityManager, owner, "test-group");
        Assert.assertEquals(group, loadedGroup);

        final Group group2 = new Group(owner, "test-group-2", "Test Group");
        UserDao.addGroup(entityManager, group2);

        final User user = new User(owner, "Matti", "Meikäläinen", "matti.meikalainen@biz.eelis.biz", "+358 40 12312313", "");
        UserDao.addUser(entityManager, user, group);
        final User loadedUser = UserDao.getUser(entityManager, owner, "matti.meikalainen@biz.eelis.biz");
        Assert.assertEquals(user, loadedUser);

        UserDao.addGroupMember(entityManager, group2, user);

        final List<Group> userGroups = UserDao.getUserGroups(entityManager, owner, user);
        Assert.assertEquals(2, userGroups.size());
        Assert.assertEquals(group, userGroups.get(0));
        Assert.assertEquals(group2, userGroups.get(1));

        UserDao.addUserPrivilege(entityManager, user, "test-key", "test-data");
        UserDao.addGroupPrivilege(entityManager, group, "test-key", "test-data");

        Assert.assertTrue(UserDao.hasUserPrivilege(entityManager, user, "test-key", "test-data"));
        Assert.assertTrue(UserDao.hasGroupPrivilege(entityManager, group, "test-key", "test-data"));

        Assert.assertEquals(2, UserDao.listPrivileges(entityManager, "test-key", "test-data").size());

        UserDao.removeUserPrivilege(entityManager, user, "test-key", "test-data");
        UserDao.removeGroupPrivilege(entityManager, group, "test-key", "test-data");

        Assert.assertFalse(UserDao.hasUserPrivilege(entityManager, user, "test-key", "test-data"));
        Assert.assertFalse(UserDao.hasGroupPrivilege(entityManager, group, "test-key", "test-data"));

        UserDao.removeGroupMember(entityManager, group, user);
        UserDao.removeGroupMember(entityManager, group2, user);
        UserDao.removeUser(entityManager, user);
        Assert.assertNull(UserDao.getUser(entityManager, owner, "matti.meikalainen@biz.eelis.biz"));

        UserDao.removeGroup(entityManager, group);
        Assert.assertNull(UserDao.getGroup(entityManager, owner, "test-group"));

    }
}

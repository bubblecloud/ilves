package org.vaadin.addons.sitekit.model.test;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.model.PostalAddress;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

/**
 * @author Tommi Laukkanen
 *
 */
public final class CompanyTest {
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "bare-site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = PROPERTIES_CATEGORY;
    /** The entity manager factory for test. */
    private static EntityManagerFactory entityManagerFactory;

    {
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
     * Tests Company entity persistence.
     */
    @Test
    @Ignore
    public void testCompanyPersistence() {
        final Customer customer = new Customer("First", "Last", "first.last@company.com", "+358 40 32234232", true, "Company", "234234");
        customer.setInvoicingAddress(new PostalAddress("", "", "", "", "", ""));
        customer.setDeliveryAddress(new PostalAddress("", "", "", "", "", ""));
        entityManager.getTransaction().begin();
        entityManager.persist(customer);
        entityManager.getTransaction().commit();
        Assert.assertNotNull("Verify UUID was assigned.", customer.getCustomerId());
        entityManager.clear();
        final Customer loadedCompany = entityManager.find(Customer.class, customer.getCustomerId());
        Assert.assertNotNull("Verify company was found.", loadedCompany);
        Assert.assertEquals("Verify ID", customer.getCustomerId(), loadedCompany.getCustomerId());
        Assert.assertNotNull("Verify visiting address loaded.", loadedCompany.getInvoicingAddress());
    }

}

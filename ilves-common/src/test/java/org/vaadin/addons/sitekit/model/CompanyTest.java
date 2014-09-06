package org.vaadin.addons.sitekit.model;

import org.junit.*;
import org.vaadin.addons.sitekit.util.TestUtil;

import javax.persistence.EntityManager;

/**
 * @author Tommi Laukkanen
 *
 */
public final class CompanyTest {
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = PROPERTIES_CATEGORY;

    /** The entity manager for test. */
    private EntityManager entityManager;

    @Before
    public void setUp() throws Exception {
        TestUtil.before();
        entityManager = TestUtil.getEntityManagerFactory().createEntityManager();
    }

    @After
    public void after() {
        TestUtil.after();
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

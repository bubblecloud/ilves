package org.bubblecloud.ilves.security;

import org.bubblecloud.ilves.model.AuditLogEntry;
import org.bubblecloud.ilves.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.EntityManager;

/**
 * Created by tlaukkan on 5/4/14.
 */
public class AuditServiceTest {
    private EntityManager entityManager;

    @Before
    public void before() throws Exception {
        TestUtil.before();
        entityManager = TestUtil.getEntityManagerFactory().createEntityManager();

    }

    @After
    public void after() throws Exception {
        TestUtil.after();
    }

    @Test
    public void testAuditLog() {
        final String componentAddress = "127.0.0.1:8080";
        final String componentType = "unit-test";

        final String userAddress = "127.0.0.1:12345";
        final String userId = "test-user-id";
        final String userName = "test-user-name";

        final String dataType = "test-data-type";
        final String dataId = "test-data-id";
        final String dataVersionOldId = "test-data-version-old";
        final String dataVersionNewId = "test-data-version-new";
        final String dataLabel = "test-data-label";

        final String event = "test-event";

        final AuditLogEntry auditLogEntryRecorded = AuditService.log(entityManager,
                event,
                componentAddress, componentType,
                userAddress, userId, userName,
                dataType, dataId, dataVersionOldId, dataVersionNewId, dataLabel);

        entityManager.clear();

        final AuditLogEntry auditLogEntryLoaded = AuditService.get(entityManager, auditLogEntryRecorded.getAuditLogEntryId());

        Assert.assertEquals(auditLogEntryRecorded.getAuditLogEntryId(), auditLogEntryLoaded.getAuditLogEntryId());
        Assert.assertEquals(componentAddress, auditLogEntryLoaded.getComponentAddress());
        Assert.assertEquals(componentType, auditLogEntryLoaded.getComponentType());
        Assert.assertEquals(userAddress, auditLogEntryLoaded.getUserAddress());
        Assert.assertEquals(userId, auditLogEntryLoaded.getUserId());
        Assert.assertEquals(userName, auditLogEntryLoaded.getUserName());
        Assert.assertEquals(dataType, auditLogEntryLoaded.getDataType());
        Assert.assertEquals(dataId, auditLogEntryLoaded.getDataId());
        Assert.assertEquals(dataVersionOldId, auditLogEntryLoaded.getDataOldVersionId());
        Assert.assertEquals(dataVersionNewId, auditLogEntryLoaded.getDataNewVersionId());
        Assert.assertEquals(dataLabel, auditLogEntryLoaded.getDataLabel());
    }

    @Test
    public void testAuditLogWithNullParameters() {
        final String componentAddress = "127.0.0.1:8080";
        final String componentType = "unit-test";

        final String event = "test-event";

        final AuditLogEntry auditLogEntryRecorded = AuditService.log(entityManager,
                event,
                componentAddress, componentType,
                null, null, null,
                null, null, null, null, null);

        entityManager.clear();

        final AuditLogEntry auditLogEntryLoaded = AuditService.get(entityManager, auditLogEntryRecorded.getAuditLogEntryId());

        Assert.assertEquals(auditLogEntryRecorded.getAuditLogEntryId(), auditLogEntryLoaded.getAuditLogEntryId());
        Assert.assertEquals(componentAddress, auditLogEntryLoaded.getComponentAddress());
        Assert.assertEquals(componentType, auditLogEntryLoaded.getComponentType());
        Assert.assertNull(auditLogEntryLoaded.getUserAddress());
        Assert.assertNull(auditLogEntryLoaded.getUserId());
        Assert.assertNull(auditLogEntryLoaded.getUserName());
        Assert.assertNull(auditLogEntryLoaded.getDataType());
        Assert.assertNull(auditLogEntryLoaded.getDataId());
        Assert.assertNull(auditLogEntryLoaded.getDataOldVersionId());
        Assert.assertNull(auditLogEntryLoaded.getDataNewVersionId());
        Assert.assertNull(auditLogEntryLoaded.getDataLabel());
    }

    @Test
    public void testAuditLogWithLongParameters() {
        final StringBuilder paddingBuilder = new StringBuilder();
        for (int i = 0; i < 255; i++) {
            paddingBuilder.append("X");
        }
        final String componentAddress = "127.0.0.1:8080" + paddingBuilder;
        final String componentType = "unit-test" + paddingBuilder;

        final String userAddress = "127.0.0.1:12345" + paddingBuilder;
        final String userId = "test-user-id" + paddingBuilder;
        final String userName = "test-user-name" + paddingBuilder;

        final String dataType = "test-data-type" + paddingBuilder;
        final String dataId = "test-data-id" + paddingBuilder;
        final String dataVersionOldId = "test-data-version-old" + paddingBuilder;
        final String dataVersionNewId = "test-data-version-new" + paddingBuilder;
        final String dataLabel = "test-data-label" + paddingBuilder;

        final String event = "test-event" + paddingBuilder;

        final AuditLogEntry auditLogEntryRecorded = AuditService.log(entityManager,
                event,
                componentAddress, componentType,
                userAddress, userId, userName,
                dataType, dataId, dataVersionOldId, dataVersionNewId, dataLabel);

        entityManager.clear();

        final AuditLogEntry auditLogEntryLoaded = AuditService.get(entityManager, auditLogEntryRecorded.getAuditLogEntryId());

        Assert.assertEquals(auditLogEntryRecorded.getAuditLogEntryId(), auditLogEntryLoaded.getAuditLogEntryId());
        Assert.assertEquals(auditLogEntryRecorded.getComponentAddress(), auditLogEntryLoaded.getComponentAddress());
        Assert.assertEquals(auditLogEntryRecorded.getComponentType(), auditLogEntryLoaded.getComponentType());
        Assert.assertEquals(auditLogEntryRecorded.getUserAddress(), auditLogEntryLoaded.getUserAddress());
        Assert.assertEquals(auditLogEntryRecorded.getUserId(), auditLogEntryLoaded.getUserId());
        Assert.assertEquals(auditLogEntryRecorded.getUserName(), auditLogEntryLoaded.getUserName());
        Assert.assertEquals(auditLogEntryRecorded.getDataType(), auditLogEntryLoaded.getDataType());
        Assert.assertEquals(auditLogEntryRecorded.getDataId(), auditLogEntryLoaded.getDataId());
        Assert.assertEquals(auditLogEntryRecorded.getDataOldVersionId(), auditLogEntryLoaded.getDataOldVersionId());
        Assert.assertEquals(auditLogEntryRecorded.getDataNewVersionId(), auditLogEntryLoaded.getDataNewVersionId());
        Assert.assertEquals(auditLogEntryRecorded.getDataLabel(), auditLogEntryLoaded.getDataLabel());
    }
}

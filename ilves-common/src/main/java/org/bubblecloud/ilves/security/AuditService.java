/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bubblecloud.ilves.security;

import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.AuditLogEntry;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Audit log service.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AuditService {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AuditService.class);

    /**
     * Log audit event.
     * @param securityContext the processing context
     * @param event the event
     */
    public static void log(final SecurityContext securityContext,
                           final String event) {
        log(securityContext.getAuditEntityManager(),
                event,
                securityContext.getLocalIpAddress() + ":" +
                        securityContext.getComponentPort() + " (" + securityContext.getServerName() + ")",
                securityContext.getComponentType(),
                securityContext.getRemoteIpAddress() + ":" +
                        securityContext.getRemotePort() + " (" + securityContext.getRemoteHost() + ")",
                securityContext.getUserId(),
                securityContext.getUserName(),
                null,
                null,
                null,
                null,
                null);
    }

    /**
     * Log audit event related to data
     * @param securityContext the processing context
     * @param event the event
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     */
    public static void log(final SecurityContext securityContext,
                           final String event,
                           final String dataType,
                           final String dataId,
                           final String dataLabel) {
        log(securityContext.getAuditEntityManager(),
                event,
                securityContext.getLocalIpAddress() + ":" +
                        securityContext.getComponentPort() + " (" + securityContext.getServerName() + ")",
                securityContext.getComponentType(),
                securityContext.getRemoteIpAddress() + ":" +
                        securityContext.getRemotePort() + " (" + securityContext.getRemoteHost() + ")",
                securityContext.getUserId(),
                securityContext.getUserName(),
                dataType,
                dataId,
                null,
                null,
                dataLabel);
    }

    /**
     * Log audit event related to versioned data.
     * @param securityContext the processing context
     * @param event the event
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataOldVersionId the old data version ID
     * @param dataNewVersionId the new data version ID
     * @param dataLabel the data label
     */
    public static void log(final SecurityContext securityContext,
                           final String event,
                           final String dataType,
                           final String dataId,
                           final String dataOldVersionId,
                           final String dataNewVersionId,
                           final String dataLabel) {
        log(securityContext.getAuditEntityManager(),
                event,
                securityContext.getLocalIpAddress() + ":" +
                securityContext.getComponentPort() + " (" + securityContext.getServerName() + ")",
                securityContext.getComponentType(),
                securityContext.getRemoteIpAddress() + ":" +
                securityContext.getRemotePort() + " (" + securityContext.getRemoteHost() + ")",
                securityContext.getUserId(),
                securityContext.getUserName(),
                dataType,
                dataId,
                dataOldVersionId,
                dataNewVersionId,
                dataLabel);
    }

    /**
     * Logs audit log entry.
     *
     * @param entityManager the entity manager
     * @param event the event
     * @param componentAddress the component address
     * @param componentType the component type
     * @param userAddress the user address
     * @param userId the user ID
     * @param userName the user name
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataOldVersionId the old data version ID
     * @param dataNewVersionId the new data version ID
     * @param dataLabel the data label
     * @return the audit log entry
     */
    protected static AuditLogEntry log(EntityManager entityManager,
                                    String event,
                                    String componentAddress,
                                    String componentType,
                                    String userAddress,
                                    String userId,
                                    String userName,
                                    String dataType,
                                    String dataId,
                                    String dataOldVersionId,
                                    String dataNewVersionId,
                                    String dataLabel) {
        final AuditLogEntry auditLogEntry = new AuditLogEntry(
                event,
                componentAddress,
                componentType,
                userAddress,
                userId,
                userName,
                dataType,
                dataId,
                dataOldVersionId,
                dataNewVersionId,
                dataLabel,
                new Date()
        );
        LOGGER.info(auditLogEntry);
        entityManager.getTransaction().begin();
        try {
            entityManager.persist(auditLogEntry);
            entityManager.getTransaction().commit();
            entityManager.detach(auditLogEntry);
            return auditLogEntry;
        } catch (final Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            LOGGER.error("Error writing audit log: " + auditLogEntry);
            throw new SecurityException(e);
        }
    }

    /**
     * Gets audit log entry with given audit log entry ID.
     * @param entityManager the entity manager
     * @param auditLogEntryId the audit log entry ID
     * @return the audit log entry or null if audit log entry does not exist.
     */
    protected static AuditLogEntry get(EntityManager entityManager, String auditLogEntryId) {
        return entityManager.getReference(AuditLogEntry.class, auditLogEntryId);
    }
}

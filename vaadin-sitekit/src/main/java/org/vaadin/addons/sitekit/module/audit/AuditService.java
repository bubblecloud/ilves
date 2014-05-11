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
package org.vaadin.addons.sitekit.module.audit;

import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.module.audit.model.AuditLogEntry;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import javax.persistence.EntityManager;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/**
 * Audit log service.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AuditService {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AuditService.class);

    private static String componentAddress;

    private static synchronized  String getComponentAddress() {
        if (componentAddress == null) {
            try {
                componentAddress = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                componentAddress = e.getMessage();
            }
            componentAddress += ":" + PropertiesUtil.getProperty("site", "http-port");
        }
        return componentAddress;
    }

    public static void log(EntityManager entityManager,
                           String remoteIpAddress,
                           int remotePort,
                           User user,
                           String event) {
        log(entityManager,remoteIpAddress,remotePort,user,event,
        null,null,null,null,null);
    }
    public static void log(EntityManager entityManager,
                                    String remoteIpAddress,
                                    int remotePort,
                                    User user,
                                    String event,
                                    String dataType,
                                    String dataId,
                                    String dataOldVersionId,
                                    String dataNewVersionId,
                                    String dataLabel) {
        log(entityManager, event, getComponentAddress(), "web",
                remoteIpAddress + ":" + remotePort,
                user.getUserId(), user.getEmailAddress(),
                dataType, dataId, dataOldVersionId, dataNewVersionId, dataLabel);
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
    public static AuditLogEntry log(EntityManager entityManager,
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
    public static AuditLogEntry get(EntityManager entityManager, String auditLogEntryId) {
        return entityManager.getReference(AuditLogEntry.class, auditLogEntryId);
    }
}

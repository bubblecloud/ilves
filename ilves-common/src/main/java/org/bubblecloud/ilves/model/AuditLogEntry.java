package org.bubblecloud.ilves.model;

import org.apache.commons.lang.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * AuditLogEntry.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "auditlogentry")
public final class AuditLogEntry implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String auditLogEntryId;

    /** Action. */
    @Column(nullable = false)
    private String event;

    /** Component IP address and port. */
    @Column(nullable = false)
    private String componentAddress;

    /** Component Type. */
    @Column(nullable = false)
    private String componentType;

    /** User IP address and port. */
    @Column(nullable = true)
    private String userAddress;

    /** User ID. */
    @Column(nullable = true)
    private String userId;

    /** User Name. */
    @Column(nullable = true)
    private String userName;

    /** Data Type. */
    @Column(nullable = true)
    private String dataType;

    /** Data ID. */
    @Column(nullable = true)
    private String dataId;

    /** Data Old Version ID. */
    @Column(nullable = true)
    private String dataOldVersionId;

    /** Data New Version ID. */
    @Column(nullable = true)
    private String dataNewVersionId;

    /** Data Label. */
    @Column(nullable = true)
    private String dataLabel;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /**
     * The default constructor.
     */
    public AuditLogEntry() {
        super();
    }

    public AuditLogEntry(String event, String componentAddress, String componentType, String userAddress, String userId, String userName, String dataType, String dataId, String dataOldVersionId, String dataNewVersionId, String dataLabel, Date created) {
        this.event =  StringUtils.abbreviate(event, 255);
        this.componentAddress = StringUtils.abbreviate(componentAddress, 60);
        this.componentType = StringUtils.abbreviate(componentType, 20);
        this.userAddress = StringUtils.abbreviate(userAddress, 60);
        this.userId = StringUtils.abbreviate(userId, 36);
        this.userName = StringUtils.abbreviate(userName, 40);
        this.dataType = StringUtils.abbreviate(dataType, 20);
        this.dataId = StringUtils.abbreviate(dataId, 36);
        this.dataOldVersionId = StringUtils.abbreviate(dataOldVersionId, 36);
        this.dataNewVersionId = StringUtils.abbreviate(dataNewVersionId, 36);
        this.dataLabel = StringUtils.abbreviate(dataLabel, 40);
        this.created = created;
    }

    public String getAuditLogEntryId() {
        return auditLogEntryId;
    }

    public String getEvent() {
        return event;
    }

    public String getComponentAddress() {
        return componentAddress;
    }

    public String getComponentType() {
        return componentType;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getDataType() {
        return dataType;
    }

    public String getDataId() {
        return dataId;
    }

    public String getDataOldVersionId() {
        return dataOldVersionId;
    }

    public String getDataNewVersionId() {
        return dataNewVersionId;
    }

    public String getDataLabel() {
        return dataLabel;
    }

    public Date getCreated() {
        return created;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Audit >>> ");
        if (userName != null) {
            builder.append(userName);
        }
        if (event != null) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(event);
        }

        if (dataLabel != null) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(dataLabel);
            builder.append('.');
        }

        if (dataId != null) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append("ID: '");
            builder.append(dataId);
            builder.append("' Type: '");
            builder.append(dataType);
            builder.append("'");
        }

        if (dataOldVersionId != null || dataNewVersionId != null) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(" Versions: '");
            builder.append(dataOldVersionId);
            builder.append("' -> '");
            builder.append(dataNewVersionId);
            builder.append("'");
        }
        return builder.toString();
    }

    @Override
    public int hashCode() {
        return auditLogEntryId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Group && auditLogEntryId.equals(((Group) obj).getGroupId());
    }
}

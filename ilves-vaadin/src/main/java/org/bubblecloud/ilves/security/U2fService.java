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

import com.yubico.u2f.data.DeviceRegistration;
import org.bubblecloud.ilves.model.AuthenticationDevice;
import org.bubblecloud.ilves.model.AuthenticationDeviceType;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.site.SiteContext;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Universal second factor (U2F) service.
 *
 * @author Tommi S.E. Laukkanen
 */
public class U2fService {

    /**
     * Checks if user has device registrations.
     * @param context the security context
     * @param emailAddress the email address
     * @return true if device registrations exist
     */
    public static boolean hasDeviceRegistrations(final SiteContext context, final String emailAddress) {
        final Company company = context.getObject(Company.class);
        final EntityManager entityManager = context.getEntityManager();
        final User user = UserDao.getUser(entityManager, company, emailAddress);
        final List<AuthenticationDevice> authenticationDevices = AuthenticationDeviceDao.getAuthenticationDevices(entityManager, user);

        for (final AuthenticationDevice authenticationDevice : authenticationDevices) {
            if (authenticationDevice.getType() == AuthenticationDeviceType.UNIVERSAL_SECOND_FACTOR) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets device registrations.
     * @param context the security context
     * @param emailAddress the email address
     * @return list of device registrations
     */
    public static List<DeviceRegistration> getDeviceRegistrations(final SiteContext context, final String emailAddress) {
        final Company company = context.getObject(Company.class);
        final EntityManager entityManager = context.getEntityManager();
        final User user = UserDao.getUser(entityManager, company, emailAddress);
        final List<AuthenticationDevice> authenticationDevices = AuthenticationDeviceDao.getAuthenticationDevices(entityManager, user);

        final List<DeviceRegistration> deviceRegistrations = new ArrayList<>();
        for (final AuthenticationDevice authenticationDevice : authenticationDevices) {
            if (authenticationDevice.getType() == AuthenticationDeviceType.UNIVERSAL_SECOND_FACTOR) {
                final String secret = SecurityUtil.decryptSecretKey(authenticationDevice.getEncryptedSecret());
                final DeviceRegistration deviceRegistration = DeviceRegistration.fromJson(secret);
                deviceRegistrations.add(deviceRegistration);
            }
        }
        return deviceRegistrations;
    }

    /**
     * Adds device registration.
     * @param context the context
     * @param emailAddress the email address
     * @param deviceRegistration the device registration
     */
    public static void addDeviceRegistration(final SiteContext context, final String emailAddress, final DeviceRegistration deviceRegistration) {
        final Company company = context.getObject(Company.class);
        final EntityManager entityManager = context.getEntityManager();
        final User user = UserDao.getUser(entityManager, company, emailAddress);

        final String secret = deviceRegistration.toJson();
        final String encryptedSecret = SecurityUtil.encryptSecretKey(secret);

        final AuthenticationDevice authenticationDevice = new AuthenticationDevice();

        authenticationDevice.setKey(deviceRegistration.getKeyHandle());
        String name;
        try {
            name = deviceRegistration.getAttestationCertificate().getSubjectDN().toString();
            if (name.startsWith("CN=")) {
                name = name.substring(3);
            }
        } catch (final Exception e) {
            name = "u2f device";
        }
        authenticationDevice.setName(name);

        authenticationDevice.setType(AuthenticationDeviceType.UNIVERSAL_SECOND_FACTOR);
        authenticationDevice.setUser(user);
        authenticationDevice.setEncryptedSecret(encryptedSecret);

        AuthenticationDeviceDao.addAuthenticationDevice(entityManager, authenticationDevice);
    }

    /**
     * Updates device registration.
     * @param context the context
     * @param emailAddress the email address
     * @param deviceRegistration the device registration
     */
    public static void updateDeviceRegistration(final SiteContext context, final String emailAddress, final DeviceRegistration deviceRegistration) {
        final Company company = context.getObject(Company.class);
        final EntityManager entityManager = context.getEntityManager();
        final User user = UserDao.getUser(entityManager, company, emailAddress);
        final String secret = deviceRegistration.toJson();
        final String encryptedSecret = SecurityUtil.encryptSecretKey(secret);

        final AuthenticationDevice authenticationDevice = AuthenticationDeviceDao.getAuthenticationDeviceByKey(entityManager, deviceRegistration.getKeyHandle());
        if (!user.getUserId().equals(authenticationDevice.getUser().getUserId())) {
            throw new SecurityException("Authentication device user mismatch.");
        }
        authenticationDevice.setEncryptedSecret(encryptedSecret);
        AuthenticationDeviceDao.updateAuthenticationDevice(entityManager, authenticationDevice);
    }

}

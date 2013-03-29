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
package org.vaadin.addons.sitekit.model;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.Session;

import java.util.UUID;
import java.util.Vector;

/**
 * Sequence for generating string ID based on UUID class.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class UuidSequence extends Sequence implements SessionCustomizer {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * The constructor.
     */
    public UuidSequence() {
        super();
    }

    /**
     * The constructor which allows setting name.
     * @param name Name of the sequence.
     */
    public UuidSequence(final String name) {
        super(name);
    }

    @Override
    public Object getGeneratedValue(final Accessor accessor,
            final AbstractSession writeSession, final String seqName) {
        return UUID.randomUUID().toString().toUpperCase();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Vector getGeneratedVector(final Accessor accessor,
            final AbstractSession writeSession, final String seqName, final int size) {
        return null;
    }

    @Override
    protected void onConnect() {
    }

    @Override
    protected void onDisconnect() {
    }

    @Override
    public boolean shouldAcquireValueAfterInsert() {
        return false;
    }

    @Override
    public boolean shouldUseTransaction() {
        return false;
    }

    @Override
    public boolean shouldUsePreallocation() {
        return false;
    }

    @Override
    public void customize(final Session session) throws Exception {
        final UuidSequence sequence = new UuidSequence("uuid");
        session.getLogin().addSequence(sequence);
    }

}

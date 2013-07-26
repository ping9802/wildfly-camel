/*
 * #%L
 * Wildfly Camel Subsystem
 * %%
 * Copyright (C) 2013 JBoss by Red Hat
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */


package org.wildfly.camel.deployment;

import static org.wildfly.camel.CamelMessages.MESSAGES;

import org.apache.camel.CamelContext;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.wildfly.camel.CamelConstants;

/**
 * Start/Stop the {@link CamelContext}
 *
 * @author Thomas.Diesler@jboss.com
 * @since 22-Apr-2013
 */
public class CamelContextActivationProcessor implements DeploymentUnitProcessor {

    @Override
    public void deploy(final DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {
        DeploymentUnit depUnit = phaseContext.getDeploymentUnit();
        CamelContext camelctx = depUnit.getAttachment(CamelConstants.CAMEL_CONTEXT_KEY);
        if (camelctx == null)
            return;

        // Start the camel context
        try {
            camelctx.start();
        } catch (Exception ex) {
            throw MESSAGES.cannotStartCamelContext(ex, camelctx);
        }
    }

    @Override
    public void undeploy(final DeploymentUnit depUnit) {
        // Stop the camel context
        CamelContext camelctx = depUnit.getAttachment(CamelConstants.CAMEL_CONTEXT_KEY);
        if (camelctx != null) {
            try {
                camelctx.stop();
            } catch (Exception ex) {
                throw MESSAGES.cannotStopCamelContext(ex, camelctx);
            }
        }
    }
}

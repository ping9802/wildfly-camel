/*
 * #%L
 * Wildfly Camel Testsuite
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

package org.wildfly.camel.test.smoke;

import java.io.InputStream;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.gravia.resource.ManifestBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.wildfly.camel.CamelContextFactory;
import org.wildfly.camel.test.smoke.subA.HelloBean;

/**
 * Deploys a module/bundle which contain a {@link HelloBean}.
 *
 * The tests then build a route that uses the bean through the Camel API.
 * This verifies access to beans within the same deployemnt that uses the Camel API.
 *
 * @author thomas.diesler@jboss.com
 * @since 24-Apr-2013
 */
@RunWith(Arquillian.class)
public class BeanTransformTestCase {

    @ArquillianResource
    CamelContextFactory contextFactory;

    @ArquillianResource
    Deployer deployer;

    @Deployment
    public static JavaArchive createdeployment() {
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "bean-transform-tests");
        archive.addClasses(HelloBean.class);
        archive.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                ManifestBuilder builder = new ManifestBuilder();
                builder.addManifestHeader("Dependencies", "org.apache.camel");
                return builder.openStream();
            }
        });
        return archive;
    }

    @Test
    public void testSimpleTransformFromModule() throws Exception {
        CamelContext camelctx = contextFactory.createWilflyCamelContext();
        camelctx.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("direct:start").bean(HelloBean.class);
            }
        });
        camelctx.start();
        ProducerTemplate producer = camelctx.createProducerTemplate();
        String result = producer.requestBody("direct:start", "Kermit", String.class);
        Assert.assertEquals("Hello Kermit", result);
    }
}

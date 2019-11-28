/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.cxf.systest.jaxrs.security.oauth2.grants;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.form.Form;
import org.apache.cxf.systest.jaxrs.security.oauth2.common.OAuth2TestUtils;
import org.apache.cxf.testutil.common.AbstractBusClientServerTestBase;
import org.apache.cxf.testutil.common.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;


/**
 * Some (negative) tests for various authorization grants.
 */
public class AuthorizationGrantNegativeTest extends AbstractBusClientServerTestBase {
    public static final String PORT = BookServerOAuth2GrantsNegative.PORT;
    public static final String PORT2 = TestUtil.getPortNumber("jaxrs-oauth2-grants2-negative");

    @BeforeClass
    public static void startServers() throws Exception {
        assertTrue("server did not launch correctly",
                launchServer(BookServerOAuth2GrantsNegative.class, true));
    }

     // Here we are sending a different client Id in both the authz + token requests
    @org.junit.Test
    public void testNonMatchingClientId() throws Exception {
        URL busFile = AuthorizationGrantNegativeTest.class.getResource("client.xml");

        String address = "https://localhost:" + PORT + "/services/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "alice", "security", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        // Get Authorization Code
        String code = OAuth2TestUtils.getAuthorizationCode(client);
        assertNotNull(code);

        // Now get the access token using a different client id
        client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "consumer-id-aud", "this-is-a-secret", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        client.type("application/x-www-form-urlencoded").accept("application/json");
        client.path("token");

        Form form = new Form();
        form.set("grant_type", "authorization_code");
        form.set("code", code);
        form.set("client_id", "consumer-id-aud");

        // Now try to get a token
        final Response post = client.post(form, Response.class);
        Assert.assertEquals(400, post.getStatus());
        Assert.assertEquals("{\"error\":\"invalid_grant\"}", slurp((InputStream) post.getEntity()));
    }

    // Here we are sending a different client Id in both the authz + token requests
    @org.junit.Test
    public void testNonMatchingClientIdBasicAuth() throws Exception {
        URL busFile = AuthorizationGrantNegativeTest.class.getResource("client.xml");

        String address = "https://localhost:" + PORT + "/services/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "alice", "security", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        // Get Authorization Code
        String code = OAuth2TestUtils.getAuthorizationCode(client);
        assertNotNull(code);

        // Now get the access token using a different client id
        client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "consumer-id-aud", "this-is-a-secret", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        client.type("application/x-www-form-urlencoded").accept("application/json");
        client.path("token");

        Form form = new Form();
        form.set("grant_type", "authorization_code");
        form.set("code", code);

        // Now try to get a token
        final Response post = client.post(form, Response.class);
        Assert.assertEquals(400, post.getStatus());
        Assert.assertEquals("{\"error\":\"invalid_grant\"}", slurp((InputStream) post.getEntity()));
    }

    // Here we are sending a different client Id in both the authz + token requests, where in the
    // token request we authenticate using a different clientId
    @org.junit.Test
    public void testNonMatchingClientDifferentClientIds() throws Exception {
        URL busFile = AuthorizationGrantNegativeTest.class.getResource("client.xml");

        String address = "https://localhost:" + PORT + "/services/";
        WebClient client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "alice", "security", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        // Get Authorization Code
        String code = OAuth2TestUtils.getAuthorizationCode(client);
        assertNotNull(code);

        // Now get the access token using a different client id
        client = WebClient.create(address, OAuth2TestUtils.setupProviders(),
                "consumer-id-aud", "this-is-a-secret", busFile.toString());
        // Save the Cookie for the second request...
        WebClient.getConfig(client).getRequestContext().put(
                org.apache.cxf.message.Message.MAINTAIN_SESSION, Boolean.TRUE);

        client.type("application/x-www-form-urlencoded").accept("application/json");
        client.path("token");

        Form form = new Form();
        form.set("grant_type", "authorization_code");
        form.set("code", code);
        form.set("client_id", "consumer-id");

        // Now try to get a token
        final Response post = client.post(form, Response.class);
        Assert.assertEquals(400, post.getStatus());
        Assert.assertEquals("{\"error\":\"invalid_client\"}", slurp((InputStream) post.getEntity()));
    }

    private static String slurp(final InputStream is) throws Exception {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        final byte[] buffer = new byte[8192];
        int bytesRead = 1;

        while ((bytesRead = is.read(buffer)) > -1) {
            os.write(buffer, 0, bytesRead);
        }

        return new String(os.toByteArray());
    }


}
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
package org.apache.cxf.jaxrs.nio;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.cxf.jaxrs.utils.JAXRSUtils;
import org.apache.cxf.message.Message;
import org.apache.cxf.transport.http.AbstractHTTPDestination;

public class NioReadEntity {
    private final NioReadHandler reader;
    private final NioReadCompletionHandler completion;
    private final NioErrorHandler error;

    public NioReadEntity(NioReadHandler reader, NioReadCompletionHandler completion, NioErrorHandler error) {
        this.reader = reader;
        this.completion = completion;
        this.error = error;
        
        try {
            final Message m = JAXRSUtils.getCurrentMessage();
            if (m != null) {
                final HttpServletRequest request = (HttpServletRequest)m.get(AbstractHTTPDestination.HTTP_REQUEST);
                if (request != null) {
                    request.getInputStream().setReadListener(new NioReadListenerImpl(this, request.getInputStream()));
                }
            }
        } catch (final IOException ex) {
            throw new RuntimeException("Unable to initialize NIO entity", ex);
        }
    }

    public NioReadHandler getReader() {
        return reader;
    }

    public NioReadCompletionHandler getCompletion() {
        return completion;
    }

    public NioErrorHandler getError() {
        return error;
    }
}
/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * The OpenSearch Contributors require contributions made to
 * this file be licensed under the Apache-2.0 license or a
 * compatible open source license.
 */

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/*
 * Modifications Copyright OpenSearch Contributors. See
 * GitHub history for details.
 */

package org.opensearch.index.reindex;

import org.opensearch.common.xcontent.NamedXContentRegistry;
import org.opensearch.rest.RestRequest;
import org.opensearch.rest.action.search.RestSearchAction;
import org.opensearch.test.rest.FakeRestRequest;
import org.opensearch.test.rest.RestActionTestCase;
import org.junit.Before;

import java.io.IOException;

import static java.util.Collections.emptyList;

public class RestDeleteByQueryActionTests extends RestActionTestCase {
    private RestDeleteByQueryAction action;

    @Before
    public void setUpAction() {
        action = new RestDeleteByQueryAction();
        controller().registerHandler(action);
    }

    public void testTypeInPath() throws IOException {
        RestRequest request = new FakeRestRequest.Builder(xContentRegistry())
            .withMethod(RestRequest.Method.POST)
            .withPath("/some_index/some_type/_delete_by_query")
            .build();

        // We're not actually testing anything to do with the client, but need to set this so it doesn't fail the test for being unset.
        verifyingClient.setExecuteLocallyVerifier((arg1, arg2) -> null);

        dispatchRequest(request);

        // checks the type in the URL is propagated correctly to the request object
        // only works after the request is dispatched, so its params are filled from url.
        DeleteByQueryRequest dbqRequest = action.buildRequest(request, DEFAULT_NAMED_WRITABLE_REGISTRY);
        assertArrayEquals(new String[]{"some_type"}, dbqRequest.getDocTypes());

        // RestDeleteByQueryAction itself doesn't check for a deprecated type usage
        // checking here for a deprecation from its internal search request
        assertWarnings(RestSearchAction.TYPES_DEPRECATION_MESSAGE);
    }

    public void testParseEmpty() throws IOException {
        final FakeRestRequest restRequest = new FakeRestRequest.Builder(new NamedXContentRegistry(emptyList())).build();
        DeleteByQueryRequest request = action.buildRequest(restRequest, DEFAULT_NAMED_WRITABLE_REGISTRY);
        assertEquals(AbstractBulkByScrollRequest.SIZE_ALL_MATCHES, request.getSize());
        assertEquals(AbstractBulkByScrollRequest.DEFAULT_SCROLL_SIZE, request.getSearchRequest().source().size());
    }
}

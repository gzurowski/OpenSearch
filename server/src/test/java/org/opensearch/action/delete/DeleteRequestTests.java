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

package org.opensearch.action.delete;

import org.opensearch.action.ActionRequestValidationException;
import org.opensearch.test.OpenSearchTestCase;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;

public class DeleteRequestTests extends OpenSearchTestCase {

    public void testValidation() {
        {
            final DeleteRequest request = new DeleteRequest("index4", "_doc", "0");
            final ActionRequestValidationException validate = request.validate();

            assertThat(validate, nullValue());
        }

        {
            //Empty types are accepted but fail validation
            final DeleteRequest request = new DeleteRequest("index4",  "",  randomBoolean() ? "" : null);
            final ActionRequestValidationException validate = request.validate();

            assertThat(validate, not(nullValue()));
            assertThat(validate.validationErrors(), hasItems("type is missing", "id is missing"));
        }
        {
            // Null types are defaulted
            final DeleteRequest request = new DeleteRequest("index4", randomBoolean() ? "" : null);
            final ActionRequestValidationException validate = request.validate();

            assertThat(validate, not(nullValue()));
            assertThat(validate.validationErrors(), hasItems("id is missing"));
        }
    }
}

/*
 * Copyright 2016-2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.data;

import org.junit.jupiter.api.Test;

import uk.gov.gchq.gaffer.commonutil.TestGroups;
import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IsEntityValidatorTest {

    @Test
    public void shouldValidateWhenEntity() {
        final Element element = new Entity.Builder()
                .group(TestGroups.ENTITY)
                .build();

        final boolean valid = new IsEntityValidator().validate(element);

        assertTrue(valid);
    }

    @Test
    public void shouldNotValidateWhenEdge() {
        final Element element = new Edge.Builder()
                .group(TestGroups.EDGE)
                .build();

        final boolean valid = new IsEntityValidator().validate(element);

        assertFalse(valid);
    }
}

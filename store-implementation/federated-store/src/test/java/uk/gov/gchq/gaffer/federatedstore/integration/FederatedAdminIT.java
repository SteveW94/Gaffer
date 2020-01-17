/*
 * Copyright 2020 Crown Copyright
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
package uk.gov.gchq.gaffer.federatedstore.integration;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.federatedstore.FederatedStoreConstants;
import uk.gov.gchq.gaffer.federatedstore.PublicAccessPredefinedFederatedStore;
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraph;
import uk.gov.gchq.gaffer.federatedstore.operation.GetAllGraphIds;
import uk.gov.gchq.gaffer.federatedstore.operation.GetAllGraphInfo;
import uk.gov.gchq.gaffer.federatedstore.operation.RemoveGraph;
import uk.gov.gchq.gaffer.integration.AbstractStoreIT;
import uk.gov.gchq.gaffer.store.StoreProperties;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.user.User;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FederatedAdminIT extends AbstractStoreIT {

    public static final User ADMIN_USER = new User("admin", Collections.EMPTY_SET, Sets.newHashSet("AdminAuth"));
    public static final User NOT_ADMIN_USER = new User("admin", Collections.EMPTY_SET, Sets.newHashSet("NotAdminAuth"));

    @Override
    protected Schema createSchema() {
        final Schema.Builder schemaBuilder = new Schema.Builder(createDefaultSchema());
        schemaBuilder.edges(Collections.EMPTY_MAP);
        schemaBuilder.entities(Collections.EMPTY_MAP);
        return schemaBuilder.build();
    }

    @Before
    public void setUp() throws Exception {
        graph.execute(new RemoveGraph.Builder()
                .graphId(PublicAccessPredefinedFederatedStore.ACCUMULO_GRAPH_WITH_EDGES)
                .build(), user);
        graph.execute(new RemoveGraph.Builder()
                .graphId(PublicAccessPredefinedFederatedStore.ACCUMULO_GRAPH_WITH_ENTITIES)
                .build(), user);
    }

    @Test
    public void shouldGetAllGraphInfoForAdmin() throws Exception {
        //given
        final String graphA = "graphA";
        graph.execute(new AddGraph.Builder()
                .graphId(graphA)
                .schema(new Schema())
                .storeProperties(StoreProperties.loadStoreProperties(StreamUtil.openStream(getClass(), "properties/singleUseMockAccStore.properties")))
                .graphAuths("authsValue1")
                .build(), user);
        assertTrue(Lists.newArrayList(graph.execute(new GetAllGraphIds(), user)).contains(graphA));
        final String expectedFedAccess = "FederatedAccess[addingUserId=UNKNOWN,graphAuths=[authsValue1],isPublic=false,disabledByDefault=false]";

        //when
        final Map<String, Object> allGraphsAndAuths = graph.execute(new GetAllGraphInfo.Builder()
                .option(FederatedStoreConstants.KEY_FEDERATION_ADMIN, "true")
                .build(), ADMIN_USER);

        //then
        assertNotNull(allGraphsAndAuths);
        assertFalse(allGraphsAndAuths.isEmpty());
        assertEquals(graphA, allGraphsAndAuths.keySet().toArray(new String[]{})[0]);
        assertEquals(expectedFedAccess, allGraphsAndAuths.values().toArray(new String[]{})[0]);

    }

    @Test
    public void shouldNotGetAllGraphInfoForNonAdmin() throws Exception {
        //given
        final String graphA = "graphA";
        graph.execute(new AddGraph.Builder()
                .graphId(graphA)
                .schema(new Schema())
                .storeProperties(StoreProperties.loadStoreProperties(StreamUtil.openStream(getClass(), "properties/singleUseMockAccStore.properties")))
                .build(), user);
        assertTrue(Lists.newArrayList(graph.execute(new GetAllGraphIds(), user)).contains(graphA));

        //when
        final Map<String, Object> allGraphsAndAuths = graph.execute(new GetAllGraphInfo.Builder().build(), NOT_ADMIN_USER);

        assertNotNull(allGraphsAndAuths);
        assertTrue(allGraphsAndAuths.isEmpty());
    }

    @Test
    public void shouldNotGetAllGraphInfoForNonAdminWithAdminDeclarationsInOption() throws Exception {
        //given
        final String graphA = "graphA";
        graph.execute(new AddGraph.Builder()
                .graphId(graphA)
                .schema(new Schema())
                .storeProperties(StoreProperties.loadStoreProperties(StreamUtil.openStream(getClass(), "properties/singleUseMockAccStore.properties")))
                .build(), user);
        assertTrue(Lists.newArrayList(graph.execute(new GetAllGraphIds(), user)).contains(graphA));

        //when
        final Map<String, Object> allGraphsAndAuths = graph.execute(new GetAllGraphInfo.Builder()
                .option(FederatedStoreConstants.KEY_FEDERATION_ADMIN, "true")
                .build(), NOT_ADMIN_USER);

        assertNotNull(allGraphsAndAuths);
        assertTrue(allGraphsAndAuths.isEmpty());
    }

    @Test
    public void shouldNotGetAllGraphInfoForAdminWithoutAdminDeclartionInOptions() throws Exception {
        //given
        final String graphA = "graphA";
        graph.execute(new AddGraph.Builder()
                .graphId(graphA)
                .schema(new Schema())
                .storeProperties(StoreProperties.loadStoreProperties(StreamUtil.openStream(getClass(), "properties/singleUseMockAccStore.properties")))
                .build(), user);
        assertTrue(Lists.newArrayList(graph.execute(new GetAllGraphIds(), user)).contains(graphA));

        //when
        final Map<String, Object> allGraphsAndAuths = graph.execute(new GetAllGraphInfo.Builder().build(), ADMIN_USER);

        assertNotNull(allGraphsAndAuths);
        assertTrue(allGraphsAndAuths.isEmpty());
    }
}

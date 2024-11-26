package org.apache.shardingsphere.infra.database.kingbase.type;

import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.ServiceLoader;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class KingbaseDatabaseTypeTest {
    @Test
    void assertGetJdbcUrlPrefixes() {
        assertThat(TypedSPILoader.getService(DatabaseType.class, "Kingbase").getJdbcUrlPrefixes(), is(Collections.singletonList("jdbc:kingbase8:")));
    }

    @Test
    void serviceLoader() {
        ServiceLoader.load(DatabaseType.class).forEach(databaseType -> {
            assertThat(databaseType.getType(), is("Kingbase"));
        });
    }
}



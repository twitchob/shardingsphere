package org.apache.shardingsphere.sql.parser.postgresql.parser;

import org.apache.shardingsphere.infra.database.core.spi.DatabaseTypedSPILoader;
import org.apache.shardingsphere.infra.database.core.type.DatabaseType;
import org.apache.shardingsphere.infra.database.kingbase.type.KingbaseDatabaseType;
import org.apache.shardingsphere.infra.database.mysql.type.MySQLDatabaseType;
import org.apache.shardingsphere.infra.database.postgresql.type.PostgreSQLDatabaseType;
import org.apache.shardingsphere.infra.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sql.parser.spi.DialectSQLParserFacade;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;


class PostgreSQLParserFacadeTest {
    @Test
    public void testSPI() {
        DialectSQLParserFacade service = DatabaseTypedSPILoader.getService(DialectSQLParserFacade.class, new PostgreSQLDatabaseType());
        System.out.println(service.getType());
    }

    @Test
    public void testSPI2() {
        Optional<DialectSQLParserFacade> service = TypedSPILoader.findService(DialectSQLParserFacade.class, new PostgreSQLDatabaseType());
        System.out.println(service.get().getType());
    }

    @Test
    public void testSPI3() {
        DialectSQLParserFacade service = TypedSPILoader.getService(DialectSQLParserFacade.class, new PostgreSQLDatabaseType());
        System.out.println(service.getType());
    }
    @Test
    public void testSPI4() {
        Collection<DialectSQLParserFacade> serviceInstances = ShardingSphereServiceLoader.getServiceInstances(DialectSQLParserFacade.class);
        for (DialectSQLParserFacade each : serviceInstances) {
            System.out.println(each.getType());
        }
    }
}
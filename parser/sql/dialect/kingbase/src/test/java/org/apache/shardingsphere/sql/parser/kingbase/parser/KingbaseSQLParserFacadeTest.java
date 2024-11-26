package org.apache.shardingsphere.sql.parser.kingbase.parser;


import org.apache.shardingsphere.infra.database.core.spi.DatabaseTypedSPILoader;
import org.apache.shardingsphere.infra.database.kingbase.type.KingbaseDatabaseType;
import org.apache.shardingsphere.infra.database.mysql.type.MySQLDatabaseType;
import org.apache.shardingsphere.infra.spi.type.typed.TypedSPILoader;
import org.apache.shardingsphere.sql.parser.spi.DialectSQLParserFacade;
import org.junit.jupiter.api.Test;

import java.util.Optional;

class KingbaseSQLParserFacadeTest {



    @Test
    public void testSPI() {
        DialectSQLParserFacade service = DatabaseTypedSPILoader.getService(DialectSQLParserFacade.class, new MySQLDatabaseType());
        System.out.println(service.getType());
    }

    @Test
    public void testSPI2() {
        Optional<DialectSQLParserFacade> service = TypedSPILoader.findService(DialectSQLParserFacade.class, new KingbaseDatabaseType());
        System.out.println(service.get().getType());
    }


}
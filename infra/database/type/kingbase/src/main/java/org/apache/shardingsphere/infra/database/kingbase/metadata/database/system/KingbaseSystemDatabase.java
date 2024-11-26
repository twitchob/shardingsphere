/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.infra.database.kingbase.metadata.database.system;

import org.apache.shardingsphere.infra.database.core.metadata.database.system.DialectSystemDatabase;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * System database of PostgreSQL.
 */
public final class KingbaseSystemDatabase implements DialectSystemDatabase {

    private static final Map<String, Collection<String>> SYSTEM_DATABASE_SCHEMA_MAP = new HashMap<>();

    private static final Collection<String> SYSTEM_SCHEMAS = new HashSet<>(Arrays.asList(
//            "anon", "dbms_sql", "perf", "pg_bitmapindex", "pg_temp_1", "pg_toast", "pg_toast_temp_1",
//            "public", "src_restrict", "sys", "sys_catalog", "SYS_HM", "sysaudit", "sysmac",
//            "xlog_record_read"
    ));

    static {
        SYSTEM_DATABASE_SCHEMA_MAP.put("kingbase", SYSTEM_SCHEMAS);
        SYSTEM_DATABASE_SCHEMA_MAP.put("security", SYSTEM_SCHEMAS);
        SYSTEM_DATABASE_SCHEMA_MAP.put("template0", SYSTEM_SCHEMAS);
        SYSTEM_DATABASE_SCHEMA_MAP.put("template1", SYSTEM_SCHEMAS);
        SYSTEM_DATABASE_SCHEMA_MAP.put("test", SYSTEM_SCHEMAS);

    }

    @Override
    public Map<String, Collection<String>> getSystemDatabaseSchemaMap() {
        return SYSTEM_DATABASE_SCHEMA_MAP;
    }

    @Override
    public Collection<String> getSystemSchemas() {
        return SYSTEM_SCHEMAS;
    }

    @Override
    public String getDatabaseType() {
        return "Kingbase";
    }
}

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

package org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.bind;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.db.protocol.packet.DatabasePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.bind.PostgreSQLBindCompletePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.bind.PostgreSQLComBindPacket;
import org.apache.shardingsphere.proxy.backend.connector.ProxyDatabaseConnectionManager;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.command.executor.CommandExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.PortalContext;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.Portal;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.KingbaseServerPreparedStatement;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Command bind executor for PostgreSQL.
 */
@RequiredArgsConstructor
public final class KingbaseComBindExecutor implements CommandExecutor {
    
    private final PortalContext portalContext;
    
    private final PostgreSQLComBindPacket packet;
    
    private final ConnectionSession connectionSession;
    
    @Override
    public Collection<DatabasePacket> execute() throws SQLException {
        KingbaseServerPreparedStatement preparedStatement = connectionSession.getServerPreparedStatementRegistry().getPreparedStatement(packet.getStatementId());
        ProxyDatabaseConnectionManager databaseConnectionManager = connectionSession.getDatabaseConnectionManager();
        List<Object> parameters = preparedStatement.adjustParametersOrder(packet.readParameters(preparedStatement.getParameterTypes()));
        Portal portal = new Portal(packet.getPortal(), preparedStatement, parameters, packet.readResultFormats(), databaseConnectionManager);
        portalContext.add(portal);
        portal.bind();
        return Collections.singleton(PostgreSQLBindCompletePacket.getInstance());
    }
}

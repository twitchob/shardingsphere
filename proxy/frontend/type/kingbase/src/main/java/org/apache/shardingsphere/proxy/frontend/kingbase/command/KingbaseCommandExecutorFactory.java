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

package org.apache.shardingsphere.proxy.frontend.kingbase.command;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.db.protocol.packet.sql.SQLReceivedPacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.PostgreSQLCommandPacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.PostgreSQLCommandPacketType;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.PostgreSQLAggregatedCommandPacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.bind.PostgreSQLComBindPacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.close.PostgreSQLComClosePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.describe.PostgreSQLComDescribePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.execute.PostgreSQLComExecutePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.extended.parse.PostgreSQLComParsePacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.command.query.simple.PostgreSQLComQueryPacket;
import org.apache.shardingsphere.proxy.backend.session.ConnectionSession;
import org.apache.shardingsphere.proxy.frontend.command.executor.CommandExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.generic.KingbaseComTerminationExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.generic.KingbaseUnsupportedCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.KingbaseAggregatedBatchedStatementsCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.KingbaseAggregatedCommandExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.bind.KingbaseComBindExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.close.KingbaseComCloseExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.describe.KingbaseComDescribeExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.execute.KingbaseComExecuteExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.flush.KingbaseComFlushExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.parse.KingbaseComParseExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.extended.sync.KingbaseComSyncExecutor;
import org.apache.shardingsphere.proxy.frontend.kingbase.command.query.simple.KingbaseComQueryExecutor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Command executor factory for Kingbase.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public final class KingbaseCommandExecutorFactory {
    
    /**
     * Create new instance of command executor.
     *
     * @param commandPacketType command packet type for Kingbase
     * @param commandPacket command packet for Kingbase
     * @param connectionSession connection session
     * @param portalContext Kingbase portal context
     * @return created instance
     * @throws SQLException SQL exception
     */
    public static CommandExecutor newInstance(final PostgreSQLCommandPacketType commandPacketType, final PostgreSQLCommandPacket commandPacket,
                                              final ConnectionSession connectionSession, final PortalContext portalContext) throws SQLException {
        if (commandPacket instanceof SQLReceivedPacket) {
            log.debug("Execute packet type: {}, sql: {}", commandPacketType, ((SQLReceivedPacket) commandPacket).getSQL());
        } else {
            log.debug("Execute packet type: {}", commandPacketType);
        }
        if (!(commandPacket instanceof PostgreSQLAggregatedCommandPacket)) {
            return getCommandExecutor(commandPacketType, commandPacket, connectionSession, portalContext);
        }
        PostgreSQLAggregatedCommandPacket aggregatedCommandPacket = (PostgreSQLAggregatedCommandPacket) commandPacket;
        if (aggregatedCommandPacket.isContainsBatchedStatements()) {
            return new KingbaseAggregatedCommandExecutor(getExecutorsOfAggregatedBatchedStatements(aggregatedCommandPacket, connectionSession, portalContext));
        }
        List<CommandExecutor> result = new ArrayList<>(aggregatedCommandPacket.getPackets().size());
        for (PostgreSQLCommandPacket each : aggregatedCommandPacket.getPackets()) {
            result.add(getCommandExecutor((PostgreSQLCommandPacketType) each.getIdentifier(), each, connectionSession, portalContext));
        }
        return new KingbaseAggregatedCommandExecutor(result);
    }
    
    private static List<CommandExecutor> getExecutorsOfAggregatedBatchedStatements(final PostgreSQLAggregatedCommandPacket aggregatedCommandPacket,
                                                                                   final ConnectionSession connectionSession, final PortalContext portalContext) throws SQLException {
        List<PostgreSQLCommandPacket> packets = aggregatedCommandPacket.getPackets();
        int batchPacketBeginIndex = aggregatedCommandPacket.getBatchPacketBeginIndex();
        int batchPacketEndIndex = aggregatedCommandPacket.getBatchPacketEndIndex();
        List<CommandExecutor> result = new ArrayList<>(batchPacketBeginIndex + packets.size() - batchPacketEndIndex);
        for (int i = 0; i < batchPacketBeginIndex; i++) {
            PostgreSQLCommandPacket each = packets.get(i);
            result.add(getCommandExecutor((PostgreSQLCommandPacketType) each.getIdentifier(), each, connectionSession, portalContext));
        }
        result.add(new KingbaseAggregatedBatchedStatementsCommandExecutor(connectionSession, packets.subList(batchPacketBeginIndex, batchPacketEndIndex + 1)));
        for (int i = batchPacketEndIndex + 1; i < packets.size(); i++) {
            PostgreSQLCommandPacket each = packets.get(i);
            result.add(getCommandExecutor((PostgreSQLCommandPacketType) each.getIdentifier(), each, connectionSession, portalContext));
        }
        return result;
    }
    
    private static CommandExecutor getCommandExecutor(final PostgreSQLCommandPacketType commandPacketType, final PostgreSQLCommandPacket commandPacket,
                                                      final ConnectionSession connectionSession, final PortalContext portalContext) throws SQLException {
        switch (commandPacketType) {
            case SIMPLE_QUERY:
                return new KingbaseComQueryExecutor(portalContext, (PostgreSQLComQueryPacket) commandPacket, connectionSession);
            case PARSE_COMMAND:
                return new KingbaseComParseExecutor((PostgreSQLComParsePacket) commandPacket, connectionSession);
            case BIND_COMMAND:
                return new KingbaseComBindExecutor(portalContext, (PostgreSQLComBindPacket) commandPacket, connectionSession);
            case DESCRIBE_COMMAND:
                return new KingbaseComDescribeExecutor(portalContext, (PostgreSQLComDescribePacket) commandPacket, connectionSession);
            case EXECUTE_COMMAND:
                return new KingbaseComExecuteExecutor(portalContext, (PostgreSQLComExecutePacket) commandPacket);
            case SYNC_COMMAND:
                return new KingbaseComSyncExecutor(connectionSession);
            case CLOSE_COMMAND:
                return new KingbaseComCloseExecutor(portalContext, (PostgreSQLComClosePacket) commandPacket, connectionSession);
            case FLUSH_COMMAND:
                return new KingbaseComFlushExecutor();
            case TERMINATE:
                return new KingbaseComTerminationExecutor();
            default:
                return new KingbaseUnsupportedCommandExecutor();
        }
    }
}

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

package org.apache.shardingsphere.db.protocol.kingbase.packet.command.query;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.db.protocol.kingbase.packet.PostgreSQLPacket;
import org.apache.shardingsphere.db.protocol.kingbase.packet.identifier.PostgreSQLMessagePacketType;
import org.apache.shardingsphere.db.protocol.kingbase.payload.PostgreSQLPacketPayload;

/**
 * No data packet for PostgreSQL.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostgreSQLNoDataPacket extends PostgreSQLPacket {
    
    private static final byte[] VALUE = {(byte) PostgreSQLMessagePacketType.NO_DATA.getValue(), 0, 0, 0, 4};
    
    private static final PostgreSQLNoDataPacket INSTANCE = new PostgreSQLNoDataPacket();
    
    /**
     * Get instance of {@link PostgreSQLNoDataPacket}.
     *
     * @return instance of {@link PostgreSQLNoDataPacket}
     */
    public static PostgreSQLNoDataPacket getInstance() {
        return INSTANCE;
    }
    
    @Override
    protected void write(final PostgreSQLPacketPayload payload) {
        payload.getByteBuf().writeBytes(VALUE);
    }
}

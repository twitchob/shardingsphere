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

package org.apache.shardingsphere.proxy.frontend.kingbase.authentication.authenticator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.authentication.AuthenticatorType;
import org.apache.shardingsphere.proxy.frontend.kingbase.authentication.authenticator.impl.KingbaseMD5PasswordAuthenticator;
import org.apache.shardingsphere.proxy.frontend.kingbase.authentication.authenticator.impl.KingbasePasswordAuthenticator;

/**
 * Authenticator type for PostgreSQL.
 */
@RequiredArgsConstructor
@Getter
public enum KingbaseAuthenticatorType implements AuthenticatorType {
    
    MD5(KingbaseMD5PasswordAuthenticator.class, true),
    
    PASSWORD(KingbasePasswordAuthenticator.class),
    
    // TODO impl SCRAM_SHA256 Authenticator
    SCRAM_SHA256(KingbaseMD5PasswordAuthenticator.class);
    
    private final Class<? extends KingbaseAuthenticator> authenticatorClass;
    
    private final boolean isDefault;
    
    KingbaseAuthenticatorType(final Class<? extends KingbaseAuthenticator> authenticatorClass) {
        this(authenticatorClass, false);
    }
}

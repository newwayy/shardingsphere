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

package org.apache.shardingsphere.infra.optimize.execute;

import lombok.RequiredArgsConstructor;
import org.apache.shardingsphere.infra.executor.kernel.model.ExecutionGroup;
import org.apache.shardingsphere.infra.executor.sql.context.ExecutionContext;
import org.apache.shardingsphere.infra.executor.sql.execute.engine.driver.jdbc.JDBCExecutionUnit;
import org.apache.shardingsphere.infra.executor.sql.execute.engine.driver.jdbc.JDBCExecutor;
import org.apache.shardingsphere.infra.executor.sql.execute.engine.driver.jdbc.JDBCExecutorCallback;
import org.apache.shardingsphere.infra.executor.sql.prepare.driver.DriverExecutionPrepareEngine;
import org.apache.shardingsphere.infra.executor.sql.prepare.driver.jdbc.ExecutorJDBCManager;
import org.apache.shardingsphere.infra.executor.sql.prepare.driver.jdbc.JDBCDriverType;
import org.apache.shardingsphere.infra.executor.sql.prepare.driver.jdbc.StatementOption;
import org.apache.shardingsphere.infra.rule.ShardingSphereRule;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Calcite abstract executor.
 */
@RequiredArgsConstructor
public final class CalciteAbstractExecutor {
    
    // TODO Consider use emptyList
    private final Collection<ShardingSphereRule> rules;
    
    private final int maxConnectionsSizePerQuery;
    
    private final ExecutorJDBCManager jdbcManager;
    
    private final JDBCExecutor jdbcExecutor;
    
    private <T> List<T> execute(final ExecutionContext executionContext, final JDBCExecutorCallback<T> callback) throws SQLException {
        Collection<ExecutionGroup<JDBCExecutionUnit>> executionGroups = createExecutionGroups(executionContext);
        return jdbcExecutor.execute(executionGroups, callback);
    }
    
    private Collection<ExecutionGroup<JDBCExecutionUnit>> createExecutionGroups(final ExecutionContext executionContext) throws SQLException {
        // TODO Set parameters for StatementOption
        DriverExecutionPrepareEngine<JDBCExecutionUnit, Connection> prepareEngine = new DriverExecutionPrepareEngine<>(
                JDBCDriverType.STATEMENT, maxConnectionsSizePerQuery, jdbcManager, new StatementOption(true), rules);
        return prepareEngine.prepare(executionContext.getRouteContext(), executionContext.getExecutionUnits());
    }
}

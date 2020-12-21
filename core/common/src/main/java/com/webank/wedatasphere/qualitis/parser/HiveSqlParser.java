/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.webank.wedatasphere.qualitis.parser;

import com.google.common.collect.Maps;
import org.apache.hadoop.hive.ql.lib.*;
import org.apache.hadoop.hive.ql.parse.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author howeye
 */
public class HiveSqlParser {

    private Map<String, List<String>> dbAndTableMap = new HashMap<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(HiveSqlParser.class);
    private static final Integer DB_AND_TABLE_LENGTH = 2;

    public Map<String, List<String>> checkSelectSqlAndGetDbAndTable(String sql) throws ParseException, SemanticException {
        ParseDriver pd = new ParseDriver();
        LOGGER.info("Start to parse sql. sql: {}", sql);
        ASTNode tree = pd.parse(sql, null);
        LOGGER.info("Succeed to parse sql. sql: {}", sql);

        NodeProcessor processor = new GetTableFromSelectSqlNodeProcessor();
        Map<Rule, NodeProcessor> rules = Maps.newLinkedHashMap();
        Dispatcher dispatcher = new DefaultRuleDispatcher(processor, rules, null);
        GraphWalker graphWalker = new DefaultGraphWalker(dispatcher);
        List<Node> topNodes = new ArrayList<Node>();
        topNodes.add(tree);
        LOGGER.info("Start to get db and table from sql. sql: {}", sql);
        graphWalker.startWalking(topNodes, null);
        LOGGER.info("Succeed to get db and table from sql. sql: {}, dbAndTable: {}", sql, dbAndTableMap);

        return dbAndTableMap;
    }

    private class GetTableFromSelectSqlNodeProcessor implements NodeProcessor {

        @Override
        public Object process(Node nd, Stack<Node> stack, NodeProcessorCtx procCtx, Object... nodeOutputs) throws SemanticException {
            ASTNode pt = (ASTNode) nd;

            if (pt.getToken() != null) {
                switch (pt.getToken().getType()) {
                    case HiveParser.TOK_TABREF:
                        // Get Content after "From"
                        try {
                            getAndSaveTable(pt);
                        } catch (HiveSqlParseException hiveSqlParseException) {
                            throw new SemanticException();
                        }
                        break;
                    default:
                        break;
                }
            }
            return null;
        }
    }

    private void getAndSaveTable(ASTNode node) throws HiveSqlParseException {
        String dbAndTable = BaseSemanticAnalyzer
                .getUnescapedName((ASTNode) node.getChild(0));
        String[] dbAndTables = dbAndTable.split("\\.");
        if (dbAndTables.length != DB_AND_TABLE_LENGTH) {
            throw new HiveSqlParseException();
        }
        String db = dbAndTables[0];
        String table = dbAndTables[1];

        if (dbAndTableMap.containsKey(db)) {
            dbAndTableMap.get(db).add(table);
        } else {
            List<String> tableList = new ArrayList<>();
            tableList.add(table);
            dbAndTableMap.put(db, tableList);
        }
    }

    public static void main(String[] args) throws SemanticException, ParseException {
        HiveSqlParser hiveSqlParser = new HiveSqlParser();
        System.out.println(hiveSqlParser.checkSelectSqlAndGetDbAndTable("select a from db1.table1 where ds=10"));
    }
}

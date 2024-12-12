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

package com.webank.wedatasphere.qualitis.query.controller;

import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.query.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDataSource;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryProject;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

/**
 * @author v_wblwyan
 * @date 2018-11-1
 */
@Path("api/v1/projector/query")
public class RuleQueryController {

    private static final Logger LOG = LoggerFactory.getLogger(RuleQueryController.class);
    @Autowired
    RuleQueryService ruleQueryService;


    @GET
    @Path("conditions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> conditions(@Context HttpServletRequest request) {
        // Get login user
        String user = HttpUtils.getUserName(request);
        try {
            Map<String, Object> results = ruleQueryService.conditions(user);
            LOG.info("[My DataSource] Succeed to the query initial conditions, the result:{}", results);
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to the query initial conditions, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @GET
    @Path("init")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> init(@Context HttpServletRequest request) {
        // Get login user
        String user = HttpUtils.getUserName(request);
        try {
            List<RuleQueryProject> results = ruleQueryService.init(user);
            LOG.info("[My DataSource] Succeed to query initial results. The number of results:{}", results == null ? 0 : results.size());
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query initial results, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> query(RuleQueryRequest param, @Context HttpServletRequest request) {
        if (param == null) {
            param = new RuleQueryRequest();
        }
        // Get login user
        param.setUser(HttpUtils.getUserName(request));
        try {
            PageRequest pageRequest = new PageRequest();
            pageRequest.setPage(param.getPage());
            pageRequest.setSize(param.getSize());
            PageRequest.checkRequest(pageRequest);
            DataInfo<RuleQueryDataSource> results = new DataInfo<>();
            List<RuleQueryDataSource> ruleQueryDataSources = ruleQueryService.filter(pageRequest, param.getUser(), param.getCluster(), param.getDb(), param.getTable(),
                false);
            results.setContent(ruleQueryDataSources);
            List<RuleQueryDataSource> allRuleDataSource = ruleQueryService.filter(null, param.getUser(), param.getCluster(), param.getDb(), param.getTable(),
                true);
            results.setTotalCount(allRuleDataSource == null ? 0 : allRuleDataSource.size());
            LOG.info("[My DataSource] Query successfully. The number of results:{}", allRuleDataSource == null ? 0 : allRuleDataSource.size());
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
        } catch (Exception e) {
            LOG.error("[My DataSource] Query failed, internal error.", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> all(@Context HttpServletRequest request, PageRequest pageRequest) {
        // Get login user
        String user = HttpUtils.getUserName(request);
        try {
            PageRequest.checkRequest(pageRequest);
            // Paging query
            List<RuleQueryDataSource> results = ruleQueryService.all(user, pageRequest);

            DataInfo<RuleQueryDataSource> dataInfo = new DataInfo<>();
            // Get total records
            List<RuleQueryDataSource> allRuleDataSource = ruleQueryService.filter(null, user, "%", "%", "%", true);
            dataInfo.setTotalCount(allRuleDataSource == null ? 0 : allRuleDataSource.size());

            dataInfo.setContent(results);
            LOG.info("[My DataSource] Succeed to query initial results. The number of results:{}", allRuleDataSource == null ? 0 : allRuleDataSource.size());
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", dataInfo);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query initial results, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @POST
    @Path("columns")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> columns(RuleQueryRequest param, @Context HttpServletRequest request) {
        // Get login user
        String loginUser = HttpUtils.getUserName(request);
        String proxyUser = param.getUser();
        if (StringUtils.isNotBlank(proxyUser) && proxyUser != loginUser) {
            loginUser = proxyUser;
        }
        try {
            List<ColumnInfoDetail> results = ruleQueryService.getColumnsByTableName(param.getCluster(), param.getDatasourceId(), param.getDb(), param.getTable(), loginUser);
            List<String> cols = ruleQueryService.findCols(param.getCluster(), param.getDb(), param.getTable(), loginUser);
            if (results == null || ! ruleQueryService.compareDataSource(cols, results)) {
                throw new UnExpectedRequestException("{&RULE_DATASOURCE_BE_MOVED}");
            }
            LOG.info("[My DataSource] Succeed to query table columns. The column number of results:{}", results == null ? 0 : results.size());
            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", results);
        } catch (MetaDataAcquireFailedException e) {
            return new GeneralResponse<>("400", e.getMessage(), null);
        }  catch (UnExpectedRequestException e) {
            return new GeneralResponse<>("400", e.getMessage(), null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query table columns, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @POST
    @Path("rules")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> rules(RuleQueryRequest param, @Context HttpServletRequest request) {
        // Get login user
        String loginUser = HttpUtils.getUserName(request);
        try {
            param.checkRequest();
            DataInfo<HiveRuleDetail> dataInfo = ruleQueryService.getRulesByColumn(param.getCluster(), param.getDb(), param.getTable()
                , "%" + param.getColumn() + "%", loginUser, param.getPage(), param.getSize());

            return new GeneralResponse<>("200", "{&QUERY_SUCCESSFULLY}", dataInfo);
        } catch (UnExpectedRequestException e) {
            LOG.error("[My DataSource] Failed to query table columns, request error", e);
            return new GeneralResponse<>("400", e.getMessage(), null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query table columns, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }

    @POST
    @Path("rules/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<?> deleteRules(RulesDeleteRequest request) {
        try {
            request.checkRequest();
            ruleQueryService.deleteRules(request);
            return new GeneralResponse<>("200", "Delete rule list successfully.", null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to delete table columns' rules, internal error", e);
            return new GeneralResponse<>("500", e.getMessage(), null);
        }
    }
}

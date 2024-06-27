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

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.metadata.response.DataInfo;
import com.webank.wedatasphere.qualitis.metadata.response.column.ColumnInfoDetail;
import com.webank.wedatasphere.qualitis.project.request.RuleQueryRequest;
import com.webank.wedatasphere.qualitis.project.response.HiveRuleDetail;
import com.webank.wedatasphere.qualitis.query.request.LineageParameterRequest;
import com.webank.wedatasphere.qualitis.query.request.RulesDeleteRequest;
import com.webank.wedatasphere.qualitis.query.response.LineageParameterResponse;
import com.webank.wedatasphere.qualitis.query.response.RuleQueryDataSource;
import com.webank.wedatasphere.qualitis.query.service.RuleQueryService;
import com.webank.wedatasphere.qualitis.request.PageRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

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
    public GeneralResponse<Map<String, Object>> conditions(@Context HttpServletRequest request) {
        // Get login user
        String user = HttpUtils.getUserName(request);
        try {
            Map<String, Object> results = ruleQueryService.conditions(user);
            LOG.info("[My DataSource] Succeed to the query initial conditions.");
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", results);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to the query initial conditions, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataInfo<RuleQueryDataSource>> query(RuleQueryRequest param, @Context HttpServletRequest request) {
        if (param == null) {
            param = new RuleQueryRequest();
        }
        // Get login user
        param.setUser(HttpUtils.getUserName(request));
        PageRequest pageRequest = new PageRequest();
        pageRequest.setPage(param.getPage());
        pageRequest.setSize(param.getSize());
        param.convertParameter();
        try {
            PageRequest.checkRequest(pageRequest);
            DataInfo<RuleQueryDataSource> results =  ruleQueryService.filter(pageRequest, param.getUser(), param.getCluster(), param.getDb(), param.getTable(), param.getDatasourceType()
            , param.getSubSystemId(), param.getTagCode(), param.getDepartmentName(), param.getDevDepartmentName(), param.getEnvName());

            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", results);
        } catch (Exception e) {
            LOG.error("[My DataSource] Query failed, internal error.", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataInfo<RuleQueryDataSource>> all(@Context HttpServletRequest request, PageRequest pageRequest) {
        // Get login user
        String user = HttpUtils.getUserName(request);
        try {
            PageRequest.checkRequest(pageRequest);
            // Paging query
            List<RuleQueryDataSource> results = ruleQueryService.all(user, pageRequest);

            DataInfo<RuleQueryDataSource> dataInfo = new DataInfo<>();
            // Get total
            int total = ruleQueryService.count(user, "%", "%", "%", null, null, null, null, null, null);
            dataInfo.setTotalCount(total);

            dataInfo.setContent(results);
            LOG.info("[My DataSource] Succeed to query initial results. The number of results:{}", total);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", dataInfo);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query initial results, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("columns")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataInfo<ColumnInfoDetail>> columns(RuleQueryRequest param, @Context HttpServletRequest request) {
        // Get login user
        String loginUser = HttpUtils.getUserName(request);
        String proxyUser = param.getUser();
        if (StringUtils.isNotBlank(proxyUser) && !proxyUser.equals(loginUser)) {
            loginUser = proxyUser;
        }
        try {
            List<ColumnInfoDetail> results = ruleQueryService.getColumnsFromMetaService(param.getCluster(), param.getDatasourceId(), param.getDb(), param.getTable(), loginUser);
            List<String> cols = ruleQueryService.findCols(param.getCluster(), param.getDb(), param.getTable(), loginUser);
            if (CollectionUtils.isEmpty(results) || ! ruleQueryService.compareDataSource(cols, results)) {
                throw new UnExpectedRequestException("{&RULE_DATASOURCE_BE_MOVED}");
            }

            results = ruleQueryService.filterColumns(results, param);

            DataInfo<ColumnInfoDetail> result = new DataInfo<>();
            result.setTotalCount(CollectionUtils.isEmpty(results) ? 0 : results.size());
            result.setContent(results.subList(param.getPage() * param.getSize(), (param.getPage() + 1) * param.getSize() <= result.getTotalCount() ? (param.getPage() + 1) * param.getSize() : result.getTotalCount()));
            LOG.info("[My DataSource] Succeed to query table columns. The column number of results:{}", result.getTotalCount());
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", result);
        } catch (MetaDataAcquireFailedException e) {
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, e.getMessage(), null);
        }  catch (UnExpectedRequestException e) {
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query table columns, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("rules")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataInfo<HiveRuleDetail>> rules(RuleQueryRequest param, @Context HttpServletRequest request) {
        // Get login user
        String loginUser = HttpUtils.getUserName(request);
        try {
            param.checkRequest();
            param.convertParameter();
            DataInfo<HiveRuleDetail> dataInfo = ruleQueryService.getRulesByCondition(param.getCluster(), param.getDb(), param.getTable()
                , param.getColumn(), loginUser, param.getRuleTemplateId(), param.getRelationObjectType(), param.getPage(), param.getSize());

            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&QUERY_SUCCESSFULLY}", dataInfo);
        } catch (UnExpectedRequestException e) {
            LOG.error("[My DataSource] Failed to query table columns, request error", e);
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, e.getMessage(), null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to query table columns, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("rules/delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteRules(RulesDeleteRequest request) {
        try {
            request.checkRequest();
            ruleQueryService.deleteRules(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "Delete rule list successfully.", null);
        } catch (Exception e) {
            LOG.error("[My DataSource] Failed to delete table columns' rules, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("tags")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<DataInfo<Map<String, Object>>> getTagList() {
        try {
            DataInfo<Map<String, Object>> dataInfo = ruleQueryService.getTagList();
            return new GeneralResponse<>(ResponseStatusConstants.OK, "get tag list successfully.", dataInfo);
        } catch (MetaDataAcquireFailedException e) {
            LOG.error("[My DataSource] Failed to get table tag, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }
    }

    @POST
    @Path("lineage/parameter")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<LineageParameterResponse> getLineageParameter(LineageParameterRequest request) {
        try {
            request.checkRequest();
            LineageParameterResponse response = ruleQueryService.getLineageParameter(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "get parameter of lineage successfully.", response);
        } catch (UnExpectedRequestException e) {
            LOG.error("[My DataSource] Failed to get parameter of lineage, request error", e);
            return new GeneralResponse<>(ResponseStatusConstants.BAD_REQUEST, e.getMessage(), null);
        } catch (MetaDataAcquireFailedException e) {
            LOG.error("[My DataSource] Failed to get parameter of lineage, internal error", e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, e.getMessage(), null);
        }

    }
}

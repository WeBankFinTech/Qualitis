package com.webank.wedatasphere.qualitis.project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.metadata.exception.MetaDataAcquireFailedException;
import com.webank.wedatasphere.qualitis.project.request.CommonChecker;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceDcnRequest;
import com.webank.wedatasphere.qualitis.project.request.OuterDataSourceRequest;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceDetailResponse;
import com.webank.wedatasphere.qualitis.project.response.OuterDataSourceVersionResponse;
import com.webank.wedatasphere.qualitis.project.service.OuterDataSourceService;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.relation.RoleNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-10-17 11:24
 * @description
 */
@Path("outer/api/v1/data_source")
public class OuterDataSourceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OuterDataSourceController.class);

    @Autowired
    private OuterDataSourceService outerDataSourceService;

    @POST
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse create(OuterDataSourceRequest outerDataSourceRequest) throws UnExpectedRequestException {
        try {
            outerDataSourceRequest.checkRequest();
            Long linkisDataSourceId = outerDataSourceService.createDataSource(outerDataSourceRequest);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", linkisDataSourceId);
        } catch (RoleNotFoundException e) {
            throw new UnExpectedRequestException("The user no roles: " + outerDataSourceRequest.getUsername());
        } catch (MetaDataAcquireFailedException e) {
            throw new UnExpectedRequestException("Failed to request Linkis: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new UnExpectedRequestException("Failed to request Linkis: Request Parameters Error!");
        } catch (UnExpectedRequestException e) {
            throw e;
        }
    }

    @POST
    @Path("/modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Long> modify(OuterDataSourceRequest outerDataSourceRequest) throws UnExpectedRequestException {
        try {
            outerDataSourceRequest.checkRequest();
            return outerDataSourceService.modifyDataSource(outerDataSourceRequest);
        } catch (RoleNotFoundException e) {
            throw new UnExpectedRequestException("The user no roles: " + outerDataSourceRequest.getUsername());
        } catch (MetaDataAcquireFailedException e) {
            throw new UnExpectedRequestException("Failed to request Linkis: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new UnExpectedRequestException("Failed to request Linkis: Request Parameters Error!");
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + outerDataSourceRequest.getUsername());
        } catch (UnExpectedRequestException e) {
            throw e;
        }
    }

    @GET
    @Path("/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<OuterDataSourceVersionResponse>> versions(@QueryParam("data_source_name") String dataSourceName, @QueryParam("operate_user") String username) throws UnExpectedRequestException {
        CommonChecker.checkString(dataSourceName, "data_source_name");
        CommonChecker.checkString(username, "operate_user");
        try {
            List<OuterDataSourceVersionResponse> versionResponseList = outerDataSourceService.versions(dataSourceName, username);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", versionResponseList);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get the versions: Server Error!", e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get the versions: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + username);
        } catch (Exception e) {
            LOGGER.error("Failed to get the versions: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        }
    }

    @GET
    @Path("/detail")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getDetail(@QueryParam("data_source_name") String dataSourceName, @QueryParam("operate_user") String username) throws UnExpectedRequestException {
        try {
            CommonChecker.checkString(dataSourceName, "data_source_name");
            CommonChecker.checkString(username, "operate_user");
            OuterDataSourceDetailResponse dataSourceResponse = outerDataSourceService.getDataSourceDetail(dataSourceName, null, username);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", dataSourceResponse);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + username);
        } catch (Exception e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: Server Error!");
        }
    }

    @POST
    @Path("/publish")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse publish(@QueryParam("data_source_name") String dataSourceName, @QueryParam("version_id") Long versionId, @QueryParam("operate_user") String username) throws UnExpectedRequestException {
        try {
            CommonChecker.checkString(dataSourceName, "data_source_name");
            CommonChecker.checkString(username, "operate_user");
            return outerDataSourceService.publish(dataSourceName, versionId, username);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + username);
        } catch (Exception e) {
            LOGGER.error("Failed to publish the dataSource: Server Error!", e);
            throw new UnExpectedRequestException("Failed to publish the dataSource: Server Error!");
        }
    }

    @POST
    @Path("/connect")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse connect(@QueryParam("data_source_name") String dataSourceName, @QueryParam("version_id") Long versionId, @QueryParam("operate_user") String username) throws UnExpectedRequestException {
        try {
            CommonChecker.checkString(dataSourceName, "data_source_name");
            CommonChecker.checkString(username, "operate_user");
            return outerDataSourceService.connect(dataSourceName, versionId, username);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + username);
        } catch (Exception e) {
            LOGGER.error("Failed to test the connection: Server Error!", e);
            throw new UnExpectedRequestException("Failed to test the connection: Server Error!");
        }
    }

    @POST
    @Path("/expire")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse expire(@QueryParam("data_source_name") String dataSourceName, @QueryParam("operate_user") String username) throws UnExpectedRequestException {
        try {
            CommonChecker.checkString(dataSourceName, "data_source_name");
            CommonChecker.checkString(username, "operate_user");
            return outerDataSourceService.expire(dataSourceName, username);
        } catch (UnExpectedRequestException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw e;
        } catch (MetaDataAcquireFailedException e) {
            LOGGER.error("Failed to get the detail: Server Error!", e);
            throw new UnExpectedRequestException("Failed to get the detail: " + e.getMessage());
        } catch (PermissionDeniedRequestException e) {
            throw new UnExpectedRequestException("The user no permissions: " + username);
        } catch (Exception e) {
            LOGGER.error("Failed to make the datasource expired: Server Error!", e);
            throw new UnExpectedRequestException("Failed to make the datasource expired: Server Error!");
        }
    }

    @POST
    @Path("/sync_update_dcn")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse updateDcn(OuterDataSourceDcnRequest outerDataSourceDcnRequest) throws UnExpectedRequestException {
        try {
            outerDataSourceService.updateDcn(outerDataSourceDcnRequest);
            return new GeneralResponse(ResponseStatusConstants.OK, "success", null);
        } catch (UnExpectedRequestException e) {
            throw e;
        }
    }

    @GET
    @Path("/datasources")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse getDataSources(@QueryParam("username") String username) {
        return new GeneralResponse(ResponseStatusConstants.OK, "success", outerDataSourceService.getDataSourceNames(username));
    }

}

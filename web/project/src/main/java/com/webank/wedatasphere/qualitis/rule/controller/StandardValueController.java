package com.webank.wedatasphere.qualitis.rule.controller;

import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.exception.PermissionDeniedRequestException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import com.webank.wedatasphere.qualitis.query.request.EditionRequest;
import com.webank.wedatasphere.qualitis.query.request.StandardValueRequest;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.response.GetAllResponse;
import com.webank.wedatasphere.qualitis.rule.request.AddStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.request.DeleteStandardValueVersionRequest;
import com.webank.wedatasphere.qualitis.rule.request.DmsQueryRequest;
import com.webank.wedatasphere.qualitis.rule.request.ModifyStandardValueRequest;
import com.webank.wedatasphere.qualitis.rule.response.StandardValueResponse;
import com.webank.wedatasphere.qualitis.rule.service.StandardValueService;
import com.webank.wedatasphere.qualitis.util.RequestParametersUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author v_gaojiedeng@webank.com
 */
@Path("api/v1/projector/standardValue")
public class StandardValueController {
    @Autowired
    private StandardValueService standardValueService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutionParametersController.class);

    @POST
    @Path("add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<StandardValueResponse> addStandardValue(AddStandardValueRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&ADD_STANDARD_VALUE_SUCCESSFULLY}", standardValueService.addStandardValue(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to add StandardValue. caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_ADD_STANDARD_VALUE}", null);
        }
    }

    @POST
    @Path("delete")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse deleteStandardValue(DeleteStandardValueVersionRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            standardValueService.deleteStandardValue(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&DELETE_STANDARD_VALUE_SUCCESSFULLY}", null);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to delete StandardValue. standard_value_id: {}, caused by system error: {}", request.getEditionId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_DELETE_STANDARD_VALUE}", null);
        }
    }

    @POST
    @Path("modify")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<StandardValueResponse> modifyStandardValue(ModifyStandardValueRequest request) throws UnExpectedRequestException, PermissionDeniedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&MODIFY_STANDARD_VALUE_SUCCESSFULLY}", standardValueService.modifyStandardValue(request));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (PermissionDeniedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to modify StandardValue . standard_value_id: {}, caused by system error: {}", request.getEditionId(), e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_MODIFY_STANDARD_VALUE}", null);
        }
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<StandardValueResponse>> getAllStandardValue(StandardValueRequest request) throws UnExpectedRequestException {
        try {
            RequestParametersUtils.transcoding(request);
            return standardValueService.getAllStandardValue(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get StandardValue, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_StandardValue}", null);
        }
    }

    @GET
    @Path("/{edition_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<StandardValueResponse> getStandardValueDetail(@PathParam("edition_id") Long editionId) throws UnExpectedRequestException {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_STANDARD_VALUE_DETAIL_SUCCESSFULLY}", standardValueService.geStandardValueDetail(editionId));
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get StandardValue detail. standard_value_id: {}, caused by system error: {}", editionId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_STANDARD_VALUE_DETAIL}", null);
        }
    }

    @POST
    @Path("edition/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<GetAllResponse<StandardValueResponse>> getEditionData(EditionRequest request) throws UnExpectedRequestException {
        try {
            return standardValueService.getAccordStandardValue(request);
        } catch (UnExpectedRequestException e) {
            LOGGER.error(e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            LOGGER.error("Failed to get Edition, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_EDITION_DATA}", null);
        }
    }

    @POST
    @Path("approve/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getApproveEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_APPROVE_ENUMN_SUCCESSFULLY}", standardValueService.getAllApproveEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get approve enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_APPROVE_ENUMN}", null);
        }
    }

    @GET
    @Path("version/{standard_value_id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Long>> getStandardValueVersion(@PathParam("standard_value_id") Long standardValueId) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_STANDARD_VALUE_VERSION_ENUM_SUCCESSFULLY}", standardValueService.getVersionList(standardValueId));
        } catch (Exception e) {
            LOGGER.error("Failed to get StandardValue detail. standard_value_id: {}, caused by system error: {}", standardValueId, e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_STANDARD_VALUE_VERSION_ENUM}", null);
        }
    }


    @POST
    @Path("source/all")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<List<Map<String, Object>>> getSourceEnumn() {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_SOURCE_ENUMN_SUCCESSFULLY}", standardValueService.getAllSourceEnum());
        } catch (Exception e) {
            LOGGER.error("Failed to get source enumn, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_SOURCE_ENUMN}", null);
        }
    }


    @POST
    @Path("dms/standard/category")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardCategory(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_DATA_SUCCESSFULLY}", standardValueService.getDmsStandardCategory(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard category, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_DATA}", null);
        }
    }

    @POST
    @Path("dms/standard/big/category")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardBigCategory(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_DATA_SUCCESSFULLY}", standardValueService.getDmsStandardBigCategory(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard big category, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_DATA}", null);
        }
    }

    @POST
    @Path("dms/standard/small/category")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardSmallCategory(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_DATA_SUCCESSFULLY}", standardValueService.getDmsStandardSmallCategory(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard big category, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_DATA}", null);
        }
    }

    @POST
    @Path("dms/standard/data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardData(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_DATA_SUCCESSFULLY}", standardValueService.getDmsStandardUrn(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard data, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_DATA}", null);
        }
    }

    @POST
    @Path("dms/standard/code")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardCode(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_CODE_SUCCESSFULLY}", standardValueService.getDmsStandardCodeName(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard code, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_CODE}", null);
        }
    }

    @POST
    @Path("dms/standard/code/table")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public GeneralResponse<Map<String, Object>> getDmsStandardCodeTable(DmsQueryRequest request) {
        try {
            return new GeneralResponse<>(ResponseStatusConstants.OK, "{&GET_DMS_STANDARD_CODE_TABLE_SUCCESSFULLY}", standardValueService.getDmsStandardCodeTable(request));
        } catch (Exception e) {
            LOGGER.error("Failed to get dms standard code table, caused by system error: {}", e.getMessage(), e);
            return new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "{&FAILED_TO_GET_DMS_STANDARD_CODE_TABLE}", null);
        }
    }


}

package com.webank.wedatasphere.qualitis.rule.service;

import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.exception.ClusterInfoNotConfigException;
import com.webank.wedatasphere.qualitis.exception.TaskNotExistException;
import com.webank.wedatasphere.qualitis.exception.UnExpectedRequestException;
import java.util.List;
import java.util.Map;

/**
 * @author allenzhou
 */
public interface FpsService {

    /**
     * Call linkis-fps-engine to download fps file.
     * @param fileId
     * @param fileHashValue
     * @param fileName
     * @param clusterName
     * @param user
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    void downloadFpsFile(String fileId, String fileHashValue, String fileName, String clusterName, User user)
        throws UnExpectedRequestException, ClusterInfoNotConfigException, TaskNotExistException;

    /**
     * Get excel sheet name.
     * @param path
     * @param clusterName
     * @param user
     * @return
     * @throws UnExpectedRequestException
     * @throws ClusterInfoNotConfigException
     * @throws TaskNotExistException
     */
    String getExcelSheetName(String path, String clusterName, User user)
        throws UnExpectedRequestException;

    /**
     * Get fps task status.
     * @param taskId
     * @param user
     * @param clusterName
     * @return
     * @throws TaskNotExistException
     * @throws ClusterInfoNotConfigException
     */
    String getTaskStatus(Integer taskId, String user, String clusterName) throws TaskNotExistException, ClusterInfoNotConfigException;
}

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

package com.webank.wedatasphere.qualitis.dao.impl;

import com.webank.wedatasphere.qualitis.constant.SpecCharEnum;
import com.webank.wedatasphere.qualitis.dao.TaskDao;
import com.webank.wedatasphere.qualitis.dao.repository.TaskRepository;
import com.webank.wedatasphere.qualitis.entity.Application;
import com.webank.wedatasphere.qualitis.entity.Task;
import com.webank.wedatasphere.qualitis.worker.repository.WorkerTaskRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author howeye
 */
@Repository
public class TaskDaoImpl implements TaskDao {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private WorkerTaskRepository workerTaskRepository;

    @Override
    public Task save(Task task) {
        return repository.save(task);
    }

    @Override
    public Task findById(Long taskId) {
        return repository.findById(taskId).orElse(null);
    }

    @Override
    public Task findByRemoteTaskIdAndClusterName(Long remoteTaskId, String clusterName) {
        return repository.findByTaskRemoteIdAndClusterName(remoteTaskId, clusterName);
    }

    @Override
    public List<Task> findByApplication(Application application) {
        return repository.findByApplication(application.getId());
    }

    @Override
    public List<Task> findByApplicationPageable(Application application, boolean isNonPassStatus, int page, int size) {
        String ruleIdsStr = application.getRuleIds();
        if (StringUtils.isNotBlank(ruleIdsStr)) {
//            Get a list of ruleId from Application
            List<Long> ruleIds = Arrays.stream(ruleIdsStr
                    .replace(SpecCharEnum.LEFT_BRACKET.getValue(), "")
                    .replace(SpecCharEnum.RIGHT_BRACKET.getValue(), "")
                    .split(SpecCharEnum.COMMA.getValue())).map(String::trim).map(Long::valueOf).collect(Collectors.toList());
//            Query a list of taskId by ruleIds
            List<Long> taskIds = repository.findTaskIdsByApplicationAndRule(isNonPassStatus, application.getId(), ruleIds);
//            Paging taskList in VM
            List<Task> taskList = repository.findAllById(getPage(taskIds, page, size));
//            Ordering taskList consistence with taskIds
            return taskList.stream()
                    .sorted(Comparator.comparing(task -> {
                        long taskId = task.getId();
                        return taskIds.indexOf(taskId);
                    }))
                    .collect(Collectors.toList());
        } else {
            Sort sort = Sort.by(Sort.Direction.DESC, "id");
            Pageable pageable = PageRequest.of(page, size, sort);
            return repository.findByApplicationPageable(application.getId(), isNonPassStatus, pageable);
        }
    }

    private List<Long> getPage(List<Long> list, int pageIndex, int pageSize) {
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.min(startIndex + pageSize, list.size());

        return list.subList(startIndex, endIndex);
    }

    @Override
    public int countByApplication(Application application) {
        return repository.countByApplication(application);
    }

    @Override
    public List<Task> findByApplicationList(List<Application> applicationList) {
        return repository.findByApplicationIn(applicationList);
    }

    @Override
    public void saveAll(List<Task> tasks) {
        repository.saveAll(tasks);
    }

    @Override
    public List<Task> findByApplicationAndStatusInAndTaskRemoteIdNotNull(Application application, List<Integer> statusList) {
        return repository.findByApplicationAndStatusInAndTaskRemoteIdNotNull(application, statusList);
    }

    @Override
    public List<Task> findWithSubmitTimeAndDatasource(String startTime, String endTime, String clusterName, String databaseName, String tableName) {
        return repository.findWithSubmitTimeAndDatasource(startTime, endTime, clusterName, databaseName, tableName);
    }

    @Override
    public Long countExecutingTaskNumber(String startBeginTime, String endBeginTime, List<Integer> taskStatusList) {
        return workerTaskRepository.countExecutingTaskNumber(startBeginTime, endBeginTime, taskStatusList);
    }

    @Override
    public void delete(Task task) {
        repository.delete(task);
    }
}

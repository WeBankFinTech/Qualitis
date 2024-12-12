package com.webank.wedatasphere.qualitis.worker.repository;

import com.webank.wedatasphere.qualitis.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author v_minminghe@webank.com
 * @date 2023-03-08 10:29
 * @description
 */
public interface WorkerTaskRepository extends JpaRepository<Task, Long> {

    /**
     * count number of task executing
     * @param startBeginTime
     * @param endBeginTime
     * @param taskStatusList
     * @return
     */
    @Query(value = "select count(1) from qualitis_application_task where begin_time >= ?1 and begin_time <= ?2 and status in (?3)", nativeQuery = true)
    Long countExecutingTaskNumber(String startBeginTime, String endBeginTime, List<Integer> taskStatusList);

}

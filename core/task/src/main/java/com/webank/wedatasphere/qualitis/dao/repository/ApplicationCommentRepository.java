package com.webank.wedatasphere.qualitis.dao.repository;

import com.webank.wedatasphere.qualitis.entity.ApplicationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author v_gaojiedeng@webank.com
 */
public interface ApplicationCommentRepository extends JpaRepository<ApplicationComment, Long> {

    /**
     * get By Code
     * @param code
     * @return
     */
    @Query(value = "SELECT t.* FROM qualitis_application_comment t WHERE t.code = ?1 ", nativeQuery = true)
    ApplicationComment getByCode(Integer code);
}

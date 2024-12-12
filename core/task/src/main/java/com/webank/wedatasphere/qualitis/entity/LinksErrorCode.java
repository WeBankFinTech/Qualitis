package com.webank.wedatasphere.qualitis.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author v_gaojiedeng@webank.com
 */

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(name = "qualitis_application_error_code_type")
public class LinksErrorCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "linkis_error_code")
    private String linkisErrorCode;

    @Column(name = "application_comment")
    private Integer applicationComment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkisErrorCode() {
        return linkisErrorCode;
    }

    public void setLinkisErrorCode(String linkisErrorCode) {
        this.linkisErrorCode = linkisErrorCode;
    }

    public Integer getApplicationComment() {
        return applicationComment;
    }

    public void setApplicationComment(Integer applicationComment) {
        this.applicationComment = applicationComment;
    }
}

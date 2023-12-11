package com.webank.wedatasphere.qualitis.rule.entity;

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
@Table(name = "qualitis_template_naming_conventions")
public class NamingConventions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "major_categories", columnDefinition = "MEDIUMTEXT")
    private String majorCategories;

    @Column(name = "kind", columnDefinition = "MEDIUMTEXT")
    private String kind;

    @Column(name = "create_user", length = 50)
    private String createUser;
    @Column(name = "create_time", length = 25)
    private String createTime;
    @Column(name = "modify_user", length = 50)
    private String modifyUser;
    @Column(name = "modify_time", length = 25)
    private String modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMajorCategories() {
        return majorCategories;
    }

    public void setMajorCategories(String majorCategories) {
        this.majorCategories = majorCategories;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getModifyUser() {
        return modifyUser;
    }

    public void setModifyUser(String modifyUser) {
        this.modifyUser = modifyUser;
    }

    public String getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(String modifyTime) {
        this.modifyTime = modifyTime;
    }
}

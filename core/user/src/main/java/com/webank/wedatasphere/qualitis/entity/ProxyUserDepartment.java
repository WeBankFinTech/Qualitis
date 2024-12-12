package com.webank.wedatasphere.qualitis.entity;

import javax.persistence.*;

@Entity
@Table(name = "qualitis_auth_proxy_user_department")
public class ProxyUserDepartment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ProxyUser proxyUser;
    @Column(name = "department")
    private String department;
    @Column(name="sub_department_code")
    private Long subDepartmentCode;

    public ProxyUserDepartment() {
        // Default Constructor
    }

    public Long getSubDepartmentCode() {
        return subDepartmentCode;
    }

    public void setSubDepartmentCode(Long subDepartmentCode) {
        this.subDepartmentCode = subDepartmentCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProxyUser getProxyUser() {
        return proxyUser;
    }

    public void setProxyUser(ProxyUser proxyUser) {
        this.proxyUser = proxyUser;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

}

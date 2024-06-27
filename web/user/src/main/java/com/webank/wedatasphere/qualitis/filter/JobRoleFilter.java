package com.webank.wedatasphere.qualitis.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.qualitis.constant.RoleTypeEnum;
import com.webank.wedatasphere.qualitis.constants.QualitisConstants;
import com.webank.wedatasphere.qualitis.constants.ResponseStatusConstants;
import com.webank.wedatasphere.qualitis.dao.UserDao;
import com.webank.wedatasphere.qualitis.dao.UserRoleDao;
import com.webank.wedatasphere.qualitis.entity.Role;
import com.webank.wedatasphere.qualitis.entity.User;
import com.webank.wedatasphere.qualitis.entity.UserRole;
import com.webank.wedatasphere.qualitis.response.GeneralResponse;
import com.webank.wedatasphere.qualitis.util.HttpUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author v_gaojiedeng@webank.com
 */
public class JobRoleFilter implements Filter {
    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Value("${devOps.enable}")
    private Boolean enable;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRoleFilter.class);

    private static final String ADD_OPERATION = "add";
    private static final String MODIFY_OPERATION = "modify";
    private static final String DELETE_OPERATION = "delete";
    private static final String OPS_ROLE = "Ops";

    @Override
    public void init(FilterConfig filterConfig) {
        // init operation
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        //资源对象：项目、规则组、规则、任务、指标、标准值、规则模板、数据源、引擎
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取当前用户名
        String username = HttpUtils.getUserName(request);
        LOGGER.info(" >>>>>>>>>> Current Login Username : <<<<<<<<<< " + username);
        //获取用户
        User user = userDao.findByUsername(username);
        //获取用户角色
        List<UserRole> userRoles = userRoleDao.findByUser(user);
        if (CollectionUtils.isEmpty(userRoles)) {
            //用户没有配置角色 返给前端页面
            ServletOutputStream out = response.getOutputStream();
            GeneralResponse generalResponse = new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Error! User does not have a role configured!", null);
            out.write(objectMapper.writeValueAsBytes(generalResponse));
            out.flush();
            return;
        }
        //Role  roleType(1系统角色 2职位角色)

        //系统角色
        List<UserRole> collectSystem = userRoles.stream().filter(item -> item.getRole().getRoleType() != null && item.getRole().getRoleType().toString().equals(RoleTypeEnum.SYSTEM_ROLE.getCode().toString())).collect(Collectors.toList());
        List<Role> roleSystem = collectSystem.stream().map(UserRole::getRole).collect(Collectors.toList());
        //职位角色
        List<UserRole> collectPosition = userRoles.stream().filter(item -> item.getRole().getRoleType() != null && item.getRole().getRoleType().toString().equals(RoleTypeEnum.POSITION_ROLE.getCode().toString())).collect(Collectors.toList());
        List<Role> rolePosition = collectPosition.stream().map(UserRole::getRole).collect(Collectors.toList());

        //获取请求url
        String url = request.getRequestURI();
        LOGGER.info(" >>>>>>>>>> Request url : <<<<<<<<<< " + url);

        if (checkPositionEnv(rolePosition, roleSystem, url, response, enable)) {
            LOGGER.info("Request accepted, roleSystem='{}', rolePosition='{}', url='{}'", roleSystem, rolePosition, url);
            filterChain.doFilter(request, response);
            return;
        }
    }

    private Boolean checkPositionEnv(List<Role> rolePosition, List<Role> roleSystem, String url, HttpServletResponse response, Boolean enable) throws IOException {
        //测试环境：所有角色的用户都可以编辑资源对象
        //生产环境：只有运维角色的用户才可以编辑资源对象，其他角色的用户限定只能查看

        //开发、测试 -> 测试环境可编辑资源对象；生产环境不可
        //运维 -> 测试、生产环境均可编辑资源对象
        //新增运维模式的系统参数：在开启运维模式的时候，之后运维角色和系统管理员才可以编辑资源对象。

        //获取职位的名称
        List<String> collect = rolePosition.stream().map(item -> item.getName()).collect(Collectors.toList());
        //是否包含系统管理员角色
        boolean flag = roleSystem.stream().filter(item -> item.getName().equals(QualitisConstants.ADMIN)).findAny().isPresent();

        //运维开关开启
        if (enable) {
            if (url.endsWith(ADD_OPERATION) || url.endsWith(MODIFY_OPERATION) || url.endsWith(DELETE_OPERATION)) {
                //运维角色和系统管理员才可以操作资源对象
                if (flag || collect.contains(OPS_ROLE)) {
                    return true;
                } else {
                    ServletOutputStream out = response.getOutputStream();
                    GeneralResponse generalResponse = new GeneralResponse<>(ResponseStatusConstants.SERVER_ERROR, "Error! Only when the operation and maintenance mode is enabled can the operation and maintenance role and system administrator edit resource objects!", null);
                    out.write(objectMapper.writeValueAsBytes(generalResponse));
                    out.flush();
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void destroy() {
        // destroy operation
    }
}

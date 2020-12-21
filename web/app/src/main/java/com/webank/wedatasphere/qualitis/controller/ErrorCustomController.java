package com.webank.wedatasphere.qualitis.controller;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author allenzhou
 */

@Controller
@Configuration
public class ErrorCustomController implements ErrorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ErrorCustomController.class);

    @Value("${server.port}")
    private int port;

    @Override
    public String getErrorPath() {
        return "/#/error";
    }

    @GetMapping("/error")
    @ResponseBody
    public String handleError(HttpServletRequest request) {
        LOGGER.error("error page "+ request.getContextPath());

        return "\n"
            + "\n"
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>404您访问的网页不存在</title>\n"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\">\n"
            + "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n"
            + "<style>\n"
            + "* { padding: 0; margin: 0 }\n"
            + "a { text-decoration: none }\n"
            + ".notfoud-container .img-404 { height: 155px; background: url(../images/404.png) center center no-repeat; -webkit-background-size: 150px auto; margin-top: 40px; margin-bottom: 20px }\n"
            + ".notfoud-container .notfound-p { line-height: 22px; font-size: 17px; padding-bottom: 15px; border-bottom: 1px solid #f6f6f6; text-align: center; color: #262b31 }\n"
            + ".notfoud-container .notfound-reason { color: #9ca4ac; font-size: 13px; line-height: 13px; text-align: left; width: 210px; margin: 0 auto }\n"
            + ".notfoud-container .notfound-reason p { margin-top: 13px }\n"
            + ".notfoud-container .notfound-reason ul li { margin-top: 10px; margin-left: 36px }\n"
            + ".notfoud-container .notfound-btn-container { margin: 40px auto 0; text-align: center }\n"
            + ".notfoud-container .notfound-btn-container .notfound-btn { display: inline-block; border: 1px solid #ebedef; background-color: #239bf0; color: #fff; font-size: 15px; border-radius: 5px; text-align: center; padding: 10px; line-height: 16px; white-space: nowrap; margin: auto 10px;}\n"
            + ".notfoud-container .notfound-reason h3 { margin-top: 13px }\n"
            + "</style>\n"
            + "</head>\n"
            + "<body>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<div class=\"notfoud-container\">\n"
            + "  <h3 class=\"notfound-p\">哎呀迷路了...</h3>\n"
            + "  <div class=\"notfound-reason\">\n"
            + "    <p>可能的原因：</p>\n"
            + "    <ul>\n"
            + "      <li>路径不存在或已被删除</li>\n"
            + "      <li>我们的服务器被外星人劫持了</li>\n"
            + "    </ul>\n"
            + "  </div>\n"
            + "  <div class=\"notfound-btn-container\"> \n"
            + "  <a class=\"notfound-btn\" href=\"http://" + request.getLocalAddr() + ":" + port + "/\">返回首页</a> \n"
            + "  <a class=\"notfound-btn\" href=\"javascript:history.go(-1);\">返回上一页</a>\n"
            + "</div>\n"
            + "  \n"
            + "</div>\n"
            + "</body>\n"
            + "</html>";
    }

    @PostMapping("/error")
    @ResponseBody
    public String handleErrorByPost(HttpServletRequest request) {
        LOGGER.error("error page "+ request.getContextPath());

        return "\n"
            + "\n"
            + "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<head>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
            + "<title>404您访问的网页不存在</title>\n"
            + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0\">\n"
            + "<meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n"
            + "<style>\n"
            + "* { padding: 0; margin: 0 }\n"
            + "a { text-decoration: none }\n"
            + ".notfoud-container .img-404 { height: 155px; background: url(../images/404.png) center center no-repeat; -webkit-background-size: 150px auto; margin-top: 40px; margin-bottom: 20px }\n"
            + ".notfoud-container .notfound-p { line-height: 22px; font-size: 17px; padding-bottom: 15px; border-bottom: 1px solid #f6f6f6; text-align: center; color: #262b31 }\n"
            + ".notfoud-container .notfound-reason { color: #9ca4ac; font-size: 13px; line-height: 13px; text-align: left; width: 210px; margin: 0 auto }\n"
            + ".notfoud-container .notfound-reason p { margin-top: 13px }\n"
            + ".notfoud-container .notfound-reason ul li { margin-top: 10px; margin-left: 36px }\n"
            + ".notfoud-container .notfound-btn-container { margin: 40px auto 0; text-align: center }\n"
            + ".notfoud-container .notfound-btn-container .notfound-btn { display: inline-block; border: 1px solid #ebedef; background-color: #239bf0; color: #fff; font-size: 15px; border-radius: 5px; text-align: center; padding: 10px; line-height: 16px; white-space: nowrap; margin: auto 10px;}\n"
            + ".notfoud-container .notfound-reason h3 { margin-top: 13px }\n"
            + "</style>\n"
            + "</head>\n"
            + "<body>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<br>\n"
            + "<div class=\"notfoud-container\">\n"
            + "  <h3 class=\"notfound-p\">哎呀迷路了...</h3>\n"
            + "  <div class=\"notfound-reason\">\n"
            + "    <p>可能的原因：</p>\n"
            + "    <ul>\n"
            + "      <li>路径不存在或已被删除</li>\n"
            + "      <li>我们的服务器被外星人劫持了</li>\n"
            + "    </ul>\n"
            + "  </div>\n"
            + "  <div class=\"notfound-btn-container\"> \n"
            + "  <a class=\"notfound-btn\" href=\"http://" + request.getLocalAddr() + ":" + port + "/\">返回首页</a> \n"
            + "  <a class=\"notfound-btn\" href=\"javascript:history.go(-1);\">返回上一页</a>\n"
            + "</div>\n"
            + "  \n"
            + "</div>\n"
            + "</body>\n"
            + "</html>";
    }
}

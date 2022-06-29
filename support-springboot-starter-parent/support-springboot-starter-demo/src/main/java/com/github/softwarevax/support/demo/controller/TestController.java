package com.github.softwarevax.support.demo.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.softwarevax.support.demo.entity.User;
import com.github.softwarevax.support.demo.service.UserService;
import com.github.softwarevax.support.page.Pagination;
import com.github.softwarevax.support.result.ResultDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/aaa")
public class TestController {

    @Autowired
    private UserService userService;

    /**
     * 字符串
     * @return
     */
    @ResponseBody
    @GetMapping("/string/hello")
    public String string() {
        return "hello";
    }

    /**
     * 指定的返回结果
     * @return
     */
    @ResponseBody
    @GetMapping("/dto/hello")
    public ResultDto<String> dto() {
        return ResultDto.successT("hello dto");
    }

    /**
     * 自定义实体
     * @return
     */
    @ResponseBody
    @GetMapping("/object/hello")
    public User object() {
        User user = new User();
        user.setId("id");
        user.setUserName("object");
        user.setSex("man");
        return user;
    }

    /**
     * response直接返回
     * @param response
     */
    //@IgnoreResultWrapper
    @GetMapping("/nullResponse/hello")
    public void nullResponse(HttpServletResponse response) {
        try (OutputStream os = response.getOutputStream()) {
            User user = new User();
            user.setId("id");
            user.setUserName("object");
            user.setSex("man");
            os.write(JSON.toJSONString(user).getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求页面
     * @return
     */
    @RequestMapping("/html/hello")
    public String html() {
        return "index";
    }

    /**
     * 抛出异常
     * @return
     */
    @ResponseBody
    @GetMapping("/exception/hello")
    public User exception() {
        User user = new User();
        user.setId("id");
        user.setUserName("object");
        user.setSex("man");
        int a = 1 / 0;
        return user;
    }

    @Pagination
    @ResponseBody
    @PostMapping("/user/list")
    public PageInfo<User> queryList(String pageSize) {
        List<User> list = userService.list();
        return new PageInfo<>(list);
    }
}

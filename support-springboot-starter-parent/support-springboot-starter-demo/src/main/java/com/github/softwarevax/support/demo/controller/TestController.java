package com.github.softwarevax.support.demo.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.github.softwarevax.support.demo.entity.User;
import com.github.softwarevax.support.demo.service.UserService;
import com.github.softwarevax.support.page.Pagination;
import com.github.softwarevax.support.result.ResultDto;
import com.github.softwarevax.support.result.annotation.IgnoreResultWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
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
@Api(tags = "测试接口")
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
    @IgnoreResultWrapper
    @GetMapping("/object/hello")
    public User object() {
        User user = new User();
        user.setId("id");
        user.setName("object");
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
            user.setName("object");
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
        user.setName("object");
        user.setSex("man");
        Assert.isTrue(1 == 0, "1不等于0");
        return user;
    }

    @Pagination(skipIfMissing = true)
    @ResponseBody
    @ApiOperation("用户列表查询")
    @PostMapping("/user/list")
    public PageInfo<User> queryList(User user) {
        List<User> list = userService.list();
        return new PageInfo<>(list);
    }
}

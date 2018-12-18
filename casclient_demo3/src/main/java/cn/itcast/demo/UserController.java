package cn.itcast.demo;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class UserController {
    @RequestMapping("findLoginName")
    public void findLoginName(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println(name);
    }
}

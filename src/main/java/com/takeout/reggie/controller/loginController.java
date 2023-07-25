package com.takeout.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.takeout.reggie.pojo.Employee;
import com.takeout.reggie.common.R;
import com.takeout.reggie.service.empService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class loginController {
    @Autowired
    private empService empservice;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee emp, HttpServletRequest request){
        //1. 将用户提交的password改为md5加密格式
        String password = emp.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,emp.getUsername());
        Employee emp_f = empservice.getOne(queryWrapper);
        //3. 如果没有查询到则返回查询失败的结果
        if(emp_f==null){
            return R.error("用户不存在");
        }
        //4. 密码比对，如果密码错误返回查询失败结果
        if (!emp_f.getPassword().equals(password)) {
            return R.error("密码错误");
        }
        //5. 查看员工状态，如果禁用，则返回员工已禁用结果
        if(emp_f.getStatus()==0){
            return R.error("员工已禁用");
        }
        //6. 登陆成功，将员工id录入session并返回登陆成功结果
        request.getSession().setAttribute("Employee",emp_f.getId());
        return R.success(emp_f);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("Employee");
        return R.success("退出登陆");
    }

    /**
     * 新增员工
     * @param emp
     * @param request
     * @return
     */
    @PostMapping
    public R<String> addEmployee(@RequestBody Employee emp,HttpServletRequest request){
        emp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        /*
        emp.setCreateTime(LocalDateTime.now());
        emp.setUpdateTime(LocalDateTime.now());
        emp.setCreateUser((Long) request.getSession().getAttribute("Employee"));
        emp.setUpdateUser((Long) request.getSession().getAttribute("Employee"));
        */
        empservice.save(emp);
        return R.success("添加成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page:{},pageSize:{},name:{}",page,pageSize,name);
        //分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //过滤条件
        queryWrapper.like(name!=null,Employee::getUsername,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        empservice.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 更新员工状态
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        /*
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser((long)request.getSession().getAttribute("Employee"));
         */
        empservice.updateById(employee);
        return R.success("修改成功");
    }


    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据员工id查询信息,{}",id);
        Employee employee = empservice.getById(id);
        return R.success(employee);
    }
}

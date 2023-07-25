package com.takeout.reggie.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.takeout.reggie.mapper.empMapper;
import com.takeout.reggie.pojo.Employee;
import com.takeout.reggie.service.empService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class empServiceImpl extends ServiceImpl<empMapper, Employee> implements empService {

}

package com.xuecheng.system;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.system.mapper.DictionaryMapper;
import com.xuecheng.system.model.po.Dictionary;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
public class SystemMapperTests {

    @Autowired
    DictionaryMapper dictionaryMapper;

    @Test
    public void testCourseBaseMapper() {
        LambdaQueryWrapper<Dictionary> queryWrapper = new LambdaQueryWrapper<>();

        List<Dictionary> list = dictionaryMapper.selectList(queryWrapper);
        System.out.println(list);

    }
}

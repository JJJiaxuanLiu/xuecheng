package com.xuecheng.content;

import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.service.CourseBaseService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class CourseBaseInfoServiceTests {

    @Resource
    CourseBaseService courseBaseService;

    @Test
    public void selectCourseBaseServiceTest(){
        PageParams pageParams = new PageParams(2L,2L);
        QueryCourseParamsDto courseParamsDto = new QueryCourseParamsDto();
        courseParamsDto.setCourseName("java");
        courseParamsDto.setAuditStatus("202004");
        courseParamsDto.setPublishStatus("203001");

        PageResult<CourseBase> pageResult = courseBaseService.queryCourseBaseList(pageParams, courseParamsDto);
        System.out.println(pageResult);
    }
}

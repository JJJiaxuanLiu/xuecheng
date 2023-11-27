package com.xuecheng.content.service;

import com.xuecheng.content.model.dto.TeachplanDto;

import java.util.List;

public interface TeachplanService {
    /**
     * 根据课程id查询课程计划
     * @param courseId
     * @return
     */
    public List<TeachplanDto> findTeachplanTree(long courseId);


    /**
     * 新增修改课程
     * @param teachplanDto
     */
    public void saveTeachplan(TeachplanDto teachplanDto);
}

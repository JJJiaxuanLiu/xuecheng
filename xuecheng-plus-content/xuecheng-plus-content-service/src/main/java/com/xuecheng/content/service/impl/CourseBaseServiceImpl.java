package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CourseBaseServiceImpl implements CourseBaseService {
    @Resource
    CourseBaseMapper courseBaseMapper;

    @Resource
    CourseMarketMapper courseMarketMapper;

    @Resource
    CourseCategoryMapper courseCategoryMapper;

    @Override
    @Transactional
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto courseParamsDto) {

        Page<CourseBase> page = new Page<>(pageParams.getPageNo(),pageParams.getPageSize());
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();

        //课程名称模糊查询
        queryWrapper.like(StringUtils.isNotEmpty(courseParamsDto.getCourseName()),CourseBase::getName,courseParamsDto.getCourseName());
        //审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getAuditStatus()),CourseBase::getAuditStatus,courseParamsDto.getAuditStatus());
        //发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(courseParamsDto.getPublishStatus()),CourseBase::getStatus,courseParamsDto.getPublishStatus());

        //使用mybatisPlus的selectPage分页查询
        Page<CourseBase> courseBasePage = courseBaseMapper.selectPage(page, queryWrapper);
        List<CourseBase> records = courseBasePage.getRecords();

        PageResult<CourseBase> pageResult = new PageResult<>(records, courseBasePage.getTotal(), page.getCurrent(), page.getSize());
        return pageResult;

    }

    /**
     * 新增课程信息
     * @param companyId
     * @param dto
     * @return
     */
    @Override
    @Transactional //当涉及到除了查询之外的所有操作时，使用事务控制
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {
        //新增课程信息到课程基本信息表和课程营销表，并返回课程基本信息dto
        //1 将传入的dto进行校验
        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            XueChengPlusException.cast("课程分类为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            XueChengPlusException.cast("课程等级为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            XueChengPlusException.cast("教育模式为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            XueChengPlusException.cast("适应人群为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            XueChengPlusException.cast("收费规则为空");
        }

        //2 将课程信息写入courseBase表
        CourseBase courseBase = new CourseBase();
        //拷贝信息
        BeanUtils.copyProperties(dto,courseBase);
        //设置审核状态
        courseBase.setAuditStatus("202002");
        //设置发布状态
        courseBase.setStatus("203001");
        //机构id
        courseBase.setCompanyId(companyId);
        //添加时间
        courseBase.setCreateDate(LocalDateTime.now());

        int insert = courseBaseMapper.insert(courseBase);
        if(insert <= 0){
            XueChengPlusException.cast("插入课程基本信息失败");
        }
        //3 将课程信息写入courseMarket表
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto,courseMarket);
        Long courseId = courseBase.getId();
        courseMarket.setId(courseId);
        int saved = saveCourseMarket(courseMarket);
        if(saved <= 0){
            XueChengPlusException.cast("保存课程营销信息失败");
        }

        //4 返回基本信息dto
        return getCourseBaseInfo(courseId);
    }

    /**
     * 更新和插入课程营销表
     * @param courseMarket
     * @return
     */
    private int saveCourseMarket(CourseMarket courseMarket)  {
        //将新增和更新集成为一个方法
        //收费规则校验
        String charge = courseMarket.getCharge();
        if(StringUtils.isBlank(charge)){
            XueChengPlusException.cast("收费规则没有选择");
        }
        //收费规则为收费
        if(charge.equals("201001")){
            if(courseMarket.getPrice() == null || courseMarket.getPrice().floatValue()<=0){
                XueChengPlusException.cast("课程为收费价格不能为空且必须大于0");
            }
        }
        //根据传入的id从表中查询
        CourseMarket courseMarketObj = courseMarketMapper.selectById(courseMarket.getId());
        if(courseMarketObj==null){
            //向表中插入
            return courseMarketMapper.insert(courseMarket);
        }
        //更新
        BeanUtils.copyProperties(courseMarket,courseMarketObj);
        courseMarketObj.setId(courseMarket.getId());
        return courseMarketMapper.updateById(courseMarketObj);



    }

    /**
     * 根据课程id在基本信息和营销信息中查询
     * @param courseId
     * @return
     */
    private CourseBaseInfoDto getCourseBaseInfo(Long courseId){
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if(courseBase == null){
            return null;
        }
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase,courseBaseInfoDto);
        if(courseMarket != null){
            BeanUtils.copyProperties(courseMarket,courseBaseInfoDto);
        }

        //查询分类名称
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;

    }
}

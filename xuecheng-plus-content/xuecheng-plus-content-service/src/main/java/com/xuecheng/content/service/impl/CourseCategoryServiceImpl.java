package com.xuecheng.content.service.impl;

import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.model.dto.CourseCategoryTreeDto;
import com.xuecheng.content.service.CourseCategoryService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseCategoryServiceImpl implements CourseCategoryService {

    @Resource
    CourseCategoryMapper courseCategoryMapper;
    @Override
    public List<CourseCategoryTreeDto> queryTreeNodes(String id) {
        //查到所有dto对象
        List<CourseCategoryTreeDto> dtoList = courseCategoryMapper.selectTreeNodes(id);
        //要将拿到的一个dto对象按照层级放入list结果集中
        //新建一个结果list
        List<CourseCategoryTreeDto> resultList = new ArrayList<>();
        //将查到的list放入map方便查询map(id,dto)
        Map<String, CourseCategoryTreeDto> dtoMap = dtoList.stream()
                //将id为1的根节点排除
                .filter((item)->!id.equals(item.getId()))
                .collect(Collectors.toMap(key -> key.getId(), value -> value));

        //遍历dtoList，找出子节点，当为根节点时，跳过
        dtoList.stream().filter((item)->!id.equals(item.getId())).forEach((item)->{
            //当节点的父节点为根节点时，加入结果集
            if(id.equals(item.getParentid())){
                resultList.add(item);
            }
            //找当前节点的父节点
            CourseCategoryTreeDto fatherNode = dtoMap.get(item.getParentid());
            //如果父节点为不为空，如果父节点的child属性为空，就new一个，加入，如果不为空，加入
            if(fatherNode!=null){
                if(fatherNode.getChildrenTreeNodes()==null){
                    fatherNode.setChildrenTreeNodes(new ArrayList<CourseCategoryTreeDto>());
                }
                fatherNode.getChildrenTreeNodes().add(item);
            }

        });
        return resultList;
    }




}

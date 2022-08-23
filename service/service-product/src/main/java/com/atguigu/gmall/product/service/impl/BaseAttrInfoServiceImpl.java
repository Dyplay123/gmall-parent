package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.mapper.BaseAttrValueMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
* @author dypiay
* @description 针对表【base_attr_info(属性表)】的数据库操作Service实现
* @createDate 2022-08-23 00:25:37
*/
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

@Resource
BaseAttrInfoMapper baseAttrInfoMapper;
@Resource
BaseAttrValueMapper baseAttrValueMapper;
    @Override
    public List<BaseAttrInfo> getAttrInfoList(Long category1Id, Long category2Id, Long category3Id) {


        return  baseAttrInfoMapper.selectBaseAttrInfoList(category1Id,category2Id,category3Id);
    }

    @Override
    public void saveAttrInfo(BaseAttrInfo baseAttrInfo) {
        if (baseAttrInfo != null){
            saveAttrInfoAndAttrValue(baseAttrInfo);


        }else {
            insertAttrInfoAndAttrValue(baseAttrInfo);
        }




    }

    private void saveAttrInfoAndAttrValue(BaseAttrInfo baseAttrInfo) {
        //修改BaseAttrInfo表的内容
        baseAttrInfoMapper.updateById(baseAttrInfo);

        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        //前端提交BaseAttrValue表的id，可以知道需要修改的是那些，剩下的应该删除
        List<Long> vids = new ArrayList<>();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            Long id = baseAttrValue.getId();
            if (id !=null){
                vids.add(id);

            }
        }

        //删除部分
        if (! vids.isEmpty()){
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
            .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId())
            .notIn(BaseAttrValue::getId,vids));
        //删除全部
        }else {
            baseAttrValueMapper.delete(new LambdaQueryWrapper<BaseAttrValue>()
                    .eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        }

       //如果id存在，那就是修改，不存在就是新增
        for (BaseAttrValue baseAttrValue : attrValueList) {
            if (baseAttrValue.getId() != null){
                baseAttrValueMapper.updateById(baseAttrValue);
            }
            if (baseAttrValue.getId() == null){
                baseAttrValue.setAttrId(baseAttrInfo.getId());
                baseAttrValueMapper.insert(baseAttrValue);
            }

        }
    }

    private void insertAttrInfoAndAttrValue(BaseAttrInfo baseAttrInfo) {
        //现将baseAttrInfo表中的属性名称，分类id,分类层级存储到数据库中，根据mybatis-pule的机制，主键自增后返回
        //这样就可以根据返回的主键，关联baseAttrValue表中的数据
        baseAttrInfoMapper.insert(baseAttrInfo);
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue baseAttrValue : attrValueList) {
            baseAttrValue.setAttrId(baseAttrInfo.getId());
            baseAttrValueMapper.insert(baseAttrValue);
        }
    }
}





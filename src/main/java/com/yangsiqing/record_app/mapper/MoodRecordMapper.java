package com.yangsiqing.record_app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yangsiqing.record_app.model.MoodRecord;
import org.apache.ibatis.annotations.Mapper;

// 标记为 MyBatis 的 Mapper 接口
@Mapper
// 继承 BaseMapper，自动获得增删改查方法
public interface MoodRecordMapper extends BaseMapper<MoodRecord> {

}
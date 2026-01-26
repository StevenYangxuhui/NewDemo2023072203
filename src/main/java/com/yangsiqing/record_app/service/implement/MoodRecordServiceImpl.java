package com.yangsiqing.record_app.service.implement;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yangsiqing.record_app.mapper.MoodRecordMapper;
import com.yangsiqing.record_app.model.MoodRecord;
import com.yangsiqing.record_app.service.MoodRecordService;
import org.springframework.stereotype.Service;

@Service // 交给 Spring 管理
public class MoodRecordServiceImpl extends ServiceImpl<MoodRecordMapper, MoodRecord> implements MoodRecordService {
    // 无需写代码，ServiceImpl 已实现 IService 的所有方法
}

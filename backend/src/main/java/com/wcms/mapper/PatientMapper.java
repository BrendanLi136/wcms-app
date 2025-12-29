package com.wcms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wcms.domain.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
}

// Separate file WoundRecordMapper.java
/*
 * package com.wcms.mapper;
 * import com.baomidou.mybatisplus.core.mapper.BaseMapper;
 * import com.wcms.domain.entity.WoundRecord;
 * import org.apache.ibatis.annotations.Mapper;
 * 
 * @Mapper
 * public interface WoundRecordMapper extends BaseMapper<WoundRecord> {}
 */

// Separate file ReminderMapper.java
/*
 * package com.wcms.mapper;
 * import com.baomidou.mybatisplus.core.mapper.BaseMapper;
 * import com.wcms.domain.entity.Reminder;
 * import org.apache.ibatis.annotations.Mapper;
 * 
 * @Mapper
 * public interface ReminderMapper extends BaseMapper<Reminder> {}
 */

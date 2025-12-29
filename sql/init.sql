CREATE DATABASE IF NOT EXISTS wcms_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE wcms_db;

-- 1. System User Table (Doctors)
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    nickname VARCHAR(50) COMMENT '医生姓名',
    role VARCHAR(20) DEFAULT 'DOCTOR' COMMENT '角色',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统用户表';

-- Insert default admin: admin / 123456 (BCrypt to be handled or plaintext for demo if needed, usually we insert hashed)
INSERT INTO sys_user (username, password, nickname) VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7.y./sRnq', 'Admin Doctor'); 
-- Note: Password is '123456' hashed with BCrypt

-- 2. Patient Table
CREATE TABLE IF NOT EXISTS patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    openid VARCHAR(64) UNIQUE COMMENT '微信OpenID',
    phone VARCHAR(20) COMMENT '手机号',
    name VARCHAR(50) COMMENT '患者姓名',
    gender TINYINT COMMENT '1:男, 2:女',
    age INT COMMENT '年龄',
    history TEXT COMMENT '病史',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '患者表';

-- 3. Wound Record Table
CREATE TABLE IF NOT EXISTS wound_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    image_paths TEXT COMMENT '图片路径，逗号分隔或JSON',
    analysis_result TEXT COMMENT 'AI分析结果',
    ai_model VARCHAR(20) COMMENT '使用的模型: DeepSeek/Doubao',
    status TINYINT DEFAULT 0 COMMENT '0:分析中, 1:完成, 2:失败',
    error_msg VARCHAR(255) COMMENT '失败原因',
    patient_name VARCHAR(64) COMMENT '患者姓名快照',
    wound_type VARCHAR(64) COMMENT '伤口类型',
    doctor_evaluation TEXT COMMENT '医生评估/结论',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(id)
) COMMENT '伤口记录表';

-- 4. Reminder Table
CREATE TABLE IF NOT EXISTS reminder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    remind_time TIME COMMENT '提醒时间 HH:mm',
    frequency TINYINT COMMENT '1:每日, 2:隔日, 3:单次',
    next_run_date DATE COMMENT '下一次提醒日期',
    content VARCHAR(255) COMMENT '短信内容',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patient(id)
) COMMENT '提醒设置表';

-- 5. AI Log / SMS Log (Optional for basic requirements but good for "Reliability")
CREATE TABLE IF NOT EXISTS sys_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(20) COMMENT 'AI / SMS',
    content TEXT,
    status TINYINT COMMENT '0:Fail, 1:Success',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) COMMENT '系统日志表';

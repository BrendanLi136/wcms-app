-- migration_add_ai_config.sql

CREATE TABLE IF NOT EXISTS `ai_config` (
  `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
  `model_url` VARCHAR(255) NOT NULL COMMENT '模型地址',
  `model_type` VARCHAR(50) NOT NULL COMMENT '模型类型/名称 (如 deepseek-chat)',
  `api_key` VARCHAR(255) NOT NULL COMMENT 'API Key',
  `remark` VARCHAR(500) COMMENT '备注',
  `is_active` TINYINT DEFAULT 1 COMMENT '是否启用 1:是, 0:否',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI配置表';

-- Initial Configuration
INSERT INTO `ai_config` (`model_url`, `model_type`, `api_key`, `remark`) VALUES 
('https://api.deepseek.com/v1', 'deepseek-chat', '', 'Default DeepSeek Config');

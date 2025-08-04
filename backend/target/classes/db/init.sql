-- 创建数据库
CREATE DATABASE IF NOT EXISTS `sensitive_word_filter` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `sensitive_word_filter`;

-- 敏感词表
CREATE TABLE IF NOT EXISTS `sensitive_word` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `word` varchar(100) NOT NULL COMMENT '敏感词',
  `category` varchar(50) DEFAULT 'default' COMMENT '分类',
  `level` tinyint(4) DEFAULT 1 COMMENT '敏感级别：1-低，2-中，3-高',
  `status` tinyint(4) DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_word` (`word`),
  KEY `idx_category` (`category`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词表';

-- 敏感词过滤记录表
CREATE TABLE IF NOT EXISTS `filter_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `original_text` text NOT NULL COMMENT '原始文本',
  `filtered_text` text NOT NULL COMMENT '过滤后文本',
  `sensitive_words` text COMMENT '检测到的敏感词（JSON格式）',
  `filter_count` int(11) DEFAULT 0 COMMENT '过滤次数',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_ip_address` (`ip_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='敏感词过滤记录表';

-- 插入一些示例敏感词
INSERT INTO `sensitive_word` (`word`, `category`, `level`) VALUES
('傻逼', '侮辱', 3),
('混蛋', '侮辱', 2),
('王八蛋', '侮辱', 2),
('狗屎', '侮辱', 2),
('操你妈', '侮辱', 3),
('法轮功', '政治', 3),
('台独', '政治', 3),
('藏独', '政治', 3),
('疆独', '政治', 3),
('赌博', '违法', 2),
('毒品', '违法', 3),
('色情', '违法', 3),
('黄色', '违法', 2),
('成人', '违法', 2),
('小姐', '违法', 1),
('按摩', '违法', 1),
('一夜情', '违法', 2),
('援交', '违法', 3),
('代孕', '违法', 2),
('假证', '违法', 2),
('办证', '违法', 2),
('发票', '违法', 1),
('代开发票', '违法', 2),
('高利贷', '违法', 2),
('裸聊', '违法', 3),
('约炮', '违法', 2),
('卖淫', '违法', 3),
('嫖娼', '违法', 3); 
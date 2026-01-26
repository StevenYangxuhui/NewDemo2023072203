-- 测试环境专用：创建库和 mood_record 表（与生产结构一致）
-- 在 MySQL 中执行：source 本文件路径  或  复制到客户端执行。

CREATE DATABASE IF NOT EXISTS mood_book_test
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE mood_book_test;

CREATE TABLE IF NOT EXISTS mood_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    happen_thing VARCHAR(255),
    mood VARCHAR(255),
    partner_image VARCHAR(255),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    submitter VARCHAR(10) NOT NULL DEFAULT 'ysq' COMMENT '提交者：nfy=她，ysq=你'
);

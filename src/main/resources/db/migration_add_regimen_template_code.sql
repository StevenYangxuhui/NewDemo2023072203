-- 已有库升级：为 chemo_regimen 增加模板代码（新建库请直接执行 create_table_production.sql）
USE cancer_app;

ALTER TABLE chemo_regimen
    ADD COLUMN template_code VARCHAR(64) NULL COMMENT '预设模板代码，如 FOLFOX；手写为 OTHER' AFTER regimen_name;

-- ============================================
-- CancerApp 生产环境建表脚本
-- 开发者 Yang SiQing，学号后五位标识 72203（截图 MySQL 客户端时可含本注释）
-- 包含 6 张核心表：patient / chemo_regimen / chemo_medication /
--                adverse_reaction / supplement_rule / daily_nutrition_plan
-- ============================================
-- 执行方式：
--   1. 在 MySQL 客户端中执行：source 本文件路径
--   2. 或复制下面 SQL 到 MySQL 客户端执行
-- ============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS cancer_app
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE cancer_app;

-- 为安全起见，这里不主动 DROP 表，如需重建请手动执行：
-- DROP TABLE IF EXISTS daily_nutrition_plan;
-- DROP TABLE IF EXISTS adverse_reaction;
-- DROP TABLE IF EXISTS chemo_medication;
-- DROP TABLE IF EXISTS chemo_regimen;
-- DROP TABLE IF EXISTS supplement_rule;
-- DROP TABLE IF EXISTS patient;

-- 1. 患者基本信息表
CREATE TABLE IF NOT EXISTS patient (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    name            VARCHAR(50)      NOT NULL COMMENT '姓名',
    gender          ENUM('M','F','U') DEFAULT 'U' COMMENT '性别 M男 F女 U未知',
    age             INT              NULL COMMENT '年龄',
    height_cm       DECIMAL(5,2)     NULL COMMENT '身高 cm',
    weight_kg       DECIMAL(5,2)     NULL COMMENT '体重 kg',
    tumor_type      VARCHAR(100)     NULL COMMENT '肿瘤类型',

    liver_function  VARCHAR(100)     NULL COMMENT '肝功能情况（简要分级/描述）',
    kidney_function VARCHAR(100)     NULL COMMENT '肾功能情况（简要分级/描述）',
    allergy_history VARCHAR(255)     NULL COMMENT '过敏史',
    diet_taboo      VARCHAR(255)     NULL COMMENT '饮食禁忌',

    kcal_need       INT              NULL COMMENT '推荐每日热量(kcal)',
    protein_need_g  DECIMAL(5,2)     NULL COMMENT '推荐每日蛋白质(g)',

    created_at      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='患者基本信息表';


-- 2. 化疗方案表
CREATE TABLE IF NOT EXISTS chemo_regimen (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    patient_id      BIGINT UNSIGNED NOT NULL COMMENT '关联 patient.id',
    regimen_name    VARCHAR(100)    NOT NULL COMMENT '方案名称，如 FOLFOX 等',
    template_code   VARCHAR(64)     NULL COMMENT '预设模板代码，如 FOLFOX；手写为 OTHER',
    start_date      DATE            NULL COMMENT '方案开始日期',
    total_cycles    INT             NULL COMMENT '计划总周期数',
    current_cycle   INT             NULL COMMENT '当前周期号',
    current_day     INT             NULL COMMENT '当前周期内第几天',

    note            VARCHAR(255)    NULL COMMENT '备注',

    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_chemo_regimen_patient
        FOREIGN KEY (patient_id) REFERENCES patient(id)
        ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='化疗方案表';


-- 3. 化疗用药明细表
CREATE TABLE IF NOT EXISTS chemo_medication (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    regimen_id      BIGINT UNSIGNED NOT NULL COMMENT '关联 chemo_regimen.id',
    drug_name       VARCHAR(100)    NOT NULL COMMENT '药物名称',
    dose            VARCHAR(50)     NULL COMMENT '剂量，如 100mg/m2',
    route           VARCHAR(50)     NULL COMMENT '给药方式，如 静脉、口服',
    frequency       VARCHAR(50)     NULL COMMENT '给药频次，如 qd、bid',
    day_in_cycle    INT             NULL COMMENT '在周期中的第几天用药',
    remark          VARCHAR(255)    NULL COMMENT '其他说明',

    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_chemo_medication_regimen
        FOREIGN KEY (regimen_id) REFERENCES chemo_regimen(id)
        ON DELETE CASCADE
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='化疗方案用药明细';


-- 4. 不良反应记录表
CREATE TABLE IF NOT EXISTS adverse_reaction (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    patient_id      BIGINT UNSIGNED NOT NULL COMMENT '关联 patient.id',
    regimen_id      BIGINT UNSIGNED NULL COMMENT '可选：关联 chemo_regimen.id',
    record_date     DATE            NOT NULL COMMENT '记录日期',

    reaction_type   VARCHAR(50)     NOT NULL COMMENT '反应类型，如 恶心、呕吐、腹泻、乏力、口腔炎 等',
    severity        TINYINT         NULL COMMENT '严重程度 1-5',
    detail          VARCHAR(255)    NULL COMMENT '详细描述',

    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_adverse_reaction_patient
        FOREIGN KEY (patient_id) REFERENCES patient(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_adverse_reaction_regimen
        FOREIGN KEY (regimen_id) REFERENCES chemo_regimen(id)
        ON DELETE SET NULL
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='不良反应记录表';


-- 5. 保健品与化疗药物营养相互作用规则表
CREATE TABLE IF NOT EXISTS supplement_rule (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    supplement_name VARCHAR(100)    NOT NULL COMMENT '保健品名称',
    ingredient_tags VARCHAR(255)    NULL COMMENT '主要成分关键词，逗号分隔',

    drug_keyword    VARCHAR(100)    NULL COMMENT '适用的化疗药物/药物类关键词',
    risk_level      ENUM('SAFE','CAUTION','AVOID') NOT NULL DEFAULT 'SAFE'
                                    COMMENT '可用/慎用/禁用',
    reason          VARCHAR(255)    NULL COMMENT '风险理由',
    suggestion      VARCHAR(255)    NULL COMMENT '建议，如间隔多久服用，替代方案等',

    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='保健品与化疗药物营养相互作用规则';


-- 6. 每日营养干预方案表
CREATE TABLE IF NOT EXISTS daily_nutrition_plan (
    id              BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    patient_id      BIGINT UNSIGNED NOT NULL COMMENT '关联 patient.id',
    regimen_id      BIGINT UNSIGNED NULL COMMENT '可选：关联 chemo_regimen.id',
    plan_date       DATE            NOT NULL COMMENT '日期',

    phase           ENUM('PRE','DURING','POST') NOT NULL
                                    COMMENT '化疗前/中/后',
    main_issue      VARCHAR(100)    NULL COMMENT '主要针对问题，如 恶心、腹泻 等',
    energy_kcal     INT             NULL COMMENT '建议能量',
    protein_g       DECIMAL(5,2)    NULL COMMENT '建议蛋白质(g)',
    diet_advice     TEXT            NULL COMMENT '饮食原则说明',
    sample_menu     TEXT            NULL COMMENT '示例食谱',

    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_daily_plan_patient
        FOREIGN KEY (patient_id) REFERENCES patient(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_daily_plan_regimen
        FOREIGN KEY (regimen_id) REFERENCES chemo_regimen(id)
        ON DELETE SET NULL
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='每日营养干预方案表';


-- 完成
-- 之后请在 application.properties 中配置指向 cancer_app 数据库的连接信息。

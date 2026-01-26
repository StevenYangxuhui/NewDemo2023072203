-- 按域名区分提交者 nfy/ysq。执行前请备份 mood_record 表。
-- 在 MySQL 中执行：source 本文件路径  或  复制下面语句到客户端执行。

ALTER TABLE mood_record ADD COLUMN submitter VARCHAR(10) NOT NULL DEFAULT 'ysq' COMMENT '提交者：nfy=她，ysq=你';

-- 创建数据库
CREATE DATABASE IF NOT EXISTS web_novel DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE web_novel;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码（加密）',
  `email` VARCHAR(100) COMMENT '邮箱',
  `avatar` VARCHAR(255) COMMENT '头像 URL',
  `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小说分类表';

-- 小说表
CREATE TABLE IF NOT EXISTS `novel` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `title` VARCHAR(200) NOT NULL COMMENT '小说标题',
  `author` VARCHAR(100) NOT NULL COMMENT '作者',
  `cover` VARCHAR(255) COMMENT '封面 URL',
  `category_id` INT COMMENT '分类 ID',
  `description` TEXT COMMENT '简介',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-下架 1-连载 2-完结',
  `word_count` BIGINT DEFAULT 0 COMMENT '总字数',
  `click_count` BIGINT DEFAULT 0 COMMENT '点击量',
  `bookmark_count` INT DEFAULT 0 COMMENT '收藏数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`) ON DELETE SET NULL,
  INDEX `idx_category` (`category_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_click` (`click_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小说表';

-- 章节表
CREATE TABLE IF NOT EXISTS `chapter` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
  `title` VARCHAR(200) NOT NULL COMMENT '章节标题',
  `content` LONGTEXT NOT NULL COMMENT '章节内容',
  `order_num` INT NOT NULL COMMENT '章节顺序',
  `word_count` INT DEFAULT 0 COMMENT '字数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`) ON DELETE CASCADE,
  INDEX `idx_novel_order` (`novel_id`, `order_num`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `chapter_id` BIGINT NOT NULL COMMENT '章节 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT DEFAULT NULL COMMENT '父评论 ID（二级评论）',
  `like_count` INT DEFAULT 0 COMMENT '点赞数',
  `status` TINYINT DEFAULT 0 COMMENT '审核状态：0-待审核 1-已通过 2-已拒绝',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`),
  FOREIGN KEY (`parent_id`) REFERENCES `comment`(`id`),
  INDEX `idx_chapter` (`chapter_id`),
  INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 收藏表
CREATE TABLE IF NOT EXISTS `bookmark` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_user_novel` (`user_id`, `novel_id`),
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 阅读历史表
CREATE TABLE IF NOT EXISTS `read_history` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `novel_id` BIGINT NOT NULL COMMENT '小说 ID',
  `chapter_id` BIGINT NOT NULL COMMENT '章节 ID',
  `read_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '阅读时间',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`novel_id`) REFERENCES `novel`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`chapter_id`) REFERENCES `chapter`(`id`) ON DELETE CASCADE,
  INDEX `idx_user_time` (`user_id`, `read_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='阅读历史表';

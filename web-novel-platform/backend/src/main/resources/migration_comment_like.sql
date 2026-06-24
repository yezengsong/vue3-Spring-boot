-- 创建评论点赞表
CREATE TABLE IF NOT EXISTS `comment_like` (
  `like_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
  `comment_id` BIGINT NOT NULL COMMENT '评论 ID',
  `user_id` BIGINT NOT NULL COMMENT '用户 ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  UNIQUE KEY `uk_user_comment` (`user_id`, `comment_id`),
  FOREIGN KEY (`comment_id`) REFERENCES `comment`(`comment_id`) ON DELETE CASCADE,
  FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`) ON DELETE CASCADE,
  INDEX `idx_comment` (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论点赞表';

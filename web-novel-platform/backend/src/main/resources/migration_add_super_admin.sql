-- 将 admin 用户升级为超级管理员
UPDATE `user` SET `role` = 'SUPER_ADMIN' WHERE `username` = 'admin';

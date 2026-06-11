import bcrypt

# Hutool BCrypt 使用的是和 Spring Security BCrypt 相同的算法
# 生成密码哈希
admin_hash = bcrypt.hashpw(b'admin123', bcrypt.gensalt(10)).decode()
user_hash = bcrypt.hashpw(b'user123', bcrypt.gensalt(10)).decode()

print(f"admin_hash = '{admin_hash}'")
print(f"user_hash = '{user_hash}'")


配置数据库：
```sql
create database enterprise;
create user 'enterprise_admin'@'localhost' identified by '0ZuYKJmdib';
grant all on enterprise.* to 'enterprise_admin'@'localhost';
```
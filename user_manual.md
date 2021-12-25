# 用户手册

进入系统后，输入 `login` 命令登录，输入 `exit` 命令退出。

系统管理员的用户名为 `root`，其余用户的用户名为其员工 ID。

> 测试命名规范：
>
> 员工名，小写字母（a, b, c）；部门名：大写字母（A, B, C）；课程名：罗马数字（I, II, III, IV）。

### 普通员工

`update-info` 命令可以更新个人信息，输入属性后根据提示键入属性值，完毕后输入 `finish` 即可保存退出。

`show-info` 命令显示个人信息。

`show-course` 命令显示正在修读的课程。

`show-history-score` 命令显示历史修读信息。

### 教员

`add-course` 命令添加一门课程：首先输入课程基本信息，然后循环输入关联的部门和课程属性（必修/选修），以 `finish` 作为部门名停止。

`set-score` 命令依次所有未结课的员工赋分，可以输入 `pass` 来跳过某员工。

### 部门主管

`department-course` 命令列出该部门的所有课程。

`department-employee` 命令列出该部门的所有员工。

`search-name` 命令根据姓名查找员工号（只能查找本部门）。

`list-score` 命令根据员工号查找成绩。

`allocate-course` 命令根据员工号和课程，将该员工加入该课程。

### 系统管理员

`add-department` 命令可以新建部门，新部门的主管为空。

`set-supervisor` 命令可以设置部门主管。

`add-employee` 命令可以新建一个员工（会自动修读部门内所有必修课）。

`set-instructor` 命令可以任命一位教员。
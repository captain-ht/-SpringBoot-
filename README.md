# 图书管理系统 Windows 操作指南

基于 `Spring Boot 3 + Spring Security + JWT + MyBatis-Plus + MySQL + Redis + Vue3 + Element Plus` 的图书管理系统，适用于中小型图书馆的图书、读者、借阅、罚款、预约、采购和统计管理。

本文档按 Windows 本地开发环境编写，从环境安装、数据库导入、后端启动、前端启动，到功能测试，按顺序一步步说明。

## 1. 项目说明

### 1.1 技术栈

- 后端：`Java 17`、`Spring Boot 3.3.0`、`Spring Security`、`JWT`、`MyBatis-Plus`
- 数据库：`MySQL 8`
- 缓存：`Redis`
- 文档：`Knife4j`
- 前端：`Vue 3`、`Vite`、`Element Plus`、`Pinia`、`Vue Router`

### 1.2 已实现功能

- 用户登录、退出、JWT 鉴权、Redis 会话校验
- 图书管理：新增、编辑、删除、分页查询、Excel 导出
- 分类管理：新增、编辑、删除、列表查询
- 读者管理：新增、编辑、删除、分页查询、Excel 导入导出
- 借阅管理：借书、还书、续借、借阅记录查询、借阅导出
- 预约管理：创建预约、取消预约、预约到期自动失效
- 罚款管理：逾期罚款生成、查询、缴费、导出
- 采购管理：采购单维护、到货入库、状态流转
- 统计看板：馆藏统计、分类统计、借阅趋势、热门图书排行
- 操作日志：记录登录、借书、还书、续借、预约、缴费等关键操作

### 1.3 默认账号

- 管理员：`admin / 123456`
- 馆员：`librarian / 123456`

## 2. 运行环境要求

请先准备以下软件：

- `JDK 17`
- `Maven 3.9+`
- `MySQL 8.x`
- `Redis`
- `Node.js 18+`
- `npm 9+`
- `IDEA` 或其他 Java IDE

建议版本：

- Java：`17`
- Maven：`3.9.x`
- Node.js：`18 LTS` 或 `20 LTS`
- MySQL：`8.0.x`
- Redis：`7.x`

## 3. Windows 环境安装

### 3.1 安装 JDK 17

1. 下载并安装 `JDK 17`
2. 配置环境变量：
   - `JAVA_HOME` 指向 JDK 安装目录，例如 `C:\Program Files\Java\jdk-17`
   - 在 `Path` 中加入 `%JAVA_HOME%\bin`
3. 打开 `cmd` 或 `PowerShell`，执行：

```bat
java -version
javac -version
```

如果输出为 `17`，说明安装成功。

注意：

- 本项目基于 `Spring Boot 3.3.0`，必须使用 `Java 17` 或更高版本
- 不建议使用 `Java 8`
- 如果 IDEA 里仍然报编译错误，需要同时检查 `Project SDK` 和 `Maven Runner JRE`

### 3.2 安装 Maven

1. 下载 Maven 压缩包并解压，例如解压到 `D:\Dev\apache-maven-3.9.8`
2. 配置环境变量：
   - `MAVEN_HOME=D:\Dev\apache-maven-3.9.8`
   - 在 `Path` 中加入 `%MAVEN_HOME%\bin`
3. 执行命令检查：

```bat
mvn -version
```

确认输出中：

- Maven 版本正常
- Java 版本为 `17`

### 3.3 安装 Node.js

1. 下载并安装 `Node.js LTS`
2. 安装完成后执行：

```bat
node -v
npm -v
```

建议输出：

- `node` 为 `18.x` 或 `20.x`
- `npm` 为 `9.x` 或更高

### 3.4 安装 MySQL 8

1. 安装 `MySQL Server 8`
2. 记住你设置的 `root` 密码
3. 确保服务已启动
4. 打开命令行测试：

```bat
mysql -u root -p
```

输入密码后，如果能进入 MySQL 控制台，说明安装成功。

### 3.5 安装 Redis

Windows 下常见有两种方案，任选一种：

#### 方案 A：使用 Redis Windows 版

1. 安装 Redis
2. 启动 Redis 服务
3. 执行：

```bat
redis-cli ping
```

如果返回：

```text
PONG
```

说明 Redis 可用。

#### 方案 B：使用 Docker 启动 Redis

如果你本机已安装 Docker Desktop，也可以直接运行：

```bat
docker run -d --name library-redis -p 6379:6379 redis:7.4-alpine
```

然后执行：

```bat
redis-cli ping
```

如果没有 `redis-cli`，只要确认容器状态为运行中即可。

## 4. 下载项目并打开

### 4.1 打开项目目录

假设项目目录为：

```text
D:\library-management-system
```

后续所有命令，都默认在这个目录或其子目录中执行。

### 4.2 IDEA 打开后需要检查的配置

打开 IDEA 后，重点检查以下项目：

1. `Project SDK` 是否为 `JDK 17`
2. `Project language level` 是否对应 `17`
3. `Maven` 使用的 JDK 是否为 `17`
4. 安装并启用 `Lombok` 插件
5. 打开 `Build, Execution, Deployment > Compiler > Annotation Processors`
6. 勾选 `Enable annotation processing`

如果这里没配好，常见问题包括：

- `java.lang.ExceptionInInitializerError`
- `TypeTag :: UNKNOWN`
- Lombok 生成的 getter/setter 不识别
- 项目可以导入但无法编译

## 5. 数据库初始化

### 5.1 创建数据库

先登录 MySQL：

```bat
mysql -u root -p
```

执行以下 SQL：

```sql
CREATE DATABASE IF NOT EXISTS library_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

说明：

- 当前项目默认连接的数据库名是 `library_management`
- 项目已经配置好连接这个库，不建议随意改库名

### 5.2 导入表结构

在项目根目录下执行：

```bat
mysql -u root -p library_management < sql\library_management.sql
```

作用：

- 初始化系统全部表结构
- 不会创建数据库

### 5.3 导入演示数据

继续执行：

```bat
mysql -u root -p library_management < sql\library_management_reset.sql
```

作用：

- 清空当前业务表中的旧数据
- 导入一套新的演示数据

注意：

- 这个脚本会覆盖当前业务数据
- 如果你已经有自己的测试数据，执行前请先备份

### 5.4 检查数据是否导入成功

进入 MySQL：

```bat
mysql -u root -p
```

切换数据库：

```sql
USE library_management;
SHOW TABLES;
SELECT * FROM sys_user;
SELECT COUNT(*) FROM library_book;
SELECT COUNT(*) FROM library_reader;
```

如果能看到表和数据，说明导入正常。

## 6. 修改后端配置

项目配置文件路径：

`src/main/resources/application.yml`

当前默认配置为：

- MySQL：`localhost:3306`
- 数据库名：`library_management`
- 用户名：`root`
- 密码：`123456`
- Redis：`localhost:6379`
- 后端端口：`8081`

如果你的 MySQL 密码不是 `123456`，请修改：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_management?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: 你的MySQL密码
```

如果 Redis 不是默认端口，也要同步修改：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

## 7. 启动 Redis

在启动后端之前，必须先保证 Redis 已启动。

如果是本地服务版 Redis：

```bat
redis-server
```

如果 Redis 已安装为 Windows 服务，只需要确认服务状态为运行中。

检查命令：

```bat
redis-cli ping
```

返回 `PONG` 表示正常。

## 8. 启动后端

### 8.1 使用 IDEA 启动

启动类：

`LibraryManagementApplication`

直接运行该启动类即可。

### 8.2 使用命令行启动

在项目根目录执行：

```bat
mvn spring-boot:run
```

### 8.3 启动成功判断

后端启动成功后，访问：

- 接口文档：`http://localhost:8081/doc.html`

如果能打开 Knife4j 页面，说明后端已正常启动。

如果启动失败，优先检查：

1. MySQL 是否启动
2. Redis 是否启动
3. `application.yml` 中数据库密码是否正确
4. 端口 `8081` 是否被占用
5. IDEA 使用的 JDK 是否为 `17`
6. Lombok 插件和注解处理是否启用

## 9. 启动前端

### 9.1 安装前端依赖

进入前端目录：

```bat
cd frontend
```

安装依赖：

```bat
npm install
```

### 9.2 启动前端开发服务

继续执行：

```bat
npm run dev
```

启动成功后，通常会看到：

- 本地访问地址：`http://localhost:5173`

### 9.3 前端代理说明

前端开发环境已配置代理：

- 前端地址：`http://localhost:5173`
- 后端地址：`http://127.0.0.1:8081`
- `/api` 请求会自动转发到后端

所以正常开发时：

- 先启动后端
- 再启动前端

## 10. 系统访问地址

启动完成后，访问以下地址：

- 前端管理端：`http://localhost:5173`
- 后端接口文档：`http://localhost:8081/doc.html`

## 11. 首次登录

打开前端登录页后，使用以下账号登录：

- 管理员：`admin / 123456`
- 馆员：`librarian / 123456`

建议先用管理员登录，方便测试全部功能。

## 12. 功能测试指南

下面给出一套按业务顺序的测试流程，建议从上到下执行，能覆盖系统核心功能。

### 12.1 登录测试

测试点：

- 使用 `admin / 123456` 登录
- 使用 `librarian / 123456` 登录
- 输入错误密码，确认系统提示登录失败
- 登录成功后刷新页面，确认仍保持登录状态
- 点击退出登录，确认跳转回登录页

预期结果：

- 正确账号可以登录
- 错误账号不能登录
- JWT 和 Redis 会话校验正常

### 12.2 首页统计测试

进入首页看板，检查：

- 馆藏总量
- 可借库存
- 借阅中数量
- 逾期数量
- 未缴罚款金额
- 分类统计图
- 近 6 月借阅趋势
- 热门图书排行

预期结果：

- 数据能正常显示
- 图表不报错
- 数值与演示数据大体匹配

### 12.3 分类管理测试

测试步骤：

1. 新增一个分类，例如：`哲学`
2. 编辑该分类描述
3. 查询分类列表是否更新
4. 删除刚新增的分类

预期结果：

- 新增、编辑、删除都成功
- 列表实时刷新

### 12.4 图书管理测试

测试步骤：

1. 查询图书列表
2. 使用书名或作者关键字搜索
3. 新增一本图书
4. 编辑该图书库存、位置、简介
5. 导出图书数据
6. 删除刚新增的图书

预期结果：

- 图书分页、搜索、增删改查正常
- 导出文件可正常下载

### 12.5 读者管理测试

测试步骤：

1. 查看读者列表
2. 新增一个读者
3. 编辑读者借阅上限、借阅天数、联系方式
4. 导出读者数据
5. 测试 Excel 导入读者
6. 删除测试读者

预期结果：

- 读者信息维护正常
- 导入导出正常

### 12.6 借阅管理测试

测试步骤：

1. 选择一个有可借库存的图书
2. 选择一个状态正常的读者
3. 执行借书
4. 检查图书 `available_stock` 是否减少
5. 检查图书 `borrowed_count` 是否增加
6. 在借阅记录中查看新记录
7. 对一条借阅中记录执行续借
8. 对借阅记录执行还书

预期结果：

- 借书成功后库存减少
- 续借后 `renew_count` 增加
- 还书后库存回补
- 借阅状态变化正确

### 12.7 预约管理测试

测试步骤：

1. 对一本图书创建预约
2. 查看预约列表
3. 取消一条预约
4. 检查状态变化

预期结果：

- 预约创建成功
- 取消后状态更新
- 到期预约可被定时任务置为失效

### 12.8 罚款管理测试

测试步骤：

1. 查看现有罚款记录
2. 对未支付罚款执行缴费
3. 选择支付方式，例如微信或现金
4. 导出罚款数据

预期结果：

- 缴费后状态从未支付变为已支付
- `pay_time`、`pay_type` 有值
- 导出正常

### 12.9 采购管理测试

测试步骤：

1. 新增采购单
2. 修改采购单状态为已下单
3. 对采购单执行到货入库
4. 检查是否新增图书或更新库存
5. 查看采购单最终状态

预期结果：

- 采购单增删改查正常
- 到货后状态变更正确
- 入库逻辑正常

### 12.10 操作日志测试

测试步骤：

1. 执行一次登录
2. 执行一次借书
3. 执行一次还书
4. 执行一次续借
5. 执行一次预约
6. 打开操作日志页面查看记录

预期结果：

- 日志中能看到对应操作
- 包含模块、动作、用户、请求路径、状态等信息

### 12.11 接口文档测试

访问：

- `http://localhost:8081/doc.html`

测试点：

- 接口分组显示正常
- 登录接口可以调通
- 业务接口返回结构正常

### 12.12 导出功能测试

建议分别测试：

- 图书导出
- 读者导出
- 借阅记录导出
- 罚款导出
- 日志导出

预期结果：

- Excel 文件下载成功
- 表头正常
- 数据内容可读

## 13. 常见问题排查

### 13.1 后端启动时报数据库连接失败

排查项：

- MySQL 是否已启动
- `application.yml` 用户名密码是否正确
- 数据库 `library_management` 是否存在
- 表是否已导入

### 13.2 后端启动时报 Redis 连接失败

排查项：

- Redis 是否已启动
- Redis 端口是否是 `6379`
- `application.yml` 中 Redis 地址是否正确

### 13.3 IDEA 编译时报 `TypeTag :: UNKNOWN`

通常是 JDK 和 Lombok 环境问题，按以下顺序检查：

1. JDK 是否为 `17`
2. IDEA 的 `Project SDK` 是否为 `17`
3. Maven 使用的 JDK 是否为 `17`
4. Lombok 插件是否安装
5. 注解处理是否启用

### 13.4 前端能启动但页面请求失败

排查项：

- 后端是否已启动
- 后端端口是否为 `8081`
- 前端代理目标是否仍是 `http://127.0.0.1:8081`
- 浏览器控制台是否有接口报错

### 13.5 登录失败但账号密码确认正确

排查项：

- `sys_user` 表中是否存在 `admin`、`librarian`
- 是否执行过 `library_management_reset.sql`
- Redis 是否正常运行

## 14. 目录结构

```text
.
├── src
│   └── main
│       ├── java/com/library
│       │   ├── common
│       │   ├── dto
│       │   ├── modules
│       │   └── vo
│       └── resources
├── frontend
│   ├── src
│   ├── package.json
│   └── vite.config.js
├── sql
│   ├── library_management.sql
│   └── library_management_reset.sql
├── pom.xml
└── docker-compose.yml
```

## 15. 补充说明

- `sql\library_management.sql`：只初始化表结构
- `sql\library_management_reset.sql`：清空旧业务数据并导入新的演示数据
- 后端默认端口：`8081`
- 前端默认端口：`5173`
- Redis 默认端口：`6379`


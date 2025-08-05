# 敏感词过滤系统

基于 Spring Boot + Vue 的前后端分离敏感词过滤系统，采用 DFA 算法实现高效的敏感词检测。

## 技术栈

### 后端
- **Spring Boot 2.7.14** - 主框架
- **MyBatis** - ORM框架
- **MySQL 8.0** - 数据库
- **Redis** - 缓存
- **Maven** - 依赖管理

### 前端
- **Vue 3** - 前端框架
- **Element Plus** - UI组件库
- **Vue Router** - 路由管理
- **Vuex** - 状态管理
- **Axios** - HTTP客户端
- **ECharts** - 图表库

## 功能特性

### 核心功能
- ✅ 实时敏感词过滤
- ✅ 批量文本处理
- ✅ 敏感词管理（增删改查）
- ✅ 过滤记录查询
- ✅ 统计分析图表

### 技术特性
- ✅ DFA算法高效检测
- ✅ Redis缓存优化
- ✅ 分页查询
- ✅ 响应式设计
- ✅ 跨域支持

## 项目结构

```
sensitive-word-filter-system/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/sensitive/wordfilter/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── mapper/      # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── config/      # 配置类
│   │       └── util/        # 工具类
│   ├── src/main/resources/
│   │   ├── mapper/          # MyBatis映射文件
│   │   ├── db/              # 数据库脚本
│   │   └── application.yml  # 配置文件
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── components/      # 组件
│   │   ├── views/           # 页面
│   │   ├── router/          # 路由
│   │   ├── store/           # 状态管理
│   │   └── utils/           # 工具类
│   ├── public/
│   └── package.json
└── README.md
```

## 快速开始

### 环境要求
- JDK 8+
- Node.js 14+
- MySQL 8.0+
- Redis 6.0+

### 后端启动

1. **创建数据库**
```sql
CREATE DATABASE sensitive_word_filter DEFAULT CHARACTER SET utf8mb4;
```

2. **修改配置**
编辑 `backend/src/main/resources/application.yml`，修改数据库和Redis连接信息。

3. **初始化数据库**
执行 `backend/src/main/resources/db/init.sql` 脚本。

4. **启动后端**
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

1. **安装依赖**
```bash
cd frontend
npm install
```

2. **启动开发服务器**
```bash
npm run serve
```

前端应用将在 `http://localhost:3000` 启动。

## API 接口

### 敏感词管理
- `GET /api/sensitive-words/enabled` - 获取所有启用的敏感词
- `GET /api/sensitive-words/page` - 分页查询敏感词
- `POST /api/sensitive-words` - 添加敏感词
- `PUT /api/sensitive-words/{id}` - 更新敏感词
- `DELETE /api/sensitive-words/{id}` - 删除敏感词
- `POST /api/sensitive-words/batch` - 批量添加敏感词

### 文本过滤
- `POST /api/filter/text` - 过滤文本
- `POST /api/filter/check` - 检测文本
- `GET /api/filter/records` - 查询过滤记录

## 核心算法

### DFA算法实现
系统采用DFA（确定有限自动机）算法实现敏感词检测，具有以下优势：
- 时间复杂度：O(n)，n为文本长度
- 空间复杂度：O(m*k)，m为敏感词数量，k为平均敏感词长度
- 支持中文、英文等多种字符
- 支持敏感词分类和级别管理

### 算法流程
1. 构建敏感词字典树
2. 遍历待检测文本
3. 在字典树中查找匹配
4. 返回检测结果

## 部署说明

### 后端部署
```bash
cd backend
mvn clean package
java -jar target/word-filter-1.0.0.jar
```

### 前端部署
```bash
cd frontend
npm run build
# 将dist目录部署到Web服务器
```

## 配置说明

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sensitive_word_filter
    username: yourusername
    password: yourpassword
```

### Redis配置
```yaml
spring:
  redis:
    host: localhost
    password:
    port: 6379
    password: 
    database: 0
```

### 敏感词配置
```yaml
sensitive:
  word:
    file-path: classpath:sensitive-words.txt
    cache-expire: 3600
    replace-char: "*"
```

## 开发说明

### 添加新的敏感词
1. 通过前端界面添加
2. 直接编辑 `sensitive-words.txt` 文件
3. 通过API接口批量导入

### 自定义过滤规则
可以修改 `SensitiveWordFilter` 类来实现自定义的过滤逻辑。

### 扩展功能
- 支持正则表达式匹配
- 支持模糊匹配
- 支持白名单机制
- 支持动态更新敏感词库

## 性能优化

### 缓存策略
- 敏感词字典树缓存到Redis
- 查询结果缓存
- 定期刷新缓存

### 数据库优化
- 索引优化
- 分页查询
- 连接池配置

### 前端优化
- 组件懒加载
- 图片压缩
- CDN加速

## 常见问题

### Q: 如何添加新的敏感词分类？
A: 在数据库的 `sensitive_word` 表中添加新的 `category` 值，前端会自动识别。

### Q: 如何修改敏感词替换字符？
A: 修改 `application.yml` 中的 `sensitive.word.replace-char` 配置。

### Q: 如何提高过滤性能？
A: 增加Redis缓存、优化数据库索引、使用更高效的算法。

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请提交 Issue 或联系开发者。 QQ：908031510

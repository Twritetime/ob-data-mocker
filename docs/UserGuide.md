# OB-Data-Mocker 用户指南

## 1. 简介

OB-Data-Mocker 是一个基于AI的数据库模拟数据生成工具，能够根据字段语义自动生成符合业务场景的测试数据。本指南将帮助您了解如何安装、配置和使用该工具。

## 2. 安装与配置

### 2.1 环境要求

- JDK 8+
- Maven 3.6+（仅开发时需要）

### 2.2 获取可执行文件

有两种方式获取可执行文件：

#### 方式一：下载预编译JAR包

从项目发布页面下载最新版本的JAR包：`ob-data-mocker-<版本号>-jar-with-dependencies.jar`

#### 方式二：从源码构建

1. 克隆代码库
   ```bash
   git clone https://github.com/oceanbase/ob-data-mocker.git
   cd ob-data-mocker
   ```

2. 编译项目
   ```bash
   mvn clean package -DskipTests
   ```

3. 获取JAR包
   编译成功后，可在`target`目录下找到`ob-data-mocker-<版本号>-jar-with-dependencies.jar`文件。

## 3. 基本使用

### 3.1 命令行参数

```bash
java -jar ob-data-mocker-<版本号>-jar-with-dependencies.jar <表名> <字段列表> <类型列表> [行数] [配置文件路径]
```

参数说明：
- **表名**：要生成数据的表名
- **字段列表**：逗号分隔的字段名列表
- **类型列表**：逗号分隔的字段类型列表
- **行数**（可选）：要生成的数据行数，默认为配置文件中的defaultRowCount或100
- **配置文件路径**（可选）：YAML配置文件路径

### 3.2 支持的数据类型

| 类型标识 | 说明 | 示例值 |
|---------|------|--------|
| STRING | 字符串类型 | "张三", "abc123" |
| INT | 整数类型 | 123, -456 |
| DECIMAL | 小数类型 | 123.45, -67.89 |
| DATETIME | 日期时间类型 | "2023-06-15T14:30:00" |
| BOOLEAN | 布尔类型 | true, false |

### 3.3 基本示例

#### 示例1：生成简单的用户数据

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar users "id,name,email,age" "INT,STRING,STRING,INT" 5
```

输出结果：
```json
[
  {
    "id": 724,
    "name": "Joseph Johnson",
    "email": "opgi6@yahoo.com",
    "age": 42
  },
  {
    "id": 684,
    "name": "Joseph Miller",
    "email": "p7k4unx8w2@yahoo.com",
    "age": 28
  },
  ...
]
```

#### 示例2：使用配置文件生成更复杂的数据

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar customers "id,name,email,age,gender,created_time,balance,status,address" "INT,STRING,STRING,INT,STRING,DATETIME,DECIMAL,INT,STRING" 10 config.yaml
```

## 4. 配置文件详解

配置文件使用YAML格式，包含全局配置和字段级别配置。

### 4.1 全局配置

```yaml
# 是否启用AI语义推断
enableAiInference: true

# 生成数据的默认行数
defaultRowCount: 100

# AI模型配置
aiModelConfig:
  modelType: BERT
  modelPath: ""
  usePretrainedModel: true
  inferenceTimeout: 5000

# 排除生成的字段列表
excludeFields:
  - created_by
  - updated_by
```

### 4.2 字段配置

```yaml
# 字段配置映射
fieldConfigs:
  # 用户ID字段配置
  id:
    type: INT
    min: "1"
    max: "1000000"
    allowNull: false
  
  # 用户名字段配置
  username:
    type: STRING
    minLength: 5
    maxLength: 20
    allowNull: false
    pattern: "^[a-zA-Z0-9_]+$"
```

### 4.3 字段配置选项

#### 通用选项

| 选项 | 类型 | 说明 | 适用类型 |
|-----|------|------|---------|
| type | 字符串 | 数据类型 | 所有 |
| allowNull | 布尔值 | 是否允许为空 | 所有 |
| nullRate | 小数 | 为空的概率（0-1） | 所有 |
| enumValues | 数组 | 枚举值列表 | 所有 |

#### 字符串类型选项

| 选项 | 类型 | 说明 |
|-----|------|------|
| minLength | 整数 | 最小长度 |
| maxLength | 整数 | 最大长度 |
| pattern | 字符串 | 正则表达式模式 |

#### 数值类型选项

| 选项 | 类型 | 说明 |
|-----|------|------|
| min | 字符串 | 最小值 |
| max | 字符串 | 最大值 |
| distributionType | 字符串 | 分布类型（UNIFORM、NORMAL、EXPONENTIAL） |

#### 日期时间类型选项

| 选项 | 类型 | 说明 |
|-----|------|------|
| min | 字符串 | 最小日期时间 |
| max | 字符串 | 最大日期时间 |
| format | 字符串 | 日期时间格式 |

## 5. 语义推断

OB-Data-Mocker 会根据字段名称和类型自动推断字段语义，并生成符合语义的数据。

### 5.1 支持的语义类型

| 语义类型 | 说明 | 字段名示例 |
|---------|------|-----------|
| NAME | 姓名 | name, username, full_name |
| EMAIL | 电子邮件 | email, mail, email_address |
| AGE | 年龄 | age, user_age |
| GENDER | 性别 | gender, sex |
| ADDRESS | 地址 | address, addr, location |
| PHONE | 电话号码 | phone, telephone, mobile |
| ID | 标识符 | id, user_id, customer_id |
| DATETIME | 日期时间 | created_time, update_time, birth_date |
| AMOUNT | 金额 | amount, price, balance |
| STATUS | 状态 | status, state |
| UNKNOWN | 未知类型 | 其他无法识别的字段 |

### 5.2 语义推断规则

系统会根据以下规则进行语义推断：

1. **字段名匹配**：根据字段名中的关键词判断语义类型
2. **字段类型匹配**：结合SQL类型进一步确认语义类型
3. **上下文关联**：考虑表名和相关字段推断语义

## 6. 高级用法

### 6.1 自定义数据分布

通过设置`distributionType`参数，可以控制数值类型数据的分布：

```yaml
age:
  type: INT
  min: "18"
  max: "80"
  distributionType: NORMAL  # 正态分布，集中在中间值
```

### 6.2 使用正则表达式模式

可以使用正则表达式定义字符串数据的格式：

```yaml
phone:
  type: STRING
  pattern: "1[3-9]\\d{9}"  # 中国大陆手机号格式
```

### 6.3 使用枚举值

对于有限集合的字段，可以使用枚举值：

```yaml
status:
  type: INT
  enumValues:
    - "0"  # 未激活
    - "1"  # 正常
    - "2"  # 锁定
```

## 7. 常见问题

### 7.1 数据生成不符合预期

**问题**：生成的数据不符合业务规则或格式要求。

**解决方案**：
1. 检查字段名是否能够准确表达语义
2. 在配置文件中明确定义字段生成规则
3. 使用正则表达式或枚举值限制数据格式

### 7.2 日期时间格式问题

**问题**：日期时间格式不正确。

**解决方案**：
在配置文件中明确指定日期时间格式：

```yaml
created_time:
  type: DATETIME
  format: "yyyy-MM-dd HH:mm:ss"
```

### 7.3 性能问题

**问题**：生成大量数据时性能较慢。

**解决方案**：
1. 禁用AI推断：`enableAiInference: false`
2. 减少复杂字段的生成
3. 分批次生成数据

## 8. 最佳实践

1. **合理命名字段**：使用语义明确的字段名，有助于系统准确推断字段语义
2. **使用配置文件**：对于重复使用的场景，创建配置文件更加高效
3. **分阶段生成数据**：先生成基本数据，再生成关联数据
4. **验证生成的数据**：使用数据库验证工具检查生成的数据是否符合约束

## 9. 附录

### 9.1 完整配置文件示例

```yaml
# 示例配置文件
enableAiInference: true
defaultRowCount: 100

aiModelConfig:
  modelType: BERT
  modelPath: ""
  usePretrainedModel: true
  inferenceTimeout: 5000

excludeFields:
  - created_by
  - updated_by

fieldConfigs:
  # 用户ID字段配置
  id:
    type: INT
    min: "1"
    max: "1000000"
    allowNull: false
  
  # 用户名字段配置
  username:
    type: STRING
    minLength: 5
    maxLength: 20
    allowNull: false
    pattern: "^[a-zA-Z0-9_]+$"
  
  # 邮箱字段配置
  email:
    type: STRING
    allowNull: false
    pattern: "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"
  
  # 年龄字段配置
  age:
    type: INT
    min: "18"
    max: "80"
    allowNull: true
    nullRate: 0.1
    distributionType: NORMAL
  
  # 性别字段配置
  gender:
    type: STRING
    enumValues:
      - "男"
      - "女"
      - "未知"
    allowNull: true
    nullRate: 0.05
  
  # 创建时间字段配置
  created_time:
    type: DATETIME
    min: "2020-01-01 00:00:00"
    max: "2023-12-31 23:59:59"
    format: "yyyy-MM-dd HH:mm:ss"
    allowNull: false
  
  # 余额字段配置
  balance:
    type: DECIMAL
    min: "0"
    max: "100000"
    allowNull: false
    distributionType: EXPONENTIAL
  
  # 状态字段配置
  status:
    type: INT
    enumValues:
      - "0"
      - "1"
      - "2"
    allowNull: false
  
  # 地址字段配置
  address:
    type: STRING
    minLength: 10
    maxLength: 200
    allowNull: true
    nullRate: 0.2
```

### 9.2 命令行选项参考

```
用法: java -jar ob-data-mocker.jar [选项] <表名> <字段列表> <类型列表> [行数] [配置文件]

选项:
  -h, --help            显示帮助信息
  -v, --version         显示版本信息
  -o, --output <file>   将输出保存到文件
  -f, --format <format> 输出格式 (json, csv, sql)
  --debug               启用调试模式
``` 
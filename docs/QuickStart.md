# OB-Data-Mocker 快速入门指南

## 1. 简介

OB-Data-Mocker 是一个基于AI的数据库模拟数据生成工具，能够根据字段语义自动生成符合业务场景的测试数据。本指南将帮助您快速上手使用该工具。

## 2. 安装

### 2.1 下载JAR包

从项目发布页面下载最新版本的JAR包：`ob-data-mocker-<版本号>-jar-with-dependencies.jar`

### 2.2 环境要求

- JDK 8+

## 3. 快速开始

### 3.1 基本命令格式

```bash
java -jar ob-data-mocker-<版本号>-jar-with-dependencies.jar <表名> <字段列表> <类型列表> [行数] [配置文件路径]
```

### 3.2 简单示例

生成5条用户数据：

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

### 3.3 使用配置文件

创建配置文件 `config.yaml`：

```yaml
# 是否启用AI语义推断
enableAiInference: true

# 生成数据的默认行数
defaultRowCount: 100

# 字段配置映射
fieldConfigs:
  # 用户ID字段配置
  id:
    type: INT
    min: "1"
    max: "1000"
    allowNull: false
  
  # 邮箱字段配置
  email:
    type: STRING
    pattern: "^[a-z0-9]+@example\\.com$"
```

使用配置文件生成数据：

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar users "id,name,email,age" "INT,STRING,STRING,INT" 5 config.yaml
```

## 4. 常见用例

### 4.1 生成用户数据

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar users "id,username,email,age,gender,created_time" "INT,STRING,STRING,INT,STRING,DATETIME" 10
```

### 4.2 生成订单数据

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar orders "id,user_id,order_amount,status,created_time" "INT,INT,DECIMAL,INT,DATETIME" 10
```

### 4.3 生成产品数据

```bash
java -jar ob-data-mocker-1.0-SNAPSHOT-jar-with-dependencies.jar products "id,name,price,category,stock,description" "INT,STRING,DECIMAL,STRING,INT,STRING" 10
```

## 5. 下一步

- 查看 [用户指南](UserGuide.md) 了解更多详细信息
- 查看 [配置示例](example-config.yaml) 了解更多配置选项
- 查看 [开发者指南](DeveloperGuide.md) 了解如何扩展工具功能 
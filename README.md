# OB-Data-Mocker

基于AI的数据库模拟数据生成工具，通过语义推断自动生成符合业务场景的测试数据。

## 1. 项目简介

OB-Data-Mocker 是一个智能化的数据模拟工具，旨在解决开发和测试过程中对真实数据的需求。传统的数据生成工具通常只能生成随机数据，而不考虑字段的实际语义和业务规则。本工具通过规则引擎和AI技术，能够理解字段的语义，并生成更符合实际业务场景的数据。

### 1.1 核心特性

- **语义理解**：通过字段名称和类型自动推断字段语义
- **智能生成**：根据语义生成符合业务规则的数据
- **灵活配置**：支持YAML配置文件定义生成规则
- **多种数据类型**：支持整数、字符串、日期时间、小数等多种数据类型
- **分布控制**：支持正态分布、指数分布等多种数据分布方式
- **命令行接口**：简单易用的命令行工具

## 2. 系统架构

OB-Data-Mocker 采用模块化设计，主要包含以下几个核心模块：

### 2.1 模块结构

```
com.oceanbase.datamocker
├── ai                  // AI语义推断模块
│   ├── FieldSemanticInferrer        // 字段语义推断接口
│   ├── SemanticType                 // 语义类型枚举
│   ├── RuleBasedSemanticInferrer    // 基于规则的推断实现
│   ├── AiSemanticInferrer           // 基于AI的推断实现
│   └── SemanticInferrerFactory      // 推断器工厂
├── config              // 配置模块
│   ├── MockerConfig                 // 全局配置
│   └── FieldConfig                  // 字段配置
├── generator           // 数据生成模块
│   ├── DataGenerator               // 数据生成器接口
│   ├── AbstractDataGenerator       // 抽象数据生成器
│   ├── StringDataGenerator         // 字符串生成器
│   ├── IntegerDataGenerator        // 整数生成器
│   ├── DecimalDataGenerator        // 小数生成器
│   ├── DateTimeDataGenerator       // 日期时间生成器
│   └── SemanticDataGenerator       // 语义数据生成器
├── util                // 工具类
│   └── ConfigLoader                // 配置加载器
├── DataMocker          // 核心类
└── cli                 // 命令行工具
    └── DataMockerCli               // 命令行入口
```

### 2.2 核心流程

1. **配置加载**：从YAML文件或命令行参数加载配置信息
2. **语义推断**：分析字段名称和类型，推断字段语义
3. **规则生成**：根据语义和配置生成数据生成规则
4. **数据生成**：根据规则生成符合要求的数据
5. **结果输出**：将生成的数据以JSON格式输出

## 3. 语义推断

语义推断是本工具的核心特性，主要通过两种方式实现：

### 3.1 规则推断

基于预定义的规则集，通过字段名称的关键词匹配和模式识别来推断字段语义。例如：

- 包含"name"、"username"的字段推断为姓名类型
- 包含"email"、"mail"的字段推断为邮箱类型
- 包含"age"的字段推断为年龄类型
- 包含"gender"、"sex"的字段推断为性别类型

### 3.2 AI推断

通过预训练的NLP模型，分析字段名称的语义，实现更准确的语义推断。当前版本提供了AI推断的框架，但实际实现使用规则推断作为替代。

## 4. 数据生成

根据字段的语义类型和配置规则，生成符合要求的数据：

### 4.1 支持的数据类型

- **STRING**：字符串类型，支持长度控制、正则模式等
- **INT**：整数类型，支持范围控制、分布类型等
- **DECIMAL**：小数类型，支持精度控制、分布类型等
- **DATETIME**：日期时间类型，支持范围控制、格式定义等
- **BOOLEAN**：布尔类型

### 4.2 分布类型

- **UNIFORM**：均匀分布，所有值出现概率相同
- **NORMAL**：正态分布，集中在中间值附近
- **EXPONENTIAL**：指数分布，集中在较小值附近

## 5. 配置文件

OB-Data-Mocker 支持通过YAML配置文件定义生成规则：

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

## 6. 使用说明

### 6.1 命令行参数

```
java -jar ob-data-mocker.jar <表名> <字段列表> <类型列表> [行数] [配置文件路径]
```

- **表名**：要生成数据的表名
- **字段列表**：逗号分隔的字段名列表
- **类型列表**：逗号分隔的字段类型列表
- **行数**：要生成的数据行数（可选，默认为配置文件中的defaultRowCount）
- **配置文件路径**：YAML配置文件路径（可选）

### 6.2 示例

生成用户表数据：

```
java -jar ob-data-mocker.jar users "id,name,email,age,gender" "INT,STRING,STRING,INT,STRING" 10
```

使用配置文件生成客户表数据：

```
java -jar ob-data-mocker.jar customers "id,name,email,age,gender,created_time,balance,status,address" "INT,STRING,STRING,INT,STRING,DATETIME,DECIMAL,INT,STRING" 10 config.yaml
```

### 6.3 输出示例

```json
[
  {
    "id": 199013,
    "name": "William Jackson",
    "email": "y0t8ofibuj@yahoo.com",
    "age": 35,
    "gender": "未知",
    "created_time": "2026-03-15T18:13:54",
    "balance": 3944.85,
    "status": 0,
    "address": "上海市西安大兴区安定门368号"
  },
  {
    "id": 635775,
    "name": "高勇",
    "email": "6rc53r2@example.com",
    "age": 43,
    "gender": "女",
    "created_time": "1976-02-06T21:53:00",
    "balance": 8124.77,
    "status": 2,
    "address": null
  }
]
```

## 7. 开发指南

### 7.1 环境要求

- JDK 8+
- Maven 3.6+

### 7.2 构建项目

```
mvn clean package
```

### 7.3 扩展语义推断

要添加新的语义推断规则，可以修改 `RuleBasedSemanticInferrer` 类：

```java
private SemanticType inferFromFieldName(String fieldName) {
    fieldName = fieldName.toLowerCase();
    
    // 添加新的规则
    if (fieldName.contains("phone") || fieldName.contains("mobile")) {
        return SemanticType.PHONE_NUMBER;
    }
    
    // 其他规则...
}
```

### 7.4 扩展数据生成器

要添加新的数据生成器，可以实现 `DataGenerator` 接口或继承 `AbstractDataGenerator` 类：

```java
public class PhoneNumberGenerator extends AbstractDataGenerator<String> {
    @Override
    public String generate(FieldConfig config) {
        // 实现电话号码生成逻辑
    }
}
```

## 8. 未来规划

- **完善AI模型集成**：实现真正的AI语义推断
- **支持更多数据类型**：如地理位置、JSON等复杂类型
- **关联字段生成**：支持字段间的依赖关系
- **数据库集成**：直接从数据库表结构生成配置
- **Web界面**：提供可视化配置界面

## 9. 许可证

本项目采用 Apache 2.0 许可证。详情请参阅 [LICENSE](LICENSE) 文件。

## 10. 贡献指南

欢迎提交问题报告和功能请求。如果您想贡献代码，请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

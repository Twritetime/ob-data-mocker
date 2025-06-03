# OB-Data-Mocker 开发者指南

## 1. 开发环境设置

### 1.1 环境要求

- JDK 8+
- Maven 3.6+
- 任意IDE（推荐IntelliJ IDEA或Eclipse）

### 1.2 获取源码

```bash
git clone https://github.com/oceanbase/ob-data-mocker.git
cd ob-data-mocker
```

### 1.3 项目导入

**IntelliJ IDEA**:
1. 选择 File -> Open
2. 导航到项目根目录并选择pom.xml
3. 选择"作为项目打开"

**Eclipse**:
1. 选择 File -> Import
2. 选择 Maven -> Existing Maven Projects
3. 导航到项目根目录并选择pom.xml

### 1.4 构建项目

```bash
mvn clean install
```

## 2. 项目结构

```
ob-data-mocker/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── oceanbase/
│   │   │           └── datamocker/
│   │   │               ├── ai/         # AI语义推断模块
│   │   │               ├── config/     # 配置模块
│   │   │               ├── generator/  # 数据生成模块
│   │   │               ├── util/       # 工具类
│   │   │               ├── DataMocker.java  # 核心类
│   │   │               └── cli/        # 命令行工具
│   │   └── resources/  # 资源文件
│   └── test/           # 测试代码
├── pom.xml             # Maven配置
└── README.md           # 项目说明
```

## 3. 核心模块详解

### 3.1 AI语义推断模块

位于`com.oceanbase.datamocker.ai`包下，负责分析字段名称和类型，推断字段语义。

**主要组件**:

- `FieldSemanticInferrer`: 字段语义推断接口
- `SemanticType`: 语义类型枚举
- `RuleBasedSemanticInferrer`: 基于规则的推断实现
- `AiSemanticInferrer`: 基于AI的推断实现
- `SemanticInferrerFactory`: 推断器工厂类

**扩展语义推断**:

要添加新的语义类型，首先在`SemanticType`枚举中添加定义：

```java
public enum SemanticType {
    // 现有类型
    NAME,
    EMAIL,
    AGE,
    
    // 添加新类型
    PHONE_NUMBER,
    
    // 默认类型
    UNKNOWN
}
```

然后在`RuleBasedSemanticInferrer`中添加推断规则：

```java
private SemanticType inferFromFieldName(String fieldName) {
    fieldName = fieldName.toLowerCase();
    
    // 添加新的规则
    if (fieldName.contains("phone") || fieldName.contains("mobile") || fieldName.contains("tel")) {
        return SemanticType.PHONE_NUMBER;
    }
    
    // 其他规则...
    return SemanticType.UNKNOWN;
}
```

### 3.2 配置模块

位于`com.oceanbase.datamocker.config`包下，负责加载和处理配置信息。

**主要组件**:

- `MockerConfig`: 全局配置类
- `FieldConfig`: 字段配置类
- `ConfigLoader`: 配置加载器

**配置类示例**:

```java
@Data
public class FieldConfig {
    private String type;
    private String min;
    private String max;
    private Integer minLength;
    private Integer maxLength;
    private String pattern;
    private Boolean allowNull = false;
    private Double nullRate = 0.0;
    private List<String> enumValues;
    private String distributionType = "UNIFORM";
    private String format;
    
    // 其他属性和方法
}
```

### 3.3 数据生成模块

位于`com.oceanbase.datamocker.generator`包下，负责根据配置和语义类型生成数据。

**主要组件**:

- `DataGenerator`: 数据生成器接口
- `AbstractDataGenerator`: 抽象数据生成器
- `StringDataGenerator`: 字符串生成器
- `IntegerDataGenerator`: 整数生成器
- `DecimalDataGenerator`: 小数生成器
- `DateTimeDataGenerator`: 日期时间生成器
- `SemanticDataGenerator`: 语义数据生成器

**实现自定义生成器**:

```java
public class PhoneNumberGenerator extends AbstractDataGenerator<String> {
    
    @Override
    public String generate(FieldConfig config) {
        // 检查是否有模式定义
        if (config.getPattern() != null && !config.getPattern().isEmpty()) {
            return generateByPattern(config.getPattern());
        }
        
        // 默认生成中国大陆手机号
        return "1" + (new Random().nextInt(9) + 1) + generateRandomDigits(9);
    }
    
    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
```

注册生成器：

```java
public class SemanticDataGenerator {
    
    private final Map<SemanticType, DataGenerator<?>> semanticGenerators = new HashMap<>();
    
    public SemanticDataGenerator() {
        // 注册现有生成器
        semanticGenerators.put(SemanticType.NAME, new NameGenerator());
        semanticGenerators.put(SemanticType.EMAIL, new EmailGenerator());
        
        // 注册新生成器
        semanticGenerators.put(SemanticType.PHONE_NUMBER, new PhoneNumberGenerator());
    }
    
    // 其他方法
}
```

## 4. 核心流程

### 4.1 数据生成流程

1. **配置加载**：从YAML文件或命令行参数加载配置
2. **语义推断**：分析字段名称和类型，推断字段语义
3. **规则生成**：根据语义和配置生成数据生成规则
4. **数据生成**：根据规则生成符合要求的数据
5. **结果输出**：将生成的数据以JSON格式输出

### 4.2 关键代码流程

```java
// 1. 初始化配置
MockerConfig config = ConfigLoader.loadConfig(configFilePath);

// 2. 创建语义推断器
FieldSemanticInferrer inferrer = SemanticInferrerFactory.createInferrer(config);

// 3. 初始化数据生成器
Map<String, DataGenerator<?>> generators = new HashMap<>();
for (String fieldName : fieldNames) {
    // 获取字段类型
    String sqlType = fieldTypes.get(fieldName);
    
    // 推断语义
    SemanticType semanticType = inferrer.inferSemanticType(fieldName, sqlType);
    
    // 生成字段配置
    FieldConfig fieldConfig = inferrer.generateFieldConfig(fieldName, sqlType, semanticType);
    
    // 创建数据生成器
    DataGenerator<?> generator = DataGeneratorFactory.createGenerator(fieldConfig, semanticType);
    generators.put(fieldName, generator);
}

// 4. 生成数据
List<Map<String, Object>> data = new ArrayList<>();
for (int i = 0; i < rowCount; i++) {
    Map<String, Object> row = new HashMap<>();
    for (String fieldName : fieldNames) {
        DataGenerator<?> generator = generators.get(fieldName);
        Object value = generator.generate();
        row.put(fieldName, value);
    }
    data.add(row);
}

// 5. 输出结果
return JsonUtils.toJson(data);
```

## 5. 扩展点

### 5.1 添加新的数据类型

1. 在`DataGeneratorFactory`中添加新类型的处理：

```java
public static DataGenerator<?> createGenerator(FieldConfig config, SemanticType semanticType) {
    switch (config.getType().toUpperCase()) {
        case "STRING":
            return new StringDataGenerator(config);
        case "INT":
            return new IntegerDataGenerator(config);
        // 添加新类型
        case "ARRAY":
            return new ArrayDataGenerator(config);
        default:
            throw new IllegalArgumentException("Unsupported data type: " + config.getType());
    }
}
```

2. 实现对应的生成器类：

```java
public class ArrayDataGenerator extends AbstractDataGenerator<List<Object>> {
    
    private final DataGenerator<?> elementGenerator;
    private final int minSize;
    private final int maxSize;
    
    public ArrayDataGenerator(FieldConfig config) {
        super(config);
        this.elementGenerator = DataGeneratorFactory.createGenerator(config.getElementConfig(), SemanticType.UNKNOWN);
        this.minSize = config.getMinSize() != null ? config.getMinSize() : 0;
        this.maxSize = config.getMaxSize() != null ? config.getMaxSize() : 10;
    }
    
    @Override
    public List<Object> generate() {
        int size = minSize + random.nextInt(maxSize - minSize + 1);
        List<Object> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(elementGenerator.generate());
        }
        return result;
    }
}
```

### 5.2 添加新的分布类型

1. 在`DistributionType`枚举中添加新分布类型：

```java
public enum DistributionType {
    UNIFORM,
    NORMAL,
    EXPONENTIAL,
    // 添加新分布类型
    PARETO
}
```

2. 在数值生成器中实现新分布类型的处理：

```java
protected double generateDistributedValue(double min, double max, String distributionType) {
    switch (DistributionType.valueOf(distributionType)) {
        case UNIFORM:
            return min + random.nextDouble() * (max - min);
        case NORMAL:
            // 正态分布生成
            double mean = (min + max) / 2;
            double stdDev = (max - min) / 6; // 99.7%的值在6个标准差内
            return Math.max(min, Math.min(max, random.nextGaussian() * stdDev + mean));
        case EXPONENTIAL:
            // 指数分布生成
            double lambda = 1.0 / ((max - min) / 3); // 平均值在1/3处
            return min + Math.min(max - min, -Math.log(1 - random.nextDouble()) / lambda);
        case PARETO:
            // 帕累托分布生成
            double alpha = 3.0; // 形状参数
            double xm = min; // 最小值
            return xm / Math.pow(random.nextDouble(), 1/alpha);
        default:
            return min + random.nextDouble() * (max - min);
    }
}
```

### 5.3 添加新的输出格式

1. 创建新的格式化器接口：

```java
public interface DataFormatter {
    String format(List<Map<String, Object>> data);
}
```

2. 实现具体的格式化器：

```java
public class CsvFormatter implements DataFormatter {
    
    @Override
    public String format(List<Map<String, Object>> data) {
        if (data == null || data.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        
        // 添加表头
        Map<String, Object> firstRow = data.get(0);
        sb.append(String.join(",", firstRow.keySet())).append("\n");
        
        // 添加数据行
        for (Map<String, Object> row : data) {
            sb.append(row.values().stream()
                    .map(this::formatValue)
                    .collect(Collectors.joining(","))).append("\n");
        }
        
        return sb.toString();
    }
    
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return "\"" + ((String) value).replace("\"", "\"\"") + "\"";
        }
        return String.valueOf(value);
    }
}
```

3. 在`DataMocker`中添加格式选择：

```java
public String generateData(String format) {
    List<Map<String, Object>> data = generateRawData();
    
    switch (format.toLowerCase()) {
        case "json":
            return new JsonFormatter().format(data);
        case "csv":
            return new CsvFormatter().format(data);
        case "sql":
            return new SqlFormatter(tableName).format(data);
        default:
            return new JsonFormatter().format(data);
    }
}
```

## 6. 测试指南

### 6.1 单元测试

项目使用JUnit 5进行单元测试。测试类应放在`src/test/java`目录下，包结构与主代码保持一致。

**测试示例**:

```java
@Test
void testStringDataGenerator() {
    // 准备测试数据
    FieldConfig config = new FieldConfig();
    config.setType("STRING");
    config.setMinLength(5);
    config.setMaxLength(10);
    
    // 创建生成器
    StringDataGenerator generator = new StringDataGenerator(config);
    
    // 生成数据并验证
    String result = generator.generate();
    assertNotNull(result);
    assertTrue(result.length() >= 5 && result.length() <= 10);
}
```

### 6.2 集成测试

集成测试应该测试多个组件的协同工作。

**测试示例**:

```java
@Test
void testEndToEndDataGeneration() {
    // 准备测试数据
    String tableName = "users";
    List<String> fieldNames = Arrays.asList("id", "name", "email");
    List<String> fieldTypes = Arrays.asList("INT", "STRING", "STRING");
    int rowCount = 5;
    
    // 创建DataMocker实例
    DataMocker mocker = new DataMocker(tableName, fieldNames, fieldTypes, rowCount);
    
    // 生成数据
    String result = mocker.generateData();
    
    // 验证结果
    assertNotNull(result);
    assertTrue(result.startsWith("[") && result.endsWith("]"));
    
    // 解析JSON并验证内容
    ObjectMapper mapper = new ObjectMapper();
    try {
        List<Map<String, Object>> data = mapper.readValue(result, new TypeReference<List<Map<String, Object>>>() {});
        assertEquals(rowCount, data.size());
        
        // 验证第一行数据
        Map<String, Object> firstRow = data.get(0);
        assertTrue(firstRow.containsKey("id"));
        assertTrue(firstRow.containsKey("name"));
        assertTrue(firstRow.containsKey("email"));
    } catch (Exception e) {
        fail("Failed to parse JSON result: " + e.getMessage());
    }
}
```

## 7. 性能优化

### 7.1 批量生成优化

对于大量数据生成，可以考虑以下优化：

1. **并行生成**：使用Java并行流或线程池并行生成数据

```java
public List<Map<String, Object>> generateRawDataParallel() {
    return IntStream.range(0, rowCount)
            .parallel()
            .mapToObj(i -> generateRow())
            .collect(Collectors.toList());
}

private Map<String, Object> generateRow() {
    Map<String, Object> row = new HashMap<>();
    for (String fieldName : fieldNames) {
        DataGenerator<?> generator = generators.get(fieldName);
        Object value = generator.generate();
        row.put(fieldName, value);
    }
    return row;
}
```

2. **缓存优化**：对于重复使用的生成器，可以缓存生成结果

```java
public class CachedDataGenerator<T> implements DataGenerator<T> {
    
    private final DataGenerator<T> delegate;
    private final Map<String, T> cache = new ConcurrentHashMap<>();
    private final int cacheSize;
    
    public CachedDataGenerator(DataGenerator<T> delegate, int cacheSize) {
        this.delegate = delegate;
        this.cacheSize = cacheSize;
    }
    
    @Override
    public T generate() {
        String key = UUID.randomUUID().toString();
        T value = delegate.generate();
        if (cache.size() < cacheSize) {
            cache.put(key, value);
        }
        return value;
    }
    
    public T getFromCache() {
        if (cache.isEmpty()) {
            return generate();
        }
        return cache.values().iterator().next();
    }
}
```

### 7.2 内存优化

对于大量数据生成，可以考虑流式处理，避免一次性加载所有数据到内存：

```java
public void generateToFile(String filePath) throws IOException {
    try (JsonGenerator generator = JsonFactory.createGenerator(new FileWriter(filePath))) {
        generator.writeStartArray();
        
        for (int i = 0; i < rowCount; i++) {
            Map<String, Object> row = generateRow();
            generator.writeObject(row);
            
            // 定期清理内存
            if (i % 1000 == 0) {
                System.gc();
            }
        }
        
        generator.writeEndArray();
    }
}
```

## 8. 发布流程

### 8.1 版本控制

项目使用语义化版本控制（Semantic Versioning）：

- **主版本号**：不兼容的API变更
- **次版本号**：向后兼容的功能性新增
- **修订号**：向后兼容的问题修正

### 8.2 构建发布包

使用Maven构建发布包：

```bash
# 更新版本号
mvn versions:set -DnewVersion=1.0.0

# 构建发布包
mvn clean package

# 构建包含依赖的可执行JAR
mvn clean package -P with-dependencies
```

### 8.3 发布检查清单

- [ ] 所有测试通过
- [ ] 代码格式符合规范
- [ ] 文档已更新
- [ ] 版本号已更新
- [ ] CHANGELOG已更新
- [ ] 性能测试已完成

## 9. 代码规范

### 9.1 Java代码规范

- 使用4个空格缩进
- 类名使用UpperCamelCase
- 方法名和变量名使用lowerCamelCase
- 常量名使用UPPER_SNAKE_CASE
- 每个类和方法都应有Javadoc注释

### 9.2 提交规范

提交消息应遵循以下格式：

```
<type>(<scope>): <subject>

<body>

<footer>
```

其中：
- **type**: feat, fix, docs, style, refactor, test, chore
- **scope**: 可选，表示修改的范围
- **subject**: 简短描述
- **body**: 详细描述
- **footer**: 可选，用于引用问题ID

示例：

```
feat(generator): 添加电话号码生成器

实现了中国大陆手机号的生成逻辑，支持自定义格式。

Closes #123
```

## 10. 常见问题与解决方案

### 10.1 依赖冲突

**问题**：运行时出现ClassNotFoundException或NoSuchMethodError。

**解决方案**：
1. 使用`mvn dependency:tree`查看依赖树
2. 使用`<exclusions>`标签排除冲突依赖
3. 使用`<dependencyManagement>`统一依赖版本

### 10.2 内存溢出

**问题**：生成大量数据时出现OutOfMemoryError。

**解决方案**：
1. 增加JVM堆内存：`java -Xmx4g -jar ob-data-mocker.jar ...`
2. 使用流式处理代替一次性加载
3. 分批次生成数据

### 10.3 性能问题

**问题**：数据生成速度较慢。

**解决方案**：
1. 使用并行流或线程池并行生成数据
2. 减少复杂字段的生成
3. 禁用AI推断：`enableAiInference: false`
4. 优化正则表达式匹配逻辑

## 11. API参考

### 11.1 核心类

**DataMocker**:

```java
public class DataMocker {
    /**
     * 构造函数
     * @param tableName 表名
     * @param fieldNames 字段名列表
     * @param fieldTypes 字段类型列表
     * @param rowCount 行数
     * @param configFilePath 配置文件路径（可选）
     */
    public DataMocker(String tableName, List<String> fieldNames, List<String> fieldTypes, 
                     int rowCount, String configFilePath);
    
    /**
     * 生成数据
     * @return JSON格式的数据
     */
    public String generateData();
    
    /**
     * 生成数据并指定输出格式
     * @param format 输出格式（json, csv, sql）
     * @return 指定格式的数据
     */
    public String generateData(String format);
    
    /**
     * 生成数据并写入文件
     * @param filePath 输出文件路径
     * @param format 输出格式（json, csv, sql）
     */
    public void generateToFile(String filePath, String format) throws IOException;
}
```

**FieldSemanticInferrer**:

```java
public interface FieldSemanticInferrer {
    /**
     * 初始化推断器
     */
    void initialize();
    
    /**
     * 推断字段语义类型
     * @param fieldName 字段名
     * @param sqlType SQL类型
     * @return 语义类型
     */
    SemanticType inferSemanticType(String fieldName, String sqlType);
    
    /**
     * 生成字段配置
     * @param fieldName 字段名
     * @param sqlType SQL类型
     * @param semanticType 语义类型
     * @return 字段配置
     */
    FieldConfig generateFieldConfig(String fieldName, String sqlType, SemanticType semanticType);
    
    /**
     * 关闭推断器，释放资源
     */
    void close();
}
```

**DataGenerator**:

```java
public interface DataGenerator<T> {
    /**
     * 生成数据
     * @return 生成的数据
     */
    T generate();
    
    /**
     * 生成可能为null的数据
     * @return 生成的数据，可能为null
     */
    default T generateNullable() {
        if (allowNull() && random.nextDouble() < nullRate()) {
            return null;
        }
        return generate();
    }
    
    /**
     * 是否允许为空
     * @return 是否允许为空
     */
    boolean allowNull();
    
    /**
     * 为空的概率
     * @return 为空的概率（0-1）
     */
    double nullRate();
}
```

## 12. 贡献指南

### 12.1 提交流程

1. Fork 项目仓库
2. 创建功能分支：`git checkout -b feature/amazing-feature`
3. 提交更改：`git commit -m 'Add some amazing feature'`
4. 推送到分支：`git push origin feature/amazing-feature`
5. 提交Pull Request

### 12.2 代码审查标准

- 代码符合项目规范
- 所有测试通过
- 新功能有对应的测试
- 文档已更新
- 没有引入新的警告或错误

### 12.3 文档贡献

文档改进同样重要，可以通过以下方式贡献：

- 修复文档错误
- 添加示例和教程
- 改进API文档
- 翻译文档

## 13. 联系方式

- **项目维护者**：[维护者姓名](mailto:maintainer@example.com)
- **问题反馈**：[GitHub Issues](https://github.com/oceanbase/ob-data-mocker/issues)
- **邮件列表**：[dev@oceanbase.org](mailto:dev@oceanbase.org) 
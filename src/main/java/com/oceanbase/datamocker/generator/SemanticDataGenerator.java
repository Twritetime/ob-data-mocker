package com.oceanbase.datamocker.generator;

import com.oceanbase.datamocker.ai.FieldSemanticInferrer;
import com.oceanbase.datamocker.ai.SemanticType;
import com.oceanbase.datamocker.config.FieldConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 语义数据生成器
 * 根据字段的语义类型生成相应的数据
 */
@Slf4j
public class SemanticDataGenerator extends AbstractDataGenerator {
    
    private final FieldSemanticInferrer semanticInferrer;
    private final Map<SemanticType, DataGenerator> semanticGenerators = new HashMap<>();
    
    public SemanticDataGenerator(FieldSemanticInferrer semanticInferrer) {
        this.semanticInferrer = semanticInferrer;
        initializeSemanticGenerators();
    }
    
    /**
     * 初始化语义生成器映射
     */
    private void initializeSemanticGenerators() {
        // 注册各种语义类型的生成器
        // 这里可以根据需要扩展更多的语义类型生成器
        registerSemanticGenerator(SemanticType.NAME, new NameGenerator());
        registerSemanticGenerator(SemanticType.EMAIL, new EmailGenerator());
        registerSemanticGenerator(SemanticType.PHONE, new PhoneGenerator());
        registerSemanticGenerator(SemanticType.ADDRESS, new AddressGenerator());
    }
    
    /**
     * 注册语义生成器
     *
     * @param type 语义类型
     * @param generator 数据生成器
     */
    public void registerSemanticGenerator(SemanticType type, DataGenerator generator) {
        semanticGenerators.put(type, generator);
        log.debug("Registered semantic generator for type: {}", type);
    }
    
    @Override
    public boolean supports(String fieldType) {
        // 语义生成器支持所有类型，因为它是基于语义而非类型
        return true;
    }
    
    @Override
    protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
        // 推断字段的语义类型
        SemanticType semanticType = semanticInferrer.inferSemanticType(fieldName, fieldConfig.getType());
        log.debug("Inferred semantic type for field '{}': {}", fieldName, semanticType);
        
        // 如果有对应的语义生成器，使用它生成数据
        DataGenerator semanticGenerator = semanticGenerators.get(semanticType);
        if (semanticGenerator != null) {
            log.debug("Using semantic generator for type: {}", semanticType);
            return semanticGenerator.generate(fieldName, fieldConfig);
        }
        
        // 如果没有对应的语义生成器，使用常规的类型生成器
        log.debug("No semantic generator found for type: {}, using type-based generator", semanticType);
        DataGenerator typeGenerator = DataGeneratorFactory.getGenerator(fieldConfig.getType());
        return typeGenerator.generate(fieldName, fieldConfig);
    }
    
    /**
     * 姓名生成器
     */
    private class NameGenerator extends AbstractDataGenerator {
        private final String[] FIRST_NAMES_CN = {"张", "王", "李", "赵", "陈", "刘", "杨", "黄", "周", "吴", "徐", "孙", "胡", "朱", "高", "林"};
        private final String[] LAST_NAMES_CN = {"伟", "芳", "娜", "秀英", "敏", "静", "丽", "强", "磊", "军", "洋", "勇", "艳", "杰", "娟", "涛", "明", "超", "秀兰"};
        private final String[] FIRST_NAMES_EN = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles", "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth"};
        private final String[] LAST_NAMES_EN = {"Smith", "Johnson", "Williams", "Jones", "Brown", "Davis", "Miller", "Wilson", "Moore", "Taylor", "Anderson", "Thomas", "Jackson", "White", "Harris"};
        
        @Override
        public boolean supports(String fieldType) {
            return true;
        }
        
        @Override
        protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
            // 随机决定生成中文名还是英文名
            boolean useChinese = random.nextBoolean();
            
            if (useChinese) {
                String firstName = FIRST_NAMES_CN[random.nextInt(FIRST_NAMES_CN.length)];
                String lastName = LAST_NAMES_CN[random.nextInt(LAST_NAMES_CN.length)];
                return firstName + lastName;
            } else {
                String firstName = FIRST_NAMES_EN[random.nextInt(FIRST_NAMES_EN.length)];
                String lastName = LAST_NAMES_EN[random.nextInt(LAST_NAMES_EN.length)];
                return firstName + " " + lastName;
            }
        }
    }
    
    /**
     * 邮箱生成器
     */
    private class EmailGenerator extends AbstractDataGenerator {
        private final String[] DOMAINS = {"gmail.com", "yahoo.com", "hotmail.com", "outlook.com", "163.com", "126.com", "qq.com", "example.com"};
        
        @Override
        public boolean supports(String fieldType) {
            return true;
        }
        
        @Override
        protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
            // 生成用户名部分
            String username = generateUsername();
            // 随机选择一个域名
            String domain = DOMAINS[random.nextInt(DOMAINS.length)];
            
            return username + "@" + domain;
        }
        
        private String generateUsername() {
            // 生成5-10个字符的随机用户名
            int length = 5 + random.nextInt(6);
            StringBuilder sb = new StringBuilder(length);
            String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
            
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 电话号码生成器
     */
    private class PhoneGenerator extends AbstractDataGenerator {
        private final String[] MOBILE_PREFIXES = {"130", "131", "132", "133", "134", "135", "136", "137", "138", "139", "150", "151", "152", "153", "155", "156", "157", "158", "159", "170", "176", "177", "178", "180", "181", "182", "183", "184", "185", "186", "187", "188", "189"};
        
        @Override
        public boolean supports(String fieldType) {
            return true;
        }
        
        @Override
        protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
            // 生成中国手机号
            String prefix = MOBILE_PREFIXES[random.nextInt(MOBILE_PREFIXES.length)];
            StringBuilder sb = new StringBuilder(prefix);
            
            // 生成后8位
            for (int i = 0; i < 8; i++) {
                sb.append(random.nextInt(10));
            }
            
            return sb.toString();
        }
    }
    
    /**
     * 地址生成器
     */
    private class AddressGenerator extends AbstractDataGenerator {
        private final String[] PROVINCES = {"北京市", "上海市", "天津市", "重庆市", "河北省", "山西省", "辽宁省", "吉林省", "黑龙江省", "江苏省", "浙江省", "安徽省", "福建省", "江西省", "山东省", "河南省", "湖北省", "湖南省", "广东省", "海南省", "四川省", "贵州省", "云南省", "陕西省", "甘肃省", "青海省"};
        private final String[] CITIES = {"北京", "上海", "广州", "深圳", "杭州", "南京", "武汉", "成都", "重庆", "西安", "苏州", "天津", "长沙", "郑州", "东莞", "青岛", "沈阳", "宁波", "昆明"};
        private final String[] DISTRICTS = {"朝阳区", "海淀区", "东城区", "西城区", "丰台区", "石景山区", "通州区", "顺义区", "房山区", "大兴区", "昌平区", "怀柔区", "平谷区", "门头沟区", "密云区", "延庆区"};
        private final String[] STREETS = {"中关村大街", "长安街", "建国路", "复兴路", "三里屯", "望京", "国贸", "西单", "王府井", "东单", "崇文门", "宣武门", "和平里", "安定门", "东直门", "西直门", "北新桥", "南锣鼓巷"};
        private final String[] BUILDING_TYPES = {"小区", "大厦", "公寓", "花园", "广场", "大楼", "中心", "家园"};
        
        @Override
        public boolean supports(String fieldType) {
            return true;
        }
        
        @Override
        protected Object doGenerate(String fieldName, FieldConfig fieldConfig) {
            StringBuilder sb = new StringBuilder();
            
            // 随机决定是否包含省份
            if (random.nextBoolean()) {
                sb.append(PROVINCES[random.nextInt(PROVINCES.length)]);
            }
            
            // 添加城市
            sb.append(CITIES[random.nextInt(CITIES.length)]);
            
            // 添加区
            sb.append(DISTRICTS[random.nextInt(DISTRICTS.length)]);
            
            // 添加街道
            sb.append(STREETS[random.nextInt(STREETS.length)]);
            
            // 添加门牌号
            sb.append(random.nextInt(500) + 1).append("号");
            
            // 随机决定是否添加建筑类型和单元号
            if (random.nextBoolean()) {
                sb.append(generateBuildingName());
                
                // 随机决定是否添加单元和房间号
                if (random.nextBoolean()) {
                    sb.append(random.nextInt(10) + 1).append("单元");
                    sb.append(random.nextInt(30) + 1).append("0").append(random.nextInt(9) + 1).append("室");
                }
            }
            
            return sb.toString();
        }
        
        private String generateBuildingName() {
            // 生成一个随机的建筑名称
            StringBuilder sb = new StringBuilder();
            
            // 随机决定是否添加形容词
            if (random.nextBoolean()) {
                String[] adjectives = {"金色", "银色", "阳光", "蓝色", "绿色", "紫荆", "红树", "碧水", "翠竹", "和谐", "幸福", "美丽"};
                sb.append(adjectives[random.nextInt(adjectives.length)]);
            }
            
            // 添加建筑类型
            sb.append(BUILDING_TYPES[random.nextInt(BUILDING_TYPES.length)]);
            
            return sb.toString();
        }
    }
} 
package com.oceanbase.datamocker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanbase.datamocker.config.MockerConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 数据模拟器测试类
 */
public class DataMockerTest {
    
    private DataMocker dataMocker;
    
    @BeforeEach
    public void setUp() {
        // 使用默认配置创建数据模拟器
        dataMocker = DataMocker.createDefault();
    }
    
    @AfterEach
    public void tearDown() {
        if (dataMocker != null) {
            dataMocker.close();
        }
    }
    
    @Test
    public void testGenerateDataWithDefaultConfig() {
        // 定义表名、字段名和字段类型
        String tableName = "users";
        List<String> fieldNames = Arrays.asList("id", "name", "email", "age");
        List<String> fieldTypes = Arrays.asList("INT", "STRING", "STRING", "INT");
        int rowCount = 5;
        
        // 生成数据
        List<Map<String, Object>> data = dataMocker.generateData(tableName, fieldNames, fieldTypes, rowCount);
        
        // 验证生成的数据
        assertNotNull(data);
        assertEquals(rowCount, data.size());
        
        // 验证每行数据的字段
        for (Map<String, Object> row : data) {
            assertEquals(fieldNames.size(), row.size());
            
            for (String fieldName : fieldNames) {
                assertTrue(row.containsKey(fieldName));
            }
            
            // 验证id字段是整数类型
            assertTrue(row.get("id") instanceof Integer);
            
            // 验证name字段是字符串类型
            assertTrue(row.get("name") instanceof String);
            
            // 验证email字段是字符串类型
            assertTrue(row.get("email") instanceof String);
            
            // 验证age字段是整数类型
            assertTrue(row.get("age") instanceof Integer);
        }
    }
    
    @Test
    public void testGenerateDataWithConfigFile() throws Exception {
        // 获取示例配置文件
        File configFile = new File(getClass().getClassLoader().getResource("example-config.yaml").getFile());
        assertTrue(configFile.exists());
        
        // 使用配置文件创建数据模拟器
        dataMocker = DataMocker.fromConfigFile(configFile);
        
        // 定义表名、字段名和字段类型
        String tableName = "users";
        List<String> fieldNames = Arrays.asList("id", "username", "email", "age", "gender", "created_time", "balance", "status", "address");
        List<String> fieldTypes = Arrays.asList("INT", "STRING", "STRING", "INT", "STRING", "DATETIME", "DECIMAL", "INT", "STRING");
        int rowCount = 5;
        
        // 生成数据
        List<Map<String, Object>> data = dataMocker.generateData(tableName, fieldNames, fieldTypes, rowCount);
        
        // 验证生成的数据
        assertNotNull(data);
        assertEquals(rowCount, data.size());
        
        // 打印生成的数据（用于调试）
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
        
        // 验证每行数据的字段
        for (Map<String, Object> row : data) {
            // 验证created_by和updated_by字段被排除
            assertFalse(row.containsKey("created_by"));
            assertFalse(row.containsKey("updated_by"));
            
            // 验证id字段在1到1000000之间
            int id = (Integer) row.get("id");
            assertTrue(id >= 1 && id <= 1000000);
            
            // 验证username字段符合配置
            String username = (String) row.get("username");
            assertTrue(username.matches("^[a-zA-Z0-9_]+$"));
            assertTrue(username.length() >= 5 && username.length() <= 20);
            
            // 验证email字段符合配置
            String email = (String) row.get("email");
            assertTrue(email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"));
            
            // 验证gender字段符合配置
            Object gender = row.get("gender");
            if (gender != null) {
                assertTrue(Arrays.asList("男", "女", "未知").contains(gender));
            }
            
            // 验证status字段符合配置
            int status = (Integer) row.get("status");
            assertTrue(Arrays.asList(0, 1, 2).contains(status));
        }
    }
} 
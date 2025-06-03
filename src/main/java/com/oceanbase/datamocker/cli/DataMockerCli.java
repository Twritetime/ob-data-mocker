package com.oceanbase.datamocker.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oceanbase.datamocker.DataMocker;
import com.oceanbase.datamocker.config.MockerConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 数据模拟器命令行工具
 */
@Slf4j
public class DataMockerCli {
    
    /**
     * 主方法
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            printUsage();
            System.exit(1);
        }
        
        try {
            String tableName = args[0];
            List<String> fieldNames = Arrays.asList(args[1].split(","));
            List<String> fieldTypes = Arrays.asList(args[2].split(","));
            
            int rowCount = 10; // 默认生成10行
            if (args.length > 3) {
                try {
                    rowCount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    log.error("Invalid row count: {}", args[3]);
                    printUsage();
                    System.exit(1);
                }
            }
            
            File configFile = null;
            if (args.length > 4) {
                configFile = new File(args[4]);
                if (!configFile.exists()) {
                    log.error("Config file not found: {}", configFile.getAbsolutePath());
                    System.exit(1);
                }
            }
            
            // 创建数据模拟器
            DataMocker dataMocker;
            if (configFile != null) {
                dataMocker = DataMocker.fromConfigFile(configFile);
            } else {
                dataMocker = DataMocker.createDefault();
            }
            
            try {
                // 生成数据
                List<Map<String, Object>> data = dataMocker.generateData(tableName, fieldNames, fieldTypes, rowCount);
                
                // 输出JSON格式的结果
                ObjectMapper mapper = new ObjectMapper();
                System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));
            } finally {
                // 关闭资源
                dataMocker.close();
            }
        } catch (Exception e) {
            log.error("Error generating mock data: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("Usage: java -jar ob-data-mocker.jar <table_name> <field_names> <field_types> [row_count] [config_file]");
        System.out.println("  table_name: Name of the table");
        System.out.println("  field_names: Comma-separated list of field names");
        System.out.println("  field_types: Comma-separated list of field types");
        System.out.println("  row_count: Number of rows to generate (default: 10)");
        System.out.println("  config_file: Path to YAML or JSON configuration file (optional)");
        System.out.println();
        System.out.println("Example:");
        System.out.println("  java -jar ob-data-mocker.jar users \"id,name,email,age\" \"INT,STRING,STRING,INT\" 20 config.yaml");
    }
} 
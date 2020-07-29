package utils;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lcy
 * @DESC:
 */
public class ExcelUtils {
    public static void main(String[] args) throws IOException {
        Long begin = System.currentTimeMillis();
        String[] arg ={"学生ID","学生姓名","资源PID","学生编号","学历","其它","所属学校","学管","销售","校区","项目所属","学历ID","学历&年级合并","年级ID"};
        ArrayList<Map<String, Object>> maps = excelReader("D://bj.xlsx", arg);
        System.out.println(String.format("数据条数:%s",maps.size()));

        Long end = System.currentTimeMillis();
        System.out.println(String.format("耗时时间：%s 秒",(end-begin)/1000));
        System.out.println(String.format("数据条数:%s",maps.size()));
    }

    public static ArrayList<Map<String,Object>> excelReader(String excelPath,String ... args) throws IOException {
        // 创建excel工作簿对象
        Workbook workbook = null;
        FormulaEvaluator formulaEvaluator = null;

        // 读取目标文件
        File excelFile = new File(excelPath);
        InputStream is = new FileInputStream(excelFile);

        // 判断文件是xlsx还是xls
        if (excelFile.getName().endsWith("xlsx")) {
            ZipSecureFile.setMinInflateRatio(-1.0d);
            workbook = new XSSFWorkbook(is);
            formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }else {
            workbook = new HSSFWorkbook(is);
            formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        }

        //判断excel文件打开是否正确
        if(workbook == null){
            System.err.println("未读取到内容,请检查路径！");
            return null;
        }
        //创建二维数组,储存excel行列数据
        ArrayList<Map<String,Object>> als = new ArrayList<Map<String,Object>>();
        //遍历工作簿中的sheet

        Sheet sheet = workbook.getSheetAt(0);
        //当前sheet页面为空,继续遍历
        if (sheet != null) {
            // 对于每个sheet，读取其中的每一行
            for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }
                Map<String,Object> dataMap = new HashMap<>();
                if(args != null && args.length> 0){
                    //根据参数当做表头处理数据
                    for(int arg = 0; arg< args.length; arg ++){
                        dataMap.put(args[arg],getValue(row.getCell(arg),formulaEvaluator));
                    }
                }else{
                    //根据Excel表头处理数据
                }
                als.add(dataMap);
            }
        }


        is.close();
        return als;
    }


    public static FormulaEvaluator  getFormulaEvaluator(Workbook workbook){
        FormulaEvaluator formulaEvaluator = null;
        if(workbook instanceof XSSFWorkbook){
            formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        }else{
            formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        }
        return formulaEvaluator;
    }

    public static Workbook getWorkbook(String filePath) {
        Workbook wb = null;
                 try {
                         if (null != filePath) {
                                 FileInputStream fis = new FileInputStream(filePath);
                                 if (filePath.endsWith(".xls")) {
                                         wb = new HSSFWorkbook(fis);
                                     } else if (filePath.endsWith(".xlsx")) {
                                         ZipSecureFile.setMinInflateRatio(-1.0d);
                                         wb = new XSSFWorkbook(fis);
                                     }
                                 return wb;
                             }
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 return null;
             }

    /**
     * 获取
     * @param cell
     * @param formulaEvaluator
     * @return
     */
    public static String getValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getRichStringCellValue().getString();
            case NUMERIC:
                // 判断是日期时间类型还是数值类型
                if (DateUtil.isCellDateFormatted(cell)) {
                    short format = cell.getCellStyle().getDataFormat();
                    SimpleDateFormat sdf = null;
                         /* 所有日期格式都可以通过getDataFormat()值来判断
162                      *     yyyy-MM-dd----- 14
163                      *    yyyy年m月d日----- 31
164                      *    yyyy年m月--------57
165                      *    m月d日  --------- 58
166                      *    HH:mm---------- 20
167                      *    h时mm分  --------- 32
168                      */
                    if (format == 14 || format == 31 || format == 57 || format == 58) {
                        //日期
                        sdf = new SimpleDateFormat("yyyy-MM-dd");
                    } else if (format == 20 || format == 32) {
                        //时间
                        sdf = new SimpleDateFormat("HH:mm");
                    }
                    return sdf.format(cell.getDateCellValue());
                } else {
                    // 对整数进行判断处理
                    double cur = cell.getNumericCellValue();
                    long longVal = Math.round(cur);
                    Object inputValue = null;
                    if (Double.parseDouble(longVal + ".0") == cur) {
                        inputValue = longVal;
                    } else {
                        inputValue = cur;
                    }
                    return String.valueOf(inputValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                //对公式进行处理,返回公式计算后的值,使用cell.getCellFormula()只会返回公式
                return String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
            //Cell.CELL_TYPE_BLANK || Cell.CELL_TYPE_ERROR
            default:
                return null;
        }
    }

}

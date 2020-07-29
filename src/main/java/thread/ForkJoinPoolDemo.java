package thread;

import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.*;
import utils.ExcelUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;

import static org.apache.poi.ss.usermodel.CellType.*;

/**
 * @author lcy
 * @DESC:
 */
public class ForkJoinPoolDemo {

    static ForkJoinPool forkJoinPool =   new ForkJoinPool
            (Runtime.getRuntime().availableProcessors(), // 并行度级别
    ForkJoinPool.defaultForkJoinWorkerThreadFactory, //创建新线程工厂
            null, true);  //失败的处理方式


    private static Map<String,Object> dateMap = new HashMap<>();

    private static LinkedList dateList = new LinkedList();
    private static int MAX_ROW = 10000;

    private static String[] args ={"学生ID","学生姓名","资源PID","学生编号","学历","其它","所属学校","学管","销售","校区","项目所属","学历ID","学历&年级合并","年级ID"};

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long begin = System.currentTimeMillis();
        Workbook workbook = ExcelUtils.getWorkbook("D://bj.xlsx");
        FormulaEvaluator formulaEvaluator = ExcelUtils.getFormulaEvaluator(workbook);
        Sheet sheetAt = workbook.getSheetAt(0);
        int lastRowNum = sheetAt.getLastRowNum();
        ForkJoinTask<LinkedList> submit = forkJoinPool.submit(new ExcelOperate(sheetAt, formulaEvaluator, 0, lastRowNum));
        LinkedList linkedList = submit.get();
        long end = System.currentTimeMillis();
        System.out.println(String.format("耗时: %s 秒",(end - begin)/1000));
    }



    static class ExcelOperate extends RecursiveTask<LinkedList> {
        private LinkedList resultList = new LinkedList<>();
        private int begin;
        private int end;
        private Sheet sheet;
        private FormulaEvaluator formulaEvaluator;

        public ExcelOperate(Sheet sheet ,FormulaEvaluator formulaEvaluator, int begin, int end) {
            this.begin = begin;
            this.end = end;
            this.sheet = sheet;
            this.formulaEvaluator = formulaEvaluator;
        }

        @Override
        protected LinkedList<T> compute() {
            if((end - begin)+1 <= MAX_ROW){
                //读取到内存中
                for (int start = begin + 1; start <= end; start ++) {
                    //简单的读取数据
                    Row row = sheet.getRow(start);
                    Map<String,Object> dataMap  = new HashMap<>();
                    if(args != null && args.length> 0){
                        //根据参数当做表头处理数据
                        for(int arg = 0; arg< args.length; arg ++){
                            dataMap.put(args[arg],ExcelUtils.getValue(row.getCell(arg),formulaEvaluator));
                        }
                    }else{
                        //根据Excel表头处理数据
                    }
                    resultList.add(dataMap);
                }
            }else{//任务拆分
                //System.out.println(Thread.currentThread()+" 子任务");
                int newEnd = (end+begin)/2;
                ExcelOperate min = new ExcelOperate(sheet,formulaEvaluator, begin, newEnd);
                //任务拆分
                min.fork();
                ExcelOperate max = new ExcelOperate(sheet,formulaEvaluator, newEnd + 1, end);
                max.fork();

                //获取处理结果
                LinkedList minList = min.join();
                LinkedList maxList = max.join();
                resultList.addAll(minList);
                resultList.addAll(maxList);
                System.out.println(resultList.size());
            }

            return resultList;
        }

        public void excelProcess(Row row){
            if(row != null){
                for(int columnNum = 0 ; columnNum < row.getLastCellNum(); columnNum++){
                    Cell cell = row.getCell(columnNum);
                    //ExcelUtils.getValue();
                    //resultList.add(ExcelUtils);
                   }
            }
        }
    }
}

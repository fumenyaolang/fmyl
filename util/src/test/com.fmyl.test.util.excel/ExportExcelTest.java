import com.fmyl.util.excel.ExportExcel;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fumenyaolang on 2018/7/4.
 */
public class ExportExcelTest {


    @Test
    public void test() {
        ExportExcel excel = new ExportExcel();
        List<List<String>> dataList = new ArrayList();
        List<String> data = new ArrayList();
        data.add(ExportExcel.EVENT_TITLE);
        data.add("学生信息表");
        dataList.add(data);
        data = new ArrayList();
        data.add(ExportExcel.EVENT_TOP);
        data.add("姓名");
        data.add("年龄");
        dataList.add(data);
        data = new ArrayList();
        data.add(ExportExcel.EVENT_DATA);
        data.add("王刚");
        data.add("23");
        dataList.add(data);
        excel.addSheet("sheetName", dataList);
        excel.exportFile("/Users/fumenyaolang/Temp", "export-test-fmyl.xls");
    }

}

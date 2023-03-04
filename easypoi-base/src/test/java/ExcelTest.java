import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import cn.afterturn.easypoi.excel.export.ExcelExportService;
import cn.afterturn.easypoi.util.PoiReflectorUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExcelTest {

    public static void main(String[] args) throws IOException, NoSuchFieldException, IllegalAccessException {
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(3);
        Field[] declaredFields = CustomerImportReq.class.getDeclaredFields();
        List<String> collect = Stream.of(declaredFields).filter(item -> Objects.nonNull(item.getAnnotation(Excel.class))).map(item -> item.getAnnotation(Excel.class).name()).collect(Collectors.toList());
        params.setImportFields(collect.toArray(new String[collect.size()]));
        List<CustomerImportReq> list = ExcelImportUtil.importExcel(
                new File("E:\\需求\\客户-导入模板朱雪20221128.xlsx"),
                CustomerImportReq.class, params);
        CommonService commonService = new CommonService();
        Iterator<CustomerImportReq> iterator = list.iterator();
        int count = 0;
        File file = new File("E:\\需求\\20221207-客户BOM改动\\汇芯微正式导入_20221216_生产测试.xlsx");
        FileInputStream inputStream = new FileInputStream(file);
        byte[] bytes = new byte[1024];
        inputStream.read(bytes);
        while (iterator.hasNext()) {
            CustomerImportReq next = iterator.next();
            if (commonService.trimImportEmptyRow(next, list)) {
                iterator.remove();
            }
            count ++;
            commonService.importCellValueStrTrim(next);
            next.setAttachments(Lists.newArrayList(bytes, bytes));
//            next.setAttachments("https://prod-oss-bk.oss-cn-shenzhen.aliyuncs.com/CRM/C8348226229644861441/202212/%E5%89%AF%E6%9C%AC%E5%AE%A2%E6%88%B7%E5%AF%BC%E5%85%A5%E6%A8%A1%E6%9D%BF1672196887385.xls?Expires=1672198687&OSSAccessKeyId=LTAI5tSgmd4bEiHhrvFM3yqe&Signature=FoW45wGy688TqlYSQZ%2FfFqUOR%2BM%3D");
        }
        System.out.println(list);
        System.out.println(count);
        testExport(list);
    }

    public static Workbook getWorkbook(ExportParams params, int size) {
        if (ExcelType.HSSF.equals(params.getType())) {
            return new HSSFWorkbook();
        } else if (size < ExcelExportUtil.USE_SXSSF_LIMIT) {
            return new XSSFWorkbook();
        } else {
            return new SXSSFWorkbook();
        }
    }

    /**
     * @param entity    表格标题属性
     * @param entityList 导出字段
     * @param dataSet   Excel对象数据List
     */
    public static Workbook exportExcel(Workbook workbook, ExportParams entity, List<ExcelExportEntity> entityList,
                                       Collection<?> dataSet) {
        new ExcelExportService().createSheetForMap(workbook, entity, entityList, dataSet);
        return workbook;
    }

    private static void testExport(List<CustomerImportReq> list) throws IOException, NoSuchFieldException {
        if (CollectionUtils.isNotEmpty(list)) {
            AtomicInteger i = new AtomicInteger();
            list.forEach(item -> {

                item.setFailReason("测试失败原因" + i.getAndIncrement());
            });
        }
        //主sheet页
        ExportParams mainParams = new ExportParams("客户信息导出", "客户信息");
        mainParams.setType(ExcelType.XSSF);
        Workbook workbook = getWorkbook(mainParams, list.size());
        workbook = exportExcel(workbook, mainParams, getExportFields(CustomerImportReq.class), list);

//        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("客户导入结果", "导入数据"), CustomerImportFailData.class, list);
//        ByteArrayOutputStream outputStream = FilePoiUtils.exportExcel(failList, "客户导入结果", "导入数据", CustomerImportReq.class);
        File file = new File("E:\\需求\\cust-export.xls");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream os = new FileOutputStream(file);
        workbook.write(os);
    }

    private static List<ExcelExportEntity> getExportFields(Class<CustomerImportReq> clazz) throws NoSuchFieldException {
        List<ExcelExportEntity> entities = new ArrayList<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Objects.isNull(field) || (!field.isAnnotationPresent(Excel.class))) {
                continue;
            }
            Excel annotation = field.getAnnotation(Excel.class);
            ExcelExportEntity entity = new ExcelExportEntity();
            if (annotation.type() == 2) {
                entity.setType(2);
                entity.setExportImageType(2);
                entity.setHeight(30);
            } else {
                entity.setType(1);
                entity.setHeight(25);
            }
            entity.setName(annotation.name());
            entity.setWidth(annotation.width());
            entity.setOrderNum(Integer.parseInt(annotation.orderNum()));
            entity.setMethod(PoiReflectorUtil.fromCache(clazz).getGetMethod(field.getName()));
            entities.add(entity);
        }
        //附件字段
        /*ExcelExportEntity entity = new ExcelExportEntity();
            entity.setType(2);
            entity.setExportImageType(2);
            entity.setHeight(30);
        entity.setName("附件");
        entity.setWidth(60);
        entity.setOrderNum(entities.get(entities.size() - 1).getOrderNum() + 1);
        entity.setMethod(PoiReflectorUtil.fromCache(clazz).getGetMethod("attachments"));
        entities.add(entity);*/
        return entities;
    }
}

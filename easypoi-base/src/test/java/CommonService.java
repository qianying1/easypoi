import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @Description
 * @Title CommonService
 * @Author qianhb
 * @Date 2022/09/09 18:11
 * @Version 1.0.0
 **/
@Slf4j
public class CommonService {

    public boolean trimImportEmptyRow(Object item, List<?> importData) throws IllegalAccessException {
        if (Objects.isNull(item) || CollectionUtils.isEmpty(importData) || !importData.contains(item)) {
            return true;
        }
        Class<?> targetClazz = item.getClass();
        boolean isAllCellEmpty = true;
        while (targetClazz != null) {
            Field[] declaredFields = item.getClass().getDeclaredFields();
            boolean allFieldDataEmpty = isAllFieldDataEmpty(declaredFields, item);
            if (!allFieldDataEmpty) {
                isAllCellEmpty = false;
                break;
            }
            targetClazz = targetClazz.getSuperclass();
        }
        return isAllCellEmpty;
    }

    private boolean isAllFieldDataEmpty(Field[] declaredFields, Object item) throws IllegalAccessException {
        int emptyCount = 0;
        boolean hasSerialNum = false;
        for (Field declaredField : declaredFields) {
            if ("serialVersionUID".equals(declaredField.getName())) {
                hasSerialNum = true;
                continue;
            }
            declaredField.setAccessible(true);
            Object field = declaredField.get(item);
            if (Objects.nonNull(field) || (field instanceof String && StringUtils.isNoneBlank(String.valueOf(field)))) {
                return false;
            }
            emptyCount++;
        }
        return emptyCount >= (declaredFields.length - (hasSerialNum ? 1 : 0));
    }

    public void distinctStrs(List<String> names) {
        if (CollectionUtils.isEmpty(names)) {
            return;
        }
        Map<String, Object> valueMap = new HashMap<>(names.size());
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            if (Objects.isNull(next)) {
                iterator.remove();
                continue;
            }
            if (valueMap.containsKey(next.trim())) {
                iterator.remove();
                continue;
            }
            valueMap.put(next, next);
        }
    }

    public String getQccField(Object field) {
        if (Objects.isNull(field)) {
            return "";
        }
        return String.valueOf(field);
    }

    public List<String> convertLongsToStrs(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        List<String> strs = new ArrayList<>(ids.size());
        ids.forEach(id -> {
            strs.add(String.valueOf(id));
        });
        return strs;
    }

    public enum SyncOwnerOperation {
        ADD,
        REMOVE,
        CHANGE_LEADER
    }

    public void importCellValueStrTrim(Object cellItem) {
        if (Objects.isNull(cellItem)) {
            return;
        }
        Class<?> aClass = cellItem.getClass();
        List<Field> fields = new ArrayList<>();
        Class<?> superclass = aClass.getSuperclass();
        while (superclass != null) {
            Field[] declaredFields = superclass.getDeclaredFields();
            if (declaredFields != null && declaredFields.length > 0) {
                for (Field declaredField : declaredFields) {
                    fields.add(declaredField);
                }
            }
            superclass = superclass.getSuperclass();
        }
        for (Field declaredField : aClass.getDeclaredFields()) {
            fields.add(declaredField);
        }
        fields.forEach(field -> {
            try {
                field.setAccessible(true);
                Object o = field.get(cellItem);
                if (Objects.nonNull(o) && o instanceof String) {//空格\t、回车\n、换行符\r、制表符\t
                    String target = (String) o;
                    if (StringUtils.isBlank(target)) {
                        field.set(cellItem, "");
                        return;
                    }
                    char[] chars = target.toCharArray();
                    Pattern p = Pattern.compile("\\s|\\t|\\r|\\n");
                    StringBuffer buffer = new StringBuffer("");
                    //前置匹配
                    boolean skip = false;
                    for (char aChar : chars) {
                        String s = String.valueOf(aChar);
                        if (p.matcher(s).matches() && !skip) {
                            continue;
                        }
                        if (!p.matcher(s).matches()) {
                            skip = true;
                        }
                        buffer.append(s);
                    }
                    //后置匹配
                    chars = buffer.toString().toCharArray();
                    int end = chars.length;
                    for (int i = chars.length-1; i >= 0; i--) {
                        String s = String.valueOf(chars[i]);
                        if (!p.matcher(s).matches() && !" ".equals(s)) {
                            end = i + 1;
                            break;
                        }
                    }
                    field.set(cellItem, buffer.substring(0, end));
                }
            } catch (IllegalAccessException e) {
                log.info("CommonService.importCellValueStrTrim access invalid!", e);
            }
        });
    }
}

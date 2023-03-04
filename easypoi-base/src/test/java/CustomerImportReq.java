import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Title CustomerSaveReq
 * @Author qianhb
 * @Date 2022/07/14 14:04
 * @Version 1.0.0
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class CustomerImportReq implements Serializable {
    private static final long serialVersionUID = 6297979571197441578L;


    @Excel(name = "客户名称*", orderNum = "1", width = 25)
    private String custName;

    @Excel(name = "企业类型*", orderNum = "2", width = 25)
    private String custCompanyType;

    @Excel(name = "事业部", orderNum = "3", width = 25)
    private String institution;

    @Excel(name = "客户简称", orderNum = "4", width = 25)
    private String custAbbrName;

    @Excel(name = "英文全称", orderNum = "5", width = 25)
    private String enName;

    @Excel(name = "英文简称", orderNum = "6", width = 25)
    private String enAbbrName;

    @Excel(name = "客户性质*", orderNum = "7", width = 25)
    private String custType;

    @Excel(name = "客户级别", orderNum = "8", width = 25)
    private String level;

    @Excel(name = "成交状态", orderNum = "9", width = 25)
    private String tradeStatus;

    @Excel(name = "客户来源*", orderNum = "10", width = 25)
    private String sourceCode;

    @Excel(name = "行业", orderNum = "11", width = 25)
    private String hyCode;

    @Excel(name = "应用", orderNum = "12", width = 25)
    private String apyTos;

    @Excel(name = "经营范围", orderNum = "13", width = 25)
    private String businessScope;

    @Excel(name = "结算币种", orderNum = "14", width = 25)
    private String currency;

    @Excel(name = "税率", orderNum = "15", width = 25)
    private String rate;

    @Excel(name = "账期", orderNum = "16", width = 25)
    private String accountPeriod;

    @Excel(name = "付款方式", orderNum = "17", width = 25)
    private String payType;

    @Excel(name = "对账日期_开始时间", orderNum = "18", width = 25)
    private String veryAccDate;

    @Excel(name = "结束时间", orderNum = "19", width = 25)
    private String veryAccDateEd;

    @Excel(name = "纳税人识别号", orderNum = "20", width = 25)
    private String taxPayerNum;

    @Excel(name = "开户银行", orderNum = "21", width = 25)
    private String accountBank;

    @Excel(name = "收款账号", orderNum = "22", width = 25)
    private String account;

    @Excel(name = "负责人*", orderNum = "23", width = 25)
    private String owner;

    @Excel(name = "团队成员", orderNum = "24", width = 50)
    private String teammates;

    @Excel(name = "备注", orderNum = "25", width = 50)
    private String remark;

    @Excel(name = "公司电话", orderNum = "26", width = 25)
    private String telephone;

    @Excel(name = "邮件", orderNum = "27", width = 30)
    private String email;

    @Excel(name = "传真", orderNum = "28", width = 30)
    private String fax;

    @Excel(name = "网址", orderNum = "29", width = 30)
    private String website;

    @Excel(name = "客户地址_地址类别", orderNum = "30", width = 25)
    private String addrType;

    @Excel(name = "国家", orderNum = "31", width = 25)
    private String country;

    @Excel(name = "省", orderNum = "32", width = 25)
    private String province;

    @Excel(name = "市", orderNum = "33", width = 25)
    private String city;

    @Excel(name = "区", orderNum = "34", width = 25)
    private String area;

    @Excel(name = "详细地址", orderNum = "35", width = 30)
    private String detailAddr;

    @ExcelCollection(name = "附件", type = byte[].class)
    @Excel(name = "附件", orderNum = "36", width = 30)
    private List<byte[]> attachments;
//    @Excel(name = "失败原因", orderNum = "22", width = 30)
    private String failReason;
}

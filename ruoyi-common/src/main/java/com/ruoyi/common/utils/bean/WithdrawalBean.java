package com.ruoyi.common.utils.bean;

import com.ruoyi.common.annotation.Excel;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;

public class WithdrawalBean implements Serializable {

    @Min(value = 1,message ="提款金额不能为空")
    @Excel(name = "提款金额", cellType = Excel.ColumnType.NUMERIC, prompt = "提款金额")
    private Double amount;

    @NotEmpty(message ="银行卡号不能为空")
    @Excel(name = "银行卡号", cellType = Excel.ColumnType.NUMERIC, prompt = "银行卡号")
    private String bankNo;

    @NotEmpty(message ="开户人不能为空")
    @Excel(name = "开户人", cellType = Excel.ColumnType.STRING, prompt = "开户人")
    private String accname;

    @NotEmpty(message ="开户行不能为空")
    @Excel(name = "开户行(例如：中国银行)", cellType = Excel.ColumnType.STRING, prompt = "开户行(例如：中国银行)")
    private String bankName;

    public WithdrawalBean(){
        super();
    }

    public WithdrawalBean(Double amount, String bankNo, String accname, String bankName){
        super();
        this.amount = amount;
        this.bankNo = bankNo;
        this.accname = accname;
        this.bankName = bankName;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getBankNo() {
        return bankNo;
    }

    public void setBankNo(String bankNo) {
        this.bankNo = bankNo;
    }

    public String getAccname() {
        return accname;
    }

    public void setAccname(String accname) {
        this.accname = accname;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WithdrawalBean) {
            WithdrawalBean bean = (WithdrawalBean) obj;
            return new BigDecimal(this.amount).compareTo(new BigDecimal(bean.amount)) == 0
                    && this.bankNo.equals(bean.bankNo);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return "WithdrawalBean [amount=" + amount + ", bankNo="
                + bankNo + ", accname=" + accname + ", bankName="
                + bankName + "]";
    }
}

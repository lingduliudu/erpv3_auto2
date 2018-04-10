package com.apt.webapp.model.crm;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import java.io.Serializable;
/**
 * <p>实体类</p>
 * <p>Table: crm_paycontrol_statistics - 信贷还款明细统计表</p>
 *
 * @since 2017-09-08 11:19:20
 */
@Entity
@Table(name = "crm_paycontrol_statistics")
/**@Alias("CrmPaycontrolStatistics")*/
public class CrmPaycontrolStatistics implements Serializable{
	@Id
	@GeneratedValue(generator = "uuidGenerator")
    @GenericGenerator(name = "uuidGenerator", strategy = "uuid")
	@Column(name="id", unique = true, nullable = false, length=40)
    /** id -  */
    private String id;

    /** crm_order_id - 订单id */
	@Column(name="crm_order_id", length=40)
    private String crmOrderId;

    /** repayment_time - 本期应还日期 */
	@Column(name="repayment_time", length=40)
    private String repaymentTime;

    /** repayment_duetime - 本期应还期数 */
	@Column(name="repayment_duetime", length=10)
    private Integer repaymentDuetime;

    /** paycontrol_id - 还款明细id */
	@Column(name="paycontrol_id", length=40)
    private String paycontrolId;

    /** overdue_day - 当前逾期天数 */
	@Column(name="overdue_day", length=10)
    private Integer overdueDay;

    /** overdue_number - 当前逾期次数 */
	@Column(name="overdue_number", length=10)
    private Integer overdueNumber;

    /** closing_period - 已结清期数 */
	@Column(name="closing_period", length=10)
    private Integer closingPeriod;

    /** remain_capital_total - 剩余本金总和 */
	@Column(name="remain_capital_total", length=14)
    private Double remainCapitalTotal;

    /** remain_overdue_accrual_total - 剩余逾期利息总和 */
	@Column(name="remain_overdue_accrual_total", length=14)
    private Double remainOverdueAccrualTotal;

    /** remain_accrual_total - 剩余利息总和 */
	@Column(name="remain_accrual_total", length=14)
    private Double remainAccrualTotal;

    /** remain_overdue_violate_money_total - 剩余逾期违约金总和 */
	@Column(name="remain_overdue_violate_money_total", length=14)
    private Double remainOverdueViolateMoneyTotal;

    /** remain_interest_total - 剩余罚息总和 */
	@Column(name="remain_interest_total", length=14)
    private Double remainInterestTotal;

    /** remain_manage_fee_total - 剩余管理费总和 */
	@Column(name="remain_manage_fee_total", length=14)
    private Double remainManageFeeTotal;

    /** status - 订单状态:0 正常、2逾期 */
	@Column(name="status", length=10)
    private Integer status;

    /** update_time - 最后更新时间 */
	@Column(name="update_time", length=40)
    private String updateTime;

	/**
	 * 功能说明：字段id()的get方法			
	 */
    public String getId(){
        return this.id;
    }
    /**
	 * 功能说明：字段id()的set方法			
	 */
    public void setId(String id){
        this.id = id;
    }
	/**
	 * 功能说明：字段crm_order_id(订单id)的get方法			
	 */
    public String getCrmOrderId(){
        return this.crmOrderId;
    }
    /**
	 * 功能说明：字段crm_order_id(订单id)的set方法			
	 */
    public void setCrmOrderId(String crmOrderId){
        this.crmOrderId = crmOrderId;
    }
	/**
	 * 功能说明：字段repayment_time(本期应还日期)的get方法			
	 */
    public String getRepaymentTime(){
        return this.repaymentTime;
    }
    /**
	 * 功能说明：字段repayment_time(本期应还日期)的set方法			
	 */
    public void setRepaymentTime(String repaymentTime){
        this.repaymentTime = repaymentTime;
    }
	/**
	 * 功能说明：字段repayment_duetime(本期应还期数)的get方法			
	 */
    public Integer getRepaymentDuetime(){
        return this.repaymentDuetime;
    }
    /**
	 * 功能说明：字段repayment_duetime(本期应还期数)的set方法			
	 */
    public void setRepaymentDuetime(Integer repaymentDuetime){
        this.repaymentDuetime = repaymentDuetime;
    }
	/**
	 * 功能说明：字段paycontrol_id(还款明细id)的get方法			
	 */
    public String getPaycontrolId(){
        return this.paycontrolId;
    }
    /**
	 * 功能说明：字段paycontrol_id(还款明细id)的set方法			
	 */
    public void setPaycontrolId(String paycontrolId){
        this.paycontrolId = paycontrolId;
    }
	/**
	 * 功能说明：字段overdue_day(当前逾期天数)的get方法			
	 */
    public Integer getOverdueDay(){
        return this.overdueDay;
    }
    /**
	 * 功能说明：字段overdue_day(当前逾期天数)的set方法			
	 */
    public void setOverdueDay(Integer overdueDay){
        this.overdueDay = overdueDay;
    }
	/**
	 * 功能说明：字段overdue_number(当前逾期次数)的get方法			
	 */
    public Integer getOverdueNumber(){
        return this.overdueNumber;
    }
    /**
	 * 功能说明：字段overdue_number(当前逾期次数)的set方法			
	 */
    public void setOverdueNumber(Integer overdueNumber){
        this.overdueNumber = overdueNumber;
    }
	/**
	 * 功能说明：字段closing_period(已结清期数)的get方法			
	 */
    public Integer getClosingPeriod(){
        return this.closingPeriod;
    }
    /**
	 * 功能说明：字段closing_period(已结清期数)的set方法			
	 */
    public void setClosingPeriod(Integer closingPeriod){
        this.closingPeriod = closingPeriod;
    }
	/**
	 * 功能说明：字段remain_capital_total(剩余本金总和)的get方法			
	 */
    public Double getRemainCapitalTotal(){
        return this.remainCapitalTotal;
    }
    /**
	 * 功能说明：字段remain_capital_total(剩余本金总和)的set方法			
	 */
    public void setRemainCapitalTotal(Double remainCapitalTotal){
        this.remainCapitalTotal = remainCapitalTotal;
    }
	/**
	 * 功能说明：字段remain_overdue_accrual_total(剩余逾期利息总和)的get方法			
	 */
    public Double getRemainOverdueAccrualTotal(){
        return this.remainOverdueAccrualTotal;
    }
    /**
	 * 功能说明：字段remain_overdue_accrual_total(剩余逾期利息总和)的set方法			
	 */
    public void setRemainOverdueAccrualTotal(Double remainOverdueAccrualTotal){
        this.remainOverdueAccrualTotal = remainOverdueAccrualTotal;
    }
	/**
	 * 功能说明：字段remain_accrual_total(剩余利息总和)的get方法			
	 */
    public Double getRemainAccrualTotal(){
        return this.remainAccrualTotal;
    }
    /**
	 * 功能说明：字段remain_accrual_total(剩余利息总和)的set方法			
	 */
    public void setRemainAccrualTotal(Double remainAccrualTotal){
        this.remainAccrualTotal = remainAccrualTotal;
    }
	/**
	 * 功能说明：字段remain_overdue_violate_money_total(剩余逾期违约金总和)的get方法			
	 */
    public Double getRemainOverdueViolateMoneyTotal(){
        return this.remainOverdueViolateMoneyTotal;
    }
    /**
	 * 功能说明：字段remain_overdue_violate_money_total(剩余逾期违约金总和)的set方法			
	 */
    public void setRemainOverdueViolateMoneyTotal(Double remainOverdueViolateMoneyTotal){
        this.remainOverdueViolateMoneyTotal = remainOverdueViolateMoneyTotal;
    }
	/**
	 * 功能说明：字段remain_interest_total(剩余罚息总和)的get方法			
	 */
    public Double getRemainInterestTotal(){
        return this.remainInterestTotal;
    }
    /**
	 * 功能说明：字段remain_interest_total(剩余罚息总和)的set方法			
	 */
    public void setRemainInterestTotal(Double remainInterestTotal){
        this.remainInterestTotal = remainInterestTotal;
    }
	/**
	 * 功能说明：字段remain_manage_fee_total(剩余管理费总和)的get方法			
	 */
    public Double getRemainManageFeeTotal(){
        return this.remainManageFeeTotal;
    }
    /**
	 * 功能说明：字段remain_manage_fee_total(剩余管理费总和)的set方法			
	 */
    public void setRemainManageFeeTotal(Double remainManageFeeTotal){
        this.remainManageFeeTotal = remainManageFeeTotal;
    }
	/**
	 * 功能说明：字段status(订单状态:0 正常、2逾期)的get方法			
	 */
    public Integer getStatus(){
        return this.status;
    }
    /**
	 * 功能说明：字段status(订单状态:0 正常、2逾期)的set方法			
	 */
    public void setStatus(Integer status){
        this.status = status;
    }
	/**
	 * 功能说明：字段update_time(最后更新时间)的get方法			
	 */
    public String getUpdateTime(){
        return this.updateTime;
    }
    /**
	 * 功能说明：字段update_time(最后更新时间)的set方法			
	 */
    public void setUpdateTime(String updateTime){
        this.updateTime = updateTime;
    }
}
package com.xwintop.xcore.base;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.ToString;

@Deprecated
@ToString
public class Bill implements Serializable {
	private static final long serialVersionUID = -8611913139453490297L;
	private String id;
	private String billNumber;
	private String name;
	public Date bizDate;
	private String createUser;
	private String lastUpdateUser;
	public String auditor;
	private Date createDate;
	private Date lastUpdateTime;
	public Date auditTime;
	private Integer version;
	private List<? extends BillEntry> billEntryList;
	private Map<String, List<? extends BillEntry>> operationEnties;

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBillNumber() {
		return this.billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBizDate() {
		return this.bizDate;
	}

	public void setBizDate(Date bizDate) {
		this.bizDate = bizDate;
	}

	public String getCreateUser() {
		return this.createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getLastUpdateUser() {
		return this.lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public String getAuditor() {
		return this.auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public Date getCreateDate() {
		return this.createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastUpdateTime() {
		return this.lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public Date getAuditTime() {
		return this.auditTime;
	}

	public void setAuditTime(Date auditTime) {
		this.auditTime = auditTime;
	}

	public List<? extends BillEntry> getBillEntryList() {
		return this.billEntryList;
	}

	public void setBillEntryList(List<? extends BillEntry> billEntryList) {
		this.billEntryList = billEntryList;
	}

	public Map<String, List<? extends BillEntry>> getOperationEnties() {
		return this.operationEnties;
	}

	public void setOperationEnties(Map<String, List<? extends BillEntry>> operationEnties) {
		this.operationEnties = operationEnties;
	}
}

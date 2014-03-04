package common.entity;

import java.io.Serializable;
import java.util.Date;

import common.dao.TInfConsumeDao;
import common.utils.OrderConstant;
import common.xml.dp.DpInf02011Request;

/**
 * @author 邱亚建 2013-4-25 上午10:42:48<br>
 *         TODO 消费实体类
 * 
 */
public class TInfConsume implements Serializable {

	private static final long serialVersionUID = 1430318219644014013L;
	/**
	 * 消费id
	 */
	private long consumeId;
	/**
	 * 客户id
	 */
	private long custId;
	/**
	 * 订单号
	 */
	private String orderNo;
	/**
	 * 账户类型
	 */
	private String acctType;

	/**
	 * keep值
	 */
	private String keep;

	/**
	 * 渠道号
	 */
	private String channelType;
	/**
	 * 终端号
	 */
	private String termId;

	/**
	 * 业务编码
	 */
	private String actionCode;

	private String pdLineId;

	/**
	 * 消费金额
	 */
	private String amount;

	/**
	 * 消费日期
	 */
	private Date acctDate;

	/**
	 * 消费状态
	 */
	private String stat;

	/**
	 * 保留字段
	 */
	private String remark;

	private String sum_stat;
	
	public long getConsumeId() {
		return consumeId;
	}

	public void setConsumeId(long consumeId) {
		this.consumeId = consumeId;
	}

	public long getCustId() {
		return custId;
	}

	public void setCustId(long custId) {
		this.custId = custId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getAcctType() {
		return acctType;
	}

	public void setAcctType(String acctType) {
		this.acctType = acctType;
	}

	public String getKeep() {
		return keep;
	}

	public void setKeep(String keep) {
		this.keep = keep;
	}

	public String getChannelType() {
		return channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}

	public String getTermId() {
		return termId;
	}

	public void setTermId(String termId) {
		this.termId = termId;
	}

	public String getActionCode() {
		return actionCode;
	}

	public void setActionCode(String actionCode) {
		this.actionCode = actionCode;
	}

	public String getPdLineId() {
		return pdLineId;
	}

	public void setPdLineId(String pdLineId) {
		this.pdLineId = pdLineId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Date getAcctDate() {
		return acctDate;
	}

	public void setAcctDate(Date acctDate) {
		this.acctDate = acctDate;
	}

	public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSum_stat() {
		return sum_stat;
	}

	public void setSum_stat(String sumStat) {
		sum_stat = sumStat;
	}
	
	public void addConsume(TInfConsume tInfConsume,TInfConsumeDao dao) {

		dao.insert(tInfConsume);
	}

	public TInfConsume(TInfConsumeDao dao, String custId, String orderNo,
			String keep, String channelType, String termId,
			String actionCode, String amount,
			String remark, String remark2, String sumStat) {
		
		setConsumeId(dao.getConsumeId());

		setCustId(Long.valueOf(custId));

		setOrderNo(orderNo);

		setAcctType("0007");

		setKeep(keep);

		setChannelType(channelType);

		setTermId(termId);

		setActionCode(actionCode);

		setPdLineId(String.valueOf(dao.getPdlineId()));

		setAmount(amount);

		setStat(OrderConstant.S0A);

		setAcctDate(new Date());

		setRemark(remark + "::" + remark2);

		setSum_stat(sumStat);
		
//		dao.insert(this);
	}

	public TInfConsume() {
		super();
	}
	
	
}

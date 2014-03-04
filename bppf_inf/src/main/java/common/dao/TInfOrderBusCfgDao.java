package common.dao;

import java.util.Map;

import org.apache.log4j.Logger;

import common.entity.TInfOrderBusCfg;
import common.entity.TInfOrderCtl;
import common.entity.mapper.TInfOrderBusCfgMapper;
import common.entity.mapper.TInfOrderCtlMapper;
import common.utils.OrderConstant;
import common.utils.SpringContextHelper;
import framework.exception.INFErrorDef;
import framework.exception.INFException;

public class TInfOrderBusCfgDao {

	protected static final Logger log = Logger
			.getLogger(TInfOrderBusCfgDao.class);

	public static BaseDao DAO = SpringContextHelper.getInfBaseDaoBean();


	public TInfOrderCtl getTInfOrder(TInfOrderCtl config) {

		String sql = "select * from T_INF_ORDERCTL where TMNNUM=? AND ORDER_CODE=? and oper_date>(sysdate-1) and STAT='S0A'";
		TInfOrderCtl r = (TInfOrderCtl) DAO.queryForObject(sql, new Object[] {
				config.getTmnnum(), config.getOrderCode()},
				new TInfOrderCtlMapper());

		return r;
	}
	
	public String getTranSeq(Map<String,String> param) {

		String r = null;
		
		String sql = "SELECT T.ORDER_CODE FROM T_INF_ORDERCTL T WHERE T.TMNNUM=? AND T.ORDER_CODE=? AND T.ORDER_STAT='S0X' AND T.STAT='S0X'";
		
		r = (String)DAO.queryForObject(sql, new Object[]{param.get("TMNNUM"),param.get("TRAN_SEQ")}, String.class);

		return r;
	}

	public void saveTranSeq(Map<String,String> param)throws Exception{
		
		String sql ="INSERT INTO T_INF_ORDERCTL(KEEP,TMNNUM,ORDER_CODE,OPER_DATE,ORDER_STAT,STAT) VALUES(?,?,?,sysdate,?,?)";
		
		
		DAO.insert(sql, new Object[]{param.get("TRAN_SEQ"),param.get("TMNNUM"),param.get("TRAN_SEQ"),"S0X","S0X"});
		
	}
	
	public String saveTInfOrder(TInfOrderCtl config) {

		String flag = null;

		StringBuffer sb = new StringBuffer();

		sb
				.append("insert into T_INF_ORDERCTL ")
				.append(
						"(KEEP, TMNNUM, ORDER_CODE, OPER_INFO, OPER_DATE)")
				.append("values (?,?,?,?,sysdate)");

		try {
			DAO.insert(sb.toString(), new Object[] { config.getKeep(),
					config.getTmnnum(), config.getOrderCode(),
					config.getOperInfo() });
		} catch (Exception e) {
			log.info("添加控制表失败 keep:" + config.getKeep() + " order:"
					+ config.getOrderCode() + "\n" + e.getMessage());
			flag = "添加订单控制失败";
		}
		return flag;
	}
	public void updateTInfOrder(TInfOrderCtl config) {
		String sql  = "update T_INF_ORDERCTL set keep=? ,order_stat=? where TMNNUM=? and ORDER_CODE=?";
		try {
			DAO.update(sql, new Object[] {config.getKeep(),config.getOrderStat(),config.getTmnnum(), config.getOrderCode()});
		} catch (Exception e) {
			log.info("更新状态失败 keep:" + config.getKeep() + " order:"
					+ config.getOrderCode() + "\n" + e.getMessage());
		}
	}
	public void updateTInfOrderStat(String tmnnum,String order_code,String stat) {
		String sql  = "update T_INF_ORDERCTL set order_stat=? where TMNNUM=? and ORDER_CODE=?";
		try {
			DAO.update(sql, new Object[] {stat,tmnnum,order_code});
			log.info("更新状态 tmnnum:" + tmnnum + " order:" + order_code + " 状态为"+stat);
		} catch (Exception e) {
			log.info("更新状态失败 tmnnum:" + tmnnum + " order:"
					+ order_code + "\n" + e.getMessage());
		}
	}
	/**
	 * 
	 * @param channelType
	 * @param keep
	 * @param tmnNum
	 * @param details
	 * @return false:不让通过 true 放行
	 */
	public String checkOrders(TInfOrderCtl config) {

		TInfOrderCtl order = getTInfOrder(config);

		if (order != null) {
				if (OrderConstant.S0F.equals(order.getOrderStat())){
//					order.setStat("S0P");
//					updateTInfOrder(order);
					return null;
				}else if(OrderConstant.S0P.equals(order.getOrderStat())){
					log.info("控制表记录状态：" + order.getOrderStat() + " order:"
							+ order.getOrderCode() + "终端号：" + order.getTmnnum());
					return "订单号：" + config.getOrderCode() + " 为重复订单 ";
				}else{
					log.info("控制表记录状态：" + order.getOrderStat() + " order:"
							+ order.getOrderCode() + "终端号：" + order.getTmnnum());
					return "订单号：" + config.getOrderCode() + " 已支付 ";
				}

		} else {
			TInfOrderCtl orderctl = new TInfOrderCtl();
			orderctl.setKeep(config.getKeep());
			orderctl.setTmnnum(config.getTmnnum());
			orderctl.setOrderCode(config.getOrderCode());
			orderctl.setOperInfo(config.getRemark()+" 新增外订单控制记录");
			String msg = saveTInfOrder(orderctl);
			if (msg != null) {
				return "订单号：" + orderctl.getOrderCode() + "" + msg;
			}
			return null;
		}

	}
	
	/**
	 * 
	 * @param channelType
	 * @param keep
	 * @param tmnNum
	 * @param details
	 * @return false:不让通过 true 放行
	 * @throws INFException 
	 */
	public void checkOrdersNew(TInfOrderCtl config) throws INFException {

		TInfOrderCtl order = getTInfOrder(config);

		if (order != null) {
				if (OrderConstant.S0F.equals(order.getOrderStat())){
//					order.setStat("S0P");
//					updateTInfOrder(order);
					return ;
				}else if(OrderConstant.S0P.equals(order.getOrderStat())){
					log.info("控制表记录状态：" + order.getOrderStat() + " order:"
							+ order.getOrderCode() + "终端号：" + order.getTmnnum());
//					return "订单号：" + config.getOrderCode() + " 为重复订单 ";
					throw new INFException(INFErrorDef.OUT_ORDERNO_REPEAT_S0P,"订单号：" + config.getOrderCode() + " 为重复订单,支付中 ");
				}else{
					log.info("控制表记录状态：" + order.getOrderStat() + " order:"
							+ order.getOrderCode() + "终端号：" + order.getTmnnum());
//					return "订单号：" + config.getOrderCode() + " 已支付 ";
					throw new INFException(INFErrorDef.OUT_ORDERNO_REPEAT,"订单号：" + config.getOrderCode() + " 已支付 ");
				}

		} else {
			TInfOrderCtl orderctl = new TInfOrderCtl();
			orderctl.setKeep(config.getKeep());
			orderctl.setTmnnum(config.getTmnnum());
			orderctl.setOrderCode(config.getOrderCode());
			orderctl.setOperInfo(config.getRemark()+" 新增外订单控制记录");
			String msg = saveTInfOrder(orderctl);
			if (msg != null) {
//				return "订单号：" + orderctl.getOrderCode() + "" + msg;
				throw new INFException(INFErrorDef.OUT_ORDERNO_REPEAT_FAIL,"订单号：" + orderctl.getOrderCode() + "" + msg);
			}
		}

	}

	/**
	 * 根据服务编码和业务编码查询业务配置
	 */
	public boolean hasOrderCfg(TInfOrderBusCfg config) {
		String sql = "select * from T_INF_ORDERBUSCFG where SVC_CODE=? AND BUS_CODE=? AND stat='S0A'";
		TInfOrderBusCfg r = (TInfOrderBusCfg) DAO.queryForObject(sql,
				new Object[] { config.getSvcCode(), config.getBusCode() },
				new TInfOrderBusCfgMapper());

		if (r != null) {
			return true;
		} else {
			return false;
		}

	}

}

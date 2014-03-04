package common.service;

import common.dao.TInfOperInLogDao;
import common.dao.TInfOrderBusCfgDao;
import common.entity.TInfOperInLog;
import common.entity.TInfOrderBusCfg;
import common.entity.TInfOrderCtl;
import framework.exception.INFErrorDef;
import framework.exception.INFException;

public class TInfOperInLogManager {

	public static TInfOperInLogDao dao = new TInfOperInLogDao();
	
	public boolean insert(TInfOperInLog tInfOperInLog) {
		return dao.insert(tInfOperInLog);
	}
	
	//查询日志标识
	public long getOperInId(){
		return dao.getOperInId();
	}
	
	/**
	 * 修改准许通过
	 * chenwc--2012-6-19下午06:16:20
	 */
	public boolean updateAllow(long operInId,long allow){
		return dao.updateAllow(operInId, allow);
	}
	
	/**
	 * 根据流水号查询入站日志
	 * chenwc--2012-6-20上午09:13:48
	 */
	public boolean selectTInfOperInLogByKeep(String keep){
		boolean bool = dao.selectTInfOperInLogByKeep(keep);
		return bool;
	}
	
	//转换操作编码
	public static String convertBussCode(String code){
		
		String r =code;
		String SEQ_ZERO = "0000";
		String tempSeq = (String)code;
		if(null != tempSeq && tempSeq.length() > 0) {
			if(tempSeq.length() < "0000".length()) {
				//序列号不足4位，在前面补0
				tempSeq = SEQ_ZERO.substring(0, SEQ_ZERO.length()-tempSeq.length()) + tempSeq;
			}				
		}		
		r = tempSeq;
		return r;
	}

	//转换代收付操作编码
	public static String convetBussType(String code){
			
			String r =null;
			
			if("BT001".equals(code)){
				r="0001";
			}else if("BT002".equals(code)){
				r="0002";
			}
			return r;
		}
	
	public static boolean verifyOrder(String orderSeq,String keep,String tmnNum,String svcInfName,String opertype) throws INFException{
		//验证外部订单号是否重复
		TInfOrderBusCfg param = new TInfOrderBusCfg();
		param.setSvcCode(svcInfName);
		param.setBusCode(opertype);
		
		TInfOrderBusCfgDao tinfDao = new TInfOrderBusCfgDao();
		
		boolean hasOrderCfg = tinfDao.hasOrderCfg(param);
		if(hasOrderCfg){//是否需要验证
			
			TInfOrderCtl p = new TInfOrderCtl();
			p.setKeep(keep);
			p.setTmnnum(tmnNum);
			p.setOrderCode(orderSeq);
			p.setRemark(svcInfName);
			
//			String msg = tinfDao.checkOrders(p);
//	
//			if(msg!=null){
////				throw new Exception(msg);
//				throw new INFException(INFErrorDef.OUT_ORDERNO_REPEAT,msg);
//			}
			tinfDao.checkOrdersNew(p);
		}
		return hasOrderCfg;
	}
	}

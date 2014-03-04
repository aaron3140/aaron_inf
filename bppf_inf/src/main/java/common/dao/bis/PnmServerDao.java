package common.dao.bis;

import org.springframework.stereotype.Repository;


/**
 * 消息管理 消息管理分为消息和公告两大类。 公告为重大事件的正式公开，接收对象一般为所有人或某类群体，所以公告不能推送给单个商户；
 * 消息为一般事件的告知，接收对象一般为某类群体或个体。所以消息不能推送给所有商户。
 * 消息和公告每次阅读都要在BPPF_BIS.T_BAP_READREC增加一条阅读记录。
 * 消息需要对当前登录人员支持删除功能，新增记录时需要在BPPF_BIS
 * .T_BAP_STAFFREC插入一条未读记录，删除时把状态修改即可（门户网站/手机客户端实现）。
 * 消息发布渠道种类分为门户网站/手机客户端/短信三种类型，短信类型需要给相关接收人员下发短信通知，另外两种各自查库读取展示。
 * 消息范围种类分为按地区发布/按产品发布/按机构发布/按商户发布，对应接收单位为地区/产品线(支持多选)/机构(支持多选)/商户(支持多选)。
 * 
 * @author aaronMing
 * 
 */
@Repository
public class PnmServerDao extends BisDao {

	@SuppressWarnings("rawtypes")
	@Override
	protected Class getFeaturedClass() {
		return this.getClass();
	}

	public String findLinkByMerid(String merId) {
		String sql = "select distinct t.link_num from t_pnm_server t where t.prtn_id = "
				+ "(select prtn.prtn_id from t_pnm_partner prtn where prtn.prtn_code = ?)";
		String linkNum = null;
		linkNum = (String) baseDao.queryForObject(sql, new Object[] { merId },
				java.lang.String.class);
		// } catch (Exception e) {
		// throw new Exception("机构接入号的绑定终端号异常");
		// }
		// linkNum = Charset.trim(linkNum);
		// if( ! linkNum.equals(tmnNum)) {
		// throw new Exception("机构接入号和绑定终端号不匹配");
		// }
		return linkNum;

	}

}

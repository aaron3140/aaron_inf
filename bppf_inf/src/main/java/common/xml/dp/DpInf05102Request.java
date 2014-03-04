package common.xml.dp;
import org.dom4j.Document;

import common.xml.CheckerAnnotion;
import common.xml.CommonReqAbs;
public class DpInf05102Request extends CommonReqAbs {

		public DpInf05102Request(String xmlStr) throws Exception {
			super(xmlStr, null);

		}
		@CheckerAnnotion(len = 25, type = CheckerAnnotion.TYPE_STR)//1
		private String orderSeq;
		
		@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)//1
		private String custCode;
		
		@CheckerAnnotion(len = 2, type = CheckerAnnotion.TYPE_NUM)
		private String transferFlag;
		
		@CheckerAnnotion(len = 54, type = CheckerAnnotion.TYPE_STR)
		private String colleCustCode;
		
		@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
		private String areacode;

		@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)//1
		private String branchCode;
		
		@CheckerAnnotion(len = 5, type = CheckerAnnotion.TYPE_STR)//1
		private String busiType;

		@CheckerAnnotion(len = 30, type = CheckerAnnotion.TYPE_STR)//1
		private String bankAcct;

		@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)//1
		private String transAccName;
		
		@CheckerAnnotion(len = 6, type = CheckerAnnotion.TYPE_NUM)//1
		private String bankCode;
		
		@CheckerAnnotion(len = 128, type = CheckerAnnotion.TYPE_STR)//1
		private String openBank;
		
		@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)//1
		private String cardFlag;
		
		@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)//1
		private String validity;
		
		@CheckerAnnotion(len = 10, type = CheckerAnnotion.TYPE_STR)//1
		private String cvn2;

		@CheckerAnnotion(len = 1, type = CheckerAnnotion.TYPE_NUM)//1
		private String privateFlag;
		
		@CheckerAnnotion(len = 8, type = CheckerAnnotion.TYPE_NUM)//1
		private String certType;
		
		@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_STR)//1
		private String certNo;
		
		@CheckerAnnotion(len = 20, type = CheckerAnnotion.TYPE_NUM)//1
		private String tel;
		
		
		@CheckerAnnotion(len = 14, type = CheckerAnnotion.TYPE_NUM)//1
		private String txnAmount;

		@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
		private String memo;

		@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
		private String remark1;

		@CheckerAnnotion(len = 200, type = CheckerAnnotion.TYPE_STR)//1
		private String remark2;
		
		
		@Override
		public void init(Document doc, Object reserved) throws Exception {
		}

		@Override
		public void setParameters(Document doc, Object reserved) throws Exception {
			orderSeq = getNodeTextM(doc, "ORDERSEQ");
			custCode = getNodeTextM(doc,"CUSTCODE");
			busiType = getNodeTextM(doc, "BUSITYPE");
			areacode = getNodeText(doc, "AREACODE");
			branchCode = getNodeText(doc, "BRANCHCODE");								
			
			bankAcct = getNodeTextM(doc, "BANKACCT");
			transAccName = getNodeTextM(doc, "TRANSACCNAME");	
			bankCode = getNodeTextM(doc,"BANKCODE");
			if(bankCode!=null&&bankCode.length()<6){
				throw new Exception("请填写六位的银行编码");
			}
			cardFlag = getNodeTextM(doc, "CARDFLAG");
//			if("1".equals(cardFlag)){
//				validity = getNodeTextM(doc,"VALIDITY");
//			}else{
				validity = getNodeText(doc,"VALIDITY");
//			}
//			if("2".equals(cardFlag)){
//				cvn2 = getNodeTextM(doc,"CVN2");
//			}else{
				cvn2 = getNodeText(doc,"CVN2");
//			}
			privateFlag = getNodeTextM(doc, "PRIVATEFLAG");
			certType = getNodeText(doc,"CERTTYPE");
			certNo = getNodeText(doc,"CERTNO");
			tel = getNodeText(doc,"TEL");
			openBank = getNodeText(doc,"OPENBANK");
			txnAmount = getNodeTextM(doc, "TXNAMOUNT");
			int index = txnAmount.indexOf(".");
			if (index != -1) {
				throw new Exception("金额以分为单位,不能有小数点");
			}
			memo = getNodeText(doc, "MEMO");
			
//			transferFlag = getNodeText(doc, "TRANSFERFLAG");
//			
//			if(transferFlag !=null&&"01".equals(transferFlag)){
//				
//				colleCustCode = getNodeTextM(doc, "COLLECUSTCODE");
//			}else{
//				
//				colleCustCode = getNodeText(doc, "COLLECUSTCODE");
//			}
			
			remark1 = getNodeText(doc, "REMARK1");
			remark2 = getNodeText(doc, "REMARK2");
		}

		public String getTransferFlag() {
			return transferFlag;
		}

		public void setTransferFlag(String transferFlag) {
			this.transferFlag = transferFlag;
		}

		public String getColleCustCode() {
			return colleCustCode;
		}

		public void setColleCustCode(String colleCustCode) {
			this.colleCustCode = colleCustCode;
		}
		
		public String getCvn2() {
			return cvn2;
		}

		public void setCvn2(String cvn2) {
			this.cvn2 = cvn2;
		}

		public String getValidity() {
			return validity;
		}

		public void setValidity(String validity) {
			this.validity = validity;
		}

//		public String getBranchCode() {
//			return branchCode;
//		}
//
//		public void setBranchCode(String branchCode) {
//			this.branchCode = branchCode;
//		}

		public String getCardFlag() {
			return cardFlag;
		}

		public void setCardFlag(String cardFlag) {
			this.cardFlag = cardFlag;
		}

		public String getPrivateFlag() {
			return privateFlag;
		}

		public void setPrivateFlag(String privateFlag) {
			this.privateFlag = privateFlag;
		}

		public String getOrderSeq() {
			return orderSeq;
		}

		public void setOrderSeq(String orderSeq) {
			this.orderSeq = orderSeq;
		}

		public String getAreacode() {
			return areacode;
		}

		public void setAreacode(String areacode) {
			this.areacode = areacode;
		}

		public String getBusiType() {
			return busiType;
		}

		public void setBusiType(String busiType) {
			this.busiType = busiType;
		}
		
		public String getBankAcct() {
			return bankAcct;
		}

		public void setBankAcct(String bankAcct) {
			this.bankAcct = bankAcct;
		}

		public String getBankCode() {
			return bankCode;
		}

		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}

		public String getTransAccName() {
			return transAccName;
		}

		public void setTransAccName(String transAccName) {
			this.transAccName = transAccName;
		}

		public String getTxnAmount() {
			return txnAmount;
		}

		public void setTxnAmount(String txnAmount) {
			this.txnAmount = txnAmount;
		}

		public String getMemo() {
			return memo;
		}

		public void setMemo(String memo) {
			this.memo = memo;
		}

		public String getRemark2() {
			return remark2;
		}

		public void setRemark2(String remark2) {
			this.remark2 = remark2;
		}


		public String getCertNo() {
			return certNo;
		}

		public void setCertNo(String certNo) {
			this.certNo = certNo;
		}

		public String getCertType() {
			return certType;
		}

		public void setCertType(String certType) {
			this.certType = certType;
		}

		public String getCustCode() {
			return custCode;
		}

		public void setCustCode(String custCode) {
			this.custCode = custCode;
		}

		public String getOpenBank() {
			return openBank;
		}

		public void setOpenBank(String openBank) {
			this.openBank = openBank;
		}

		public String getTel() {
			return tel;
		}

		public void setTel(String tel) {
			this.tel = tel;
		}

		public String getBranchCode() {
			return branchCode;
		}

		public void setBranchCode(String branchCode) {
			this.branchCode = branchCode;
		}

		public String getRemark1() {
			return remark1;
		}

		public void setRemark1(String remark1) {
			this.remark1 = remark1;
		}

	

}

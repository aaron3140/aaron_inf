package common.platform.provider.server;

//import org.apache.log4j.*;
import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import common.platform.provider.bean.ByteBean;


public class DataProcess {
	byte[] headOfPackage ;//64byte
	byte[] tailOfPackage ;	
	ByteBean byteBean = new ByteBean();
	private Logger Log = Logger.getLogger(DataProcess.class);
	
	/**
	 * 处理包数据
	 * @param data
	 * @return
	 */
	public PackageDataSet process (byte[] data) throws Exception {
		PackageDataSet packageDataSet = new PackageDataSet();
		try{
			headOfPackage = byteBean.subByte(data,0,72);
			String [] headOfPackageArray = new String [11];
			headOfPackageArray = ProcessHeadOfPackage(headOfPackage,headOfPackageArray);
			
//			/*  test 
			 for (int i = 0 ; i <11; i ++){
				 Log.info(i+":"+ headOfPackageArray[i] );
			 }
				
//			*/
			if  (!  (Integer.valueOf(headOfPackageArray[2])>72 )){
				return null;
			}
			tailOfPackage = byteBean.subByte(data,72);
			if ( headOfPackageArray[4].equals("0") ){//bian ping 
				int NoOfArgument = Integer.valueOf(headOfPackageArray[6]);// get the number of Argument
				if ( NoOfArgument != 0 ){
					String [] tailOfPackageArray = new String [NoOfArgument];
					ProcessTailOfPackage0(tailOfPackage,NoOfArgument,tailOfPackageArray);
//					/*test
					for (int i = 0 ; i <NoOfArgument; i ++){
						Log.info(i+":"+ tailOfPackageArray[i] );
					}
//					*/	
					packageDataSet.setNoOfArray(1);
					String [][][] argumentArrayData = new String [1][1][NoOfArgument];
					argumentArrayData[0][0] = tailOfPackageArray;
					packageDataSet.setSign("0");
		   			packageDataSet.setArgumentArrayData(argumentArrayData);
		   			packageDataSet.setNoOfArgument(NoOfArgument);
				
				}
				
			}else if ( headOfPackageArray[4].equals("1") ){
				int noOfArray = Integer.valueOf(headOfPackageArray[6]);//get the number of Argument array
//				id of arygument array 
				String [] idOfArgumentArray = new String [noOfArray];
//				number of row
				int [] noOfArrayRow = new int [noOfArray];
//				number of argument
				int [] noOfArgument = new int [noOfArray];
				String [][][] argumentArrayData = new String [noOfArray][][];
				try{
				ProcessTailOfPackage1(noOfArray,tailOfPackage,idOfArgumentArray,noOfArrayRow,noOfArgument,argumentArrayData);
				packageDataSet.setSign("1");
				packageDataSet.setArgumentArrayData(argumentArrayData);
				packageDataSet.setIdOfArgumentArray(idOfArgumentArray);
				packageDataSet.setNoOfArrayRowSet(noOfArrayRow);
				packageDataSet.setNoOfArray(noOfArray);
				packageDataSet.setNoOfArgumentSet(noOfArgument);
//				/*test  测试解包的结果
//				for (int i = 0 ; i <noOfArray; i ++){
//					for (int j = 0 ; j < noOfArrayRow[i]; j++){
//						for (int k = 0 ; k < noOfArgument[i]; k++){
//							Log.info("结果:"+argumentArrayData[i][j][k]);
//						}
//					}
//				}
//				*/					
				}catch(Exception e){
					Log.error(e.getMessage());
					System.out.print(e);
					throw e;
				}
				
			}
			
		}catch (Exception e){
			Log.error(e.getMessage());
			System.out.println(e);
			throw e;
		}
		return packageDataSet;
			
		
	}
	
	
	/**
	 * 处理包头数据
	 * @param headOfPackage
	 * @param headOfPackageArray
	 * @return
	 */
	private String [] ProcessHeadOfPackage  (byte[]  headOfPackage,String []headOfPackageArray){
//		String [] headOfPackageArray = new String [11];
		int [] sign =  {4,10,8,8,1,1,2,2,2,26,8};//the length of each headOfPackage' element 
		int n = 11;//the number of array
		int index = 0,beginIndex = 0,endIndex = 0;
		while ( n != 0 ){			
			endIndex = endIndex + sign[index];
			try{
				headOfPackageArray[index] = new String( byteBean.subByte(headOfPackage,beginIndex, endIndex),"GBK").trim();
			}catch(UnsupportedEncodingException e)
		    {
			       return null;
			}		
			beginIndex = endIndex;
			n --;
			index ++; 
		}
		return headOfPackageArray;
		
	}
	/**
	 * 处理扁平式包体数据
	 * @param tailOfPackage
	 * @param NoOfArgument
	 * @param tailOfPackageArray
	 * @return
	 */
	private byte[] ProcessTailOfPackage0(byte[] tailOfPackage,int NoOfArgument,String [] tailOfPackageArray){
//		String [] tailOfPackageArray = new String [NoOfArgument];
		String [] ArgumentNumbers = new String [NoOfArgument];
		int index=0;
		int length;
		while ( NoOfArgument != 0 ){
			try{
			ArgumentNumbers[index] =new String(byteBean.subByte(tailOfPackage, 0, 4),"GBK") ; //argument code
			length = Integer.valueOf(new String( byteBean.subByte(tailOfPackage, 4, 8),"GBK") );//length of argument value
			tailOfPackageArray[index] =ArgumentNumbers[index] +new String( byteBean.subByte(tailOfPackage, 8, 8 + length),"GBK");//argument code(4btye) + argument value
			tailOfPackage = byteBean.subByte(tailOfPackage, 8+length);
			}catch(Exception e){
				Log.error(e.getMessage());
			//	System.out.print(e);
			}
			index ++;
			NoOfArgument --;
		}
		// return leave data 
		return tailOfPackage;
	}
	/**
	 * 处理复合式包的包体数据
	 * @param noOfArray
	 * @param tailOfPackage
	 * @param idOfArgumentArray
	 * @param noOfArrayRow
	 * @param noOfArgument
	 * @param argumentArrayData
	 */
	private void ProcessTailOfPackage1(int noOfArray,byte[] tailOfPackage,String [] idOfArgumentArray,int [] noOfArrayRow,int [] noOfArgument, String[][][]argumentArrayData){
		
		for (int i = 0 ; i < noOfArray; i ++){
			try {
				idOfArgumentArray[i] =new String( byteBean.subByte(tailOfPackage, 0, 3),"GBK");//get the number of argument array
				noOfArrayRow[i] = Integer.valueOf(new String(byteBean.subByte(tailOfPackage, 3, 6),"GBK") );//row number of the argument array 
				noOfArgument[i] = Integer.valueOf(new String(byteBean.subByte(tailOfPackage, 6, 8),"GBK"));//number of argument  in row  
				tailOfPackage = byteBean.subByte(tailOfPackage,  8) ;
				String [][] argumentRowData = new String [ noOfArrayRow[i] ] [ noOfArgument[i] ];
				for (int j = 0 ; j < noOfArrayRow[i]; j ++){//j 行数据
					tailOfPackage = ProcessTailOfPackage0(tailOfPackage,noOfArgument[i],argumentRowData[j]);//每行数据
				}
				argumentArrayData[i] = argumentRowData;
			}catch(Exception e){
				Log.error(e.getMessage());
				System.out.print(e);
			}
		}
	}
	
}

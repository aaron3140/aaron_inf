package common.platform.provider.bean;
/* 
 *
 */
public class ByteBean {
	/**
	 * 得到byte数组的子数据 截取 begin 到 end 部分 当begin =0 end =2 截取数据的 第1 第2 位
	 * @param by
	 * @param begin
	 * @param end
	 * @return
	 */
	public byte[] subByte(byte[] by,int begin, int end){
		byte[] sub_byte = "".getBytes();
		if ( end - begin > 0 && by.length >= end ){
			sub_byte = new byte[end - begin];
			for (int i = 0 ,j = 0; i < by.length ; i++){
				if ( i >= begin && i <end ){
					sub_byte[j] = by[i];
					j++;
				}
			}
			
		}
		return sub_byte;
	}
	/**
	 * 得到byte数组的子数据 截取 begin 以后部分  begin ＝0 截取全部 类推
	 * @param by
	 * @param begin
	 * @return
	 */
	public byte[] subByte(byte[] by,int begin){
		byte[] sub_byte = "".getBytes();
		if ( by.length > begin ){
			sub_byte = new byte[by.length - begin];
			for( int i =0 , j = 0; i < by.length ; i ++){
				if ( i >= begin ){
					sub_byte[j] = by[i];
					j ++;
				}	
			}
		}
		return sub_byte;
	}
}

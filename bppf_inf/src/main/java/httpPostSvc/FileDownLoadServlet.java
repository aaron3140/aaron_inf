package httpPostSvc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.dao.TBapUpFileDao;
import common.utils.Charset;
import common.utils.DownloadTool;

@SuppressWarnings("serial")
public class FileDownLoadServlet extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id=request.getParameter("fileId");
        id=new String(id.getBytes("ISO-8859-1"),"utf-8");
        download2(id,request,response);
	}
	
	@SuppressWarnings("deprecation")
    public HttpServletResponse download(String id,HttpServletRequest request, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
//          File file = new File(request.getSession().getServletContext().getRealPath("/")+"/"+path);
        	String path = id;
            File file = new File(DownloadTool.DOWNLOADPATH + path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            //String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("utf-8"),"ISO-8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }
	
	 public HttpServletResponse download2(String id,HttpServletRequest request, HttpServletResponse response) {
			BufferedOutputStream bos = null;
			BufferedInputStream bis = null;
				
			try {
//				Long fileId = Long.valueOf(request.getParameter("fileId"));
//				String clientId = request.getParameter("clientId");
				//
				String path = TBapUpFileDao.getPath(id);
				File downfile = new File(path);
//				System.out.println(path);
				if ( !downfile.exists() || !downfile.isFile() ) {
					throw new Exception(path+"文件不存在");
				} else {
					//
					String guestCharset = request.getCharacterEncoding();
					String fileName = downfile.getName();
					fileName = URLEncoder.encode(fileName, "UTF-8");
					if (fileName.length() > 150) {
						fileName = new String(fileName.getBytes(guestCharset), "ISO8859-1");
					}
					//
					bis = new BufferedInputStream(new FileInputStream(downfile));
					response.reset();
					response.setBufferSize(1);
					response.setContentType("application/x-download");
					response.setHeader("Content-Disposition","attachment;filename=" + fileName);
					
					//告诉客户端允许断点续传多线程连接下载    
					//响应的格式是:    
					//Accept-Ranges: bytes    
					response.setHeader("Accept-Ranges", "bytes");    
					long p = 0; 
					long l = downfile.length();
					long q = l - 1;   

	  
					//如果是第一次下,还没有断点续传,状态是默认的 200,无需显式设置    
					//响应的格式是:    
					//HTTP/1.1 200 OK    
					  
					 if (request.getHeader("Range") != null) //客户端请求的下载的文件块的开始字节    
				 	 {    
					   //如果是下载文件的范围而不是全部,向客户端声明支持并开始文件块下载    
					   //要设置状态    
					   //响应的格式是:    
					   //HTTP/1.1 206 Partial Content    
					   response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);//206    
					  
					   //从请求中得到开始的字节    
					   //请求的格式是:    
					   //Range: bytes=[文件块的开始字节]-    
					   String[] ranges = request.getHeader("Range").trim().replaceAll("bytes=","").trim().split("-");
					   p = Long.parseLong(ranges[0]);
					   if (ranges.length > 1) {
					   		q = Long.parseLong(ranges[1]);
					   } else {
					   		q = 	l - 1;
					   }
					}
					
					//下载的文件(或块)长度    
					//响应的格式是:    
					//Content-Length: [文件的总大小] - [客户端请求的下载的文件块的开始字节]    				  
					response.setContentLength(new Long(q - p + 1).intValue());
					
					if (p != 0)    
					{    
				  		//不是从最开始下载,    
			   			//响应的格式是:    
				   		//Content-Range: bytes [文件块的开始字节]-[文件的总大小 - 1]/[文件的总大小]    
					   response.setHeader("Content-Range","bytes " + new Long(p).toString() + "-" + new Long(q).toString() + "/" + new Long(l).toString()); 
					}  else {			
					     /*if (!Charset.isEmpty(appvfId) || !Charset.isEmpty(clientId)	)  {
							//下载客户端、版本文件记录下载时间和手机号
							String mobile = Charset.trim(request.getHeader("x-up-calling-line-id"));
							String staffId = (String) session.getAttribute("staffId");
							String objType = null;
							Long objId = 0L;
							String operType = "DOWNLOAD";
							String operStaff = null;
							if(staffId == null || "null".equals(staffId) || "".equals(staffId)){
								if(mobile == null || "null".equals(mobile) || "".equals(mobile)){
									mobile = "other";
								}
								staffId = mobile;
							}
							operStaff = staffId;
							Date operTime = new Date();
							String resultCode = "000000";
							String resultInfo = "成功";
							String memo = mobile;
							IOperlogManager om = OperlogManager.getInstance();
							TVmOperlog tVmOperlog = new TVmOperlog();
							if (!Charset.isEmpty(appvfId)) {
								IAppvfManager am = AppvfManager.getInstance();
								am.updateDLCount(Long.valueOf(appvfId));//appvf的文件下载数加1
								
								objType = "APPVF";
								objId = Long.valueOf(appvfId);
							} else {
								objType = "CLIENT";
								objId = Long.valueOf(clientId);;
							}
							tVmOperlog.setObjType(objType);
							tVmOperlog.setObjId(objId);
							tVmOperlog.setOperType(operType);
							tVmOperlog.setOperStaff(operStaff);
							tVmOperlog.setOperTime(operTime);
							tVmOperlog.setResultCode(resultCode);
							tVmOperlog.setResultInfo(resultInfo);
							tVmOperlog.setMemo(memo);
							Transaction transaction = HibernateSessionFactory.getSession().beginTransaction();
							
							om.add(tVmOperlog);
							transaction.commit();
						}*/
						
					}  
	  
					bos = new BufferedOutputStream(response.getOutputStream());
					byte[] buff = new byte[512];
					int bytesRead;
					bis.skip(p);
					long needed = q - p + 1;
					while( (bytesRead = bis.read(buff,0, (new Long(buff.length).longValue() > needed ? new Long(needed).intValue() : buff.length) )) != -1 ) {
						bos.write(buff,0,bytesRead);
						bos.flush();
						response.flushBuffer();
						
						needed = needed - bytesRead;
						if (needed <= 0) break;
					}

//					out.clear();
//					out = pageContext.pushBody();
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		 return response;
	 }
}

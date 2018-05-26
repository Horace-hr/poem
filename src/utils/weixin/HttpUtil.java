package utils.weixin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class HttpUtil {

	
	/**
	 * @param target
	 * @return
	 * @throws IOException
	 */
	public static String getContentFromUrl(String target)throws IOException{
		java.net.URL url = new java.net.URL(target);
		URLConnection conn = url.openConnection();
		conn.addRequestProperty("User-Agent","www.qq.com");
		InputStream is = conn.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
		
		StringBuilder temp = new StringBuilder();
		String line = null;
		while((line = reader.readLine())!=null){
			temp.append(line);
		}
		is.close();
		
		return temp.toString();
	}
	
	public static String postXml(String urlStr,String xmlInfo){
		 try {  
	            URL url = new URL(urlStr);  
	            URLConnection con = url.openConnection();  
	            con.setDoOutput(true);  
	            con.setRequestProperty("Pragma:", "no-cache");  
	            con.setRequestProperty("Cache-Control", "no-cache");  
	            con.setRequestProperty("Content-Type", "text/xml");
	  
	            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());      
	            System.out.println("urlStr=" + urlStr);  
	            System.out.println("xmlInfo=" + xmlInfo);  
	            // new String(xmlInfo.getBytes("ISO-8859-1");
//	            out.write(xmlInfo);  
	            out.write(new String(xmlInfo.getBytes("ISO-8859-1")));  
	            out.flush();  
	            out.close();  
	            BufferedReader br = new BufferedReader(new InputStreamReader(con  
	                    .getInputStream(),"UTF-8"));  
	            String line = "";  
	            StringBuffer result = new StringBuffer();
	            for (line = br.readLine(); line != null; line = br.readLine()) {  
	            	result.append(line);  
	            } 
	            return result.toString();
	        } catch (MalformedURLException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
		return "";
	}


}


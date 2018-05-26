package utils;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import com.jfinal.upload.UploadFile;

public class UploadFileUtil {
		/*
		 * 上传文件并返回文件名
		 */
		public static String FileUploaded(UploadFile file , HttpServletRequest request , String fileAddress) {
			//获取上传文件的类型  如 .jpg .png.....
			String fileType=file.getFileName().substring(file.getFileName().lastIndexOf("."));
		    String fileName = StringUtil.getKey() + fileType ;
			String fileRoute = request.getSession().getServletContext().getRealPath(fileAddress)+"/"+fileName;
		    //重命名
			file.getFile().renameTo(new File(fileRoute));
			return fileRoute ;
		}
}

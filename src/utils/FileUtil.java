package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;

public class FileUtil {
	
	public static String reName(UploadFile file) {
		String fileName = file.getFileName() ;
		String fileType = fileName.substring(fileName.lastIndexOf("."));
		File uploadedFile = new File(file.getUploadPath() +  File.separator + fileName ) ;
		String newName =  StrKit.getRandomUUID() + fileType ;
		uploadedFile.renameTo(new File(file.getUploadPath() + File.separator + newName)) ;
		return newName ;
	}
	
	public static int getAmount(String valueString) {
		Float float1 = Float.parseFloat(valueString);
		float1 = float1*100 ;
		return Math.round(float1);
	}
		
		/*
		 * 导出数据
		 */
		public static void exportMessage(List<Record> recordList , String savePath) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = null ;
			
			String titleString = "订单编号，提交时间，单位名称，充值卡号，充值金额，充值状态，处理时间";
			String[] titles = titleString.split("，");
			for (int i = 0; i < titles.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(titles[i]);
			}
			
			Record record = null ;
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time ;
			String timeString;
			for (int i = 1; i <= recordList.size(); i++) {
				row = sheet.createRow(i);
				record = recordList.get(i-1);
				
				cell = row.createCell(0);
				cell.setCellValue(record.getStr("orderNumber"));  //订单编号
				
				cell = row.createCell(1);
				time = record.getDate("submitTime"); //时间
				timeString = time == null ? "" : sFormat.format(time);
				cell.setCellValue(timeString); //提交时间
				
				cell = row.createCell(2);
				cell.setCellValue(record.getStr("company")); //单位名称
				
				cell = row.createCell(3);
				cell.setCellValue(record.getStr("cardNumber")); //卡号
				
				cell = row.createCell(4);
				cell.setCellValue(record.getInt("amount")); //充值金额
				
				cell = row.createCell(5);
				int status = record.getInt("status");
				String statusString = "待付款" ;
				if (status > -1) {
					statusString = status == 0 ? "待充值" : "已充值" ;
				}
				cell.setCellValue(statusString); //充值状态
				
				cell = row.createCell(6);
				time = record.getDate("dealTime"); //时间
				timeString = time == null ? "" : sFormat.format(time);
				cell.setCellValue(timeString); //处理时间
				
			}
			
			File file = new File(savePath);
			try {
				file.createNewFile();
				FileOutputStream stream = FileUtils.openOutputStream(file);
				workbook.write(stream);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public static String getFloat(int amount) {
			BigDecimal bigDecimal = new BigDecimal(amount);
			bigDecimal = bigDecimal.divide(new BigDecimal(100));
			return bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue() + "";
		}
		
		//导出加油记录
		public static void exportOilMessage(List<Record> recordList , String savePath) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet();
			HSSFRow row = sheet.createRow(0);
			HSSFCell cell = null ;
			
			String titleString = "卡号，加油时间，加油站，油品，单价，加油量，加油金额，返利金额，返利状态";
			String[] titles = titleString.split("，");
			for (int i = 0; i < titles.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(titles[i]);
			}
			
			Record record = null ;
			SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date time ;
			String statusString;
			boolean isRebated ;
			String timeString;
			for (int i = 1; i <= recordList.size(); i++) {
				row = sheet.createRow(i);
				record = recordList.get(i-1);
				
				cell = row.createCell(0);
				cell.setCellValue(record.getStr("cardNumber")); //卡号
				
				cell = row.createCell(1);
				time = record.getDate("time"); //时间
				timeString = time == null ? "" : sFormat.format(time);
				cell.setCellValue(timeString); //提交时间
				
				cell = row.createCell(2);
				cell.setCellValue(record.getStr("address")); //加油站
				
				cell = row.createCell(3);
				cell.setCellValue(record.getStr("oilName")); //油品
				
				cell = row.createCell(4);
				cell.setCellValue(getFloat(record.getInt("price"))); //单价
				
				cell = row.createCell(5);
				cell.setCellValue(record.getBigDecimal("count")+""); //加油量
				
				cell = row.createCell(7);
				cell.setCellValue(getFloat(record.getInt("amount"))); //加油金额
				
				cell = row.createCell(7);
				cell.setCellValue(getFloat(record.getInt("rebate"))); //返利金额
				
				cell = row.createCell(8);
				isRebated = record.getBoolean("isRebated");
				statusString = "待返利" ;
				if (isRebated) {
					statusString = "已返利" ;
				}
				cell.setCellValue(statusString); //返利状态
				
			}
			
			File file = new File(savePath);
			try {
				file.createNewFile();
				FileOutputStream stream = FileUtils.openOutputStream(file);
				workbook.write(stream);
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		

		
}

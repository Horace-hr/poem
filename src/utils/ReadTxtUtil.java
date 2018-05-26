package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Chapter;
import model.Classify;
import model.Config;
import model.Novel;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.ehcache.CacheKit;

public class ReadTxtUtil {
	
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("*");
        }
        return dest;
    }
	
	public static String filterNovelName(String fileName) {
        fileName = fileName.substring(0 , fileName.lastIndexOf(".")) ;
        fileName = fileName.replace("《", "").replace("》", "") ;
        return fileName ;
    }
	
	public static boolean isNumeric(String str){
	  for (int i = 0; i < str.length(); i++){
	    if (!Character.isDigit(str.charAt(i))){
	       return false;
	    }
	  }
	  return true;
	}
	
	public static int getChapterNum(String fileName) {
        fileName = fileName.substring(1 , fileName.indexOf("章")) ;
        if (ReadTxtUtil.isNumeric(fileName)) {
			return Integer.parseInt(fileName) ;
		}
        return -1 ;
    }
	
	public static void readNovel(String routes , String fileName ) {
		try {
            String encoding="GBK";
            File file=new File( PathKit.getWebRootPath() +  routes + fileName );
            if(file.isFile() && file.exists()){ //判断文件是否存在
                InputStreamReader read = new InputStreamReader(
                new FileInputStream(file),encoding);//考虑到编码格式
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                
                Novel novel = new Novel() ;
                novel.setId(Novel.dao.createNovelId()) ;
                String nName = ReadTxtUtil.filterNovelName(fileName) ;
                novel.setNName(nName) ;
                novel.setPic( "/upload/novelPic/" + nName + ".jpg" ) ;
                novel.setCount(0);
                novel.setFreeNum(0);
                boolean isDesc = false ;
                int chapterNum = 0 ; 
                int count = 0 ; //当前章节总字数
                boolean isChapter = false ;
                StringBuffer descBuffer = new StringBuffer() ;
                StringBuffer contentBuffer = new StringBuffer() ;
                Chapter chapter = new Chapter() ;
                int chapterPrice = Config.dao.findByKey(Config.KEY_PRICE).getValue() ; //千字价格
                int lineNum = 0 ;
                while((lineTxt = bufferedReader.readLine()) != null){
               	 if (StrKit.isBlank(lineTxt)) {
						continue ;
					}else{
						lineNum ++ ;
						if (lineTxt.replace(" ", "").startsWith("作者") && lineNum < 5 ) {
							novel.setAuthor(lineTxt.replace("作者 ", "").replace(" ", "")) ;
						}else if (lineTxt.replace(" ", "").startsWith("分类") && lineNum < 5) {
							lineTxt = lineTxt.replace("分类 ", "").replace(" ", "") ;
							Classify classify = Classify.dao.findByNameInCache(lineTxt,true) ;
							novel.setClassifyId(classify.getId()) ;
						}else if (lineTxt.replace(" ", "").startsWith("免费章节") && lineNum < 5) {
							novel.setFreeNum(Integer.parseInt(lineTxt.replace("免费章节 ", "").replace(" ", "") )) ;
						}else if (lineTxt.replace(" ", "").startsWith("简介") && lineNum < 5 ) {
							isDesc = true ;
							continue ; 
						}else if ( lineTxt.indexOf("第") != -1 && lineTxt.indexOf("章") != -1 && lineTxt.indexOf("第") < lineTxt.indexOf("章 ") && lineTxt.indexOf("章") - lineTxt.indexOf("第") < 9 ) {
							//简介结束
							if (chapterNum == 0) {
								novel.setDesc(descBuffer.toString()) ;
								descBuffer.setLength(0) ;
								isDesc = false ;
							}
							
							if (chapterNum > 0 ) {
								//上一章结束,保存上一章节的信息
								chapter.setCount(count);
								if ( chapterNum <= novel.getFreeNum() ) {
									chapter.setPrice(0);
								}else {
									chapter.setPrice( Math.round( ( chapter.getCount()*chapterPrice )/1000 ) );
								}
								chapter.setContent(contentBuffer.toString()) ;
								chapter.setIncome(0) ;
								chapter.save();
								
								novel.setCount(novel.getCount() + count ) ;
								
								contentBuffer.setLength(0) ;
								count = 0 ;
							}
							
							//新章节开始啦~
							isChapter = true ;
							chapterNum ++ ;
							
							chapter = new Chapter() ;
							chapter.setId(StrKit.getRandomUUID());
							chapter.setTitle(lineTxt.substring(lineTxt.indexOf("章 ")+1)) ;
							chapter.setNumber(chapterNum) ;
							chapter.setTime(new Date()) ;
							chapter.setNovelId(novel.getId()) ;
							if (chapterNum == 1) { //保存小说的初始信息
								novel.setFChapterId(chapter.getId());
							}
							continue ;
						}
						if (isDesc) {
							descBuffer.append( lineTxt) ;
						}else if (isChapter) {
							contentBuffer.append( "<p class='paragraph'>" + lineTxt + "</p>" );
							count = count + lineTxt.replace(" ", "").length() ;
						}
					}
                }  //while end
                read.close();
                file.delete() ;
                
                //读取结束，保存小说信息，保存最后一章的章节信息
                chapter.setCount(count);
                if ( chapterNum <= novel.getFreeNum() ) {
					chapter.setPrice(0);
				}else {
					chapter.setPrice( Math.round( ( chapter.getCount()*chapterPrice )/1000 ) );
				}
				chapter.setContent(contentBuffer.toString()) ;
				chapter.setIncome(0) ;
				chapter.save();
				
				novel.setCount(novel.getCount() + count ) ;
				novel.setLChapterId(chapter.getId());
				novel.setLChapterNum(chapter.getNumber());
				novel.setLChapterTitle(chapter.getTitle());
				novel.setUpdateTime(chapter.getTime());
				novel.save() ;
				
				Novel.dao.clearCache(novel.getId());
				//清理缓存：查询小说章节分页信息
				CacheKit.removeAll(DicUtil.CACHE_CHAPTER_NOVEL);
                
	     }else{
	         System.out.println("找不到指定的文件");
	     }
	     } catch (Exception e) {
	         System.out.println("读取文件内容出错");
	         e.printStackTrace();
	     }
		
	}
	
}

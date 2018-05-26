package poem;

import java.util.Random;

public class StringTest {

	public String getStringRandom(int length) {  
        
        String val = "";  
        Random random = new Random();  
          
        //参数length，表示生成几位随机数  
        for(int i = 0; i < length; i++) {  
              
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";  
            //输出字母还是数字  
            if( "char".equalsIgnoreCase(charOrNum) ) {  
                //输出是大写字母还是小写字母  
               // int temp = random.nextInt(2) % 2 == 0 ? 65 : 97;  
            	int temp = 97;  
                val += (char)(random.nextInt(8) + temp);  
            } else if( "num".equalsIgnoreCase(charOrNum) ) {  
                val += String.valueOf(random.nextInt(12));  
            }  
        }  
        return val;  
    }  
      
    public static void  main(String[] args) {  
    	StringTest test = new StringTest();  
        //测试  
    	for (int i = 0; i <2; i++) {
    		 System.out.println(test.getStringRandom(14));  
		}
       
        
    }  
    
    
}

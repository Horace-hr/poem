package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DicUtil {

	public static final int PAGE_SIZE = 10;

	public static final String CACHE_USER_BY_NAME = "userByName";
	
	public static final String CACHE_USER_COUNT = "userCount";

	public static final String CACHE_CLASSIFY = "classify";
	
	public static final String CACHE_CLASSIFY_PARENT = "classifyParent";
	
	public static final String CACHE_CLASSIFY_LIST = "classifyList";

	public static final String CACHE_NOVEL = "novel";
	
	public static final String CACHE_FREEBOOKS = "freeBooks";
	
	public static final String CACHE_READTIMES = "readTimes";
	
	public static final String CACHE_MAX_READTIMES = "maxReadTimes";
	
	public static final String CACHE_PAGE_NOVEL = "pageNovel";

	public static final String CACHE_CHAPTER = "chapter";
	
	public static final String CACHE_CHAPTER_NOVEL = "chapterNovel";

	public static final String CACHE_CHANNEL = "channel";

	public static final String CACHE_BUY_RECORD = "buyRecord";

	public static final String CACHE_HISTORY = "history";

	public static final String CACHE_HISTORY_LASTEST = "historyLastest";
	
	public static final String CACHE_HISTORY_LIST = "historyList";
	
	public static final String CACHE_HISTORY_USERS = "historyUsers";

	public static final String CACHE_USER_INFO = "userInfo";

	public static final String CACHE_PAGE_COMMENT = "pageComment";

	public static final String CACHE_CONFIG = "config";

	public static final String CACHE_RECHARGE = "recharge";
	
	public static final String CACHE_RECHARGE_TODAY = "rechargeToday";

	public static final String CACHE_RECHARGE_RECORD = "rechargeRecord";

	public static final String CACHE_SIGNIN = "signIn";

	public static final String CACHE_GIVING = "giving";

	public static final String CACHE_STATISTIC = "statistic";
	
	public static final String CACHE_STATISTIC_INFO = "statisticInfo";

	public static final String CACHE_VIEW_USER = "viewUser";

	public static final String CACHE_PROPERTY = "property";

	public static final String CACHE_POEM = "poem";

	public static final String CACHE_PAGE_POEM = "pagePoem";
	
	public static final String CACHE_POEM_PARAGRAPH = "poemParagraph";
	
	public static final String CACHE_ORIGINAL_POEM = "originalPoem";

	public static Map<Integer, Integer> GIVING_MAP = null;

	public static Map<Integer, Integer> getMap() {
		if (null == GIVING_MAP) {
			GIVING_MAP = new HashMap<Integer, Integer>();
			GIVING_MAP.put(3000, 0);
			GIVING_MAP.put(5000, 1000);
			GIVING_MAP.put(10000, 2500);
			GIVING_MAP.put(20000, 6000);
			GIVING_MAP.put(50000, 20000);
			GIVING_MAP.put(100000, 50000);
		}
		return GIVING_MAP;
	}

	public static int getRan(int amount) {
		int min = (int) Math.floor(amount / 10);
		int max = amount * 2;
		int res = min;
		Random random = new Random();
		do {
			res = random.nextInt(max + 1);
		} while (res >= amount / 2 && res % 7 != 0);
		return res;
	}
}

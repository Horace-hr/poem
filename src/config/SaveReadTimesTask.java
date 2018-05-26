package config;

import model.Novel;
import model.Statistic;

public class SaveReadTimesTask implements Runnable{

	@Override
	public void run() {
		Novel.dao.saveReadTimes() ;
		Statistic.dao.saveAll() ;
	}

}

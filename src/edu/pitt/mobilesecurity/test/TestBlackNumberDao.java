package edu.pitt.mobilesecurity.test;

import java.util.List;
import java.util.Random;

import edu.pitt.mobilesecurity.db.dao.BlackNumberDao;
import edu.pitt.mobilesecurity.domain.BlackNumberInfo;
import android.test.AndroidTestCase;

public class TestBlackNumberDao extends AndroidTestCase {

	public BlackNumberDao dao;
	
	@Override
	protected void setUp() throws Exception {
		dao = new BlackNumberDao(getContext());
		super.setUp();
	}
	
	public void testAdd() throws Exception {
		Random random = new Random();
		for (int i=0; i<200 ; i++) {
			int mode = random.nextInt(3)+1;
			dao.add(Long.toString(15006185580l+i), Integer.toString(mode));	
		}
		
	}
	
	public void testFind() throws Exception {
		boolean result = dao.find("15006185580");
		assertEquals(true, result);
	}
	
	public void testUpdae() throws Exception {
		dao.update("15006185580", "1");
	}
	
	public void testDelete() throws Exception {
		dao.delete("15006185580");
	}
	
	public void testFindAll() throws Exception {
		List<BlackNumberInfo> infos = dao.findAll();
		for(BlackNumberInfo info : infos) {
			System.out.println(info);
		}
	}
}

package org.yooz.test;

import android.content.Context;
import android.test.AndroidTestCase;

import org.yooz.bean.BlackNumberInfo;
import org.yooz.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

public class TestBlackNumberDao extends AndroidTestCase {
	
	private Context mContext;
	
	@Override
	protected void setUp() throws Exception {
		mContext = getContext();
		super.setUp();
	}
	public void testAdd() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		Random random = new Random();
		for (int i = 0; i < 200; i++) {
			String number = 1354550000 + i + "";
			String mode = String.valueOf(random.nextInt(3)+1);
			dao.add(number, mode);
		}
	}
	
	public void delete() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		dao.delete("1354550000");
	}
	
	public void testFind() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		String findNumber = dao.findNumber("1354550001");
		assertEquals(true, findNumber);
	}
	
	public void testFindAll() {
		BlackNumberDao dao = new BlackNumberDao(mContext);
		List<BlackNumberInfo> infos = dao.findAll();
		for (BlackNumberInfo blackNumberInfo : infos) {
			String number = blackNumberInfo.getNumber();
			String mode = blackNumberInfo.getMode();
			System.out.println(number+":"+mode);
		}
	}
}

package com.ssafy.exam.model.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ssafy.exam.model.dto.Member;
import com.ssafy.exam.model.dto.Mobile;

public class FileMobileDao implements MobileDao {
	private final File memberDataFile, mobileDataFile;
	private List<Member> members;
	private List<Mobile> mobiles;
	
	
	private static FileMobileDao instance;
	
	public static FileMobileDao getInstance() {
		if(instance == null) {
			instance = new FileMobileDao();
		}
		return instance; 
	}
	
	private FileMobileDao() {
		String path = FileMobileDao.class.getResource("/").getPath() + "member.dat";
		memberDataFile = new File(path);
		path = FileMobileDao.class.getResource("/").getPath() + "mobile.dat";
		mobileDataFile = new File(path);

		try {
			if (!memberDataFile.exists()) {
				memberDataFile.createNewFile();
			}
			if (!mobileDataFile.exists()) {
				mobileDataFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    //Q1-3. 프로젝트 실행을 위해 아래 코드를 확인하여 완성하세요.	
	public void load() {
		try (ObjectInputStream memOis = new ObjectInputStream(new FileInputStream(memberDataFile));
				ObjectInputStream mobileOis = new ObjectInputStream(new FileInputStream(mobileDataFile))) {
			members = (List) memOis.readObject();
			mobiles = (List) mobileOis.readObject();

			System.out.println("회원 정보 로딩 완료: " + members.size() + "명");
			System.out.println("핸드폰 정보 로딩 완료:" + mobiles.size() + "개");
		} catch (Exception e) {
			System.out.println("저장된 정보가 없습니다.");
			members = Collections.synchronizedList(new ArrayList<>());
			mobiles = Collections.synchronizedList(new ArrayList<>());
		}
	}

	public void save() {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(memberDataFile));
				ObjectOutputStream mobileOos = new ObjectOutputStream(new FileOutputStream(mobileDataFile))) {
			oos.writeObject(members);
			System.out.println("회원 정보 저장 완료");
			mobileOos.writeObject(mobiles);
			System.out.println("핸드폰 정보 저장 완료");
		} catch (Exception e) {
			throw new RuntimeException("정보 저장 실패", e);
		}
	}

	public void reset(Connection con) {
		synchronized (members) {
			int length = 10;
			members.clear();
			for (int i = 0; i < length; i++) {
				members.add(new Member(i, "테스트" + (length - i), "test" + i + "@ssafy.com", "1234", "USER"));
			}
			members.add(new Member(100, "관리자", "admin@ssafy.com", "1234", "ADMIN"));
			System.out.println("멤버 초기화 완료 " + members.size() + " :" + members.get(0));
		}
	}
	public void mobileReset(Connection con) {
		synchronized (mobiles) {
			mobiles.clear();
			mobiles.add(new Mobile("S26ZD95P", "갤럭시 S26", 1475900, "삼성", "white"));
			mobiles.add(new Mobile("NT550XDZ-AD5A", "갤럭시폴드", 1729000, "삼성", "black"));
			mobiles.add(new Mobile("ios-2655", "iphoneX", 1700000, "애플", "silver"));
			System.out.println("핸드폰 정보 초기화 완료 " + mobiles.size() + " :" + mobiles.get(0));
		}
	}
	@Override
	public Member login(Connection con, String email, String password) {
		for(Member member : members) {
			if(member.getEmail().equals(email) && member.getPassword().equals(password) ) {
				return member;
			}
		}
		return null;
	}

	@Override
	public List<Mobile> selectAll(Connection conn) {
		// TODO Auto-generated method stub
		return mobiles;
	}

	@Override
	public Mobile selectByCode(Connection conn, String code) {
		for(Mobile mobile : mobiles) {
			if(mobile.getCode().equals(code)) return mobile;
		}
		return null;
	}

	@Override
	public int insert(Connection conn, Mobile mobile) {
		mobiles.add(mobile);
		return 1;
	}

	@Override
	public int deleteByCode(Connection conn, String code) { 
		for(Mobile mobile : mobiles) {
			String mc = mobile.getCode();
			if(mc.equals(code)) {
				mobiles.remove(mobile);
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int update(Connection conn, Mobile mobile) {
		for(int i = 0; i < mobiles.size(); i++) {
			if(mobiles.get(i).getCode().equals(mobile.getCode())) {
				mobiles.set(i, mobile);
				return 1;
			}
		}
		return 0;
	}

}

package com.gl.givuluv.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gl.givuluv.domain.dto.CBoardDTO;
import com.gl.givuluv.domain.dto.FileDTO;
import com.gl.givuluv.domain.dto.FollowDTO;
import com.gl.givuluv.domain.dto.LikeDTO;
import com.gl.givuluv.mapper.BoardMapper;
import com.gl.givuluv.mapper.FileMapper;
import com.gl.givuluv.mapper.LikeMapper;
import com.gl.givuluv.mapper.OrgMapper;
import com.gl.givuluv.mapper.OrgapproveMapper;
import com.gl.givuluv.mapper.UserMapper;

@Service
public class CBoardServiceImpl implements CBoardService {
	@Autowired
	private BoardMapper bmapper;

	@Autowired
	private FileMapper fmapper;
	
	@Autowired
	private LikeMapper lmapper;
	
	@Autowired
	private OrgMapper omapper;
	
	@Autowired
	private UserMapper umapper;
	
	@Autowired
	private OrgapproveMapper oamapper;
	
	@Override
	public boolean regist(CBoardDTO cboard, String filenames) {
		if (bmapper.insertCampaign(cboard) == 1) {
			int cBoardnum = bmapper.getCampaignLastNumByConnectid(cboard.getConnectid());
			String[] filenameList = filenames.split(",");
			if (!filenameList[0].isEmpty()) {
				for (String systemname : filenameList) {
					FileDTO file = new FileDTO();
					file.setConnectionid(cBoardnum + "");
					file.setType('C');
					file.setSystemname(systemname);
					fmapper.insertFile(file);
				}
			}
			System.out.println("regist cboard 성공");
			return true;
		}
		System.out.println("regist cboard 실패");
		return false;
	}

	@Override
	public int getCampaignLastNumByConnectid(String connectid) {
		return bmapper.getCampaignLastNumByConnectid(connectid);
	}

	@Override
	public CBoardDTO getCampaign(int cBoardnum) {
		return bmapper.getCampaignByCBoardnum(cBoardnum);
	}
	
	@Override
	public List<CBoardDTO> getCampaignBoardListOfUser(String loginUser, int cBoardnum, int amount){
//		1. user의 카테고리 가져오기
		String categories = umapper.getUserCategoryById(loginUser);
//		2. 카테고리 ,로 나누기
		String [] categoryList = categories.split(",");
		System.out.println(categoryList);
//		3. 카테고리 갯수에 따라서 동적 쿼리문 수행하기
		return bmapper.getCampaignListByCategories(categoryList, cBoardnum, amount);
	}
	
	@Override
	public List<CBoardDTO> getCampaignList(int boardlastnum, int amount){
		return bmapper.getCampaignList(boardlastnum, amount);
	}

	@Override
	public int getlikeCount(int cBoardnum) {
		LikeDTO like = new LikeDTO();
		like.setConnectid(cBoardnum);
		like.setType('C');
		return lmapper.likeCount(like);
	}

	@Override
	public String getCategory(CBoardDTO cboard) {
		if (cboard.getType() == 'O') {
			return omapper.getCategoryByOrgid(cboard.getConnectid());
		}
		return null;
	}

	@Override
	public LikeDTO getCampaignLike(int cBoardnum, String loginUser) {
		LikeDTO like = new LikeDTO();
		like.setConnectid(cBoardnum);
		like.setUserid(loginUser);
		like.setType('C');
		return lmapper.getLike(like);
	}

	@Override
	public String getCampaignProfileFileLink(CBoardDTO cboard) {
		if(cboard.getType() == 'O') {
			FileDTO file = new FileDTO();
			file.setConnectionid(cboard.getConnectid());
			file.setType('O');
			return "/summernoteImage/"+ fmapper.getFile(file).getSystemname();
		}
		return null;
	}
	
	@Override
	public String getCampaignWriterName(CBoardDTO cboard) {
		if(cboard.getType() == 'O') {
			return omapper.getOrgById(cboard.getConnectid()).getOrgname();
		}
		return null;
	}

	@Override
	public FollowDTO getfollowOfCampaign(CBoardDTO cboard, String loginUser) {
		FollowDTO follow = new FollowDTO();
		if(cboard.getType() == 'O') {
			follow.setOrgid(cboard.getConnectid());
			follow.setUserid(loginUser);
		}
		return umapper.getfollow(follow);
	}

	@Override
	public boolean insertLike(LikeDTO like) {
		like.setType('C');
		if(lmapper.insertLike(like) == 1) {
			return true;
		}
		return false;
	}

	@Override
	public boolean cancelLike(LikeDTO like) {
		like.setType('C');
		if(lmapper.deleteByLikeDTO(like) == 1) {
			return true;
		}
		return false;
	}

	@Override
	public int isApproveOrg(String loginOrg) {
		return oamapper.isApproveOrg(loginOrg);
	}

	@Override
	public int isApproveOrgX(String loginOrg) {
		return oamapper.isApproveOrgX(loginOrg);
	}
}

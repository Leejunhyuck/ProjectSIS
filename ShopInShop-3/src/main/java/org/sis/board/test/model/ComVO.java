package org.sis.board.test.model;

import java.sql.Date;
import java.util.List;

import lombok.Data;

@Data
public class ComVO {

	
	private Integer bno;
	private String title, content,mid;
	private String category;
	private Date regdate;
	private int replycnt;
	private int comlike;
	private int viewcnt;
	
	private List<ComAttachVO> attachList;
	
}
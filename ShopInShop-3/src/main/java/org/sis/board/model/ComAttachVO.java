package org.sis.board.model;

import lombok.Data;

@Data
public class ComAttachVO {
	
	private Integer bno;
	private String fileName;
	private String uploadPath;
	private String uuid;
	private boolean image;
}

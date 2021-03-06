package org.sis.board.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.sis.board.model.ComAttachVO;
import org.sis.board.model.ComVO;
import org.sis.board.model.Criteria;
import org.sis.board.model.PageMaker;
import org.sis.board.service.ComService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;

@Controller
@RequestMapping("/community/*")
@Log
@AllArgsConstructor
@CrossOrigin
public class ComController {

	private ComService service;

	@GetMapping("/list")
	public void listPage(@ModelAttribute("cri") Criteria cri ,Model model) {
		log.info("list Page.......................");
		int totalCount = service.selectPageCount(cri);
		
		model.addAttribute("list", service.getList(cri));
		model.addAttribute("pm", new PageMaker(cri, totalCount));
	}
	
	@GetMapping("/register")
	public void registerPage() {
		log.info("Get Resiger Page................"); 
	}
	
	@PostMapping("/register")
	public String postRegiste(@ModelAttribute ComVO vo,RedirectAttributes rttr) {
		log.info("Register vo: "+vo);
		
		service.register(vo);
		rttr.addFlashAttribute("reuslt","success");
		
		return "redirect:/community/list";
	}
	
	@GetMapping({"/read","/modify"})
	public void read(@ModelAttribute("cri") Criteria cri, Model model) {
		log.info("bno: "+cri.getBno());
		
		model.addAttribute("vo", service.select(cri.getBno()));
		model.addAttribute("bnoPrevNext",service.getPrevNext(cri.getBno()));
	}
	
	@PostMapping("/modify")
	public String modify(ComVO vo,@ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
		log.info("수정: "+vo);
		service.modify(vo);
		rttr.addFlashAttribute("result", "success");
		
		return "redirect:/community/list"+cri.getLink();
	}
	
	@PostMapping("/remove")
	public String remove(@ModelAttribute("cri") Criteria cri, RedirectAttributes rttr) {
		log.info("del bno: "+cri.getBno());
		
		List<ComAttachVO> attachList = service.getAttachList(cri.getBno());
		
		if(service.remove(cri.getBno()) == 1) {
			
			deleteFiles(attachList);
		}
		
		
		rttr.addFlashAttribute("result", "success");
		cri.setPage(1);
		
		return "redirect:/community/list"+cri.getLink();
		
	}
	
	
	@GetMapping(value="/getAttachList", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<ComAttachVO>> getAttachList(Integer bno){
		log.info("getAttach: "+bno);
		return new ResponseEntity<>(service.getAttachList(bno),HttpStatus.OK);
	}
	
	private void deleteFiles(List<ComAttachVO> attachList) {
		if(attachList == null || attachList.size() == 0) {
			return;
		}
		log.info("delete attach files ...." + attachList);
		
		attachList.forEach(attach -> {
			try {
				Path file = Paths.get("C:\\upload\\com\\"+attach.getUploadPath()+"\\"+attach.getUuid()+"_"+attach.getFileName());
				Files.deleteIfExists(file);
				
				//Path thumbNail = Paths.get("C:\\upload\\"+attach.getUploadPath()+"\\s_"+attach.getUuid()+"_"+attach.getFileName());
				//Files.deleteIfExists(thumbNail);
			}catch(Exception e){
				log.info("delete file error"+ e.getMessage());
			}// end catch .. 
		}); // end for each
	}
	
	@GetMapping(value="/hotlist_com", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public ResponseEntity<List<ComVO>> gethotList(){
		
		return new ResponseEntity<>(service.hotList(),HttpStatus.OK);
	}
	
	
	


}

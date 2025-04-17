package org.zerock.FlexPaper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.FlexPaper.dto.CanvasDTO;
import org.zerock.FlexPaper.dto.ThumbnailRequestDTO;
import org.zerock.FlexPaper.service.CanvasSerivce;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/canvas")
public class CanvasController {

    private final CanvasSerivce canvasSerivce;
    private final ObjectMapper objectMapper;

    @GetMapping("/create/{bundleId}")
    public String create(@PathVariable("bundleId") Long bundleId, Model model) {
        CanvasDTO canvasDTO = canvasSerivce.getCanvasDTOByBundleId(bundleId);
        try{
            String canvasJson = objectMapper.writeValueAsString(canvasDTO);
            model.addAttribute("canvasJson", canvasJson);
        } catch (Exception e){
            e.printStackTrace();
        }

        model.addAttribute("bundleId", bundleId);
        return "canvas/create";
    }

    @PostMapping("/save")
    public String save(@RequestBody CanvasDTO canvasDTO) {

        canvasSerivce.save(canvasDTO);
        return "redirect:/bundle/detail/" + canvasDTO.getBundleId();
    }

    @PostMapping("/save-thumbnails/{bundleId}")
    public ResponseEntity<String> saveCanvasThumbnail(@PathVariable String bundleId,
                                                      @RequestBody List<ThumbnailRequestDTO> images) {
        try {
            canvasSerivce.saveCanvasThumbnails(bundleId, images);
            return ResponseEntity.ok("Thumbnails saved successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving thumbnails");
        }
    }

    /*@GetMapping("/edit/{id}")
    public String edit(@PathVariable Long pno, Model model) {
        PaperDTO paperDTO = paperService.findById(pno);
        return "canvas/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute PaperDTO paperDTO) {
        paperService.update(paperDTO);
        return "redirect:/bundle/view/{bundleId}";
    }*/
}

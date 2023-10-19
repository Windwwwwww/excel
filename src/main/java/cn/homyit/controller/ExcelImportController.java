package cn.homyit.controller;

import cn.homyit.dao.ExcelDao;
import cn.homyit.domain.ExcelAdmin;
import cn.homyit.domain.Result;
import cn.homyit.dto.UserLinkDto;
import cn.homyit.dto.UserLinkListDto;
import cn.homyit.service.ExcelImportService;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/excel")
@Validated
@PreAuthorize("@AdminService.isAdmin()")
public class ExcelImportController {
    @Autowired
    private ExcelImportService excelImportService;
    @Value("${file.upload.path}")
    private String pathName;

    @Autowired
    private ExcelDao excelDao;
    @PostMapping("/import")
    public Result importExcel(@RequestParam("file")MultipartFile file, String startTime,String endTime) throws IOException, SQLException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTime start=null;
        DateTime end=null;
        if(startTime!=null&&endTime!=null) {
            start = DateUtil.parse(startTime, pattern);
            end = DateUtil.parse(endTime, pattern);
        }

        String filename=file.getOriginalFilename();
        return excelImportService.generateDataTable(filename,file,start,end);
    }

    @GetMapping("/getExcelList")
    public Result getExcelList()  {
       return excelImportService.getExcelList();
    }

    @GetMapping("/getExcel/{saveName}")
    public Result getExcel(@PathVariable String saveName) throws IOException {
        return excelImportService.getExcel(saveName);
    }

    @PostMapping ("/sendOne")
    public Result sendOneLink(@RequestBody @Valid UserLinkDto userLinkDto, BindingResult bindingResult){
        return excelImportService.sendOneLink(userLinkDto);

    }

    @PostMapping("/sendLinks")
    public Result sendLinks(@RequestBody @Valid UserLinkListDto userLinkDtos,BindingResult  bindingResult){
        return excelImportService.sendLinks(userLinkDtos);

    }




}

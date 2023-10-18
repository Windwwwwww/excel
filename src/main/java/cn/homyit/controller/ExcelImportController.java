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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/excel")
@PreAuthorize("@AdminService.isAdmin()")
public class ExcelImportController {
    @Autowired
    private ExcelImportService excelImportService;
    @Value("${file.upload.path}")
    private String pathName;

    @Autowired
    private ExcelDao excelDao;
    @PostMapping("import")
    public Result importExcel(@RequestParam("file")MultipartFile file, String startTime,String endTime) throws IOException, SQLException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        DateTime start=DateUtil.parse(startTime,pattern);
        DateTime end=DateUtil.parse(endTime,pattern);
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
    public Result sendOneLink(@RequestBody UserLinkDto userLinkDto){
        return excelImportService.sendOneLink(userLinkDto);

    }

    @PostMapping("/sendLinks")
    public Result sendLinks(@RequestBody UserLinkListDto userLinkDtos){
        return excelImportService.sendLinks(userLinkDtos.getUserLinkDtos());

    }




}

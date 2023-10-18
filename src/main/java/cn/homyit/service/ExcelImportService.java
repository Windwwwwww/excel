package cn.homyit.service;

import cn.homyit.domain.ExcelAdmin;
import cn.homyit.domain.Result;
import cn.homyit.domain.UserLink;
import cn.homyit.dto.UserLinkDto;
import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface ExcelImportService extends IService<ExcelAdmin>{

    Result generateDataTable(String filename, MultipartFile file, DateTime startTime,DateTime endTime) throws IOException, SQLException;
    Result getExcel(String saveName) throws IOException;

    Result sendOneLink(UserLinkDto userLinkDto);

    Result getExcelList();

    Result sendLinks(List<UserLinkDto> userLinkDtos);
}

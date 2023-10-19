package cn.homyit.service.impl;

import cn.homyit.dao.ExcelDao;
import cn.homyit.dao.UserLinkDao;
import cn.homyit.domain.Code;
import cn.homyit.domain.ExcelAdmin;
import cn.homyit.domain.Result;
import cn.homyit.domain.UserLink;
import cn.homyit.dto.UserLinkDto;
import cn.homyit.dto.UserLinkListDto;
import cn.homyit.exception.SystemException;
import cn.homyit.service.ExcelImportService;
import cn.homyit.utils.BeanCopyUtils;
import cn.homyit.utils.PinyinUtils;
import cn.homyit.utils.RedisCache;
import cn.homyit.utils.SecurityUtils;
import cn.homyit.vo.ExcelListVo;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.convert.RedisData;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ExcelImportImpl extends ServiceImpl<ExcelDao,ExcelAdmin> implements ExcelImportService {

    @Value("${file.upload.path}")
    private String pathName;

    @Autowired
    private ExcelDao excelDao;


    @Autowired
    private UserLinkDao userLinkDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    @Transactional(readOnly =false,rollbackFor = {Exception.class})
    public Result generateDataTable(String fileName, MultipartFile file, DateTime startTime,DateTime endTime) throws IOException, SQLException {

        if (!fileName.matches("^.+\\.(?i)(xls)$") && !fileName.matches("^.+\\.(?i)(xlsx)$")) {
            throw new SystemException(Code.IMPORT_ERR,"上传文件格式不正确");
        }
        boolean isExcel2003 = true;
        if (fileName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        //文件起别名
        String tempName = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        String saveName= tempName+(isExcel2003?".xls":".xlsx");
        save(new ExcelAdmin(saveName,fileName,startTime,endTime));
        //上传excel到服务器
        File upload=new File(pathName);
        //没有就创建
        if (!upload.exists()){
            upload.mkdirs();
        }
        file.transferTo(new File(upload,saveName));

        List<String> columnNames=getColumnNamesFromExcel(pathName+saveName);
        createTable(tempName,columnNames);
        insertData(saveName,tempName,columnNames);

        List<List<String>> dataTable=getDataTable(pathName+saveName,isExcel2003);

        Map<String,Object> map=new HashMap<>();
        map.put("table",dataTable);
        map.put("saveName",saveName);

        return new Result(Code.IMPORT_OK,map);
    }

    @Override
    public Result getExcel(String saveName) throws IOException {
        boolean isExcel2003=true;
        if (saveName.matches("^.+\\.(?i)(xlsx)$")) {
            isExcel2003 = false;
        }
        List<List<String>> dataTable=getDataTable(pathName+saveName,isExcel2003);
        return new Result(Code.IMPORT_OK,dataTable);

    }

    @Override
    @Transactional(readOnly = false,rollbackFor = Exception.class)
    public Result sendOneLink(UserLinkDto userLinkDto) {
        String pattern="yyyy-MM-dd HH:mm:ss";
        DateTime start=null;
        DateTime end=null;
        if(userLinkDto.getStartTime()!=null&&userLinkDto.getEndTime()!=null){
            start= DateUtil.parse(userLinkDto.getStartTime(),pattern);
            end=DateUtil.parse(userLinkDto.getEndTime(),pattern);
        }
        String[] t= userLinkDto.getSaveName().split("\\.");
        String tempName=t[0];
        //生成链接格式为http://selfinfo/表名/userid
        String url=new StringBuilder("https://selfinfo/").append(tempName).append("/").append(userLinkDto.getUserId()).toString();
        userLinkDao.insert(new UserLink(Integer.parseInt(userLinkDto.getUserId()),url, SecurityUtils.getUserId(),start,end));
//        userLinkDao.insert(new UserLink(Integer.parseInt(userLinkDto.getUserId()),url,1,start,end));

        return new Result(Code.IMPORT_OK,"发送成功");


    }
    //界面上设计一个选定主键的功能，用作发送信息时的userid来定位发送给谁消息，可以记录下对应的列号

    @Override
    public Result getExcelList() {
        LambdaQueryWrapper<ExcelAdmin> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ExcelAdmin::getCreateId, SecurityUtils.getUserId());
//        lambdaQueryWrapper.eq(ExcelAdmin::getCreateId,1);

        List<ExcelAdmin> excelAdminList= excelDao.selectList(lambdaQueryWrapper);
        List<ExcelListVo> excelListVos=excelAdminList.stream()
                .map(excelAdmin -> BeanCopyUtils.copyBean(excelAdmin,ExcelListVo.class))
                .collect(Collectors.toList());

        return new Result(Code.IMPORT_OK,excelAdminList);
    }

    @Override
    @Transactional(readOnly = false,rollbackFor = Exception.class)
    public Result sendLinks(UserLinkListDto userLinkDtos) {
        //TODO 记得做判空
        int sendBy= SecurityUtils.getUserId();
//        int sendBy=1;
        String saveName=userLinkDtos.getSaveName();
        String[] t=saveName.split("\\.");
        String tempName=t[0];
        String pattern="yyyy-MM-dd HH:mm:ss";
        DateTime start;
        DateTime end;
        if(userLinkDtos.getStartTime()!=null&&userLinkDtos.getEndTime()!=null){
            start= DateUtil.parse(userLinkDtos.getStartTime(),pattern);
            end=DateUtil.parse(userLinkDtos.getEndTime(),pattern);
        } else {
            end = null;
            start = null;//放到这里是因为下面的Lambda语句会报错
        }
        StringBuilder s=new StringBuilder("https://selfinfo/").append(tempName).append("/");
        List<UserLink> userLinks=userLinkDtos.getUserIdList().stream()
                .map(userid -> new UserLink(Integer.parseInt(userid),s.append(userid).toString(),sendBy,start,end))
                .collect(Collectors.toList());
        userLinks.stream()
                .map(userLink -> userLinkDao.insert(userLink))
                .collect(Collectors.toList());
        return new Result(Code.IMPORT_OK,"发送成功");
    }

    public List<List<String>> getDataTable(String filePath,boolean isExcel2003) throws IOException {
        List<List<String>> dataTable = new ArrayList<>();
        FileInputStream inputStream=new FileInputStream(filePath);
        Workbook wb = null;
        if (isExcel2003) {
            wb = new HSSFWorkbook(inputStream);
        } else {
            wb = new XSSFWorkbook(inputStream);
        }
        Sheet sheet = wb.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            List<String> rowData = new ArrayList<>();

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowData.add(getCellValue(cell));
            }

            dataTable.add(rowData);
        }

        wb.close();
        inputStream.close();
        return dataTable;
    }

    private String getCellValue(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf(cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return "";
        }
    }

    private List<String> getColumnNamesFromExcel(String filepath) throws IOException {
        //TODO 这里需要加一个表格格式合法性校验
        List<String> columnNames = new ArrayList<>();
        FileInputStream fileInputStream=new FileInputStream(filepath);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        Row headerRow = sheet.getRow(0);
        Iterator<Cell> cellIterator = headerRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String name=cell.getStringCellValue();
            String englishName= new String();
            englishName=redisCache.getCacheMapValue("chinese:english",name);
            if(englishName==null){
                englishName=PinyinUtils.convertToPinyin(name);
                if(!englishName.equals(name)){
                    redisCache.setCacheMapValue("chinese:english",name,englishName);
                    redisCache.setCacheMapValue("english:chinese",englishName,name);
                }

            }
            columnNames.add(englishName);
        }

        workbook.close();
        fileInputStream.close();

        return columnNames;
    }
    /**
     *
     * @param tableName
     * @param columnNames
     * @throws SQLException
     * 这里是用的中文作为字段名，映射成英文更合适，目前想到的解决方案是用第三方utils把中文字段翻译成英文再建表，
     * 然后把中文和英文的映射关系存到redis数据库中，这样下一次遇到相同的中文字段时可以直接从redis数据库中拿，不需要再次调用工具类转换
     * 防止部分多音字转换错误
     */
    private void createTable(String tableName, List<String> columnNames) throws SQLException {
        //tableName是英文和数字不需要转换
        StringBuilder sql = new StringBuilder("CREATE TABLE ").append(tableName).append(" (");
        for (String columnName : columnNames) {
            sql.append(columnName).append(" VARCHAR(255), ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            statement.executeUpdate();
        }
    }

    private void insertData(String saveName,String tableName, List<String> columnNames) throws IOException, SQLException {
        String path=pathName+saveName;
        FileInputStream fileInputStream=new FileInputStream(path);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        for (String columnName : columnNames) {
            sql.append(columnName).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(") VALUES (");
        for (int i = 0; i < columnNames.size(); i++) {
            sql.append("?, ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(")");

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                //PreparedStatement参数索引从1开始
                for (int columnIndex = 0; columnIndex < columnNames.size(); columnIndex++) {
                    Cell cell = row.getCell(columnIndex);
                    statement.setString(columnIndex + 1, getCellValue(cell));
                }
                statement.addBatch();//将该行添加到批处理中
            }
            statement.executeBatch();
        }catch(NullPointerException e){
            e.printStackTrace();
            throw new SystemException(Code.SYSTEM_ERR,"表格格式不合法,表头字段名不能为空");
        }

        workbook.close();
        fileInputStream.close();
    }


}

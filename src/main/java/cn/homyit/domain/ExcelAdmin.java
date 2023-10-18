package cn.homyit.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_excel")
public class ExcelAdmin implements Serializable {
    private int SId;

    private String saveName;
    private String fileName;
    @TableField(fill= FieldFill.INSERT)
    private int createId;

    private Date startTime;
    private Date endTime;

    public ExcelAdmin(String saveName, String fileName, DateTime startTime, DateTime endTime) {
        this.saveName = saveName;
        this.fileName = fileName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

}

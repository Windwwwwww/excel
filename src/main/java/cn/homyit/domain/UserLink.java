package cn.homyit.domain;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_userlink")
public class UserLink {

    private int userId;
    private String link;
    @TableField(fill= FieldFill.INSERT)
    private int sendBy;


    private Date startTime;
    private Date endTime;
}

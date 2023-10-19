package cn.homyit.dto;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLinkDto {

    @NotEmpty(message = "用户id不能为空")
    private String userId;
    private String saveName;
    private int sendBy;

    private String  startTime;
    private String  endTime;
}

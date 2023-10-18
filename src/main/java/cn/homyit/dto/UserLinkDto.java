package cn.homyit.dto;

import cn.hutool.core.date.DateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLinkDto {
    private String userId;
    private String saveName;
    private int sendBy;

    private String  startTime;
    private String  endTime;
}

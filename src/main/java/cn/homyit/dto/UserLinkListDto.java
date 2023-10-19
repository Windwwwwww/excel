package cn.homyit.dto;

import io.netty.util.concurrent.ImmediateEventExecutor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
@Data
public class UserLinkListDto {
   @Size(min=1,message = "用户id不能为空")
    private List<String> userIdList;
    private String saveName;
    private int sendBy;

    private String  startTime;
    private String  endTime;
}

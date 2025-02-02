package com.cn.bdth.api;

import com.cn.bdth.dto.PutExchangeDto;
import com.cn.bdth.dto.ServerConfigDto;
import com.cn.bdth.dto.admin.AnnouncementDto;
import com.cn.bdth.dto.admin.UserPutDto;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.DrawingService;
import com.cn.bdth.service.ServerService;
import com.cn.bdth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员权限接口
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ServerService serverService;

    private final UserService userService;

    private final DrawingService drawingService;

    /**
     * 生成兑换码
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/exchange/build", name = "生成对话码", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result buildExchangeCode(@RequestBody @Validated final PutExchangeDto dto) {
        serverService.buildRedemptionCode(dto);
        return Result.ok();
    }


    /**
     * 更新服务器配置参数
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/server/put/config", name = "用于保存或更新服务器参数", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result heavyLoadServerConfig(@RequestBody @Validated final ServerConfigDto dto) {
        serverService.heavyLoadDisposition(dto);
        return Result.ok();
    }

    /**
     * 获取服务器配置
     *
     * @return the bot configuration
     */
    @GetMapping(value = "/server/get/config", name = "获取服务器配置", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getServerConfig() {
        return Result.data(serverService.getDisposition());
    }

    /**
     * 分页获取用户信息
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the bot configuration
     */
    @GetMapping(value = "/user/get/page", name = "分页获取用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserPages(@RequestParam(defaultValue = "1") final int pageNum, final String prompt) {
        return Result.data(
                userService.getUserPageVo(pageNum, prompt)
        );
    }

    /**
     * 修改用户信息
     *
     * @param dto the dto
     * @return the bot configuration
     */
    @PostMapping(value = "/user/put/data", name = "修改用户数据信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result updateUserById(@RequestBody @Validated final UserPutDto dto) {
        userService.updateById(dto);
        return Result.ok();
    }

    /**
     * 分页获取兑换码
     *
     * @param pageNum the page num
     * @param prompt  the prompt
     * @return the result
     */
    @GetMapping(value = "/exchange/get/page", name = "兑换码列表", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result redemptionCodePage(@RequestParam(defaultValue = "1") int pageNum, @RequestParam final String prompt) {
        return Result.data(serverService.getRedemptionCodePage(pageNum, prompt));
    }

    /**
     * 根据ID删除兑换码
     *
     * @param id the id
     * @return the result
     */
    @PostMapping(value = "/exchange/delete/{id}", name = "删除兑换码", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteExchangeById(@PathVariable final Long id) {
        serverService.deleteRedemptionCodeBasedOnTheId(id);
        return Result.ok();
    }

    /**
     * 获取绘图列表分页
     *
     * @param pageNum the pageNum
     * @return the result
     */
    @GetMapping(value = "/drawing/page", name = "获取绘图分页", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getDrawingPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(drawingService.getDrawingPage(pageNum));
    }

    /**
     * 设置公共状态
     *
     * @return the result
     */
    @PostMapping(value = "/drawing/status/{id}", name = "设置绘图状态", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putDrawingStatus(@PathVariable final Long id) {
        drawingService.putPublicDrawingOps(id);
        return Result.ok();
    }

    /**
     * 更新公告
     *
     * @return the result
     */
    @PostMapping(value = "/put/announcement", name = "更新公告", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putAnnouncement(@RequestBody @Validated final AnnouncementDto dto) {
        serverService.putAnnouncement(dto);
        return Result.ok();
    }
}

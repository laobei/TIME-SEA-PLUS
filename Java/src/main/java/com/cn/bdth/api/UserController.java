package com.cn.bdth.api;


import com.cn.bdth.dto.StarDialogueDto;
import com.cn.bdth.exceptions.UploadException;
import com.cn.bdth.msg.Result;
import com.cn.bdth.service.DrawingService;
import com.cn.bdth.service.StarService;
import com.cn.bdth.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 小程序 (用户操作性接口) 非公开
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final StarService starService;

    private final DrawingService drawingService;

    /**
     * 当前用户信息结果。
     *
     * @return the result
     */
    @PostMapping(value = "/current/info", name = "用户当前用户信息", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result currentUserInfo() {

        return Result.data(userService.getCurrentUserInfo());

    }


    /**
     * 用户更新头像
     *
     * @param avatar the avatar
     * @return the result
     */
    @PostMapping(value = "/upload/avatar", name = "用户更新头像", consumes = "multipart/form-data")
    public Result userUploadAvatar(@Valid @NotNull(message = "用户头像不能为空") final MultipartFile avatar) {
        try {
            userService.editUserAvatar(avatar);
            return Result.ok();
        } catch (UploadException e) {
            return Result.error(e.getMessage());
        }

    }

    /**
     * 修改用户昵称
     *
     * @param userName the username
     * @return the result
     */
    @PostMapping(value = "/upload/username", name = "用户更新用户名", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result userUpdateName(@Validated @NotBlank(message = "用户昵称不能为空") @RequestParam final String userName) {
        userService.editUserName(userName);
        return Result.ok();
    }


    /**
     * 分页获取收藏
     *
     * @return the result
     */
    @GetMapping(value = "/star/page", name = "分页获取收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getStarPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(starService.getUserStarVoPage(pageNum));
    }


    /**
     * 删除指定收藏
     *
     * @return the result
     */
    @PostMapping(value = "/star/delete/{id}", name = "删除指定收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result deleteStarById(@PathVariable final Long id) {
        starService.deleteById(id);
        return Result.ok();
    }


    /**
     * 查看指定收藏
     *
     * @return the result
     */
    @GetMapping(value = "/stat/get/data", name = "查看指定收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getStarDetailById(@RequestParam(required = true) final Long starId) {

        return Result.data(starService.getUserStarDetail(starId));
    }


    /**
     * 添加收藏
     *
     * @return the result
     */
    @PostMapping(value = "/stat/put/data", name = "添加收藏", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result putStarDialogue(@RequestBody @Validated final StarDialogueDto dto) {
        return Result.data(starService.starDialogue(dto));
    }


    /**
     * 获取我的作品管理
     *
     * @return the result
     */
    @GetMapping(value = "/drawing/page", name = "获取我的作品", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result getUserOpsPage(@RequestParam(defaultValue = "1") final int pageNum) {
        return Result.data(drawingService.getUserDrawingOpsPage(pageNum));
    }
}

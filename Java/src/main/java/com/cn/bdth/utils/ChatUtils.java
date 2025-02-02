package com.cn.bdth.utils;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cn.bdth.constants.OperateConstant;
import com.cn.bdth.dto.GptDto;
import com.cn.bdth.entity.User;
import com.cn.bdth.exceptions.FrequencyException;
import com.cn.bdth.mapper.UserMapper;
import com.cn.bdth.model.GptModel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * BOT工具类
 *
 * @author 时间海 @github dulaiduwang003
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
@Data
public class ChatUtils {


    @Value("${sensitive}")
    private String regex;

    @Value("${isSensitive}")
    private Boolean isSensitive;

    @Value("${enable-gpt}")
    private Boolean enableGpt;

    private final UserMapper userMapper;

    private final RedisUtils redisUtils;

    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isSusceptible(final String data) {
        // 将字符串中的英文转换为大写并去除所有空格
        String processedInput = data.toUpperCase().replaceAll("\\s+", "");
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(processedInput);
        return matcher.find();
    }

    public List<GptModel.Messages> conversionStructure(final GptDto dto) {
        List<GptModel.Messages> messages;
        try (Stream<GptDto.Context> contextStream = dto.getContext().stream()) {
            messages = contextStream.parallel()
                    .flatMap(context -> Stream.of(
                            new GptModel.Messages().setRole("user").setContent(context.getQuestion()),
                            new GptModel.Messages().setRole("system").setContent(context.getAnswer())
                    ))
                    .collect(Collectors.toList());
        }
        messages.add(
                new GptModel.Messages().setRole("user").setContent(dto.getPrompt())
        );
        return messages;
    }

    public void deplete(final Long requiredFrequency, final Long userId) {
        final User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda().eq(User::getUserId, userId)
                .select(User::getFrequency, User::getUserId)
        );
        if (user.getFrequency() < requiredFrequency) {
            throw new FrequencyException();
        }
        userMapper.update(null, new UpdateWrapper<User>()
                .lambda()
                .eq(User::getUserId, userId)
                .setSql("frequency = frequency -" + requiredFrequency)
        );
    }

    public void compensate(final Long requiredFrequency, final Long userId) {
        userMapper.update(null, new UpdateWrapper<User>()
                .lambda()
                .eq(User::getUserId, userId)
                .setSql("frequency = frequency + " + requiredFrequency)
        );
    }

    @Async
    public void lastOperationTime(final Long userId) {
        //设置当前最后操作时间
        redisUtils.setValueTimeout(OperateConstant.USER_CALL_TIME + userId, LocalDateTime.now(), 604800);
    }
}

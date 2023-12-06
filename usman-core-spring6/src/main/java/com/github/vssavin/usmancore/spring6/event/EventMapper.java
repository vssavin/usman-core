package com.github.vssavin.usmancore.spring6.event;

import com.github.vssavin.usmancore.event.EventDto;
import com.github.vssavin.usmancore.spring6.user.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper to convert between event entity and event dto.
 *
 * @author vssavin on 06.12.2023.
 */
@Mapper(componentModel = "spring")
interface EventMapper {

    UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "user", expression = "java(userMapper.toDto(event.getUser()))")
    EventDto toDto(Event event);

    @Mapping(target = "user", expression = "java(userMapper.toEntity(eventDto.getUser()))")
    Event toEntity(EventDto eventDto);

}

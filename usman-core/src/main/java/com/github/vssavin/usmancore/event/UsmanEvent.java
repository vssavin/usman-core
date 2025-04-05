package com.github.vssavin.usmancore.event;

import com.github.vssavin.usmancore.user.UsmanUser;

import java.util.Date;

public interface UsmanEvent {

    Long getId();

    void setId(Long id);

    Long getUserId();

    void setUserId(Long userId);

    Date getEventTimestamp();

    void setEventTimestamp(Date eventTimestamp);

    String getEventMessage();

    void setEventMessage(String eventMessage);

    EventType getEventType();

    void setEventType(EventType eventType);

    UsmanUser getUser();

}

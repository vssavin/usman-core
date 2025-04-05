package com.github.vssavin.usmancore.event;

import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.querydsl.core.types.Predicate;

public interface UsmanEventService {

    EventDto addUserEvent(UsmanUser user, EventType eventType, String eventMessage);

    Paged<EventDto> findEvents(EventFilter eventFilter, int pageNumber, int pageSize);

    Predicate eventFilterToPredicate(EventFilter eventFilter);

}

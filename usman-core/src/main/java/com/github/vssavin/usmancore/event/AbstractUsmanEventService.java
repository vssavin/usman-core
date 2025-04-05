package com.github.vssavin.usmancore.event;

import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.data.pagination.Paging;
import com.github.vssavin.usmancore.exception.event.EventServiceException;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.function.Function;

public abstract class AbstractUsmanEventService implements UsmanEventService {

    public abstract UsmanEvent createEvent(UsmanUser user, EventType eventType, String eventMessage, UsmanEvent event);

    public abstract Page<UsmanEvent> findAll(Pageable pageable);

    public abstract Page<UsmanEvent> findAll(Predicate predicate, Pageable pageable);

    public abstract Function<UsmanEvent, EventDto> toDtoFunction();

    @Override
    public EventDto addUserEvent(UsmanUser user, EventType eventType, String eventMessage) {
        try {
            UsmanEvent event = createEvent(user, eventType, eventMessage, null);
            user.addEvent(event);
            return toDtoFunction().apply(event);
        }
        catch (Exception e) {
            String errorMessage = String.format(
                    "An error occurred while creating event [user=%s][eventType=%s][message=%s]", user, eventType,
                    eventMessage);
            throw new EventServiceException(errorMessage, e);
        }
    }

    @Override
    public Paged<EventDto> findEvents(EventFilter eventFilter, int pageNumber, int pageSize) {
        String message = String.format(
                "An error occurred while searching for events with params: pageNumber = %d, pageSize = %d, filter: [%s]!",
                pageNumber, pageSize, eventFilter);
        Pageable pageable;
        try {
            pageable = PageRequest.of(pageNumber - 1, pageSize);

            Page<UsmanEvent> list = new PageImpl<>(Collections.emptyList());

            if (eventFilter == null || eventFilter.isEmpty()) {
                list = findAll(pageable);
            }
            else {
                Predicate predicate = eventFilterToPredicate(eventFilter);
                if (predicate != null) {
                    list = findAll(predicate, pageable);
                }
            }

            Page<EventDto> events = list.map(this.toDtoFunction());

            return new Paged<>(events, Paging.of(events.getTotalPages(), pageNumber, pageSize));
        }
        catch (Exception e) {
            throw new EventServiceException(message, e);
        }
    }

    // @Override
    // public Predicate eventFilterToPredicate(EventFilter eventFilter) {
    // return null;
    // }

}

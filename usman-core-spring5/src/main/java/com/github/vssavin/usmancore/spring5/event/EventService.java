package com.github.vssavin.usmancore.spring5.event;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.data.pagination.Paging;
import com.github.vssavin.usmancore.event.EventDto;
import com.github.vssavin.usmancore.event.EventFilter;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.exception.event.EventServiceException;
import com.github.vssavin.usmancore.spring5.user.User;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Main implementation of event service.
 *
 * @author vssavin on 07.12.2023.
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    @UsmanRouteDatasource
    public EventDto createEvent(User user, EventType eventType, String eventMessage) {
        try {
            Event event = new Event(user.getId(), eventType, new Timestamp(System.currentTimeMillis()), eventMessage,
                    user);
            user.getEvents().add(event);
            return eventMapper.toDto(event);
        }
        catch (Exception e) {
            String errorMessage = String.format(
                    "An error occurred while creating event [user=%s][eventType=%s][message=%s]", user, eventType,
                    eventMessage);
            throw new EventServiceException(errorMessage, e);
        }
    }

    @Transactional
    @UsmanRouteDatasource
    public Paged<EventDto> findEvents(EventFilter eventFilter, int pageNumber, int pageSize) {
        String message = String.format(
                "An error occurred while searching for events with params: pageNumber = %d, pageSize = %d, filter: [%s]!",
                pageNumber, pageSize, eventFilter);
        Pageable pageable;
        try {
            pageable = PageRequest.of(pageNumber - 1, pageSize);

            Page<Event> list = new PageImpl<>(Collections.emptyList());

            if (eventFilter == null || eventFilter.isEmpty()) {
                list = eventRepository.findAll(pageable);
            }
            else {
                Predicate predicate = eventFilterToPredicate(eventFilter);
                if (predicate != null) {
                    list = eventRepository.findAll(predicate, pageable);
                }
            }

            Page<EventDto> events = list.map(eventMapper::toDto);

            return new Paged<>(events, Paging.of(events.getTotalPages(), pageNumber, pageSize));
        }
        catch (Exception e) {
            throw new EventServiceException(message, e);
        }

    }

    Predicate eventFilterToPredicate(EventFilter eventFilter) {
        QEvent event = QEvent.event;
        BooleanExpression expression = null;
        List<BooleanExpression> expressions = new ArrayList<>();
        if (eventFilter.getEventId() != null) {
            expressions.add(event.id.eq(eventFilter.getEventId()));
        }

        if (eventFilter.getUserId() != null) {
            expressions.add(event.userId.eq(eventFilter.getUserId()));
        }

        if (eventFilter.getUserLogin() != null && !eventFilter.getUserLogin().isEmpty()) {
            expressions.add(event.user.login.eq(eventFilter.getUserLogin()));
        }

        if (eventFilter.getEventType() != null) {
            expressions.add(event.eventType.eq(eventFilter.getEventType()));
        }

        if (eventFilter.getStartEventTimestamp() != null) {
            expressions.add(event.eventTimestamp.between(eventFilter.getStartEventTimestamp(),
                    eventFilter.getEndEventTimestamp()));
        }

        for (BooleanExpression expr : expressions) {
            if (expression == null) {
                expression = expr;
            }
            else {
                expression = expression.eq(expr);
            }
        }

        return expression;
    }

}

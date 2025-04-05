package com.github.vssavin.usmancore.spring5.event;

import com.github.vssavin.usmancore.aspect.UsmanRouteDatasource;
import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.event.*;
import com.github.vssavin.usmancore.user.UsmanUser;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Main implementation of event service.
 *
 * @author vssavin on 07.12.2023.
 */
@Service
public class EventService extends AbstractUsmanEventService {

    private final EventRepository eventRepository;

    private final EventMapper eventMapper;

    @Autowired
    public EventService(EventRepository eventRepository, EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public UsmanEvent createEvent(UsmanUser user, EventType eventType, String eventMessage, UsmanEvent event) {
        return new Event(user.getId(), eventType, new Timestamp(System.currentTimeMillis()), eventMessage, user);
    }

    @Override
    public Page<UsmanEvent> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable).map(e -> e);
    }

    @Override
    public Page<UsmanEvent> findAll(Predicate predicate, Pageable pageable) {
        return eventRepository.findAll(predicate, pageable).map(e -> e);
    }

    @Override
    public Function<UsmanEvent, EventDto> toDtoFunction() {
        return event -> eventMapper.toDto((Event) event);
    }

    @Transactional
    @UsmanRouteDatasource
    @Override
    public EventDto addUserEvent(UsmanUser user, EventType eventType, String eventMessage) {
        return super.addUserEvent(user, eventType, eventMessage);
    }

    @Transactional
    @UsmanRouteDatasource
    @Override
    public Paged<EventDto> findEvents(EventFilter eventFilter, int pageNumber, int pageSize) {
        return super.findEvents(eventFilter, pageNumber, pageSize);
    }

    @Override
    public Predicate eventFilterToPredicate(EventFilter eventFilter) {
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

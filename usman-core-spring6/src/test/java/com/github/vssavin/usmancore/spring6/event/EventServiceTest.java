package com.github.vssavin.usmancore.spring6.event;

import com.github.vssavin.usmancore.data.pagination.Paged;
import com.github.vssavin.usmancore.event.EventDto;
import com.github.vssavin.usmancore.event.EventFilter;
import com.github.vssavin.usmancore.event.EventType;
import com.github.vssavin.usmancore.event.UsmanEventService;
import com.github.vssavin.usmancore.exception.event.EventServiceException;
import com.github.vssavin.usmancore.spring6.user.User;
import com.querydsl.core.types.Predicate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author vssavin on 07.12.2023
 */
@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    private final EventMapper eventMapper = Mappers.getMapper(EventMapper.class);

    private final EventFilter containsLoginFilter = new EventFilter(null, null, "login", null, null, null);

    private Predicate containsUserEventPredicate;

    @Mock
    private EventRepository eventRepository;

    private UsmanEventService eventService;

    @Before
    public void setUp() {
        eventService = new EventService(eventRepository, eventMapper);
        List<Event> eventList = new ArrayList<>();
        eventList.add(new Event());
        containsUserEventPredicate = eventService.eventFilterToPredicate(containsLoginFilter);
        when(eventRepository.findAll(eq(containsUserEventPredicate), any(Pageable.class)))
            .thenReturn(new PageImpl<>(eventList));
        when(eventRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(eventList));
    }

    @Test
    public void shouldAddUserEventSuccess() {
        User user = new User("", "", "", "", "");
        EventDto eventDto = eventService.addUserEvent(user, EventType.LOGGED_IN, "User created");
        Event event = eventMapper.toEntity(eventDto);
        Assert.assertEquals("User should be equal!", user, event.getUser());
    }

    @Test(expected = EventServiceException.class)
    public void shouldThrowException_WhenAddUserEventWithWrongUser() {
        eventService.addUserEvent(null, EventType.LOGGED_OUT, "");
    }

    @Test
    public void shouldFindEventsSuccess() {
        Paged<EventDto> pagedEventDto = eventService.findEvents(containsLoginFilter, 1, 5);
        Assert.assertFalse("Should contain at least one event", pagedEventDto.getPage().getContent().isEmpty());
    }

    @Test
    public void shouldFindAllWithPredicateOnlyOneTime_WhenPredicatePassed() {
        Pageable pageable = Pageable.ofSize(5);
        eventService.findEvents(containsLoginFilter, 1, pageable.getPageSize());
        verify(eventRepository, times(1)).findAll(containsUserEventPredicate, pageable);
    }

    @Test
    public void shouldFindAllWithoutPredicateOnlyOneTime_WhenOnlyPageablePassed() {
        Pageable pageable = Pageable.ofSize(5);
        eventService.findEvents(EventFilter.emptyEventFilter(), 1, pageable.getPageSize());
        verify(eventRepository, times(1)).findAll(pageable);
    }

    @Test(expected = EventServiceException.class)
    public void shouldThrowException_WhenWrongPageNumberSpecified() {
        EventFilter filter = EventFilter.emptyEventFilter();
        eventService.findEvents(filter, 0, 5);
    }

}

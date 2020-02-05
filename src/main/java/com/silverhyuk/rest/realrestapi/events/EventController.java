package com.silverhyuk.rest.realrestapi.events;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {

    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid EventDto eventDto, Errors errors) {

        if(errors.hasErrors()) {
            //return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }

        eventValidator.validate(eventDto, errors);
        if(errors.hasErrors()) {
            //return ResponseEntity.badRequest().build();
            return ResponseEntity.badRequest().body(errors);
        }

        Event event  = modelMapper.map(eventDto, Event.class);
        event.update();
        Event newEvent = eventRepository.save(event);
        WebMvcLinkBuilder linkBuilder = linkTo(EventController.class).slash(newEvent.getId());
        URI createdUri = linkBuilder.toUri();

        EventResource eventResource = new EventResource(event);
        eventResource.add(linkTo(EventController.class).withRel("query-events"));
        eventResource.add(linkBuilder.withSelfRel());
        eventResource.add(linkBuilder.withRel("update-event"));
        return ResponseEntity.created(createdUri).body(eventResource);
    }
}

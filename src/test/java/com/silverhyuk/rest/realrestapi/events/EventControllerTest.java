package com.silverhyuk.rest.realrestapi.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EventControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void createEvent() throws Exception{
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Rest API Devrlopments with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 19, 00, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 20, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 01, 19, 14, 00))
                .endEventDateTime(LocalDateTime.of(2019, 01, 20, 23, 59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 2번 출구")
                .build();

        mockMvc.perform(post("/api/events/")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .accept(MediaTypes.HAL_JSON)
                    .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(Matchers.not(true)))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
        ;

    }

    /**
     * 프로퍼티 추가
     * spring.jackson.deserialization.fail-on-unknown-properties=true
     *  > 규칙 강화 - 알수 없는 입력값이 오면 badRequest로 응답
     *
     * @throws Exception
     */
    @Test
    public void createEvent_bad_request() throws Exception{
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Rest API Devrlopments with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 19, 00, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 20, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 01, 19, 14, 00))
                .endEventDateTime(LocalDateTime.of(2019, 01, 20, 23, 59))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 2번 출구")
                .eventStatus(EventStatus.PUBLISHED)
                .free(true)
                .offline(false)
                .build();

        mockMvc.perform(post("/api/events/")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())

        ;

    }

    @Test
    public void createEvent_bad_request_empty_input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_bad_request_wrong_input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest API Devrlopments with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 01, 21, 00, 00))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 01, 20, 23, 59))
                .beginEventDateTime(LocalDateTime.of(2019, 01, 18, 14, 00))
                .endEventDateTime(LocalDateTime.of(2019, 01, 15, 23, 59))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}
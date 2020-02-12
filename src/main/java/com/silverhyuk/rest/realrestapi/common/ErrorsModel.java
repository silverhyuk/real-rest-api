package com.silverhyuk.rest.realrestapi.common;

import com.silverhyuk.rest.realrestapi.index.IndexController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.validation.Errors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class ErrorsModel extends EntityModel<Errors> {
    public ErrorsModel(Errors content, Link... links) {
        super(content, links);
        add(WebMvcLinkBuilder.linkTo(methodOn(IndexController.class).index()).withRel("index"));
    }
}

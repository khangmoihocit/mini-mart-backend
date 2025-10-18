package com.khangmoihocit.minimart.service;

import java.util.List;

public interface BaseCRUDService<Response, Request> {
    Response save(Request request);
    Response update(String id, Request request);
    void delete(String id);
    Response findById(String id);
    List<Response> findAll();
}

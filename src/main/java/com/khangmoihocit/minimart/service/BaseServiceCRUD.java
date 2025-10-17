package com.khangmoihocit.minimart.service;

public interface BaseServiceCRUD <Response, Request> {
    Response save(Request request);
    Response update(String id, Request request);
    void delete(String id);
    Response findById(String id);
}

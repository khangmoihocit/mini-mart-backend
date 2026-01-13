package com.khangmoihocit.minimart.service;

public interface FakeDataService {
    String generateFakeProducts(int count);
    void deleteAllFakeProducts();

    String generateFakeUsers(int count);

    String generateFakeOrders(int count);
}


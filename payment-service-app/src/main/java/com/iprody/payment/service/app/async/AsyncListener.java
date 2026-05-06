package com.iprody.payment.service.app.async;

interface AsyncListener<T extends Message> {

    void onMessage(T message);
}

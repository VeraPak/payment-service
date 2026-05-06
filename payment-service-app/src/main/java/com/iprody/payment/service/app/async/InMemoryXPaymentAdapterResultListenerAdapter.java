package com.iprody.payment.service.app.async;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InMemoryXPaymentAdapterResultListenerAdapter implements AsyncListener<XPaymentAdapterResponseMessage> {

    private final MessageHandler<XPaymentAdapterResponseMessage> handler;

    @Override
    public void onMessage(XPaymentAdapterResponseMessage msg) {
        handler.handle(msg);
    }
}
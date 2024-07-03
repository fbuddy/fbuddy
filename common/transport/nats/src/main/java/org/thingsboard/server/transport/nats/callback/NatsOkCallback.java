package org.thingsboard.server.transport.nats.callback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.transport.TransportServiceCallback;

public class NatsOkCallback implements TransportServiceCallback<Void> {
    private final DeferredResult<ResponseEntity> responseWriter;

    public NatsOkCallback(DeferredResult<ResponseEntity> responseWriter) {
        this.responseWriter = responseWriter;
    }

    @Override
    public void onSuccess(Void msg) {
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.OK));
    }

    @Override
    public void onError(Throwable e) {
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}

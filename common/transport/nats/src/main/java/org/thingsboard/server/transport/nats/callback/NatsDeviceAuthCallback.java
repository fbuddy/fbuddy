package org.thingsboard.server.transport.nats.callback;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.transport.TransportContext;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.common.transport.auth.SessionInfoCreator;
import org.thingsboard.server.common.transport.auth.ValidateDeviceCredentialsResponse;
import java.util.function.Consumer;

import org.thingsboard.server.gen.transport.TransportProtos;
import org.thingsboard.server.gen.transport.TransportProtos.SessionInfoProto;

import java.util.UUID;

public class NatsDeviceAuthCallback implements TransportServiceCallback<ValidateDeviceCredentialsResponse> {
    private final TransportContext transportContext;
    private final DeferredResult<ResponseEntity> responseWriter;
    private final Consumer<SessionInfoProto> onSuccess;

    public NatsDeviceAuthCallback(TransportContext transportContext, DeferredResult<ResponseEntity> responseWriter, Consumer<SessionInfoProto> onSuccess) {
        this.transportContext = transportContext;
        this.responseWriter = responseWriter;
        this.onSuccess = onSuccess;
    }

    @Override
    public void onSuccess(ValidateDeviceCredentialsResponse msg) {
        if (msg.hasDeviceInfo()) {
            onSuccess.accept(SessionInfoCreator.create(msg, transportContext, UUID.randomUUID()));
        } else {
            responseWriter.setResult(new ResponseEntity<>(HttpStatus.UNAUTHORIZED));
        }
    }

    @Override
    public void onError(Throwable e) {
        //log.warn("Failed to process request", e);
        responseWriter.setResult(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}

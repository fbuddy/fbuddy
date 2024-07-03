package org.thingsboard.server.transport.nats;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParser;
import io.nats.client.*;
import lombok.extern.slf4j.Slf4j;
import org.msgpack.jackson.dataformat.MessagePackFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.thingsboard.server.common.adaptor.JsonConverter;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.TbTransportService;
import org.thingsboard.server.common.msg.TbMsgMetaData;
import org.thingsboard.server.common.transport.TransportService;
import org.thingsboard.server.common.transport.TransportServiceCallback;
import org.thingsboard.server.gen.transport.TransportProtos.ValidateDeviceTokenRequestMsg;
import org.thingsboard.server.transport.nats.callback.NatsDeviceAuthCallback;
import org.thingsboard.server.transport.nats.util.ReturnCode;

import javax.annotation.PostConstruct;

@Service("NatsTransportService")
@Slf4j
public class NatsTransportService implements TbTransportService {
    private static final String NATS_URL = "nats://natsadmin:Phee8aiv5yodocohxaiqu5@192.168.14.101:4222"; // URL of your NATS server
    @Autowired
    private NatsTransportContext transportContext;


    @PostConstruct
    public void init() {
        log.info("NATS transport started.");
        String subject = "subject1";
        try {
            Connection nc = Nats.connect(NATS_URL);
            MessageHandler messageHandler = new MessageHandler() {
                @Override
                public void onMessage(Message message) {
                    try {
                        //String receivedMessage = new String(message.getData(), StandardCharsets.UTF_8);
                        ObjectMapper objectMapper = new ObjectMapper(new MessagePackFactory());
                        String receivedMessage = objectMapper.readValue(message.getData(), new TypeReference<String>() {
                        });

                        String topicName = message.getHeaders().getFirst("topic");
                        String deviceToken = message.getHeaders().getFirst("deviceToken");
                        log.info("Received message: " + receivedMessage);
                        //transportService.process(sessionInfo, receivedMessage, getMetadata(topicName),
                        //        getPubAckCallback(message));

                        DeferredResult<ResponseEntity> responseWriter = new DeferredResult<>();


                        transportContext.getTransportService().process(DeviceTransportType.NATS, ValidateDeviceTokenRequestMsg.newBuilder().setToken(deviceToken).build(), new NatsDeviceAuthCallback(transportContext, responseWriter, sessionInfo -> {
                            TransportService transportService = transportContext.getTransportService();
                            transportService.process(sessionInfo, JsonConverter.convertToTelemetryProto(new JsonParser().parse(receivedMessage)), getMetadata(topicName), getPubAckCallback(nc, message));
                        }));

                    } catch (Exception e) {
                        message.nak();
                    }
                }
            };

            //Subscription subscription = nc.subscribe(subject);
            Dispatcher d = nc.createDispatcher((msg) -> {
                messageHandler.onMessage(msg);
            });
            d.subscribe(subject);

            log.info("Subscription to NATS subject '" + subject + "' successful.");

        } catch (Exception e) {
            System.err.println("Failed to subscribe to NATS subject '" + subject + "': " + e.getMessage());
            e.printStackTrace();
        }
    }

    private TbMsgMetaData getMetadata(String topicName) {
        TbMsgMetaData md = new TbMsgMetaData();
        md.putValue(DataConstants.NATS_TOPIC, topicName);
        return md;
    }

    private <T> TransportServiceCallback<Void> getPubAckCallback(Connection nc, final Message msg) {
        return new TransportServiceCallback<>() {
            @Override
            public void onSuccess(Void dummy) {
                //log.trace("[{}] Published msg: {}", sessionId, msg);
                //ack(ctx, msgId, ReturnCode.SUCCESS);
                nc.publish(msg.getReplyTo(), ReturnCode.SUCCESS.name().getBytes());
            }

            @Override
            public void onError(Throwable e) {
                //log.trace("[{}] Failed to publish msg: {}", sessionId, msg, e);
                //closeCtx(ctx);
                msg.nak();
            }
        };
    }

    @Override
    public String getName() {
        return DataConstants.NATS_TRANSPORT_NAME;
    }
}

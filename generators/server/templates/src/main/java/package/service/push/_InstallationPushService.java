package <%=packageName%>.service.push;

import <%=packageName%>.domain.Installation;
import <%=packageName%>.domain.push.PushMessage;
import <%=packageName%>.domain.push.PushType;
import <%=packageName%>.service.push.sender.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by deividi on 08/09/16.
 */
@Service
public class InstallationPushService {

    @Autowired
    private PushService pushService;

    @Async
    public void confirm(Installation installation) {
        PushMessage message = new PushMessage();

        message.setType(PushType.SAMPLE_PUSH);
        Map<String, Object> payload = new HashMap<>();
        payload.put(PushType.Extras.PAYLOAD, installation.getUser()); //please use a mapstruct to reduce push size
        message.setPayload(payload);
        message.setTimeToLive(600);
        Notification.Builder builder = new Notification.Builder(null);
        builder.title("Instalação confirmada");
        builder.body("Instalação confirmada para seu dispositivo");
        message.setNotification(builder.build());

        List<Installation> tokens = new ArrayList<>();
        tokens.add(installation);
        pushService.sendMessageForProviders(tokens, message);
    }


}

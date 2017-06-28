<%#
 Copyright 2013-2017 the original author or authors from the JHipster project.

 This file is part of the JHipster project, see https://jhipster.github.io/
 for more information.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-%>
package <%=packageName%>.service.push;

import <%=packageName%>.config.push.FCMCredentials;
import <%=packageName%>.domain.push.Platform;
import <%=packageName%>.domain.push.PushMessage;
import <%=packageName%>.service.InstallationService;
import <%=packageName%>.service.push.sender.Constants;
import <%=packageName%>.service.push.sender.Message;
import <%=packageName%>.service.push.sender.MulticastResult;
import <%=packageName%>.service.push.sender.Result;
import <%=packageName%>.service.push.sender.Sender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static <%=packageName%>.service.push.sender.Constants.ERROR_MESSAGE_TOO_BIG;

/**
 * Created by deividi on 04/08/16.
 */
@Service
@SenderType(values = {Platform.ANDROID, Platform.IOS, Platform.WEB})
public class FCMSenderService implements PushSenderService {

    private final Logger log = LoggerFactory.getLogger(FCMSenderService.class);

    @Autowired
    FCMCredentials credentials;

    @Autowired
    InstallationService installationService;

    @Autowired
    @Qualifier("PUSH")
    ObjectMapper mapper;

    private static final Set<String> GCM_ERROR_CODES = new HashSet<>(Arrays.asList(Constants.ERROR_INVALID_REGISTRATION, Constants.ERROR_NOT_REGISTERED));

    @Override
    public void sendMessage(Platform platform, Set<String> tokens, PushMessage message, PushSenderCallback callback) {

        log.info("Sending messages to FCM");

        if (tokens.isEmpty()) {
            callback.onSuccess();
            return;
        }

        final List<String> registrationIDs = new ArrayList<>(tokens);

        Message.Builder fcmBuilder = new Message.Builder();

        int ttl = message.getTimeToLive();
        if (ttl != -1) {
            fcmBuilder.timeToLive(ttl);
        }

        message.getPayload().keySet().forEach(key -> {
            try {
                if (message.getPayload().get(key) != null) {
                    fcmBuilder.addData(key, mapper.writeValueAsString(message.getPayload().get(key)));
                }
            } catch (JsonProcessingException e) {
                log.error("Error creating data object to send to GCM. key: {}, value: {}", key, message.getPayload().get(key), e);
            }
        });

        if (platform.equals(Platform.IOS)) {
            fcmBuilder.notification(message.getNotification());
        } else {
            // Dont send notification to android, android must validate manually!
            fcmBuilder.notification(null);
        }

        fcmBuilder.addData("type", message.getType().name());

        fcmBuilder.addData("push_id", message.getId());

        Message firebaseMessage = fcmBuilder.build();

        try {

            final Sender sender = new Sender(credentials.getApiKey());
            processFCM(registrationIDs, firebaseMessage, sender);
            log.info("Finished sent messages to GCM");
            callback.onSuccess();

        } catch (Exception e) {
            log.error("Error creating data object to send to GCM", e);
            callback.onError("Error sending payload to GCM server");
        }

    }

    private void processFCM(List<String> registrationIDs, Message gcmMessage, Sender sender) throws IOException {
        MulticastResult multicastResult = sender.send(gcmMessage, registrationIDs, 10);
        cleanupInvalidRegistrationIDsForVariant(multicastResult, registrationIDs, gcmMessage);
    }

    private void cleanupInvalidRegistrationIDsForVariant(MulticastResult multicastResult, List<String> registrationIDs, Message message) {

        final List<Result> results = multicastResult.getResults();
        multicastResult.getCanonicalIds();

        final Set<String> inactiveTokens = new HashSet<>();
        final Map<String, String> changedTokens = new HashMap<>();

        for (int i = 0; i < results.size(); i++) {
            final Result result = results.get(i);

            final String errorCodeName = result.getErrorCodeName();

            if (errorCodeName != null && GCM_ERROR_CODES.contains(errorCodeName)) {
                log.warn("Token has been invalidated: {}", registrationIDs.get(i));
                inactiveTokens.add(registrationIDs.get(i));
            }

            if (errorCodeName != null && ERROR_MESSAGE_TOO_BIG.contains(errorCodeName)) {
                log.error("Message too big! {}", message);
            }

            if (result.getCanonicalRegistrationId() != null) {
                log.warn("Token {} has a new canonical ID: {}", registrationIDs.get(i), results.get(i).getCanonicalRegistrationId());
                changedTokens.put(registrationIDs.get(i), results.get(i).getCanonicalRegistrationId());
            }
        }

        installationService.removeInstallationsForDeviceTokens(inactiveTokens);
        installationService.updateInstallationsForCanonicalIDs(changedTokens);
    }
}

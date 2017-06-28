package <%=packageName%>.domain.push;

import <%=packageName%>.service.push.sender.Notification;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PushMessage {

    private String id = UUID.randomUUID().toString();

    private Notification notification;

    private String action;

    private PushType type;

    private int timeToLive = -1;

    private Map<String, Object> payload = new HashMap<>();

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PushType getType() {
        return type;
    }

    public void setType(PushType type) {
        this.type = type;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

}

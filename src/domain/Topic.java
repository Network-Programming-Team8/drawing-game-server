package domain;

public class Topic {
    private final int suggesterId;
    private final String topic;

    public Topic(int suggesterId, String topic) {
        this.suggesterId = suggesterId;
        this.topic = topic;
    }

    public int getSuggesterId() {
        return suggesterId;
    }

    public String getTopic() {
        return topic;
    }
}

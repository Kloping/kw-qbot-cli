package top.kloping.chatai;

import lombok.Data;

import java.util.List;

@Data
public class Assistant {
    private String id;
    private List<Choice> choices;
    private long created;
    private String model;
    private String object;
    private Usage usage;

    @Data
    public static class Choice {
        private int index;
        private Message message;
        private String finishReason;

        @Data
        public static class Message {
            private String role;
            private String content;
        }
    }

    @Data
    public static class Usage {
        private long promptTokens;
        private long completionTokens;
        private long totalTokens;
        private CompletionTokensDetails completionTokensDetails;
        private PromptTokensDetails promptTokensDetails;

        @Data
        public static class CompletionTokensDetails {
            private long reasoningTokens;
        }

        @Data
        public static class PromptTokensDetails {
            private long cachedTokens;
        }
    }
}

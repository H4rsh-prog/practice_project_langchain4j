package com.ai.model;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.moderation.Moderation;
import dev.langchain4j.model.moderation.ModerationModel;
import dev.langchain4j.model.moderation.ModerationRequest;
import dev.langchain4j.model.moderation.ModerationResponse;
import dev.langchain4j.model.output.Response;

public class NoModerationModel implements ModerationModel {
	
    public ModerationResponse moderate(ModerationRequest moderationRequest) {return ModerationResponse.builder().moderation(Moderation.notFlagged()).build();}
    public ModerationResponse doModerate(ModerationRequest moderationRequest) {return ModerationResponse.builder().moderation(Moderation.notFlagged()).build();}
    public Response<Moderation> moderate(String text) {return Response.from(Moderation.notFlagged());}
    public Response<Moderation> moderate(Prompt prompt) {return Response.from(Moderation.notFlagged());}
    public Response<Moderation> moderate(ChatMessage message) {return Response.from(Moderation.notFlagged());}
    public Response<Moderation> moderate(List<ChatMessage> messages) {return Response.from(Moderation.notFlagged());}
    public Response<Moderation> moderate(TextSegment textSegment) {return Response.from(Moderation.notFlagged());}
}

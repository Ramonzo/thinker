package com.chatbot.thinker.service.openai.interfaces;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateRun {
    public String assistant_id;
    public Boolean stream;
    public Thread thread;
    public List<Tool> tools = new ArrayList<>();

    public static class Thread {
        public List<Message> messages = new ArrayList<>();
    }

    public static class Tool {
        public String type;
        public Function function;
    }

    public static class Function {
        public String name;
        public String description;
        public Parameters parameters;
    }

    public static class Parameters {
        public String type;
        public Properties properties;
        public List<String> required = new ArrayList<>();
    }

    public static class Properties {
        public Location location;
        public Unit unit;
    }

    public static class Location {
        public String type;
        public String description;
    }

    public static class Unit {
        public String type;
        public List<String> enums = new ArrayList<>();
    }
}

package com.seyeong.youtube_block_application2.domain;

public class Daily {

    private int from;
    private int to;

    public Daily(int from, int to) {
        this.from = from;
        this.to = to;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

}

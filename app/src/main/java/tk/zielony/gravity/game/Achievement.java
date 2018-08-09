package tk.zielony.gravity.game;

public enum Achievement {
    LOW_APM("Sunday gamer", "keep apm below 5 after 1 minute"),
    PLAY_ZOOMED_OUT("Eyes on the world", "play zoomed out for 5 minutes"),
    PLAY_ZOOMED_IN("Careful scientist", "play zoomed in for 5 minutes"),
    DESTROY_SPACESHIP("It's not easy being green", "destroy a spaceship"),
    GOOD_GAME("Good game", "Play for 20 minutes"),
    CREATE_EARTH("Sweet home earth", "Create an earth"),
    CREATE_SUN("Let's land at night", "Create a sun"),
    SLOW_JAVA("Knock, knock ...Java!", "Go below 10 fps"),
    HIGH_APM("Micro master", "Keep apm above 150 after 1 minute"),
    UPSIDE_DOWN("Bouncing of the ceiling", "Flip your phone upside down"),
    CREATE_SATURN("Outer sailor's home", "Create a saturn"),
    CREATE_MARS("Red planet", "Create a mars");

    private final String title;
    private final String description;

    Achievement(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
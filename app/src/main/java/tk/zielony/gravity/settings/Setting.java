package tk.zielony.gravity.settings;

public enum Setting {
    ACCELEROMETER("accelerometer"), COLLISIONS("collisions"), BLACK_HOLES(
            "black holes"), LABELS("labels"), UNSTABLE_STARS("unstable stars");

    Setting(final String text) {
        this.text = text;
    }

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
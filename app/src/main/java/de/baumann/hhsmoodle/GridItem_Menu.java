package de.baumann.hhsmoodle;


class GridItem_Menu {
    private final String title;
    String getTitle() {
        return title;
    }

    private final int icon;
    public int getIcon() {
        return icon;
    }

    GridItem_Menu(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }
}

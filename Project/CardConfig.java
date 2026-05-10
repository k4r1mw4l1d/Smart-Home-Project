import javafx.scene.layout.VBox;

class CardConfig {
    VBox card;
    int colSpan;
    int rowSpan;

    public CardConfig(VBox card, int colSpan, int rowSpan) {
        this.card = card;
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
    }
}
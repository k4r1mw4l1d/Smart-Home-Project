import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

//control state and card (return card -> buttons / image -> change state)

public class CardImages {
    public VBox card;
    public ImageView imageView;

    public CardImages(VBox card, ImageView imageView) {
        this.card = card;
        this.imageView = imageView;
    }
}
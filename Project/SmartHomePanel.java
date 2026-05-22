import java.time.format.DateTimeFormatter;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import java.time.LocalDateTime;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.scene.image.*;

import java.util.ArrayList;
import java.time.LocalTime;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.*;

import java.util.Random;


public class SmartHomePanel extends Application {

    private static final String ICONS = "icons/";
    private static final String IMAGES = "images/";
    MQTT mqttService = new MQTT();
    private Label welcome;
    private ArrayList<VBox> allCards = new ArrayList<>();
    private ArrayList<Label> allCardsLabels = new ArrayList<>();
    private ArrayList<Label> allRoomsLabels = new ArrayList<>();
    private ArrayList<Label> allRoomsStatus = new ArrayList<>();
    private boolean darkMode = false;
    private Label dateLabel;
    private VBox side;
    private VBox dashboard;
    private HBox header;
    private VBox onScreen;
    private BorderPane root;
    private String name;
    private VBox roomsMenu;
    private HBox activeRoom;
    private MasterRoom masterRoom;
    private KidsRoom kidsRoom;
    private Bathroom bathroom;
    private Kitchen kitchen;
    private LivingRoom livingRoom;
    private Door_security doorSecurity;
    private ExternalTempSensor tempSensor;
    private GardenWaterSystem waterSystem;
    private Camera camera;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) {

        // ───── 1. Panel's background ───────────────────────
        root = new BorderPane();

        // ───── 2. Linking Classes (Initializing Objects) ─────
        masterRoom = new MasterRoom(
                "1",
                "Master Device",
                "Master Room",
                false,
                24,
                false,
                "",
                false,
                false
        );

        kidsRoom = new KidsRoom(
                "2",
                "Kids Device",
                "Kids Room",
                false,
                24,
                false,
                false,
                false,
                false
        );

        kidsRoom.setMasterRoom(masterRoom);

        livingRoom = new LivingRoom(
                "3",
                "Living Device",
                "Living Room",
                false,
                24,
                false,
                false,
                false,
                ""
        );

        bathroom = new Bathroom(
                "4",
                "Bathroom Device",
                "Bathroom",
                false,
                false,
                false,
                25,
                true
        );

        kitchen = new Kitchen(
                "5",
                "Kitchen Device",
                "Kitchen",
                false,
                false,
                false,
                60,
                false,
                5,
                false,
                24
        );

        doorSecurity = new Door_security(
                "6",
                "Out doors",
                "Outdoors",
                true,
                false
        );

        tempSensor = new ExternalTempSensor(
                "7",
                "Temp sensor",
                "Outdoors",
                25
        );

        waterSystem = new GardenWaterSystem(
                "8",
                "Water system",
                "Outdoors",
                false,
                false,
                30

        );

        camera = new Camera(
                "9",
                "Camera",
                "Security",
                false,
                false,
                false
        );


        // ───── 3. Connect Program to cloud ──────────────
        mqttService.setModels(livingRoom, masterRoom, kidsRoom, kitchen, bathroom);
        mqttService.connect();

        // ───── Main program ───────────────────────────────────
        showWelcomeScreen();

        ScrollPane scroll = new ScrollPane(root);
        scroll.setStyle("-fx-background: transparent;" +
                "-fx-background-color: transparent;" +
                "-fx-padding: 0;"
        );

        scroll.setFitToWidth(true);
        scroll.setFitToHeight(true);

        Scene scene = new Scene(scroll, 1500, 900);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Smart Home Panel");
        primaryStage.setMaximized(true);
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
        Image icon = new Image(IMAGES + "ProgIcon.png");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    //─────Main program ───────────────────────────
    public void loadProgram() {

        root.setLeft(buildSidebar());
        root.setCenter(buildDashboard());
        setOnScreen(buildMainScreen());
        enableLightMode();
    }

    //─────First page ─────────────────────────────
    public void showWelcomeScreen() {

        VBox screen = new VBox(20);
        screen.setAlignment(Pos.TOP_CENTER);
        screen.setPadding(new Insets(40, 0, 40, 0));
        screen.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, #1b55cf, #0a1e3d);"
        );

        // ───── Top Section ────────────────────
        VBox top = new VBox(10);
        top.setAlignment(Pos.TOP_CENTER);

        ImageView logo = addIcon("smartHome", 350);

        Label welcome = new Label("Welcome!");
        welcome.setStyle(
                "-fx-text-fill: white;" +
                        "-fx-font-size: 50px;" +
                        "-fx-font-weight: bold;"
        );

        Label sub = new Label("Smart Home System");
        sub.setStyle(
                "-fx-text-fill: #c2c2c2;" +
                        "-fx-font-size: 20px;"
        );

        top.getChildren().addAll(logo, welcome, sub);

        // ─────Empty region to fill space─────────────
        Region spacer = new Region();
        spacer.setPrefHeight(80);

        // ───── Input ───────────────────────
        VBox field = new VBox(10);
        field.setAlignment(Pos.CENTER);
        field.setMaxWidth(300);

        Label enter = new Label("Please, enter your name");
        enter.setStyle(
                "-fx-text-fill: #c2c2c2;" +
                        "-fx-font-size: 20px;"
        );

        TextField enterName = new TextField();
        enterName.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-padding: 10 14 10 14;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-color: transparent;"
        );

        field.getChildren().addAll(enter, enterName);

        // ───── Button ────────────────────
        Button bt = new Button("Enter");
        bt.setStyle(
                "-fx-background-color: white;" +
                        "-fx-text-fill: #1b55cf;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 12;" +
                        "-fx-padding: 10 30 10 30;"
        );

        screen.getChildren().addAll(top, spacer, field, bt);

        root.setCenter(screen);

        bt.setOnMouseEntered(e ->
                bt.setStyle(
                        "-fx-background-color: #dbe7ff;" +
                                "-fx-text-fill: #1b55cf;" +
                                "-fx-font-size: 18px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 10 30 10 30;"
                )
        );

        bt.setOnMouseExited(e ->
                bt.setStyle(
                        "-fx-background-color: white;" +
                                "-fx-text-fill: #1b55cf;" +
                                "-fx-font-size: 18px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-background-radius: 12;" +
                                "-fx-padding: 10 30 10 30;"
                )
        );

        bt.setOnAction(e -> {
            name = enterName.getText();
            loadProgram();
        });

        enterName.setOnKeyPressed(e -> {
            name = enterName.getText();

            if (e.getCode() == KeyCode.ENTER) {
                loadProgram();
            }
        });

        mqttService.setModels(livingRoom, masterRoom, kidsRoom, kitchen, bathroom);
        mqttService.connect();
    }

    //─────Sidebar ────────────────────────────────
    public VBox buildSidebar() {
        // ─────Sidebar background──────────────────
        side = new VBox(10);
        side.setPrefWidth(220);
        side.setPadding(new Insets(10, 0, 10, 0));
        side.setStyle(
                "-fx-background-color: #0a1e3d;-fx-border-color: #1c3760;-fx-border-width: 0 1 0 0;"
        );

        // ─────Logo───────────────────────
        try {
            ImageView logo = new ImageView(new Image(ICONS + "smartHome.png"));
            logo.setFitWidth(120);
            logo.setFitHeight(120);
            logo.setPreserveRatio(true);

            HBox logoBox = new HBox(logo);
            logoBox.setAlignment(Pos.CENTER);
            logoBox.setPadding(new Insets(20, 0, 20, 0));

            side.getChildren().add(logoBox);
        } catch (Exception e) {
            System.out.println("Icon not found");
            ImageView empty = new ImageView();
            empty.setFitWidth(120);
            empty.setFitHeight(120);
            side.getChildren().add(empty);
        }

        // ─────Sidebar -> main─────────────
        HBox main = navRows("home", 24, "Main", "#c2c2c2", 16);

        mouseHover(main, "#1d3e6e");

        main.setOnMouseClicked(e -> {

            setOnScreen(buildMainScreen());
            setActive(main, "#1d3e6e");

            if (darkMode) {
                enableDarkMode();
            } else {
                enableLightMode();
            }
        });

        side.getChildren().addAll(main);

        // ─────Sidebar -> rooms list─────────────
        HBox rooms = navRows("room", 24, "Rooms", "#c2c2c2", 16);

        mouseHover(rooms, "#1d3e6e");

        Region spacerArrow = new Region();
        HBox.setHgrow(spacerArrow, Priority.ALWAYS);

        Label arrow = new Label("▼");
        arrow.setStyle(
                "-fx-text-fill: #c2c2c2;" +
                        "-fx-font-size: 11px;"
        );

        rooms.getChildren().addAll(spacerArrow, arrow);

        roomsMenu = roomsSideMenu();
        rooms.setOnMouseClicked(e -> {

            boolean show = roomsMenu.isVisible();

            roomsMenu.setVisible(!show);
            roomsMenu.setManaged(!show);

            arrow.setText(!show ? "▲" : "▼");

            setActive(rooms, "#1d3e6e");
        });

        side.getChildren().addAll(rooms, roomsMenu);

        // ─────Sidebar -> themes─────────────
        HBox themes = navRows("lightMode", 24, "Themes", "#c2c2c2", 16);

        mouseHover(themes, "#1d3e6e");

        themes.setOnMouseClicked(e -> {

            if (!darkMode) {
                enableDarkMode();
                darkMode = true;
            } else {
                enableLightMode();
                darkMode = false;
            }

        });

        side.getChildren().addAll(themes);

        // ─────Sidebar -> Alarm─────────────
        HBox alarm = navRows("addAlarm", 24, "Alarms", "#c2c2c2", 16);

        mouseHover(alarm, "#1d3e6e");

        alarm.setOnMouseClicked(e -> {

            setOnScreen(buildAlarmCards());
            setActive(alarm, "#133466");

            if (darkMode) {
                enableDarkMode();
                darkMode = true;
            } else {
                enableLightMode();
                darkMode = false;
            }
        });

        side.getChildren().addAll(alarm);


        // ─────Empty region to fill space─────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        side.getChildren().add(spacer);

        // ─────Sidebar -> Avatar─────────────
        HBox avatar = navRows("avatar", 50, "", "#c2c2c2", 16);

        mouseHover(avatar, "#1d3e6e");

        Label nameLabel = new Label(name);
        TextField nameField = new TextField(name);

        nameLabel.setStyle("-fx-text-fill: #c2c2c2; -fx-font-size: 16px;");
        nameField.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-text-fill: #c2c2c2; -fx-font-size: 16px;");

        nameField.setVisible(false);
        nameField.setManaged(false);

        avatar.getChildren().addAll(nameLabel, nameField);

        avatar.setOnMouseClicked(e -> {

            nameField.setText(nameLabel.getText());

            nameLabel.setVisible(false);
            nameLabel.setManaged(false);

            nameField.setVisible(true);
            nameField.setManaged(true);

            nameField.selectAll();
        });

        nameField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {

                name = nameField.getText().trim();
                welcome.setText("Welcome Back, " + name + "!");

                nameLabel.setText(name);

                nameLabel.setVisible(true);
                nameLabel.setManaged(true);

                nameField.setVisible(false);
                nameField.setManaged(false);
                nameField.requestFocus();
            }
        });


        side.getChildren().add(avatar);

        return side;

    }

    // ─────Icons building───────────────────────
    public ImageView addIcon(String name, double size) {
        try {
            ImageView icon = new ImageView(new Image(ICONS + name + ".png"));
            icon.setFitWidth(size);
            icon.setFitHeight(size);
            icon.setPreserveRatio(true);
            return icon;
        } catch (Exception e) {
            System.out.println("Icon not found");
            ImageView empty = new ImageView();
            empty.setFitWidth(size);
            empty.setFitHeight(size);
            return empty;
        }
    }

    // ─────Nav-rows maker───────────────────────
    public HBox navRows(String iconName, double iconSize, String rowLabel, String labelColor, double labelSize) {
        HBox items = new HBox(13);
        items.setPadding(new Insets(11, 16, 11, 16));

        ImageView icon = addIcon(iconName, iconSize);
        Label iconLabel = new Label(rowLabel);
        iconLabel.setStyle("-fx-font-size: " + labelSize + "px;-fx-text-fill: " + labelColor + ";");

        items.setAlignment(Pos.CENTER_LEFT);
        items.getChildren().addAll(icon, iconLabel);
        return items;
    }

    // ─────Dashboard─────────────────────
    public VBox buildDashboard() {
        dashboard = new VBox(15);
        dashboard.setStyle("-fx-background-color: #edf0f5;");
        dashboard.setPadding(new Insets(0, 0, 24, 0));
        dashboard.getChildren().add(buildHeader());

        onScreen = new VBox();

        dashboard.getChildren().add(onScreen);

        return dashboard;
    }

    // ─────Main─────────────────────
    public VBox buildMainScreen() {

        VBox main = new VBox(25);
        main.setPadding(new Insets(25));

        VBox welcomeBox = new VBox(5);

        welcome = new Label("Welcome Back, " + name + "!");
        welcome.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0a1e3d;"
        );

        Label sub = new Label("Everything in one place.");
        sub.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #6b7280;"
        );

        welcomeBox.getChildren().addAll(welcome, sub);

        // ───── Rooms ─────────────────────
        HBox roomsCards = new HBox(20);

        VBox living = roomStatusCard(
                "oLiving",
                "Living Room",
                livingRoom.isLightsOn() ? "ON" : "OFF",
                livingRoom.isAcOn() ? "ON" : "OFF",
                livingRoom.getTemperature(),
                doorSecurity.isDoorOpen() ? "Opened" : "Closed"
        );


        VBox master = roomStatusCard(
                "rBed",
                "Master Room",
                masterRoom.isLightsOn() ? "ON" : "OFF",
                masterRoom.isAcOn() ? "ON" : "OFF",
                masterRoom.getTemperature(),
                masterRoom.isDoorOpen() ? "Opened" : "Closed"
        );

        VBox kids = roomStatusCard(
                "yKids",
                "Kids Room",
                kidsRoom.isLightsOn() ? "ON" : "OFF",
                kidsRoom.isAcOn() ? "ON" : "OFF",
                kidsRoom.getTemperature(),
                masterRoom.isDoorOpen() ? "Opened" : "Closed"
        );


        HBox.setHgrow(living, Priority.ALWAYS);
        HBox.setHgrow(kids, Priority.ALWAYS);
        HBox.setHgrow(master, Priority.ALWAYS);

        roomsCards.getChildren().addAll(
                living,
                master,
                kids
        );

        HBox addons = new HBox(20);

        VBox kkitchen = roomStatusCard(
                "bKitchen",
                "Kitchen",
                kitchen.isLightsOn() ? "ON" : "OFF",
                kitchen.isStoveOn() ? "ON" : "OFF",
                kitchen.getTemperature(),
                kitchen.isFridgeOn() ? "ON" : "OFF"
        );

        VBox bath = roomStatusCard(
                "gBath",
                "Bathroom",
                bathroom.isLightsOn() ? "ON" : "OFF",
                bathroom.isHeaterOn() ? "ON" : "OFF",
                bathroom.getWaterTemperature(),
                bathroom.isDoorOpen() ? "Opened" : "Closed"
        );

        HBox.setHgrow(kkitchen, Priority.ALWAYS);
        HBox.setHgrow(bath, Priority.ALWAYS);

        addons.getChildren().addAll(kkitchen, bath);

        main.getChildren().addAll(
                welcomeBox,
                roomsCards,
                addons
        );

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0.1), e -> {

                    // ───── Living room ─────────────────────
                    ((Label) living.lookup("#lightsLabel"))
                            .setText("💡 Lights: " + (livingRoom.isLightsOn() ? "ON" : "OFF"));

                    ((Label) living.lookup("#acLabel"))
                            .setText("❄ AC: " + (livingRoom.isAcOn() ? "ON" : "OFF"));

                    ((Label) living.lookup("#tempLabel"))
                            .setText("🌡 Temp: " + livingRoom.getTemperature() + "°C");

                    ((Label) living.lookup("#doorLabel"))
                            .setText("🚪 Door: " + (doorSecurity.isDoorOpen() ? "Opened" : "Closed"));

                    // ───── Master room ─────────────────────
                    ((Label) master.lookup("#lightsLabel"))
                            .setText("💡 Lights: " + (masterRoom.isLightsOn() ? "ON" : "OFF"));

                    ((Label) master.lookup("#acLabel"))
                            .setText("❄ AC: " + (masterRoom.isAcOn() ? "ON" : "OFF"));

                    ((Label) master.lookup("#tempLabel"))
                            .setText("🌡 Temp: " + masterRoom.getTemperature() + "°C");

                    ((Label) master.lookup("#doorLabel"))
                            .setText("🚪 Door: " + (masterRoom.isDoorOpen() ? "Opened" : "Closed"));

                    // ───── Kids room ─────────────────────
                    ((Label) kids.lookup("#lightsLabel"))
                            .setText("💡 Lights: " + (kidsRoom.isLightsOn() ? "ON" : "OFF"));

                    ((Label) kids.lookup("#acLabel"))
                            .setText("❄ AC: " + (kidsRoom.isAcOn() ? "ON" : "OFF"));

                    ((Label) kids.lookup("#tempLabel"))
                            .setText("🌡 Temp: " + kidsRoom.getTemperature() + "°C");

                    ((Label) kids.lookup("#doorLabel"))
                            .setText("🚪 Door: " + (masterRoom.isDoorOpen() ? "Opened" : "Closed"));

                    // ───── Kitchen ─────────────────────
                    ((Label) kkitchen.lookup("#lightsLabel"))
                            .setText("💡 Lights: " + (kitchen.isLightsOn() ? "ON" : "OFF"));

                    ((Label) kkitchen.lookup("#acLabel"))
                            .setText("🔥 Stove: " + (kitchen.isStoveOn() ? "ON" : "OFF"));

                    ((Label) kkitchen.lookup("#tempLabel"))
                            .setText("🌡 Temp: " + kitchen.getTemperature() + "°C");

                    ((Label) kkitchen.lookup("#doorLabel"))
                            .setText("❄ Fridge: " + (kitchen.isFridgeOn() ? "ON" : "OFF"));

                    // ───── Bathroom ─────────────────────
                    ((Label) bath.lookup("#lightsLabel"))
                            .setText("💡 Lights: " + (bathroom.isLightsOn() ? "ON" : "OFF"));

                    ((Label) bath.lookup("#acLabel"))
                            .setText("🔥 Heater: " + (bathroom.isHeaterOn() ? "ON" : "OFF"));

                    ((Label) bath.lookup("#tempLabel"))
                            .setText(String.format("🌡 Heater Temp: %.1f°C",
                                    bathroom.getWaterTemperature()));

                    ((Label) bath.lookup("#doorLabel"))
                            .setText("🚪 Door: " + (bathroom.isDoorOpen() ? "Opened" : "Closed"));

                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        allRoomsLabels.add(welcome);
        allCardsLabels.add(sub);
        return main;
    }

    // ─────Rooms menu─────────────────────
    public VBox roomsSideMenu() {
        roomsMenu = new VBox(8);
        roomsMenu.setPadding(new Insets(0, 0, 0, 35));
        roomsMenu.setStyle("-fx-background-color: #12294a;");
        roomsMenu.setVisible(false);
        roomsMenu.setManaged(false);

        // ─────Living room─────────────────────
        HBox living = navRows("living", 20, "Living Room", "#c2c2c2", 14);
        mouseHover(living, "#1d3e6e");
        living.setOnMouseClicked(e -> {
            setOnScreen(buildLivingRoom());
            setActive(living, "#133466");
        });

        // ─────Master room─────────────────────
        HBox master = navRows("masterRoom", 20, "Master room", "#c2c2c2", 14);
        mouseHover(master, "#1d3e6e");
        master.setOnMouseClicked(e -> {
            setOnScreen(buildMasterRoom());
            setActive(master, "#133466");
        });

        // ─────Kids room───────────────────────
        HBox kids = navRows("kidsRoom", 20, "Kids room", "#c2c2c2", 14);
        mouseHover(kids, "#1d3e6e");
        kids.setOnMouseClicked(e -> {
            setOnScreen(buildKidsRoom());
            setActive(kids, "#133466");
        });

        // ─────Kitchen─────────────────────────
        HBox kitchen = navRows("kitchen", 20, "Kitchen", "#c2c2c2", 14);
        mouseHover(kitchen, "#1d3e6e");
        kitchen.setOnMouseClicked(e -> {
            setOnScreen(buildKitchen());
            setActive(kitchen, "#133466");
        });

        // ─────Bathroom────────────────────────
        HBox bath = navRows("bathroom", 20, "Bathroom", "#c2c2c2", 14);
        mouseHover(bath, "#1d3e6e");
        bath.setOnMouseClicked(e -> {
            setOnScreen(buildBathroom());
            setActive(bath, "#133466");
        });

        // ─────Outdoors────────────────────────
        HBox outdoors = navRows("outdoors", 20, "Outdoors", "#c2c2c2", 14);
        mouseHover(outdoors, "#1d3e6e");
        outdoors.setOnMouseClicked(e -> {
            setOnScreen(buildOutdoors());
            setActive(outdoors, "#133466");
        });

        // ─────Cameras─────────────────────────
        HBox cameras = navRows("camera", 20, "Security", "#c2c2c2", 14);
        mouseHover(cameras, "#1d3e6e");
        cameras.setOnMouseClicked(e -> {
            setOnScreen(buildSecurity());
            setActive(cameras, "#133466");
        });

        roomsMenu.getChildren().addAll(living, master, kids, kitchen, bath, outdoors, cameras);
        return roomsMenu;
    }

    // ─────Header─────────────────────
    public HBox buildHeader() {
        header = new HBox(15);
        header.setPadding(new Insets(12, 20, 5, 20));
        header.setPrefHeight(50);
        header.setStyle(
                "-fx-background-color: #ffffff;-fx-border-color: #d9d9d9;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 6);"
        );

        dateLabel = new Label(currentDateTime());
        startClock();
        dateLabel.setStyle("-fx-font-size: 16px;-fx-text-fill: #000000;");
        header.getChildren().add(dateLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().add(spacer);

        header.getChildren().add(addIcon("wifi", 24));
        header.getChildren().add(addIcon("notification", 24));
        header.getChildren().add(addIcon("avatar", 30));

        return header;
    }

    // ─────Date creation─────────────────────
    public String currentDateTime() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM d - hh:mm a"));
    }

    // ─────Update time─────────────────────
    public void startClock() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("EEEE, MMMM d - hh:mm a");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    dateLabel.setText(LocalDateTime.now().format(formatter));
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // ─────Cards─────────────────────
    public GridPane buildCards(CardConfig... cardsList) {

        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);

        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(24));

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(33.3);
        c.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(c, c, c);

        int col = 0, row = 0;

        for (CardConfig item : cardsList) {

            grid.add(item.card, col, row);

            GridPane.setColumnSpan(item.card, item.colSpan);
            GridPane.setRowSpan(item.card, item.rowSpan);

            col += item.colSpan;

            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        return grid;
    }

    // ─────Card contents─────────────────────
    public CardImages makeCard(String icon, String title, String imageName, double size) {

        VBox card = new VBox(10);
        card.setPadding(new Insets(15));

        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Double.MAX_VALUE);

        card.setStyle(cardStyle());

        allCards.add(card);

        // ───── Header ─────
        HBox header = cardHead(icon, 24, title);

        // ───── Image ─────
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);

        try {
            imageView.setImage(new Image(IMAGES + imageName + ".png"));
        } catch (Exception e) {
            System.out.println("");
        }

        VBox imgBox = new VBox(imageView);
        imgBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(imgBox, Priority.ALWAYS);

        card.getChildren().addAll(header, imgBox);

        cardHover(card);

        return new CardImages(card, imageView);
    }

    // ─────Cards header─────────────────────
    public HBox cardHead(String iconName, double size, String rowLabel) {
        HBox items = new HBox(13);
        items.setPadding(new Insets(11, 16, 11, 5));

        ImageView icon = addIcon(iconName, size);
        Label iconLabel = new Label(rowLabel);
        if (darkMode) {
            iconLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: white;");
        } else {
            iconLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: black;");
        }

        items.setAlignment(Pos.CENTER_LEFT);
        items.getChildren().addAll(icon, iconLabel);
        allCardsLabels.add(iconLabel);
        return items;
    }

    // ───── rooms status (main) ─────────────────────
    public VBox roomStatusCard(
            String icon,
            String title,
            String lightStatus,
            String acStatus,
            double tempVal,
            String doorStatus
    ) {

        VBox card = new VBox(12);
        card.setPadding(new Insets(18));
        cardHover(card);
        card.setStyle(cardStyle());

        // ───── Header ─────────────────────
        HBox header = cardHead(icon, 24, title);

        // ───── Status Section ─────────────────────
        VBox status = new VBox(8);

        Label lights = new Label("💡 Lights: " + lightStatus);
        Label AC = new Label("❄ AC: " + acStatus);
        Label temp = new Label("🌡 Temp: " + tempVal);
        Label door = new Label("🚪 Door: " + doorStatus);

        status.getChildren().addAll(lights, AC, temp, door);

        card.getChildren().addAll(header, status);

        lights.setId("lightsLabel");
        AC.setId("acLabel");
        temp.setId("tempLabel");
        door.setId("doorLabel");

        allCards.add(card);
        allRoomsStatus.add(lights);
        allRoomsStatus.add(AC);
        allRoomsStatus.add(temp);
        allRoomsStatus.add(door);
        return card;
    }

    // ─────Mouse hover on box─────────────────────
    public void mouseHover(HBox item, String onColor) {

        item.setOnMouseEntered(e -> {

            item.setStyle(
                    "-fx-background-color: " + onColor + ";" +
                            "-fx-background-radius: 15;"
            );
        });

        item.setOnMouseExited(e -> {

            if (item != activeRoom) {

                item.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-background-radius: 15;"
                );
            }
        });
    }

    // ─────Light mode─────────────────────
    public void enableLightMode() {

        side.setStyle("-fx-background-color: #0a1e3d;");
        dashboard.setStyle("-fx-background-color: #edf0f5;");
        header.setStyle(
                "-fx-background-color: #ffffff;-fx-border-color: #d9d9d9;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 6);"
        );
        dateLabel.setStyle("-fx-font-size: 16px;-fx-text-fill: #000000;");
        roomsMenu.setStyle("-fx-background-color: #12294a;");

        for (VBox card : allCards) {
            card.setStyle("-fx-background-color: #ffffff;-fx-background-radius: 12;-fx-border-radius: 12;" +
                    "-fx-border-color: #d9d9d9;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 20, 0.2, 0, 6);");
        }
        for (Label cardLabel : allCardsLabels) {
            cardLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: #000000;");
        }

        for (Label roomLabel : allRoomsLabels) {
            roomLabel.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-font-size: 30;" +
                            "-fx-text-fill: #000000;"
            );
        }

        for (Label status : allRoomsStatus) {
            status.setStyle(
                    "-fx-font-size: 15;" +
                            "-fx-text-fill: #000000;"
            );
        }
    }

    // ────Dark mode─────────────────────
    public void enableDarkMode() {

        side.setStyle("-fx-background-color: #0b1120;");
        dashboard.setStyle("-fx-background-color: #121826;");
        header.setStyle("-fx-background-color: #1a2333;");
        dateLabel.setStyle("-fx-font-size: 16px;-fx-text-fill: #ffffff;");
        roomsMenu.setStyle("-fx-background-color: #0e1d33;");

        for (VBox card : allCards) {
            card.setStyle("-fx-background-color: #202b42;-fx-background-radius: 12;-fx-border-radius: 12;" +
                    "-fx-border-color: #2f3d5c;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 12, 0, 0, 4);");
        }

        for (Label cardLabel : allCardsLabels) {
            cardLabel.setStyle("-fx-font-size: 20px;-fx-text-fill: #ffffff;");
        }

        for (Label roomLabel : allRoomsLabels) {
            roomLabel.setStyle("-fx-font-size: 30px;-fx-text-fill: #ffffff;-fx-font-weight: bold;");
        }

        for (Label status : allRoomsStatus) {
            status.setStyle(
                    "-fx-font-size: 15;" +
                            "-fx-text-fill: #ffffff;"
            );
        }
    }

    // ────Switching scenes─────────────────────
    public void setOnScreen(Pane page) {

        onScreen.getChildren().clear();
        onScreen.getChildren().add(page);
    }

    // ──── Set color on click─────────────────────────
    public void setActive(HBox item, String activeColor) {

        if (activeRoom != null) {

            activeRoom.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-background-radius: 15;"
            );
        }

        item.setStyle(
                "-fx-background-color: " + activeColor + ";" +
                        "-fx-background-radius: 15;"
        );

        activeRoom = item;
    }

    // ──── Cards animations─────────────────────────
    public void cardHover(VBox card) {

        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
        scaleUp.setToX(1.03);
        scaleUp.setToY(1.03);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
        scaleDown.setToX(1);
        scaleDown.setToY(1);

        card.setOnMouseEntered(e -> scaleUp.playFromStart());
        card.setOnMouseExited(e -> scaleDown.playFromStart());
    }

    // ──── Styling cards─────────────────────────
    public String cardStyle() {

        if (darkMode) {
            return "-fx-background-color: #202b42;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-radius: 12;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 18, 0.2, 0, 5);";
        } else {
            return "-fx-background-color: white;" +
                    "-fx-background-radius: 12;" +
                    "-fx-border-radius: 12;" +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 18, 0.2, 0, 5);";
        }
    }

    // ──── Quick Action (Active scene) ────────────────
    public VBox masterQuickActionItem(String iconName, String btText, String scene) {

        VBox item = new VBox(6);
        item.setAlignment(Pos.CENTER);

        ImageView icon = addIcon(iconName, 32);
        Button bt = new Button(btText);

        styleSceneButton(bt);

        bt.setPrefWidth(100);
        bt.setPrefHeight(34);

        bt.setOnAction(e -> masterRoom.setSmartScene(scene));

        item.getChildren().addAll(icon, bt);

        return item;
    }

    // ──── Quick Action (Active scene) ────────────────
    public VBox livingQuickActionItem(String iconName, String btText, String scene) {

        VBox item = new VBox(6);
        item.setAlignment(Pos.CENTER);
        ImageView icon = addIcon(iconName, 50);
        Button bt = new Button(btText);

        styleSceneButton(bt);

        bt.setPrefWidth(100);
        bt.setPrefHeight(34);

        bt.setOnAction(e -> livingRoom.setSmartScene(scene));

        item.getChildren().addAll(icon, bt);

        return item;
    }

    // ──── Quick Action (Active scene) ────────────────
    public VBox kidsQuickActionItem(String iconName, String btText) {

        VBox item = new VBox(6);
        item.setAlignment(Pos.CENTER);
        ImageView icon = addIcon(iconName, 50);
        Button bt = new Button(btText);

        styleSceneButton(bt);

        bt.setPrefWidth(100);
        bt.setPrefHeight(34);

        bt.setOnAction(e -> kidsRoom.bedTimeMode());

        item.getChildren().addAll(icon, bt);

        return item;
    }

    // ──── Smart scenes buttons style───────────────────
    private void styleSceneButton(Button bt) {

        bt.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-font-size: 14px;"
        );
    }

    // ──── Title for all rooms─────────────────────────
    public Label roomTitle(String roomTitle) {

        Label title = new Label("● " + roomTitle);
        title.setPadding(new Insets(20));
        title.setMinWidth(Region.USE_PREF_SIZE);

        if (darkMode) {
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 30; -fx-text-fill: #ffffff;");
        } else {
            title.setStyle("-fx-font-weight: bold; -fx-font-size: 30; -fx-text-fill: #000000;");
        }
        allRoomsLabels.add(title);
        return title;
    }

    /*==========================================
     ─────────── Living room cards──────────────
    ==========================================*/

    // ──── 1. Living room─────────────────────
    public VBox buildLivingRoom() {

        VBox lroom = new VBox(0);
        lroom.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(livingLightCard(), 1, 1),
                new CardConfig(livingACCard(), 1, 1),
                new CardConfig(livingTempSliderCard(), 1, 1),
                new CardConfig(livingTVCard(), 1, 1),
                new CardConfig(livingCurtainsCard(), 1, 1)
        );

        HBox topBar = new HBox(0);
        topBar.setPrefWidth(50);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        spacer.setPrefWidth(866);

        topBar.getChildren().addAll(
                roomTitle("Living Room"),
                spacer,
                livingScenesCard()
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        lroom.getChildren().addAll(
                topBar,
                cards
        );

        return lroom;
    }

    // ──── Light card ──────────────────────
    public VBox livingLightCard() {

        CardImages result = makeCard("lightIcon", "Lights", "lightsOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "lightsOn.png");
        Image offImg = new Image(IMAGES + "lightsOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = livingRoom.isLightsOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        toggle.setOnAction(e -> {
            livingRoom.setLightsOn(!livingRoom.isLightsOn());
            String mqttStatus = livingRoom.isLightsOn() ? "ON" : "OFF";
            mqttService.publish("home/livingroom/light", mqttStatus);
        });

        livingRoom.lightsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── AC card ─────────────────────────
    public VBox livingACCard() {

        CardImages result = makeCard("AC", "Air Conditioner", "acOff", 400);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "acOn.png");
        Image offImg = new Image(IMAGES + "acOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button plus = new Button("+");
        Button minus = new Button("-");
        Button toggle = new Button();

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        plus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );
        minus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );

        boolean initial = livingRoom.isAcOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        final int[] acTemp = {20};

        tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
        tempLabel.setText(acTemp[0] + " °C");

        toggle.setOnAction(e -> {
            boolean nextState = !livingRoom.isAcOn();
            livingRoom.setAcOn(nextState);
            mqttService.publish("home/livingroom/ac", nextState ? "ON" : "OFF");
        });

        livingRoom.acOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            plus.setDisable(!newVal);
            minus.setDisable(!newVal);
        });

        plus.setOnAction(e -> {
            if (!livingRoom.isAcOn()) return;
            if (acTemp[0] < 40) {
                acTemp[0] += 1;
                tempLabel.setText(acTemp[0] + "°C");
                mqttService.publish("home/livingroom/ac/temp", String.valueOf(acTemp[0]));
            }
        });

        minus.setOnAction(e -> {
            if (!livingRoom.isAcOn()) return;
            if (acTemp[0] > 10) {
                acTemp[0] -= 1;
                tempLabel.setText(acTemp[0] + "°C");
                mqttService.publish("home/livingroom/ac/temp", String.valueOf(acTemp[0]));
            }
        });

        HBox tempControls = new HBox(10, minus, tempLabel, plus);
        tempControls.setAlignment(Pos.CENTER);

        VBox controls = new VBox(12, status, tempControls, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── TV card ─────────────────────────
    public VBox livingTVCard() {

        CardImages result = makeCard("TV", "TV Screen", "TV", 300);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "TVOnLiv.png");
        Image offImg = new Image(IMAGES + "TVOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );


        toggle.setOnAction(e -> {
            boolean nextState = !livingRoom.isTvOn();
            livingRoom.setTvOn(nextState);
            mqttService.publish("home/livingroom/tv", nextState ? "ON" : "OFF");
        });

        boolean initial = livingRoom.isTvOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        livingRoom.tvOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Temp card ───────────────────────
    public VBox livingTempSliderCard() {

        CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);

        VBox card = result.card;

        Label valueLabel = new Label();
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        int initial = (int) livingRoom.getTemperature();
        valueLabel.setText(initial + " °C");


        VBox content = new VBox(10, valueLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Curtains card ────────────────────
    public VBox livingCurtainsCard() {
        CardImages resul = makeCard("curtains", "Curtains Control", "curtainsClose", 250);

        VBox card = resul.card;
        ImageView img = resul.imageView;

        Image openImg = new Image(IMAGES + "curtainsOpen.png");
        Image closeImg = new Image(IMAGES + "curtainsClose.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = livingRoom.isCurtainsOn();
        img.setImage(initial ? openImg : closeImg);
        styleStatusC(status, initial);
        toggle.setText(initial ? "Close" : "Open");

        toggle.setOnAction(e -> {
            boolean nextState = !livingRoom.isCurtainsOn();
            livingRoom.setCurtainsOn(nextState);
            mqttService.publish("home/livingroom/curtains", nextState ? "OPEN" : "CLOSE");
        });

        livingRoom.curtainsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? openImg : closeImg);
            styleStatusC(status, newVal);
            toggle.setText(newVal ? "Close" : "Open");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Scenes card ───────────────────────
    public VBox livingScenesCard() {

        VBox card = new VBox(12);

        card.setPadding(new Insets(12));
        cardHover(card);
        card.setPrefWidth(520);
        card.setPrefHeight(170);
        allCards.add(card);

        card.setStyle(
                cardStyle() +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 14, 0, 0, 4);"
        );

        HBox head = cardHead("quickActions", 20, "Quick Actions");

        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        actions.getChildren().addAll(
                livingQuickActionItem("movie", "Movie", "movie mode"),
                livingQuickActionItem("night", "Night", "night mode")
        );

        card.getChildren().addAll(head, actions);

        return card;
    }

    /*==========================================
     ─────────── Master Room Cards─────────────
    ==========================================*/

    // ──── 2. Master room─────────────────────
    public VBox buildMasterRoom() {

        VBox mroom = new VBox(0);
        mroom.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(masterLightCard(), 1, 1),
                new CardConfig(masterACCard(), 1, 1),
                new CardConfig(masterTempSliderCard(), 1, 1),
                new CardConfig(masterTVCard(), 1, 1),
                new CardConfig(masterDoorCard(), 1, 1),
                new CardConfig(masterBabySafeCard(), 1, 1)
        );

        HBox topBar = new HBox(0);
        topBar.setPrefWidth(50);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        spacer.setPrefWidth(850);

        topBar.getChildren().addAll(
                roomTitle("Master Room"),
                spacer,
                masterScenesCard()
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        mroom.getChildren().addAll(
                topBar,
                cards
        );

        return mroom;
    }

    // ──── Light card ──────────────────────
    public VBox masterLightCard() {

        CardImages result = makeCard("lightIcon", "Lights", "lightsOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "lightsOn.png");
        Image offImg = new Image(IMAGES + "lightsOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = masterRoom.isLightsOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        toggle.setOnAction(e -> {
            masterRoom.setLightsOn(!masterRoom.isLightsOn());
            String MasterRoomStatus = masterRoom.isLightsOn() ? "ON" : "OFF";
            mqttService.publish("home/masterroom/light", MasterRoomStatus);
        });

        masterRoom.lightsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── AC card ─────────────────────────
    public VBox masterACCard() {

        CardImages result = makeCard("AC", "Air Conditioner", "acOff", 400);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "acOn.png");
        Image offImg = new Image(IMAGES + "acOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button plus = new Button("+");
        Button minus = new Button("-");
        Button toggle = new Button();

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        plus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );
        minus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );

        boolean initial = masterRoom.isAcOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        final int[] acTemp = {20};

        tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
        tempLabel.setText(acTemp[0] + " °C");

        masterRoom.acOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            plus.setDisable(!newVal);
            minus.setDisable(!newVal);
        });

        plus.setOnAction(e -> {
            if (!masterRoom.isAcOn()) return;
            if (acTemp[0] < 40) {
                acTemp[0] += 1;
                tempLabel.setText(acTemp[0] + "°C");
            }
        });

        minus.setOnAction(e -> {
            if (!masterRoom.isAcOn()) return;
            if (acTemp[0] > 10) {
                acTemp[0] -= 1;
                tempLabel.setText(acTemp[0] + "°C");
            }
        });

        toggle.setOnAction(e ->
                masterRoom.setAcOn(!masterRoom.isAcOn())
        );

        HBox tempControls = new HBox(10, minus, tempLabel, plus);
        tempControls.setAlignment(Pos.CENTER);

        VBox controls = new VBox(12, status, tempControls, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── TV card ─────────────────────────
    public VBox masterTVCard() {

        CardImages result = makeCard("TV", "TV Screen", "TV", 300);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "TVOn.png");
        Image offImg = new Image(IMAGES + "TVOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                masterRoom.setTvOn(!masterRoom.isTvOn())
        );

        boolean initial = masterRoom.isTvOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        masterRoom.tvOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Temp card ───────────────────────
    public VBox masterTempSliderCard() {

        CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);

        VBox card = result.card;

        Label valueLabel = new Label();
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        int initial = (int) masterRoom.getTemperature();
        valueLabel.setText(initial + " °C");

        VBox content = new VBox(10, valueLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Doors card ───────────────────────
    public VBox masterDoorCard() {

        CardImages result = makeCard("door", "Doors Safety", "doorOpen", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image openImg = new Image(IMAGES + "doorOpen.png");
        Image closedImg = new Image(IMAGES + "doorClosed.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                masterRoom.setDoorOpen(!masterRoom.isDoorOpen())
        );

        boolean open = masterRoom.isDoorOpen();
        img.setImage(open ? openImg : closedImg);
        styleStatusC(status, open);
        toggle.setText(open ? "Close" : "Open");

        masterRoom.doorOpenProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? openImg : closedImg);
            styleStatusC(status, newVal);
            toggle.setText(newVal ? "Close" : "Open");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Baby safety ───────────────────────
    public VBox masterBabySafeCard() {

        CardImages result = makeCard("safeLock", "Baby Safety", "safe", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image safeImg = new Image(IMAGES + "Safe.png");
        Image unSafeImg = new Image(IMAGES + "unSafe.png");

        Label status = new Label();
        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;"
        );
        resetBtn.setVisible(false);

        boolean initial = kidsRoom.isSafe();
        img.setImage(initial ? unSafeImg : safeImg);
        styleStatusS(status, !initial);
        status.setText(initial ? "Baby is not Safe" : "Baby is Safe");
        resetBtn.setVisible(initial);

        kidsRoom.babySafetyProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? unSafeImg : safeImg);
            status.setText(newVal ? "Baby is not Safe" : "Baby is Safe");
            styleStatusS(status, !newVal);
            resetBtn.setVisible(newVal);
        });

        resetBtn.setOnAction(e -> {
            kidsRoom.setBabySafety(false);
            kidsRoom.setAwake(false);
            kidsRoom.setLightsOn(false);
            masterRoom.setLightsOn(false);
            masterRoom.setDoorOpen(false);
        });


        VBox controls = new VBox(10, status, resetBtn);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Scenes card ───────────────────────
    public VBox masterScenesCard() {

        VBox card = new VBox(12);

        card.setPadding(new Insets(12));
        cardHover(card);
        card.setPrefWidth(520);
        card.setPrefHeight(170);
        allCards.add(card);

        card.setStyle(
                cardStyle() +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 14, 0, 0, 4);"
        );

        HBox head = cardHead("quickActions", 20, "Quick Actions");

        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        actions.getChildren().addAll(
                masterQuickActionItem("sleep", "Sleep", "sleep mode"),
                masterQuickActionItem("relax", "Relax", "relax mode")
        );

        card.getChildren().addAll(head, actions);

        return card;
    }

    /*==========================================
     ─────────── Kids room cards──────────────
    ==========================================*/

    // ──── 3. Kids room ───────────────────────
    public VBox buildKidsRoom() {

        VBox kroom = new VBox(0);
        kroom.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(kidsLightCard(), 1, 1),
                new CardConfig(kidsACCard(), 1, 1),
                new CardConfig(kidsTempSliderCard(), 1, 1),
                new CardConfig(kidsAwakeCard(), 1, 1),
                new CardConfig(masterBabySafeCard(), 1, 1)
        );

        HBox topBar = new HBox(0);
        topBar.setPrefWidth(50);
        topBar.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        spacer.setPrefWidth(883);

        topBar.getChildren().addAll(
                roomTitle("Kids Room"),
                spacer,
                kidsScenesCard()
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        kroom.getChildren().addAll(
                topBar,
                cards
        );

        return kroom;
    }

    // ──── Light card ──────────────────────
    public VBox kidsLightCard() {

        CardImages result = makeCard("lightIcon", "Lights", "lightsOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "lightsOn.png");
        Image offImg = new Image(IMAGES + "lightsOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = kidsRoom.isLightsOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        toggle.setOnAction(e -> {
            kidsRoom.setLightsOn(!kidsRoom.isLightsOn());
            String KidsRoomStatus = kidsRoom.isLightsOn() ? "ON" : "OFF";
            mqttService.publish("home/kidsroom/light", KidsRoomStatus);
        });

        kidsRoom.lightsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── AC card ─────────────────────────
    public VBox kidsACCard() {

        CardImages result = makeCard("AC", "Air Conditioner", "acOff", 400);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "acOn.png");
        Image offImg = new Image(IMAGES + "acOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button plus = new Button("+");
        Button minus = new Button("-");
        Button toggle = new Button();

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        plus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );
        minus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );

        boolean initial = kidsRoom.isAcOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        final int[] acTemp = {20};

        tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
        tempLabel.setText(acTemp[0] + " °C");

        kidsRoom.acOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            plus.setDisable(!newVal);
            minus.setDisable(!newVal);
        });

        plus.setOnAction(e -> {
            if (!kidsRoom.isAcOn()) return;
            if (acTemp[0] < 40) {
                acTemp[0] += 1;
                tempLabel.setText(acTemp[0] + "°C");
            }
        });

        minus.setOnAction(e -> {
            if (!kidsRoom.isAcOn()) return;
            if (acTemp[0] > 10) {
                acTemp[0] -= 1;
                tempLabel.setText(acTemp[0] + "°C");
            }
        });

        toggle.setOnAction(e -> {
            boolean nextState = !kidsRoom.isAcOn();
            kidsRoom.setAcOn(nextState);
            mqttService.publish("home/kidsroom/ac", nextState ? "ON" : "OFF"); // ← missing
        });

        HBox tempControls = new HBox(10, minus, tempLabel, plus);
        tempControls.setAlignment(Pos.CENTER);

        VBox controls = new VBox(12, status, tempControls, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Temp card ───────────────────────
    public VBox kidsTempSliderCard() {

        Random random = new Random();
        CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);

        VBox card = result.card;

        Label valueLabel = new Label();
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        int initial = (int) kidsRoom.getTemperature();
        valueLabel.setText(initial + " °C");

        VBox content = new VBox(10, valueLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Awake card ─────────────────────────
    public VBox kidsAwakeCard() {

        CardImages result = makeCard("awake", "Baby Status", "asleep", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image asleep = new Image(IMAGES + "asleep.png");
        Image awake = new Image(IMAGES + "awake.png");

        img.setImage(asleep);

        kidsRoom.awakeProperty().addListener((obs, oldVal, newVal) -> {

            img.setImage(newVal ? awake : asleep);

            if (newVal) {
                kidsRoom.setLightsOn(true);
                kidsRoom.setAcOn(true);
            } else {
                kidsRoom.setLightsOn(false);
                kidsRoom.setAcOn(false);
            }
        });

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Scenes card ───────────────────────
    public VBox kidsScenesCard() {

        VBox card = new VBox(12);

        card.setPadding(new Insets(12));
        cardHover(card);
        card.setPrefWidth(520);
        card.setPrefHeight(170);
        allCards.add(card);

        card.setStyle(
                cardStyle() +
                        "-fx-background-radius: 18;" +
                        "-fx-border-radius: 18;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 14, 0, 0, 4);"
        );

        HBox head = cardHead("quickActions", 20, "Quick Actions");

        HBox actions = new HBox(18);
        actions.setAlignment(Pos.CENTER);

        actions.getChildren().addAll(
                kidsQuickActionItem("sleep", "Bed Time")
        );

        card.getChildren().addAll(head, actions);

        return card;
    }

    /*==========================================
     ─────────── Kitchen cards─────────────────
    ==========================================*/

    // ──── 4. Kitchen ─────────────────────────
    public VBox buildKitchen() {

        VBox kitRoom = new VBox(0);
        kitRoom.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(kitchenLightCard(), 1, 1),
                new CardConfig(kitchenStoveCard(), 1, 1),
                new CardConfig(kitchenFireDetectorCard(), 1, 1),
                new CardConfig(kitchenTempSlider(), 1, 1),
                new CardConfig(kitchenFridgeCard(), 1, 1),
                new CardConfig(kitchenDishWasherCard(), 1, 1)
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        kitRoom.getChildren().addAll(
                roomTitle("Kitchen"),
                cards
        );

        return kitRoom;
    }

    // ──── Light card ──────────────────────
    public VBox kitchenLightCard() {

        CardImages result = makeCard("lightIcon", "Lights", "lightsOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "lightsOn.png");
        Image offImg = new Image(IMAGES + "lightsOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = kitchen.isLightsOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        toggle.setOnAction(e -> {
            kitchen.setLightsOn(!kitchen.isLightsOn());
            String KitchenStatus = kitchen.isLightsOn() ? "ON" : "OFF";
            mqttService.publish("home/kitchen/light", KitchenStatus);
        });

        kitchen.lightsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Stove card ─────────────────────────
    public VBox kitchenStoveCard() {

        CardImages result = makeCard("stove", "Stove", "stoveOff", 160);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "stoveOn.png");
        Image offImg = new Image(IMAGES + "stoveOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button toggle = new Button();
        Button plus = new Button("+");
        Button minus = new Button("-");

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        plus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );

        minus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-background-radius: 8;" +
                "-fx-min-width: 40;"
        );

        double stoveMinTemp = 100;
        double stoveMaxTemp = 300;

        boolean initial = kitchen.isStoveOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1b55cf;");
        tempLabel.setText(kitchen.getStoveTemperature() + " °C");

        kitchen.stoveTemperatureProperty().addListener((obs, oldVal, newVal) -> {
            tempLabel.setText(String.format("%.1f °C", newVal.doubleValue()));
        });

        kitchen.stoveOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            plus.setDisable(!newVal);
            minus.setDisable(!newVal);
        });

        toggle.setOnAction(e ->
                kitchen.setStoveOn(!kitchen.isStoveOn())
        );

        plus.setOnAction(e -> {
            double v = kitchen.getStoveTemperature() + 5;
            if (v <= stoveMaxTemp) kitchen.setStoveTemperature(v);
        });

        minus.setOnAction(e -> {
            double v = kitchen.getStoveTemperature() - 5;
            if (v >= stoveMinTemp) kitchen.setStoveTemperature(v);
        });

        HBox tempControls = new HBox(10, minus, tempLabel, plus);
        tempControls.setAlignment(Pos.CENTER);

        VBox controls = new VBox(12, status, tempControls, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Fridge card ─────────────────────────
    public VBox kitchenFridgeCard() {

        CardImages result = makeCard("fridge", "Fridge", "fridgeOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "fridgeOn.png");
        Image offImg = new Image(IMAGES + "fridgeOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button toggle = new Button();
        Button plus = new Button("+");
        Button minus = new Button("-");

        double minTemp = -7;
        double maxTemp = 7;

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        plus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        minus.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        boolean initial = kitchen.isFridgeOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1b55cf;");
        tempLabel.setText(kitchen.getFridgeTemperature() + " °C");

        plus.setDisable(!initial);
        minus.setDisable(!initial);

        kitchen.fridgeTemperatureProperty().addListener((o, a, newVal) ->
                tempLabel.setText(newVal + " °C")
        );

        kitchen.fridgeOnProperty().addListener((o, a, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            plus.setDisable(!newVal);
            minus.setDisable(!newVal);
        });

        toggle.setOnAction(e ->
                kitchen.setFridgeOn(!kitchen.isFridgeOn())
        );

        plus.setOnAction(e -> {
            if (!kitchen.isFridgeOn()) return;
            kitchen.setFridgeTemperature(Math.min(maxTemp, kitchen.getFridgeTemperature() + 1));
        });

        minus.setOnAction(e -> {
            if (!kitchen.isFridgeOn()) return;
            kitchen.setFridgeTemperature(Math.max(minTemp, kitchen.getFridgeTemperature() - 1));
        });

        HBox tempControls = new HBox(10, minus, tempLabel, plus);
        tempControls.setAlignment(Pos.CENTER);

        VBox controls = new VBox(12, status, tempControls, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── DishWasher card ───────────────────────
    public VBox kitchenDishWasherCard() {

        CardImages result = makeCard("dishWasher", "Dish Washer", "dishWasherOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "dishWasherOn.png");
        Image offImg = new Image(IMAGES + "dishWasherOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                kitchen.setDishWasherOn(!kitchen.isDishWasherOn())
        );

        boolean initial = kitchen.isDishWasherOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        kitchen.dishWasherOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Fire detector card ───────────────────────
    public VBox kitchenFireDetectorCard() {

        CardImages result = makeCard("fire", "Fire Detector", "fireOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image fireOn = new Image(IMAGES + "fireOn.png");
        Image fireOff = new Image(IMAGES + "fireOff.png");

        Label status = new Label();
        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        boolean safe = kitchen.getTemperature() < 60;
        img.setImage(safe ? fireOff : fireOn);
        status.setText(safe ? "Safe" : "FIRE ALERT!");
        status.setStyle(
                safe ?
                        "-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;"
                        :
                        "-fx-text-fill: #d62828; -fx-font-size: 18px; -fx-font-weight: bold;"
        );

        kitchen.temperatureProperty().addListener((obs, oldVal, newVal) -> {
            boolean noFire = newVal.doubleValue() < 60;
            img.setImage(noFire ? fireOff : fireOn);
            status.setText(noFire ? "Safe" : "FIRE ALERT!");
            status.setStyle(
                    noFire ?
                            "-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;"
                            :
                            "-fx-text-fill: #d62828; -fx-font-size: 18px; -fx-font-weight: bold;"
            );

            if (!noFire) {
                kitchen.setStoveOn(false);
                kitchen.setDishWasherOn(false);
                kitchen.setFridgeOn(false);
                kitchen.setLightsOn(true);
            }
        });

        VBox content = new VBox(10, img, status);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Temp card ───────────────────────
    public VBox kitchenTempSlider() {

        CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);

        VBox card = result.card;

        Label valueLabel = new Label();
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        int initial = (int) kitchen.getTemperature();
        valueLabel.setText(initial + " °C");

        VBox content = new VBox(10, valueLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    /*==========================================
     ─────────── Bathroom cards─────────────────
    ==========================================*/

    // ──── 5. Bathroom ────────────────────────
    public VBox buildBathroom() {

        VBox bath = new VBox(0);
        bath.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(bathLightCard(), 1, 1),
                new CardConfig(bathDoorCard(), 1, 1),
                new CardConfig(bathHeaterCard(), 1, 1),
                new CardConfig(bathOccupiedCard(), 1, 1)
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        bath.getChildren().addAll(
                roomTitle("Bathroom"),
                cards
        );

        return bath;
    }

    // ──── Light card ──────────────────────
    public VBox bathLightCard() {

        CardImages result = makeCard("lightIcon", "Lights", "lightsOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "lightsOn.png");
        Image offImg = new Image(IMAGES + "lightsOff.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = bathroom.isLightsOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        toggle.setOnAction(e -> {
            bathroom.setLightsOn(!bathroom.isLightsOn());
            String BathroomStatus = bathroom.isLightsOn() ? "ON" : "OFF";
            mqttService.publish("home/bathroom/light", BathroomStatus);
        });

        bathroom.lightsOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Doors card ───────────────────────
    public VBox bathDoorCard() {

        CardImages result = makeCard("door", "Doors Status", "doorOpen", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image openImg = new Image(IMAGES + "doorOpen.png");
        Image closedImg = new Image(IMAGES + "doorClosed.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                bathroom.setDoorOpen(!bathroom.isDoorOpen())
        );

        boolean open = bathroom.isDoorOpen();
        img.setImage(open ? openImg : closedImg);
        styleStatusC(status, open);
        toggle.setText(open ? "Close" : "Open");

        bathroom.doorOpenProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? openImg : closedImg);
            styleStatusC(status, newVal);
            toggle.setText(newVal ? "Close" : "Open");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Heater card ─────────────────────────
    public VBox bathHeaterCard() {

        CardImages result = makeCard("heater", "Heater", "heaterOff", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "heaterOn.png");
        Image offImg = new Image(IMAGES + "heaterOff.png");

        Label status = new Label();
        Label tempLabel = new Label();

        Button toggle = new Button();

        toggle.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;"
        );

        tempLabel.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        toggle.setOnAction(e ->
                bathroom.setHeaterOn(!bathroom.isHeaterOn())
        );

        boolean initial = bathroom.isHeaterOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");
        tempLabel.setText((int) bathroom.getWaterTemperature() + " °C");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(5), e -> {

                    double temp = bathroom.getWaterTemperature();

                    if (bathroom.isHeaterOn()) {
                        temp += 0.1;
                    } else {
                        temp -= 0.1;
                    }

                    if (temp < 10) temp = 10;
                    if (temp > 70) temp = 70;

                    bathroom.setWaterTemperature(temp);
                    tempLabel.setText((int) temp + " °C");
                    bathroom.configHeater();
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        bathroom.heaterOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox controls = new VBox(12, status, tempLabel, toggle);
        controls.setAlignment(Pos.CENTER);
        card.getChildren().add(controls);

        return card;
    }

    // ──── Occupied card ─────────────────────────
    public VBox bathOccupiedCard() {

        Random random = new Random();

        CardImages result = makeCard("occupied", "Bathroom Status", "bathFree", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image occupiedImg = new Image(IMAGES + "bathOccupied.png");
        Image freeImg = new Image(IMAGES + "bathFree.png");

        Label status = new Label();
        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        boolean initial = bathroom.isOccupied();
        img.setImage(initial ? occupiedImg : freeImg);
        styleStatusF(status, !initial);

        bathroom.occupiedProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? occupiedImg : freeImg);
            styleStatusF(status, !newVal);
        });

        // ──── Changing over time ───────────────
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {

                    boolean state = random.nextBoolean();
                    bathroom.setOccupied(state);
                    bathroom.setDoorOpen(!state);
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();


        VBox content = new VBox(10, status);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    /*==========================================
    ─────────── Outdoors cards─────────────────
    ==========================================*/

    // ──── 6. Outdoors ────────────────────────
    public VBox buildOutdoors() {

        VBox out = new VBox(0);
        out.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(outWaterPump(), 1, 1),
                new CardConfig(outDoors(), 1, 1),
                new CardConfig(outTempSlider(), 1, 1),
                new CardConfig(outHumidity(), 1, 1),
                new CardConfig(outRainingStatus(), 1, 1),
                new CardConfig(outMotionSensor(), 1, 1)
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        out.getChildren().addAll(
                roomTitle("Outdoors"),
                cards
        );

        return out;
    }

    // ──── Water Pump card ──────────────────────
    public VBox outWaterPump() {

        CardImages result = makeCard("water", "Garden Watering", "waterOn.png", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "waterOn.png");
        Image offImg = new Image(IMAGES + "waterOff.png");

        Label status = new Label();
        Button toggle = new Button();

        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                waterSystem.setWateringOn(!waterSystem.isWateringOn())
        );

        boolean initial = waterSystem.isWateringOn();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);
        toggle.setText(initial ? "Turn OFF" : "Turn ON");

        waterSystem.wateringOnProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
            toggle.setText(newVal ? "Turn OFF" : "Turn ON");
        });

        VBox content = new VBox(10, status, toggle);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Doors card ───────────────────────
    public VBox outDoors() {

        CardImages result = makeCard("door", "Doors Status", "doorOpen", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image openImg = new Image(IMAGES + "doorOpen.png");
        Image closedImg = new Image(IMAGES + "doorClosed.png");

        Label status = new Label();
        Button toggle = new Button();

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 15px;" +
                        "-fx-background-radius: 10;"
        );

        toggle.setOnAction(e ->
                doorSecurity.setDoorOpen(!doorSecurity.isDoorOpen())
        );

        boolean open = doorSecurity.isDoorOpen();
        img.setImage(open ? openImg : closedImg);
        styleStatusC(status, open);
        toggle.setText(open ? "Close" : "Open");

        doorSecurity.doorOpenProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? openImg : closedImg);
            styleStatusC(status, newVal);
            toggle.setText(newVal ? "Close" : "Open");
        });

        VBox controls = new VBox(10, status, toggle);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Temp card ─────────────────────────
    public VBox outTempSlider() {

        Random random = new Random();
        CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);

        VBox card = result.card;

        Label valueLabel = new Label();
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        int initial = (int) tempSensor.getTemperature();
        valueLabel.setText(initial + " °C");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {

                    int current = (int) tempSensor.getTemperature();
                    int change = random.nextInt(-3, 4);
                    current += change;

                    if (current < 5) current = 5;
                    if (current > 40) current = 40;

                    tempSensor.setTemperature(current);
                    valueLabel.setText(current + " °C");
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        VBox content = new VBox(10, valueLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Humidity card ──────────────────────
    public VBox outHumidity() {

        CardImages result = makeCard("humidity", "Humidity Sensor", "humidity", 200);

        VBox card = result.card;

        Label humidityLabel = new Label();
        humidityLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #1b55cf;"
        );

        humidityLabel.setText((int) waterSystem.getHumidity() + " %");

        VBox content = new VBox(10, humidityLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Raining card ──────────────────────
    public VBox outRainingStatus() {

        Random random = new Random();

        CardImages result = makeCard("rains", "Weather Status", "rainsOff.png", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image rainingImg = new Image(IMAGES + "rainsOn.png");
        Image sunnyImg = new Image(IMAGES + "rainsOff.png");

        final boolean[] raining = {false};
        img.setImage(sunnyImg);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {

                    raining[0] = random.nextBoolean();

                    if (raining[0]) {
                        img.setImage(rainingImg);
                        waterSystem.setWateringOn(false);
                    } else {
                        img.setImage(sunnyImg);
                    }
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        return card;
    }

    // ──── Motion sensor card ─────────────────────────
    public VBox outMotionSensor() {

        Random random = new Random();

        CardImages result = makeCard("motion", "Motion Sensor", "noMotion", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image motionOn = new Image(IMAGES + "noMotion.png");
        Image motionOff = new Image(IMAGES + "motion.png");

        Label status = new Label();
        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        boolean initial = doorSecurity.isMotionDetected();
        img.setImage(!initial ? motionOn : motionOff);
        styleStatusM(status, !initial);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {

                    boolean motionDetected = random.nextBoolean();

                    if (motionDetected) {
                        img.setImage(motionOn);
                        doorSecurity.setMotionDetected(true);
                    } else {
                        img.setImage(motionOff);
                        doorSecurity.setDoorOpen(false);
                        doorSecurity.setMotionDetected(false);
                    }

                    styleStatusM(status, doorSecurity.isMotionDetected());
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        VBox content = new VBox(10, status);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── 7. Security ─────────────────────────
    public VBox buildSecurity() {

        VBox cam = new VBox(0);
        cam.setPadding(new Insets(20));

        GridPane cards = buildCards(
                new CardConfig(motionSensor(), 1, 1),
                new CardConfig(cameraVideo(), 1, 1),
                new CardConfig(nightVision(), 1, 1),
                new CardConfig(faceCard(), 1, 1),
                new CardConfig(recStorage(), 1, 1),
                new CardConfig(emergency(), 1, 1)
        );

        cards.setMaxWidth(Double.MAX_VALUE);
        cam.getChildren().addAll(
                roomTitle("Security"),
                cards
        );

        return cam;
    }

    // ──── Motion sensor card ─────────────────────────
    public VBox motionSensor() {

        Random random = new Random();

        CardImages result = makeCard("motion", "Motion Sensor", "noMotion", 200);

        VBox card = result.card;
        ImageView img = result.imageView;

        Image motionOn = new Image(IMAGES + "noMotion.png");
        Image motionOff = new Image(IMAGES + "motion.png");

        Label status = new Label();
        status.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        boolean initial = doorSecurity.isMotionDetected();
        img.setImage(!initial ? motionOn : motionOff);
        styleStatusM(status, !initial);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(15), e -> {

                    boolean motionDetected = random.nextBoolean();

                    if (motionDetected) {
                        img.setImage(motionOn);
                        doorSecurity.setMotionDetected(true);
                    } else {
                        img.setImage(motionOff);
                        doorSecurity.setDoorOpen(false);
                        doorSecurity.setMotionDetected(false);
                    }

                    styleStatusM(status, doorSecurity.isMotionDetected());
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        VBox content = new VBox(10, status);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Camera video card ─────────────────────────
    public VBox cameraVideo() {

        CardImages result = makeCard("cam", "Camera Video", "cam", 300);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "cam.png");
        Image offImg = new Image(IMAGES + "onCam.png");

        boolean initial = !doorSecurity.isMotionDetected();
        img.setImage(initial ? onImg : offImg);

        doorSecurity.motionDetectedProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
        });

        VBox controls = new VBox(10);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Night vision card ─────────────────────────
    public VBox nightVision() {

        CardImages result = makeCard("nightVision", "Night Vision", "visionOn", 250);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image onImg = new Image(IMAGES + "visionOn.png");
        Image offImg = new Image(IMAGES + "visionOff.png");

        Label status = new Label();
        Button toggle = new Button("Toggle");

        toggle.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;"
        );

        boolean initial = camera.isNightVision();
        img.setImage(initial ? onImg : offImg);
        styleStatusO(status, initial);

        toggle.setOnAction(e ->
                camera.setNightVision(!camera.isNightVision())
        );

        camera.nightVisionProperty().addListener((obs, oldVal, newVal) -> {
            img.setImage(newVal ? onImg : offImg);
            styleStatusO(status, newVal);
        });

        VBox content = new VBox(10, img, status, toggle);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Face card ─────────────────────────
    public VBox faceCard() {

        CardImages result = makeCard("face", "Face Recognition", "face", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image faceOn = new Image(IMAGES + "faceOn.png");
        Image faceOff = new Image(IMAGES + "faceOff.png");

        Label status = new Label("Waiting... ");
        status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");

        Button scanBtn = new Button("Scan");
        scanBtn.setStyle(
                "-fx-background-color: #1b55cf;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-background-radius: 10;"
        );

        img.setImage(faceOff);

        scanBtn.setOnAction(e -> {
            img.setImage(faceOn);
            status.setText("Face Detected");
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");

            Timeline reset = new Timeline(
                    new KeyFrame(Duration.seconds(3), ev -> {
                        img.setImage(faceOff);
                        status.setText("Waiting...");
                        status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                    })
            );

            reset.play();
        });

        VBox controls = new VBox(10, status, scanBtn);
        controls.setAlignment(Pos.CENTER);

        card.getChildren().add(controls);

        return card;
    }

    // ──── Storage card ─────────────────────────
    public VBox recStorage() {

        CardImages result = makeCard("storage", "Recording Storage", "good", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image goodImg = new Image(IMAGES + "good.png");
        Image fullImg = new Image(IMAGES + "full.png");

        Label sizeLabel = new Label();

        final int[] size = {0};
        final boolean[] isFull = {false};

        img.setImage(goodImg);
        sizeLabel.setText("0 MB");
        sizeLabel.setStyle("-fx-text-fill: #1b55cf; -fx-font-size: 16px; -fx-font-weight: bold;");

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.minutes(30), e -> {

                    if (isFull[0]) return;

                    size[0]++;

                    if (size[0] >= 521) {
                        size[0] = 521;
                        isFull[0] = true;
                        img.setImage(fullImg);
                        sizeLabel.setText("FULL 521 MB");
                        sizeLabel.setStyle("-fx-text-fill: #1b55cf; -fx-font-size: 16px; -fx-font-weight: bold;");
                    } else {
                        sizeLabel.setText(size[0] + " MB");
                        sizeLabel.setStyle("-fx-text-fill: #1b55cf; -fx-font-size: 16px; -fx-font-weight: bold;");
                    }
                })
        );

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        VBox content = new VBox(10, img, sizeLabel);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Emergency card ─────────────────────────
    public VBox emergency() {

        CardImages result = makeCard("emergency", "Emergency System", "noEmerg", 200);
        VBox card = result.card;
        ImageView img = result.imageView;

        Image safeImg = new Image(IMAGES + "noEmerg.png");
        Image dangerImg = new Image(IMAGES + "emerg.png");


        boolean initial = camera.getEmergency();
        img.setImage(initial ? dangerImg : safeImg);

        doorSecurity.motionDetectedProperty().addListener((obs, oldVal, newVal) -> {

            if (!newVal) {
                img.setImage(dangerImg);
                masterRoom.setDoorOpen(true);
                doorSecurity.setDoorOpen(true);
            } else {
                img.setImage(safeImg);
                masterRoom.setDoorOpen(false);
                doorSecurity.setDoorOpen(false);
            }
        });

        VBox content = new VBox(10, img);
        content.setAlignment(Pos.CENTER);

        card.getChildren().add(content);

        return card;
    }

    // ──── Status styles ────────────────────────────────────────────

    public void styleStatusO(Label status, boolean state) {
        if (state) {
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("ON");
        } else {
            status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("OFF");
        }
    }

    public void styleStatusC(Label status, boolean state) {
        if (state) {
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Opened");
        } else {
            status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Closed");
        }
    }

    public void styleStatusF(Label status, boolean state) {
        if (state) {
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Free");
        } else {
            status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Occupied");
        }
    }

    public void styleStatusS(Label status, boolean state) {
        if (state) {
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Baby is safe");
        } else {
            status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Baby is not safe");
        }
    }

    public void styleStatusM(Label status, boolean state) {
        if (state) {
            status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("No motion detected");
        } else {
            status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
            status.setText("Motion detected");
        }
    }

    // ──── Alarm ───────────────────
    public VBox buildAlarmCards() {
        VBox alarmCards = new VBox(20);
        alarmCards.setPadding(new

                Insets(20));


        HBox topBar = new HBox(0);
        topBar.setPrefWidth(50);
        topBar.setAlignment(Pos.CENTER_LEFT);

        topBar.getChildren().

                addAll(
                        roomTitle("Alarm"));


        alarmCards.setMaxWidth(Double.MAX_VALUE);
        alarmCards.getChildren().addAll(
                topBar,
                card1()
        );

        return alarmCards;
    }

    // ──── Alarm ───────────────────
    public VBox card1() {

        VBox card = new VBox(15);
        card.setPadding(new Insets(15));
        card.setPrefWidth(300);
        card.setPrefHeight(160);

        card.setStyle("-fx-background-color: white;" +
                "-fx-background-radius: 18;" +
                "-fx-border-radius: 18;" +
                "-fx-border-color: #dfe7f2;");

        Label title = new Label("Alarm 1");
        title.setStyle("-fx-font-size: 20px;-fx-font-weight: bold;-fx-text-fill: #c21e0c;");

        ComboBox<String> hourBox = new ComboBox<>();
        for (int i = 1; i <= 12; i++) hourBox.getItems().add(String.format("%02d", i));
        hourBox.setValue("07");

        ComboBox<String> minuteBox = new ComboBox<>();
        for (int i = 0; i < 60; i++) minuteBox.getItems().add(String.format("%02d", i));
        minuteBox.setValue("00");

        ComboBox<String> ampmBox = new ComboBox<>();
        ampmBox.getItems().addAll("AM", "PM");
        ampmBox.setValue("AM");

        HBox timeRow = new HBox(10, hourBox, minuteBox, ampmBox);
        timeRow.setAlignment(Pos.CENTER);

        Label status = new Label("Alarm OFF");
        status.setStyle("-fx-font-size: 15px;-fx-font-weight: bold;-fx-text-fill: #1b55cf;");

        Label msg = new Label();
        msg.setStyle("-fx-font-size: 13px;-fx-text-fill: #666;");

        Button enableBtn = new Button("Enable Alarm");
        enableBtn.setStyle("-fx-background-color: #085405;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;");

        Button stopBtn = new Button("Stop Alarm");
        stopBtn.setStyle("-fx-background-color: #cf0c0c;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;");

        Button disableBtn = new Button("Disable Alarm");
        disableBtn.setStyle("-fx-background-color: #1b55cf;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 15px;" +
                "-fx-background-radius: 10;");

        stopBtn.setVisible(false);

        Timeline[] timeline = new Timeline[1];

        enableBtn.setOnAction(e -> {

            int hour = Integer.parseInt(hourBox.getValue());
            int minute = Integer.parseInt(minuteBox.getValue());
            String period = ampmBox.getValue();

            if (period.equals("PM") && hour != 12) hour += 12;
            if (period.equals("AM") && hour == 12) hour = 0;

            LocalTime alarmTime = LocalTime.of(hour, minute);

            status.setText("Alarm ON");
            msg.setText("Alarm set for " + alarmTime);

            timeline[0] = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {

                LocalTime now = LocalTime.now();

                if (now.getHour() == alarmTime.getHour()
                        && now.getMinute() == alarmTime.getMinute()
                        && now.getSecond() == 0) {

                    status.setText("ALARM RINGING");
                    msg.setText("Press stop to turn off");
                    stopBtn.setVisible(true);

                    masterRoom.setAlarm(alarmTime);
                }
            }));

            timeline[0].setCycleCount(Animation.INDEFINITE);
            timeline[0].play();
        });

        stopBtn.setOnAction(e -> {
            masterRoom.stopAlarm();
            status.setText("Alarm OFF");
            msg.setText("");
            stopBtn.setVisible(false);
        });

        disableBtn.setOnAction(e -> {
            if (timeline[0] != null) timeline[0].stop();
            masterRoom.stopAlarm();
            status.setText("Alarm Disabled");
            msg.setText("");
            stopBtn.setVisible(false);
        });

        HBox buttons = new HBox(10, enableBtn, stopBtn, disableBtn);
        card.getChildren().addAll(title, timeRow, status, msg, buttons);

        allCards.add(card);

        return card;
    }
}
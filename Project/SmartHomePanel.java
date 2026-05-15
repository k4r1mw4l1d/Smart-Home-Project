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
    
    import java.util.Scanner;
    import java.util.Random;
    
    
    public class SmartHomePanel extends Application {
    
        private static final String ICONS = "file:C:/Users/strea/Documents/engineering/Second term CSE/Smart Hoe/Smart-Home-Project/Project/resources/icons/";
        private static final String IMAGES = "file:C:/Users/strea/Documents/engineering/Second term CSE/Smart Hoe/Smart-Home-Project/Project/resources/images/";
        private ArrayList<VBox> allCards = new ArrayList<>();
        private ArrayList<Label> allCardsLabels = new ArrayList<>();
        private ArrayList<Label> allRoomsLabels = new ArrayList<>();
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
        MQTT mqttService = new MQTT();
    
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
                    25
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
                    25,
                    30
            );

            waterSystem = new GardenWaterSystem(
                    "8",
                    "Water system",
                    "Outdoors",
                    false,
                    20.5,
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
                    false,
                    "1080",
                    512,
                    false
            );

            // ───── 3. Connect Program to cloud (The Right Way) ─────
            // ملحوظة: شيلنا تعريف MQTT المتكرر عشان نستخدم المتغير اللي فوق (Global)
            mqttService.setModels(livingRoom, masterRoom, kitchen, bathroom); // نربط الموديل الأول
            mqttService.connect();                      // نفتح الاتصال بعد ما الداتا بقت جاهزة

            // ───── 4. UI Setup ───────────────────────────────────
            showWelcomeScreen();

            Scene scene = new Scene(root, 1100, 700);
            primaryStage.setScene(scene);
            //primaryStage.setFullScreen(true);
            //primaryStage.setFullScreenExitHint("");
            primaryStage.setTitle("Smart Home");
            primaryStage.setMaximized(true);
            primaryStage.show();
        }
        //─────Main program ───────────────────────────
        public void loadDashboard() {
    
            root.setLeft(buildSidebar());
            root.setCenter(buildDashboard());
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
                            "-fx-background-radius: 12;" +
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
                loadDashboard();
            });
    
            enterName.setOnKeyPressed(e -> {
                name = enterName.getText();
    
                if (e.getCode() == KeyCode.ENTER) {
                    loadDashboard();
                }
            });
            //──────────── Connect Program to cloud ────────
            mqttService.setModels(livingRoom, masterRoom, kitchen, bathroom);
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
    
            // ─────Sidebar -> rooms list─────────────
            HBox rooms = navRows("room", 24, "Rooms", "#c2c2c2", 16);
            mouseHover(rooms, "#1d3e6e", "#0a1e3d");
    
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
            mouseHover(themes, "#133466", "#0a1e3d");
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
            mouseHover(alarm, "#133466", "#0a1e3d");
            alarm.setOnMouseClicked(e -> {
    
                setScreen(buildAlarm());
                setActive(alarm, "#133466");
    
            });
    
            side.getChildren().addAll(alarm);
    
            // ─────Empty region to fill space─────────────
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            side.getChildren().add(spacer);
    
            // ─────User icon───────────────────────
            side.getChildren().add(navRows("avatar", 50, name, "#c2c2c2", 16));
    
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

            // --- التعديل هنا ---
            ScrollPane scrollPane = new ScrollPane(onScreen);
            scrollPane.setFitToWidth(true); // عشان يملأ العرض
            scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;"); // لإخفاء الحدود

            VBox.setVgrow(scrollPane, Priority.ALWAYS); // عشان ياخد باقي مساحة الطول
            dashboard.getChildren().add(scrollPane);

            return dashboard;
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
    
            mouseHover(living, "#133466", "#12294a");
    
            living.setOnMouseClicked(e -> {
                setScreen(buildLivingRoom());
                setActive(living, "#133466");
            });
    
            // ─────Master room─────────────────────
            HBox master = navRows("masterRoom", 20, "Master room", "#c2c2c2", 14);
    
            mouseHover(master, "#133466", "#12294a");
    
            master.setOnMouseClicked(e -> {
                setScreen(buildMasterRoom());
                setActive(master, "#133466");
            });
    
            // ─────Kids room───────────────────────
            HBox kids = navRows("kidsRoom", 20, "Kids room", "#c2c2c2", 14);
    
            mouseHover(kids, "#133466", "#12294a");
    
            kids.setOnMouseClicked(e -> {
                setScreen(buildKidsRoom());
                setActive(kids, "#133466");
            });
    
            // ─────Kitchen─────────────────────────
            HBox kitchen = navRows("kitchen", 20, "Kitchen", "#c2c2c2", 14);
    
            mouseHover(kitchen, "#133466", "#12294a");
    
            kitchen.setOnMouseClicked(e -> {
                setScreen(buildKitchen());
                setActive(kitchen, "#133466");
            });
    
            // ─────Bathroom────────────────────────
            HBox bath = navRows("bathroom", 20, "Bathroom", "#c2c2c2", 14);
    
            mouseHover(bath, "#133466", "#12294a");
    
            bath.setOnMouseClicked(e -> {
                setScreen(buildBathroom());
                setActive(bath, "#133466");
            });
    
            // ─────Outdoors────────────────────────
            HBox outdoors = navRows("outdoors", 20, "Outdoors", "#c2c2c2", 14);
    
            mouseHover(outdoors, "#133466", "#12294a");
    
            outdoors.setOnMouseClicked(e -> {
                setScreen(buildOutdoors());
                setActive(outdoors, "#133466");
            });
    
            // ─────Cameras─────────────────────────
            HBox cameras = navRows("camera", 20, "Security", "#c2c2c2", 14);
    
            mouseHover(cameras, "#133466", "#12294a");
    
            cameras.setOnMouseClicked(e -> {
                setScreen(buildSecurity());
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
    
            // ─────Header -> Date, Month, and time─────────────────────
            dateLabel = new Label(currentDateTime());
            startClock();
            dateLabel.setStyle("-fx-font-size: 16px;-fx-text-fill: #000000;");
            header.getChildren().add(dateLabel);
    
            // ─────Empty region to fill space─────────────
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            header.getChildren().add(spacer);
    
            //─────Header -> icons─────────────────────
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
            GridPane.setHgrow(grid, Priority.ALWAYS);
            GridPane.setVgrow(grid, Priority.ALWAYS);
    
    
            grid.setHgap(20);
            grid.setVgap(20);
            grid.setPadding(new Insets(24));
    
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(33.3);
            c.setHgrow(Priority.ALWAYS);
    
            grid.getColumnConstraints().addAll(c, c, c);
    
            int col = 0;
            int row = 0;
    
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
                System.out.println("Image not found");
            }
    
            VBox imgBox = new VBox(imageView);
            imgBox.setAlignment(Pos.CENTER);
            VBox.setVgrow(imgBox, Priority.ALWAYS);
    
            // ───── Add to card ─────
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
    
        // ─────Mouse hover on box─────────────────────
    
        public void mouseHover(HBox item, String onColor, String offColor) {
    
            item.setOnMouseEntered(e -> {
    
                item.setStyle(
                        "-fx-background-color: " + onColor + ";" +
                                "-fx-background-radius: 15;"
                );
            });
    
            item.setOnMouseExited(e -> {
    
                if (item != activeRoom) {
    
                    item.setStyle(
                            "-fx-background-color: " + offColor + ";"
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
                                "-fx-font-size: 30;"
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
        }
    
        /*==========================================
         ─────────── Building Dashboard─────────────
         ==========================================*/
    
        // ────Switching scenes─────────────────────
        public void setScreen(Pane page) {
    
            onScreen.getChildren().clear();
            onScreen.getChildren().add(page);
        }
    
        // ──── Set color on click─────────────────────────
        public void setActive(HBox item, String activeColor) {
    
            if (activeRoom != null) {
    
                String normalColor = (activeRoom.getParent() == roomsMenu)
                        ? "#12294a"
                        : "#0a1e3d";
    
                activeRoom.setStyle(
                        "-fx-background-color: " + normalColor + ";"
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
         ─────────── Building sidebar─────────────
        ==========================================*/
    
        /*==========================================
         ─────────── Building Rooms─────────────
        ==========================================*/
    
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
    
            // ─────Empty region to fill space─────────────
            Region spacer = new Region();
            spacer.setPrefWidth(866);
    
            // ───── Quick Actions ────────────────────
            HBox actions = new HBox(18);
            actions.setAlignment(Pos.CENTER);
    
            // ───── Quick Actions (icons & buttons) ───────────
            VBox movie = livingQuickActionItem("movie", "Movie mode", "movie mode");
            movie.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
            VBox night = livingQuickActionItem("night", "Night mode", "night mode");
            night.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
    
            actions.getChildren().addAll(movie, night);
    
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
    
        // ──── Light cards──────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = livingRoom.isLightsOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            toggle.setOnAction(e -> {
                livingRoom.setLightsOn(!livingRoom.isLightsOn());
                String mqttStatus = livingRoom.isLightsOn() ? "ON" : "OFF";
                mqttService.publish("home/livingroom/light", mqttStatus);
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── AC cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = livingRoom.isAcOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
    
            final int[] acTemp = {20}; // to be able to change later
    
            tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
            tempLabel.setText(acTemp[0] + " °C");
    
            // ──── Changing (image - status - bt)──────────────────────
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
                }
            });
    
            minus.setOnAction(e -> {
                if (!livingRoom.isAcOn()) return;
    
                if (acTemp[0] > 10) {
                    acTemp[0] -= 1;
                    tempLabel.setText(acTemp[0] + "°C");
                }
            });

            toggle.setOnAction(e -> {
                boolean nextState = !livingRoom.isAcOn();
                mqttService.publish("home/livingroom/ac", nextState ? "ON" : "OFF");
            });
    
            HBox tempControls = new HBox(10, minus, tempLabel, plus);
            tempControls.setAlignment(Pos.CENTER);
    
            VBox controls = new VBox(12,
                    status,
                    tempControls,
                    toggle
            );
    
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── TV cards─────────────────────────
        public VBox livingTVCard() {
    
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

            toggle.setOnAction(e -> {
                boolean nextState = !livingRoom.isTvOn();
                mqttService.publish("home/livingroom/tv", nextState ? "ON" : "OFF");
            });
    
    
            // ──── Making the room for first time──────────────────────
            boolean initial = livingRoom.isTvOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Temp cards───────────────────────
        public VBox livingTempSliderCard() {
    
            Random random = new Random();
            CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);
    
            VBox card = result.card;
    
            Label valueLabel = new Label();
            valueLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1b55cf;"
            );
    
    
            // ──── Making the room for first time──────────────────────
            int initial = (int) livingRoom.getTemperature();
            valueLabel.setText(initial + " °C");
    
            // ──── Change temp over time──────────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
    
                        int current = (int) livingRoom.getTemperature();
    
                        int change = random.nextInt(-3, 4);
                        current += change;
    
                        //threshold
                        if (current < 5) current = 5;
                        if (current > 40) current = 40;
    
                        livingRoom.setTemperature(current);
    
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
    
        // ──── Curtains cards────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = livingRoom.isLightsOn();
            img.setImage(initial ? openImg : closeImg);
            styleStatusC(status, initial);
            toggle.setText(initial ? "Close" : "Open");

            toggle.setOnAction(e -> {
                boolean nextState = !livingRoom.isCurtainsOn();
                mqttService.publish("home/livingroom/curtains", nextState ? "OPEN" : "CLOSE");
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Scenes cards───────────────────────
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
    
            // ───── Title ─────
            HBox head = cardHead("quickActions", 20, "Quick Actions");
    
            // ───── Actions ─────
            HBox actions = new HBox(18);
            actions.setAlignment(Pos.CENTER);
    
            actions.getChildren().addAll(
                    livingQuickActionItem("movie", "Movie", "movie mode"),
                    livingQuickActionItem("night", "Night", "night mode")
            );
    
            card.getChildren().addAll(head, actions);
    
            return card;
        }
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
    
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
    
            // ─────Empty region to fill space─────────────
            Region spacer = new Region();
            spacer.setPrefWidth(850);
    
            // ───── Quick Actions ────────────────────
            HBox actions = new HBox(18);
            actions.setAlignment(Pos.CENTER);
    
            // ───── Quick Actions (icons & buttons) ───────────
            VBox sleep = masterQuickActionItem("sleep", "Sleep mode", "sleep mode");
            sleep.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
            //VBox romance = masterQuickActionItem("romance", "Romance mode", "romance mode");
            //romance.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
            VBox relax = masterQuickActionItem("relax", "Relax mode", "relax mode");
            relax.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
            actions.getChildren().addAll(sleep ,relax);
    
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
    
        // ──── Light cards──────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = masterRoom.isLightsOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");

            toggle.setOnAction(e -> {
                masterRoom.setLightsOn(!masterRoom.isLightsOn());
                String MasterRoomStatus = masterRoom.isLightsOn() ? "ON" : "OFF";
                mqttService.publish("home/masterroom/light", MasterRoomStatus);
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── AC cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = masterRoom.isAcOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
    
            final int[] acTemp = {20}; // to be able to change later
    
            tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
            tempLabel.setText(acTemp[0] + " °C");
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
            VBox controls = new VBox(12,
                    status,
                    tempControls,
                    toggle
            );
    
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── TV cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Temp cards───────────────────────
        public VBox masterTempSliderCard() {
    
            Random random = new Random();
            CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);
    
            VBox card = result.card;
    
            Label valueLabel = new Label();
            valueLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1b55cf;"
            );
    
    
            // ──── Making the room for first time──────────────────────
            int initial = (int) masterRoom.getTemperature();
            valueLabel.setText(initial + " °C");
    
            // ──── Change temp over time──────────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
    
                        int current = (int) masterRoom.getTemperature();
    
                        int change = random.nextInt(-3, 4);
                        current += change;
    
                        //threshold
                        if (current < 5) current = 5;
                        if (current > 40) current = 40;
    
                        masterRoom.setTemperature(current);
    
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
    
        // ──── Doors cards───────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean open = masterRoom.isDoorOpen();
            img.setImage(open ? openImg : closedImg);
            styleStatusC(status, open);
            toggle.setText(open ? "Close" : "Open");
    
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Baby safety───────────────────────
        public VBox masterBabySafeCard() {
    
            Random random = new Random();
    
            CardImages result = makeCard(
                    "safeLock",
                    "Baby Safety",
                    "safe",
                    200
            );
    
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kidsRoom.isSafe();
    
            img.setImage(initial ? unSafeImg : safeImg);
            styleStatusS(status, !initial);
            status.setText(initial ? "Baby is not Safe" : "Baby is Safe");
            resetBtn.setVisible(initial);
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
            // ──── Changing status over time for demonstration ──────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> {
    
                        boolean unsafe = random.nextBoolean();
    
                        if (unsafe) {
                            kidsRoom.setBabySafety(true);
    
                            masterRoom.setLightsOn(true);
                            masterRoom.setDoorOpen(true);
    
                        } else {
                            kidsRoom.setBabySafety(false);
    
                            kidsRoom.setAwake(false);
                            kidsRoom.setLightsOn(false);
    
                            masterRoom.setLightsOn(false);
                            masterRoom.setDoorOpen(false);
                        }
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox controls = new VBox(10, status, resetBtn);
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── Scenes cards───────────────────────
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
                    //masterQuickActionItem("romance", "Romance", "romance mode"),
                    masterQuickActionItem("relax", "Relax", "relax mode")
            );
    
            card.getChildren().addAll(head, actions);
    
            return card;
        }
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        /*==========================================
         ─────────── Kids room cards──────────────
        ==========================================*/
    
        // ──── 3. Kids room───────────────────────
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
    
            // ─────Empty region to fill space─────────────
            Region spacer = new Region();
            spacer.setPrefWidth(883);
    
            // ───── Quick Actions ────────────────────
            HBox actions = new HBox(18);
            actions.setAlignment(Pos.CENTER);
    
            // ───── Quick Actions (icons & buttons) ───────────
            VBox sleep = kidsQuickActionItem("sleep", "Bed time");
            sleep.setStyle("-fx-background-color: #12294a; -fx-background-radius: 12; -fx-padding: 10;");
    
    
            actions.getChildren().addAll(sleep);
    
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
    
        // ──── Light cards──────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kidsRoom.isLightsOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");

            toggle.setOnAction(e -> {
                kidsRoom.setLightsOn(!kidsRoom.isLightsOn());
                String KidsRoomStatus = kidsRoom.isLightsOn() ? "ON" : "OFF";
                mqttService.publish("home/kidsroom/light", KidsRoomStatus);
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── AC cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kidsRoom.isAcOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
    
            final int[] acTemp = {20}; // to be able to change later
    
            tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;-fx-text-fill: #1b55cf;");
            tempLabel.setText(acTemp[0] + " °C");
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
            toggle.setOnAction(e ->
                    kidsRoom.setAcOn(!kidsRoom.isAcOn())
            );
    
            HBox tempControls = new HBox(10, minus, tempLabel, plus);
            tempControls.setAlignment(Pos.CENTER);
    
            VBox controls = new VBox(12,
                    status,
                    tempControls,
                    toggle
            );
    
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── Temp cards───────────────────────
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
    
    
            // ──── Making the room for first time──────────────────────
            int initial = (int) kidsRoom.getTemperature();
            valueLabel.setText(initial + " °C");
    
            // ──── Change temp over time──────────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> {
    
                        int current = (int) kidsRoom.getTemperature();
    
                        int change = random.nextInt(-3, 4);
                        current += change;
    
                        //threshold
                        if (current < 5) current = 5;
                        if (current > 40) current = 40;
    
                        kidsRoom.setTemperature(current);
    
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
    
        // ──── Awake cards─────────────────────────
        public VBox kidsAwakeCard() {
    
            Random random = new Random();
            CardImages result = makeCard("awake", "Baby Status", "asleep", 200);
    
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image asleep = new Image(IMAGES + "asleep.png");
            Image awake = new Image(IMAGES + "awake.png");
    
            // ──── Making the room for first time──────────────────────
            img.setImage(asleep);
    
            // ──── Changing (image)──────────────────────
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
    
    
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(28), e -> {
    
                        boolean state = random.nextBoolean();
    
                        kidsRoom.setAwake(state);
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox controls = new VBox(10);
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── Scenes cards───────────────────────
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
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        /*==========================================
         ─────────── Kitchen cards─────────────────
        ==========================================*/
    
        // ──── 4. Kitchen─────────────────────────
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
    
        // ──── Light cards──────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kitchen.isLightsOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");

            toggle.setOnAction(e -> {
                kitchen.setLightsOn(!kitchen.isLightsOn());
                String KitchenStatus = kitchen.isLightsOn() ? "ON" : "OFF";
                mqttService.publish("home/kitchen/light", KitchenStatus);
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Stove cards─────────────────────────
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
    
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kitchen.isStoveOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1b55cf;");
            tempLabel.setText(kitchen.getStoveTemperature() + " °C");
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
            VBox controls = new VBox(12,
                    status,
                    tempControls,
                    toggle
            );
    
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── Fridge cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kitchen.isFridgeOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            tempLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1b55cf;");
            tempLabel.setText(kitchen.getFridgeTemperature() + " °C");
    
            plus.setDisable(!initial);
            minus.setDisable(!initial);
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── DishWasher cards───────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = kitchen.isDishWasherOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Fire detector cards───────────────────────
        public VBox kitchenFireDetectorCard() {
    
            CardImages result = makeCard("fire", "Fire Detector", "fireOff", 200);
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image fireOn = new Image(IMAGES + "fireOn.png");
            Image fireOff = new Image(IMAGES + "fireOff.png");
    
            Label status = new Label();
    
            status.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
    
            // ──── Making the room for first time──────────────────────
            boolean safe = kitchen.getTemperature() < 60;
    
            img.setImage(safe ? fireOff : fireOn);
            status.setText(safe ? "Safe" : "FIRE ALERT!");
    
            status.setStyle(
                    safe ?
                            "-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;"
                            :
                            "-fx-text-fill: #d62828; -fx-font-size: 18px; -fx-font-weight: bold;"
            );
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Temp cards───────────────────────
        public VBox kitchenTempSlider() {
    
            Random random = new Random();
            CardImages result = makeCard("temp", "Temperature Sensor", "tempSlider", 200);
    
            VBox card = result.card;
    
            Label valueLabel = new Label();
    
            valueLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1b55cf;"
            );
    
            // ──── initial ────
            int initial = (int) kitchen.getTemperature();
            valueLabel.setText(initial + " °C");
    
            // ──── auto change ────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
    
                        int current = (int) kitchen.getTemperature();
    
                        int change = random.nextInt(-3, 4);
                        current += change;
    
                        if (current < 5) current = 5;
                        if (current > 80) current = 80;
    
                        kitchen.setTemperature(current);
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
    
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        /*==========================================
         ─────────── Bathroom cards─────────────────
        ==========================================*/
    
        // ──── 5. Bathroom────────────────────────
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
    
        // ──── Light cards──────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = bathroom.isLightsOn();
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");

            toggle.setOnAction(e -> {
                bathroom.setLightsOn(!bathroom.isLightsOn());
                String BathroomStatus = bathroom.isLightsOn() ? "ON" : "OFF";
                mqttService.publish("home/bathroom/light", BathroomStatus);
            });
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Doors cards───────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean open = bathroom.isDoorOpen();
            img.setImage(open ? openImg : closedImg);
            styleStatusC(status, open);
            toggle.setText(open ? "Close" : "Open");
    
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Heater cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────
            boolean initial = bathroom.isHeaterOn();
    
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
            tempLabel.setText((int) bathroom.getWaterTemperature() + " °C");
    
    
            // ───── Changing over time───────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(2), e -> {
    
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
    
            // ──── Changing (image - status - bt)──────────────────────
            bathroom.heaterOnProperty().addListener((obs, oldVal, newVal) -> {
    
                img.setImage(newVal ? onImg : offImg);
                styleStatusO(status, newVal);
                toggle.setText(newVal ? "Turn OFF" : "Turn ON");
            });
    
            VBox controls = new VBox(12,
                    status,
                    tempLabel,
                    toggle
            );
    
            controls.setAlignment(Pos.CENTER);
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── Occupied cards─────────────────────────
        public VBox bathOccupiedCard() {
    
            Random random = new Random();
    
            CardImages result = makeCard("occupied", "Bathroom Status", "bathFree", 200);
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image occupiedImg = new Image(IMAGES + "bathOccupied.png");
            Image freeImg = new Image(IMAGES + "bathFree.png");
    
            Label status = new Label();
            status.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
    
            // ──── Making the room for first time──────────────────────
            boolean initial = bathroom.isOccupied();
            img.setImage(initial ? occupiedImg : freeImg);
            styleStatusF(status, !initial);
    
            // ──── Changing (image - status)──────────────────────
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
                        bathroom.setLightsOn(state);
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox content = new VBox(10, status);
            content.setAlignment(Pos.CENTER);
    
            card.getChildren().add(content);
    
            return card;
        }
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        /*==========================================
        ─────────── Outdoors cards─────────────────
        ==========================================*/
    
        // ──── 6. Outdoors────────────────────────
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
    
            status.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
    
            toggle.setStyle(
                    "-fx-background-color: #1b55cf;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 15px;" +
                            "-fx-background-radius: 10;"
            );
    
            toggle.setOnAction(e -> {
    
                waterSystem.setWateringOn(
                        !waterSystem.isWateringOn()
                );
            });
    
            // ──── Making the room for first time──────────────────────
            boolean initial = waterSystem.isWateringOn();
    
            img.setImage(initial ? onImg : offImg);
    
            styleStatusO(status, initial);
    
            toggle.setText(initial ? "Turn OFF" : "Turn ON");
    
    
            // ──── Changing (image - status - but) ──────────────────────
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
    
        // ──── Doors cards───────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean open = doorSecurity.isDoorOpen();
            img.setImage(open ? openImg : closedImg);
            styleStatusC(status, open);
            toggle.setText(open ? "Close" : "Open");
    
    
            // ──── Changing (image - status - bt)──────────────────────
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
    
        // ──── Temp cards─────────────────────────
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
    
    
            // ──── Making the room for first time──────────────────────
            int initial = (int) tempSensor.getTemperature();
            valueLabel.setText(initial + " °C");
    
            // ──── Change temp over time──────────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
    
                        int current = (int) tempSensor.getTemperature();
    
                        int change = random.nextInt(-3, 4);
                        current += change;
    
                        //threshold
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
    
            Random random = new Random();
    
            CardImages result = makeCard("humidity", "Humidity Sensor", "humidity", 200);
    
            VBox card = result.card;
            ImageView img = result.imageView;
    
    
            Label humidityLabel = new Label();
    
            humidityLabel.setStyle(
                    "-fx-font-size: 22px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1b55cf;"
            );
    
            // ──── Making the room for first time──────────────────────
            final int[] humidity = {50};
    
            humidityLabel.setText(humidity[0] + " %");
    
    
            // ──── Changing over time ──────────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
    
                        int change = random.nextInt(-10, 11);
    
                        humidity[0] += change;
    
                        if (humidity[0] < 10)
                            humidity[0] = 10;
    
                        if (humidity[0] > 90)
                            humidity[0] = 90;
    
                        humidityLabel.setText(humidity[0] + " %");
    
                        if (humidity[0] < 30) {
    
                            waterSystem.setWateringOn(true);
    
                            humidityLabel.setStyle(
                                    "-fx-font-size: 22px;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-text-fill: #e67e22;"
                            );
                        } else if (humidity[0] > 70) {
    
                            waterSystem.setWateringOn(false);
    
                            humidityLabel.setStyle(
                                    "-fx-font-size: 22px;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-text-fill: #3498db;"
                            );
                        } else {
    
                            humidityLabel.setStyle(
                                    "-fx-font-size: 22px;" +
                                            "-fx-font-weight: bold;" +
                                            "-fx-text-fill: #2ecc71;"
                            );
                        }
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
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
    
    
            // ──── Making the room for first time──────────────────────
            final boolean[] raining = {false};
            img.setImage(sunnyImg);
    
            // ──── Changing over time ──────────────────────
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
    
        // ──── Motion sensor cards─────────────────────────
        public VBox outMotionSensor() {
    
            Random random = new Random();
    
            CardImages result = makeCard("motion", "Motion Sensor", "noMotion", 200);
    
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image motionOn = new Image(IMAGES + "noMotion.png");
            Image motionOff = new Image(IMAGES + "motion.png");
    
            Label status = new Label();
            status.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
    
            // ──── Making the room for first time──────────────────────
            boolean initial = doorSecurity.isMotionDetected();
    
            img.setImage(!initial ? motionOn : motionOff);
    
            styleStatusM(status, !initial);
    
            // ──── Changing over time──────────────────────
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
    
                        styleStatusM(
                                status,
                                doorSecurity.isMotionDetected()
                        );
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox content = new VBox(10, status);
            content.setAlignment(Pos.CENTER);
    
            card.getChildren().add(content);
    
            return card;
        }
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        // ──── 7. Security─────────────────────────
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
    
    
        // ──── Motion sensor cards─────────────────────────
        public VBox motionSensor() {
    
            Random random = new Random();
    
            CardImages result = makeCard("motion", "Motion Sensor", "noMotion", 200);
    
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image motionOn = new Image(IMAGES + "noMotion.png");
            Image motionOff = new Image(IMAGES + "motion.png");
    
            Label status = new Label();
            status.setStyle(
                    "-fx-font-size: 18px;" +
                            "-fx-font-weight: bold;"
            );
    
            // ──── Making the room for first time──────────────────────
            boolean initial = doorSecurity.isMotionDetected();
    
            img.setImage(!initial ? motionOn : motionOff);
    
            styleStatusM(status, !initial);
    
            // ──── Changing over time──────────────────────
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
    
                        styleStatusM(
                                status,
                                doorSecurity.isMotionDetected()
                        );
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox content = new VBox(10, status);
            content.setAlignment(Pos.CENTER);
    
            card.getChildren().add(content);
    
            return card;
        }
    
        // ──── Camera video cards─────────────────────────
        public VBox cameraVideo() {
    
            CardImages result = makeCard("cam", "Camera Video", "cam", 300);
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image onImg = new Image(IMAGES + "cam.png");
            Image offImg = new Image(IMAGES + "onCam.png");
    
            // ──── Making the room for first time──────────────────────
            boolean initial = !doorSecurity.isMotionDetected();
            img.setImage(initial ? onImg : offImg);
    
            // ──── Changing (image - status)──────────────────────
            doorSecurity.motionDetectedProperty().addListener((obs, oldVal, newVal) -> {
    
                img.setImage(!newVal ? onImg : offImg);
                doorSecurity.setMotionDetected(newVal);
            });
    
            // ──── Changing over time ─────────────────
            Timeline autoChange = new Timeline(
                    new KeyFrame(Duration.seconds(15), e -> {
                        doorSecurity.setMotionDetected(!doorSecurity.isMotionDetected());
                    })
            );
    
            autoChange.setCycleCount(Animation.INDEFINITE);
            autoChange.play();
    
            VBox controls = new VBox(10);
            controls.setAlignment(Pos.CENTER);
    
            card.getChildren().add(controls);
    
            return card;
        }
    
        // ──── night vision cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            boolean initial = camera.isNightVision();
    
            img.setImage(initial ? onImg : offImg);
            styleStatusO(status, initial);
    
            toggle.setOnAction(e -> {
                camera.setNightVision(!camera.isNightVision());
            });
    
            // ──── Changing (image - status - bt)──────────────────────
            camera.nightVisionProperty().addListener((obs, oldVal, newVal) -> {
                img.setImage(newVal ? onImg : offImg);
                styleStatusO(status, newVal);
            });
    
            VBox content = new VBox(10, img, status, toggle);
            content.setAlignment(Pos.CENTER);
    
            card.getChildren().add(content);
    
            return card;
        }
    
        // ──── face card cards─────────────────────────
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
    
            // ──── Making the room for first time──────────────────────
            img.setImage(faceOff);
    
            // ──── Changing (image - status - bt)──────────────────────
            scanBtn.setOnAction(e -> {
    
                img.setImage(faceOn);
                status.setText("Face Detected");
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
    
                // ──── Changing over time ─────────────────
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
    
        // ──── storage cards─────────────────────────
        public VBox recStorage() {
    
            CardImages result = makeCard("storage", "Recording Storage", "good", 200);
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image goodImg = new Image(IMAGES + "good.png");
            Image fullImg = new Image(IMAGES + "full.png");
    
            Label sizeLabel = new Label();
    
            final int[] size = {0};
            final boolean[] isFull = {false};
    
            // ──── Making the room for first time──────────────────────
            img.setImage(goodImg);
            sizeLabel.setText("0 MB");
            sizeLabel.setStyle(
                    "-fx-text-fill: #1b55cf;" +
                            "-fx-font-size: 16px;" +
                            "-fx-font-weight: bold;"
            );
    
            // ──── Changing over time ─────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.minutes(30), e -> {
    
                        if (isFull[0]) return;
    
                        size[0]++;
    
                        if (size[0] >= 521) {
                            size[0] = 521;
                            isFull[0] = true;
    
                            img.setImage(fullImg);
                            sizeLabel.setText("FULL 521 MB");
                            sizeLabel.setStyle(
                                    "-fx-text-fill: #1b55cf;" +
                                            "-fx-font-size: 16px;" +
                                            "-fx-font-weight: bold;"
                            );
    
                        } else {
                            sizeLabel.setText(size[0] + " MB");
                            sizeLabel.setStyle(
                                    "-fx-text-fill: #1b55cf;" +
                                            "-fx-font-size: 16px;" +
                                            "-fx-font-weight: bold;"
                            );
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
    
        // ──── emergency cards─────────────────────────
        public VBox emergency() {
    
            CardImages result = makeCard("emergency", "Emergency System", "noEmerg", 200);
            VBox card = result.card;
            ImageView img = result.imageView;
    
            Image safeImg = new Image(IMAGES + "noEmerg.png");
            Image dangerImg = new Image(IMAGES + "emerg.png");
    
            Label status = new Label();
    
            // ──── Making the room for first time──────────────────────
            boolean initial = camera.getEmergency();
            img.setImage(initial ? dangerImg : safeImg);
            styleStatusN(status, !initial);
    
            // ──── Changing (image - status)──────────────────────
            camera.emergencyProperty().addListener((obs, oldVal, newVal) -> {
    
                if (newVal) {
                    img.setImage(dangerImg);
                    styleStatusN(status, !newVal);
                    masterRoom.setDoorOpen(true);
                    doorSecurity.setDoorOpen(true);
                } else {
                    img.setImage(safeImg);
                    styleStatusN(status, !newVal);
                    masterRoom.setDoorOpen(false);
                    doorSecurity.setDoorOpen(false);
                }
            });
    
            // ──── Changing over time ─────────────────
            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.seconds(30), e -> {
                        camera.setEmergency(!camera.getEmergency());
                    })
            );
    
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();
    
            VBox content = new VBox(10, img, status);
            content.setAlignment(Pos.CENTER);
    
            card.getChildren().add(content);
    
            return card;
        }
    
        //─────────────────────────────────────────────────────────────────────────────────────────────────────────
    
        // ──── All status style (ON / OFF)───────────────────
        public void styleStatusO(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("ON");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("OFF");
            }
        }
    
        // ──── All status style (Close / Open)───────────────────
        public void styleStatusC(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Opened");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Closed");
            }
        }
    
        // ──── All status style (Free / Occupied)───────────────────
        public void styleStatusF(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Free");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Ocuupied");
            }
        }
    
        // ──── Safe toggle───────────────────
        public void styleStatusS(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Baby is safe");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Baby is not safe");
            }
        }
    
        // ──── Safe toggle───────────────────
        public void styleStatusN(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("No emergency");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("Emergency");
            }
        }
    
        // ──── All status style (No motion detected / motion detected)───────────────────
        public void styleStatusM(Label status, boolean state) {
            if (state) {
                status.setStyle("-fx-text-fill: #2ecc71;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("No motion detected");
            } else {
                status.setStyle("-fx-text-fill: #d62828;-fx-font-size: 18px; -fx-font-weight: bold;");
                status.setText("motion detected");
            }
        }
    
        // ──── Set alarm───────────────────
        public VBox buildAlarm() {
    
            VBox card = new VBox(15);
    
            card.setPadding(new Insets(15));
    
            card.setPrefWidth(300);
            card.setPrefHeight(160);
    
            card.setStyle(
                    "-fx-background-color: white;" +
                            "-fx-background-radius: 18;" +
                            "-fx-border-radius: 18;" +
                            "-fx-border-color: #dfe7f2;"
            );
    
            // ──── Title ───────────────────
            Label title = new Label("Alarm");
    
            title.setStyle(
                    "-fx-font-size: 20px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #133466;"
            );
    
            // ──── Hour list ───────────────────
            ComboBox<String> hourBox = new ComboBox<>();
    
            for (int i = 1; i <= 12; i++) {
                hourBox.getItems().add(String.format("%02d", i));
            }
    
            hourBox.setValue("07");
    
            // ──── Minute list ───────────────────
            ComboBox<String> minuteBox = new ComboBox<>();
    
            for (int i = 0; i < 60; i++) {
                minuteBox.getItems().add(String.format("%02d", i));
            }
    
            minuteBox.setValue("00");
    
            // ──── AM / PM ───────────────────
            ComboBox<String> ampmBox = new ComboBox<>();
    
            ampmBox.getItems().addAll("AM", "PM");
            ampmBox.setValue("AM");
    
            HBox timeRow = new HBox(10,
                    hourBox,
                    minuteBox,
                    ampmBox
            );
    
            timeRow.setAlignment(Pos.CENTER);
    
            // ──── Status ───────────────────
            Label status = new Label("Alarm OFF");
    
            status.setStyle(
                    "-fx-font-size: 15px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #1b55cf;"
            );
    
            // ──── Message ───────────────────
            Label msg = new Label();
    
            msg.setStyle(
                    "-fx-font-size: 13px;" +
                            "-fx-text-fill: #666;"
            );
    
            // ──── Enable button ───────────────────
            Button enableBtn = new Button("Enable Alarm");
    
            enableBtn.setFocusTraversable(false);
    
            enableBtn.setStyle(
                    "-fx-background-color: #1b55cf;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;"
            );
    
            // ──── Stop button ───────────────────
            Button stopBtn = new Button("Stop Alarm");
    
            stopBtn.setVisible(false);
            stopBtn.setFocusTraversable(false);
    
            stopBtn.setStyle(
                    "-fx-background-color: #d62828;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-background-radius: 10;"
            );
    
            // ──── Enable alarm ───────────────────
            enableBtn.setOnAction(e -> {
    
                int hour = Integer.parseInt(hourBox.getValue());
                int minute = Integer.parseInt(minuteBox.getValue());
    
                String period = ampmBox.getValue();
    
                // convert to 24h
                if (period.equals("PM") && hour != 12) {
                    hour += 12;
                }
    
                if (period.equals("AM") && hour == 12) {
                    hour = 0;
                }
    
                LocalTime alarmTime = LocalTime.of(hour, minute);
    
                status.setText("Alarm ON");
                msg.setText("Alarm set for " + alarmTime);
    
                Timeline alarmTimeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), ev -> {
    
                            LocalTime now = LocalTime.now();
    
                            if (now.getHour() == alarmTime.getHour()
                                    && now.getMinute() == alarmTime.getMinute()
                                    && now.getSecond() == 0) {
    
                                status.setText("ALARM RINGING");
                                msg.setText("Press stop to turn off");
    
                                stopBtn.setVisible(true);
    
                                masterRoom.setAlarm(alarmTime, new Scanner(System.in));
                            }
                        })
                );
    
                alarmTimeline.setCycleCount(Animation.INDEFINITE);
                alarmTimeline.play();
            });
    
            // ──── Stop alarm ───────────────────
            stopBtn.setOnAction(e -> {
    
                AlarmTime.stopAlarm();
    
                status.setText("Alarm OFF");
    
                msg.setText("");
    
                stopBtn.setVisible(false);
            });
    
            card.getChildren().addAll(
                    title,
                    timeRow,
                    status,
                    msg,
                    enableBtn,
                    stopBtn
            );
    
            return card;
        }
    }
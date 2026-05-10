import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;


/*
    ================GUI Parts================
    1. main menu -> our names - tips.
    2. living room.
    3. master room.
    4. kids room.
    5. kitchen.
    6. bathroom.
    7. outdoors.
    8. cameras.
*/

public class JavaFX extends Application {

    // ── Icon base path ──────────────────────────────────────────
    private static final String ICONS = "file:D:/SmartHome/Project/resources/icons/";

    // ── State flags ─────────────────────────────────────────────
    private boolean lightsOn = true;
    private boolean acOn = true;
    private boolean doorLocked = true;
    private boolean tvOn = true;
    private double acTemp = 24;

    // ── Live image views (need to swap icons on toggle) ─────────
    private ImageView lightImg, lockImg, tvImg;
    private Label lightBadge, acBadge, lockBadge, tvBadge;
    private Label acTempLabel;
    private Label statusBar;

    // ────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        launch(args);
    }

    // ─────────────────────────────────────────────────────────────
    @Override
    public void start(Stage stage) {

        // ══ ROOT ════════════════════════════════════════════════
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #EEF1F8;");

        // ══ SIDEBAR ═════════════════════════════════════════════
        root.setLeft(buildSidebar());

        // ══ MAIN CONTENT ════════════════════════════════════════
        VBox main = new VBox(18);
        main.setPadding(new Insets(24, 24, 24, 24));

        // header
        main.getChildren().add(buildHeader());

        // card grid  (2 rows × 3 cols)
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(16);

        // row constraints – equal height
        for (int i = 0; i < 2; i++) {
            RowConstraints rc = new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            grid.getRowConstraints().add(rc);
        }
        // col constraints
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            grid.getColumnConstraints().add(cc);
        }

        grid.add(buildLightCard(), 0, 0);
        grid.add(buildACCard(), 1, 0);
        grid.add(buildTempCard(), 2, 0);
        grid.add(buildDoorCard(), 0, 1);
        grid.add(buildTVCard(), 1, 1);
        // col 2 row 1 intentionally empty (matches design)

        VBox.setVgrow(grid, Priority.ALWAYS);
        main.getChildren().add(grid);

        // bottom row
        main.getChildren().add(buildBottomRow());

        // status bar
        statusBar = new Label("All systems are running smoothly");
        statusBar.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #DDE2EE;
                -fx-border-width: 1 0 0 0;
                -fx-padding: 10 20;
                -fx-font-size: 13px;
                -fx-text-fill: #3B82F6;
                """);
        HBox.setHgrow(statusBar, Priority.ALWAYS);
        statusBar.setMaxWidth(Double.MAX_VALUE);

        VBox wrapper = new VBox(main, statusBar);
        VBox.setVgrow(main, Priority.ALWAYS);
        root.setCenter(wrapper);

        // ══ SCENE ═══════════════════════════════════════════════
        Scene scene = new Scene(root, 1100, 720);
        stage.setTitle("Smart Home – Master Room");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    // ────────────────────────────────────────────────────────────
    //  SIDEBAR
    // ────────────────────────────────────────────────────────────
    private VBox buildSidebar() {

        VBox side = new VBox(6);
        side.setPrefWidth(185);
        side.setStyle("-fx-background-color: #1A2236;");
        side.setPadding(new Insets(0, 0, 20, 0));

        // logo
        HBox logo = new HBox(10);
        logo.setPadding(new Insets(22, 16, 22, 16));
        logo.setAlignment(Pos.CENTER_LEFT);
        ImageView homeIco = icon("home.png", 28);
        Label logoLbl = new Label("SMART\nHOME");
        logoLbl.setStyle("-fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold;");
        logo.getChildren().addAll(homeIco, logoLbl);
        side.getChildren().add(logo);

        // nav items
        String[][] items = {
                {"home.png", "Dashboard", "active"},
                {"living.png", "Rooms", ""},
                {"settings.png", "Devices", ""},
                {"lightIcon.png", "Scenes", ""},
                {"alarmYellow.png", "Alarms", ""},
                {"settings.png", "Settings", ""},
        };

        for (String[] item : items) {
            side.getChildren().add(navItem(item[0], item[1], item[2].equals("active")));
        }

        // spacer
        Region sp = new Region();
        VBox.setVgrow(sp, Priority.ALWAYS);
        side.getChildren().add(sp);

        // weather widget
        VBox weather = new VBox(4);
        weather.setPadding(new Insets(12, 16, 12, 16));
        weather.setStyle("-fx-background-color: #243047; -fx-background-radius: 12;");
        weather.setMargin(weather, new Insets(0, 12, 0, 12));
        Insets wm = new Insets(0, 12, 0, 12);
        VBox.setMargin(weather, wm);

        Label wTemp = new Label("30°C");
        wTemp.setStyle("-fx-text-fill: white; -fx-font-size: 22px; -fx-font-weight: bold;");
        Label wDesc = new Label("Partly Cloudy");
        wDesc.setStyle("-fx-text-fill: #8899BB; -fx-font-size: 12px;");
        Label wCity = new Label("📍 Cairo, Egypt");
        wCity.setStyle("-fx-text-fill: #8899BB; -fx-font-size: 11px;");
        weather.getChildren().addAll(wTemp, wDesc, wCity);
        side.getChildren().add(weather);

        return side;
    }

    private HBox navItem(String iconFile, String label, boolean active) {
        HBox row = new HBox(12);
        row.setPadding(new Insets(11, 16, 11, 16));
        row.setAlignment(Pos.CENTER_LEFT);
        if (active) row.setStyle("-fx-background-color: #2D3F5E;");

        ImageView ico = icon(iconFile, 18);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: " + (active ? "white" : "#8899BB") + "; -fx-font-size: 13px;");
        row.getChildren().addAll(ico, lbl);
        return row;
    }

    // ────────────────────────────────────────────────────────────
    //  HEADER
    // ────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox h = new HBox();
        h.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Tuesday, 5th May  ·  3:35 PM");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1A2236;");

        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);

        ImageView wifi = icon("wifi.png", 22);
        ImageView notif = icon("notification.png", 22);

        // avatar placeholder
        Label avatar = new Label("👤");
        avatar.setStyle("""
                -fx-background-color: #DDE2EE;
                -fx-background-radius: 50;
                -fx-padding: 6 8;
                -fx-font-size: 16px;
                """);

        HBox right = new HBox(14, wifi, notif, avatar);
        right.setAlignment(Pos.CENTER);

        h.getChildren().addAll(title, sp, right);
        return h;
    }

    // ────────────────────────────────────────────────────────────
    //  CARD helpers
    // ────────────────────────────────────────────────────────────
    private VBox card() {
        VBox c = new VBox(10);
        c.setPadding(new Insets(18));
        c.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 16;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 8, 0, 0, 2);
                """);
        return c;
    }

    private Label badge(String text, boolean on) {
        Label b = new Label(text);
        b.setStyle("-fx-background-color: " + (on ? "#D1FAE5" : "#FEE2E2") + ";"
                + "-fx-text-fill: " + (on ? "#059669" : "#DC2626") + ";"
                + "-fx-background-radius: 6; -fx-padding: 2 8; -fx-font-size: 12px; -fx-font-weight: bold;");
        return b;
    }

    // ────────────────────────────────────────────────────────────
    //  LIGHT CARD
    // ────────────────────────────────────────────────────────────
    private VBox buildLightCard() {
        VBox c = card();

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        HBox left = new HBox(8, icon("lightIcon.png", 20), label("Main Light", true));
        left.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        lightBadge = badge("ON", lightsOn);
        top.getChildren().addAll(left, sp, lightBadge);

        lightImg = icon("lightIcon.png", 80);
        StackPane imgWrap = new StackPane(lightImg);
        imgWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(imgWrap, Priority.ALWAYS);

        // toggle row
        ToggleButton tog = new ToggleButton(lightsOn ? "ON" : "OFF");
        tog.setSelected(lightsOn);
        styleToggle(tog, lightsOn);
        tog.setOnAction(e -> {
            lightsOn = tog.isSelected();
            tog.setText(lightsOn ? "ON" : "OFF");
            styleToggle(tog, lightsOn);
            lightBadge.setText(lightsOn ? "ON" : "OFF");
            lightBadge.setStyle(badgeStyle(lightsOn));
        });

        HBox row = new HBox(10, tog, icon("lightMode.png", 18));
        row.setAlignment(Pos.CENTER);

        c.getChildren().addAll(top, imgWrap, row);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  AC CARD
    // ────────────────────────────────────────────────────────────
    private VBox buildACCard() {
        VBox c = card();

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        HBox left = new HBox(8, icon("AC.png", 20), label("AC", true));
        left.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        acBadge = badge("ON", acOn);
        top.getChildren().addAll(left, sp, acBadge);

        acTempLabel = new Label((int) acTemp + "°C");
        acTempLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1A2236;");
        StackPane tempWrap = new StackPane(acTempLabel);
        tempWrap.setAlignment(Pos.CENTER);

        ImageView acImg = icon("AC.png", 64);
        StackPane acWrap = new StackPane(acImg);
        acWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(acWrap, Priority.ALWAYS);

        // minus / temp / plus
        Button minus = roundBtn("−");
        Button plus = roundBtn("+");
        Label curTemp = new Label((int) acTemp + "°C");
        curTemp.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1A2236;");

        minus.setOnAction(e -> {
            acTemp = Math.max(16, acTemp - 1);
            curTemp.setText((int) acTemp + "°C");
            acTempLabel.setText((int) acTemp + "°C");
        });
        plus.setOnAction(e -> {
            acTemp = Math.min(32, acTemp + 1);
            curTemp.setText((int) acTemp + "°C");
            acTempLabel.setText((int) acTemp + "°C");
        });

        HBox ctrl = new HBox(12, minus, curTemp, plus);
        ctrl.setAlignment(Pos.CENTER);

        c.getChildren().addAll(top, acTempLabel, acWrap, ctrl);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  TEMPERATURE CARD
    // ────────────────────────────────────────────────────────────
    private VBox buildTempCard() {
        VBox c = card();

        HBox top = new HBox(8, icon("temp.png", 20), label("Temperature", true));
        top.setAlignment(Pos.CENTER_LEFT);

        Label big = new Label("30°C");
        big.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #1A2236;");

        // simple visual thermometer bar using a VBox
        VBox barBg = new VBox();
        barBg.setPrefWidth(36);
        barBg.setStyle("-fx-background-color: linear-gradient(to bottom, #EF4444 0%, #60A5FA 100%); "
                + "-fx-background-radius: 20;");
        barBg.setPrefHeight(180);

        // labels 35 30 25 20 15 10
        VBox scaleBox = new VBox();
        scaleBox.setAlignment(Pos.TOP_RIGHT);
        for (int v : new int[]{35, 30, 25, 20, 15, 10}) {
            Label s = new Label(v + " —");
            s.setStyle("-fx-font-size: 11px; -fx-text-fill: #8899BB;");
            scaleBox.getChildren().add(s);
            if (v > 10) {
                Region gap = new Region();
                gap.setPrefHeight(16);
                scaleBox.getChildren().add(gap);
            }
        }

        HBox thermRow = new HBox(8, barBg, scaleBox);
        thermRow.setAlignment(Pos.CENTER);
        VBox.setVgrow(thermRow, Priority.ALWAYS);

        Label unit = new Label("°C");
        unit.setStyle("-fx-font-size: 12px; -fx-text-fill: #8899BB;");

        c.getChildren().addAll(top, big, thermRow, unit);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  DOOR CARD
    // ────────────────────────────────────────────────────────────
    private VBox buildDoorCard() {
        VBox c = card();

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        HBox left = new HBox(8, icon("safeLock.png", 20), label("Door Lock", true));
        left.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        lockBadge = badge("LOCKED", doorLocked);
        top.getChildren().addAll(left, sp, lockBadge);

        lockImg = doorLocked ? icon("safeLock.png", 80) : icon("lockOpen.png", 80);
        StackPane imgWrap = new StackPane(lockImg);
        imgWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(imgWrap, Priority.ALWAYS);

        Button lockBtn = new Button(doorLocked ? "🔒  LOCKED" : "🔓  UNLOCKED");
        lockBtn.setStyle(lockBtnStyle(doorLocked));
        lockBtn.setMaxWidth(Double.MAX_VALUE);
        lockBtn.setOnAction(e -> {
            doorLocked = !doorLocked;
            lockBtn.setText(doorLocked ? "🔒  LOCKED" : "🔓  UNLOCKED");
            lockBtn.setStyle(lockBtnStyle(doorLocked));
            lockBadge.setText(doorLocked ? "LOCKED" : "UNLOCKED");
            lockBadge.setStyle(badgeStyle(doorLocked));
            // swap icon
            imgWrap.getChildren().setAll(doorLocked ? icon("safeLock.png", 80) : icon("lockOpen.png", 80));
        });

        c.getChildren().addAll(top, imgWrap, lockBtn);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  TV CARD
    // ────────────────────────────────────────────────────────────
    private VBox buildTVCard() {
        VBox c = card();

        HBox top = new HBox();
        top.setAlignment(Pos.CENTER_LEFT);
        HBox left = new HBox(8, icon("TV.png", 20), label("TV", true));
        left.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        tvBadge = badge("ON", tvOn);
        top.getChildren().addAll(left, sp, tvBadge);

        tvImg = icon("TV.png", 90);
        StackPane imgWrap = new StackPane(tvImg);
        imgWrap.setAlignment(Pos.CENTER);
        VBox.setVgrow(imgWrap, Priority.ALWAYS);

        ToggleButton tog = new ToggleButton(tvOn ? "ON" : "OFF");
        tog.setSelected(tvOn);
        styleToggle(tog, tvOn);
        tog.setOnAction(e -> {
            tvOn = tog.isSelected();
            tog.setText(tvOn ? "ON" : "OFF");
            styleToggle(tog, tvOn);
            tvBadge.setText(tvOn ? "ON" : "OFF");
            tvBadge.setStyle(badgeStyle(tvOn));
        });

        HBox row = new HBox(10, tog);
        row.setAlignment(Pos.CENTER);
        c.getChildren().addAll(top, imgWrap, row);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  BOTTOM ROW  (Smart Modes | Alarm | Quick Actions)
    // ────────────────────────────────────────────────────────────
    private HBox buildBottomRow() {
        HBox row = new HBox(16);
        row.getChildren().addAll(buildScenesPanel(), buildAlarmPanel(), buildQuickPanel());
        return row;
    }

    private VBox buildScenesPanel() {
        VBox c = card();
        c.setPrefWidth(300);
        HBox titleRow = new HBox(8, icon("lightMode.png", 18), label("Smart Modes", true));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        HBox modes = new HBox(10);
        modes.setAlignment(Pos.CENTER);

        String[][] modeData = {
                {"sleepMode.png", "Sleep Mode"},
                {"relaxMode.png", "Relax Mode"},
                {"romance.png", "Romance Mode"},
        };
        for (String[] m : modeData) {
            VBox btn = new VBox(6, icon(m[0], 36), new Label(m[1]));
            ((Label) btn.getChildren().get(1)).setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
            btn.setAlignment(Pos.CENTER);
            btn.setPadding(new Insets(10));
            btn.setStyle("-fx-background-color: #F3F4F8; -fx-background-radius: 12;");
            btn.setCursor(javafx.scene.Cursor.HAND);
            modes.getChildren().add(btn);
        }

        Label hint = new Label("✦  One tap to set the perfect ambiance");
        hint.setStyle("-fx-font-size: 11px; -fx-text-fill: #8899BB;");
        c.getChildren().addAll(titleRow, modes, hint);
        return c;
    }

    private VBox buildAlarmPanel() {
        VBox c = card();
        c.setPrefWidth(240);
        HBox titleRow = new HBox(8, icon("alarmYellow.png", 18), label("Alarm", true));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label time = new Label("07:30 AM");
        time.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1A2236;");

        ToggleButton tog = new ToggleButton("ON");
        tog.setSelected(true);
        styleToggle(tog, true);
        tog.setOnAction(e -> {
            boolean on = tog.isSelected();
            tog.setText(on ? "ON" : "OFF");
            styleToggle(tog, on);
        });

        HBox timeRow = new HBox();
        timeRow.setAlignment(Pos.CENTER_LEFT);
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        timeRow.getChildren().addAll(time, sp, tog);

        Label every = new Label("Everyday");
        every.setStyle("-fx-font-size: 12px; -fx-text-fill: #8899BB;");
        Label song = new Label("🎵  iphone_alarm.wav");
        song.setStyle("-fx-font-size: 12px; -fx-text-fill: #8899BB;");

        c.getChildren().addAll(titleRow, timeRow, every, song);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  ROOMS PANEL  (inside sidebar — already in nav items above)
    //  The design shows the room list; it's handled by navItem rows.
    // ────────────────────────────────────────────────────────────

    private VBox buildQuickPanel() {
        VBox c = card();
        HBox.setHgrow(c, Priority.ALWAYS);
        HBox titleRow = new HBox(8, icon("lightMode.png", 18), label("Quick Actions", true));
        titleRow.setAlignment(Pos.CENTER_LEFT);

        String[][] actions = {
                {"lightIcon.png", "All Lights\nON", "#D1FAE5", "#059669"},
                {"lightIcon.png", "All Lights\nOFF", "#FEE2E2", "#DC2626"},
                {"settings.png", "All Devices\nON", "#D1FAE5", "#059669"},
                {"settings.png", "All Devices\nOFF", "#FEE2E2", "#DC2626"},
        };

        HBox btns = new HBox(10);
        btns.setAlignment(Pos.CENTER);
        for (String[] a : actions) {
            VBox btn = new VBox(6, icon(a[0], 28), label(a[1], false));
            ((Label) btn.getChildren().get(1)).setStyle(
                    "-fx-font-size: 11px; -fx-text-fill: " + a[3] + "; -fx-text-alignment: center;");
            btn.setAlignment(Pos.CENTER);
            btn.setPadding(new Insets(10));
            btn.setStyle("-fx-background-color: " + a[2] + "; -fx-background-radius: 12;");
            btn.setCursor(javafx.scene.Cursor.HAND);
            HBox.setHgrow(btn, Priority.ALWAYS);
            btns.getChildren().add(btn);
        }

        c.getChildren().addAll(titleRow, btns);
        return c;
    }

    // ────────────────────────────────────────────────────────────
    //  UTILITY METHODS
    // ────────────────────────────────────────────────────────────
    private ImageView icon(String name, double size) {
        try {
            ImageView iv = new ImageView(new Image(ICONS + name));
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            iv.setPreserveRatio(true);
            return iv;
        } catch (Exception e) {
            // fallback: empty image view so layout doesn't break
            ImageView iv = new ImageView();
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            return iv;
        }
    }

    private Label label(String text, boolean bold) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 14px; -fx-text-fill: #1A2236;"
                + (bold ? " -fx-font-weight: bold;" : ""));
        return l;
    }

    private void styleToggle(ToggleButton t, boolean on) {
        t.setStyle("-fx-background-color: " + (on ? "#3B82F6" : "#CBD5E1") + ";"
                + "-fx-text-fill: white; -fx-background-radius: 20;"
                + "-fx-padding: 4 16; -fx-font-weight: bold;");
    }

    private String badgeStyle(boolean on) {
        return "-fx-background-color: " + (on ? "#D1FAE5" : "#FEE2E2") + ";"
                + "-fx-text-fill: " + (on ? "#059669" : "#DC2626") + ";"
                + "-fx-background-radius: 6; -fx-padding: 2 8;"
                + "-fx-font-size: 12px; -fx-font-weight: bold;";
    }

    private String lockBtnStyle(boolean locked) {
        return "-fx-background-color: " + (locked ? "#D1FAE5" : "#FEE2E2") + ";"
                + "-fx-text-fill: " + (locked ? "#059669" : "#DC2626") + ";"
                + "-fx-background-radius: 10; -fx-padding: 8;"
                + "-fx-font-size: 13px; -fx-font-weight: bold;";
    }

    private Button roundBtn(String text) {
        Button b = new Button(text);
        b.setStyle("""
                -fx-background-color: #EEF1F8;
                -fx-background-radius: 50;
                -fx-font-size: 18px;
                -fx-font-weight: bold;
                -fx-text-fill: #1A2236;
                -fx-min-width: 34;
                -fx-min-height: 34;
                """);
        return b;
    }
}

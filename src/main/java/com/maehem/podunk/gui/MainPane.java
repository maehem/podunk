/*  GNU GPLv3 License, with additional terms

    Podunk Project -- https://github.com/maehem/podunk
    A city simulator in JavaFX
    Copyright (C) 2024 Mark J. Koch  ( @maehem on GitHub )

    This project is derived from MicropolisJ. https://github.com/SimHacker/micropolis
    Copyright (C) 2013 Jason Long
    Portions Copyright (C) 1989-2007 Electronic Arts Inc.

    Podunk (derived from MicropolisJ) is free software; you can redistribute it
    and/or modify it under the terms of the GNU GPLv3, with additional terms.
    See the README file, included in this distribution, for details.

 */
package com.maehem.podunk.gui;

import com.maehem.podunk.AppProperties;
import com.maehem.podunk.Podunk;
import com.maehem.podunk.engine.*;
import com.maehem.podunk.logging.Logging;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.WindowEvent;

/**
 * Micropolis MainWindow Swing UI
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class MainPane extends BorderPane implements Micropolis.Listener, EarthquakeListener {

    public static final Logger LOGGER = Logging.LOGGER;
    public static final String PANE_SIZE_W_PROP_KEY = "RootPane.W";
    public static final String PANE_SIZE_H_PROP_KEY = "RootPane.H";
    static final String EXTENSION = "cty";
    static final String SOUNDS_PREF = "enable_sounds";

    public static final int ICON_HEIGHT = 24;
    public final ResourceBundle MSG;
    protected Micropolis engine;
    MicropolisDrawingArea drawingArea;
    ScrollPane drawingAreaScroll;
    DemandIndicatorPane demandInd;
    MessagesPane messagesPane;
    Label mapLegendLbl;
    OverlayMapView mapView;
    NotificationPane notificationPane;
    EvaluationPane evaluationPane;
    GraphsPane graphsPane;
    //Label dateLbl = new Label();
    //Label fundsLbl = new Label();
    //Label popLbl = new Label();
    Label currentToolLbl = new Label();
    Label currentToolCostLbl = new Label();
    //Map<MicropolisTool, ToggleButton> toolBtns;
    private final ToggleGroup toolToggles = new ToggleGroup();
    EnumMap<MapState, RadioMenuItem> mapStateMenuItems = new EnumMap<>(MapState.class);
    //private final ToggleGroup mapStateToggles = new ToggleGroup();
    CheckMenuItem autoBudgetMenuItem;
    CheckMenuItem autoBulldozeMenuItem;
    CheckMenuItem disastersMenuItem;
    CheckMenuItem soundsMenuItem;
    Map<Speed, MenuItem> priorityMenuItems;
    Map<Integer, MenuItem> difficultyMenuItems;

    MicropolisTool currentTool;
    File currentFile;
    // used when a tool is being pressed
    ToolStroke toolStroke;
    // where the tool was last applied during the current drag
    int lastX;
    int lastY;

    private boolean doSounds = true;
    private boolean dirty1 = false;  //indicates if a tool was successfully applied since last save
    private boolean dirty2 = false;  //indicates if simulator took a step since last save
    private long lastSavedTime = 0;  //real-time clock of when file was last saved
    private boolean autoBudgetPending;
    final String PRODUCT_NAME;
    private final DateFundsPane dateFundsPopPane;

    public MainPane() {
        this(new Micropolis());

    }

    public MainPane(Micropolis engine) {
        this.engine = engine;
        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.
        LOGGER.log(Level.CONFIG, "Create Root Pane.");
        PRODUCT_NAME = MSG.getString("PRODUCT");

        //setPrefSize(200, 200);
        //setIconImage(appIcon.getImage());
        // Top: Menu
        makeMenu(); // Top.  TODO Mac top bar
        // Left: Layer Select

        // Center: Map/Layers
        drawingArea = new MicropolisDrawingArea(engine);
        drawingAreaScroll = new ScrollPane(drawingArea);
        demandInd = new DemandIndicatorPane();
        messagesPane = new MessagesPane();
        dateFundsPopPane = new DateFundsPane();

        mapView = new OverlayMapView(engine);
        //evaluationPane = new EvaluationPane(engine);
        graphsPane = new GraphsPane(engine);

        drawingAreaScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        drawingAreaScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        drawingAreaScroll.setMinSize(300, 300);
        drawingAreaScroll.setPrefSize(800, 600);
        //drawingAreaScroll.setPannable(true);

        //mainArea.add(drawingAreaScroll);
        setCenter(drawingAreaScroll);
        // Right: Toolbar/RCI/MiniMap
        makeRightBar();
        // Bottom: Realtime Info
        makeBottomBar();

        //JPanel mainArea = new JPanel(new BorderLayout());
        //BorderPane mainArea = new BorderPane();
        //add(mainArea, BorderLayout.CENTER);
        //setCenter(mainArea);
        //ToolBar tb = makeToolbar();
        //mainArea.add(tb, BorderLayout.WEST);
        //mainArea.setLeft(tb);
//        Box evalGraphsBox = new Box(BoxLayout.Y_AXIS);
//        mainArea.add(evalGraphsBox, BorderLayout.SOUTH);
//
//        graphsPane = new GraphsPane(engine);
//        graphsPane.setVisible(false);
//        evalGraphsBox.add(graphsPane);
//
//        evaluationPane = new EvaluationPane(engine);
//        evaluationPane.setVisible(false);
//        evalGraphsBox.add(evaluationPane, BorderLayout.SOUTH);
//        JPanel leftPane = new JPanel(new GridBagLayout());
//        add(leftPane, BorderLayout.WEST);
//        GridPane leftPane = new GridPane();
//        setLeft(leftPane);
//
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = c.gridy = 0;
//        c.anchor = GridBagConstraints.SOUTHWEST;
//        c.insets = new Insets(4, 4, 4, 4);
//        c.weightx = 1.0;
//
//        demandInd = new DemandIndicator();
//        leftPane.add(demandInd, c);
//
//        c.gridx = 1;
//        c.weightx = 0.0;
//        c.fill = GridBagConstraints.BOTH;
//        c.insets = new Insets(4, 20, 4, 4);
//
//        leftPane.add(makeDateFunds(), c);
//
//        c.gridx = 0;
//        c.gridy = 1;
//        c.gridwidth = 2;
//        c.weighty = 0.0;
//        c.anchor = GridBagConstraints.NORTH;
//        c.insets = new Insets(0, 0, 0, 0);
//        //JPanel mapViewContainer = new JPanel(new BorderLayout());
//        BorderPane mapViewContainer = new BorderPane();
//        //mapViewContainer.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//        mapViewContainer.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
//        leftPane.add(mapViewContainer, c);
//        MenuBar mapMenu = new MenuBar();
//        //mapViewContainer.add(mapMenu, BorderLayout.NORTH);
//        mapViewContainer.setTop(mapMenu);
//
//        Menu zonesMenu = new Menu(MSG.getString("menu.zones"));
//        setupKeys(zonesMenu, "menu.zones");
//        mapMenu.getMenus().add(zonesMenu);
//
//        zonesMenu.getItems().add(makeMapStateMenuItem("menu.zones.ALL", MapState.ALL));
//        zonesMenu.getItems().add(makeMapStateMenuItem("menu.zones.RESIDENTIAL", MapState.RESIDENTIAL));
//        zonesMenu.getItems().add(makeMapStateMenuItem("menu.zones.COMMERCIAL", MapState.COMMERCIAL));
//        zonesMenu.getItems().add(makeMapStateMenuItem("menu.zones.INDUSTRIAL", MapState.INDUSTRIAL));
//        zonesMenu.getItems().add(makeMapStateMenuItem("menu.zones.TRANSPORT", MapState.TRANSPORT));
//
//        Menu overlaysMenu = new Menu(MSG.getString("menu.overlays"));
//        setupKeys(overlaysMenu, "menu.overlays");
//        mapMenu.getMenus().add(overlaysMenu);
//
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.POPDEN_OVERLAY", MapState.POPDEN_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.GROWTHRATE_OVERLAY", MapState.GROWTHRATE_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.LANDVALUE_OVERLAY", MapState.LANDVALUE_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.CRIME_OVERLAY", MapState.CRIME_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.POLLUTE_OVERLAY", MapState.POLLUTE_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.TRAFFIC_OVERLAY", MapState.TRAFFIC_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.POWER_OVERLAY", MapState.POWER_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.FIRE_OVERLAY", MapState.FIRE_OVERLAY));
//        overlaysMenu.getItems().add(makeMapStateMenuItem("menu.overlays.POLICE_OVERLAY", MapState.POLICE_OVERLAY));
//
//        mapMenu.add(Box.createHorizontalGlue());
//        mapLegendLbl = new Label();
//        mapMenu.getMenus().add(mapLegendLbl);
//
//        mapView = new OverlayMapView(engine);
//        mapView.connectView(drawingArea, drawingAreaScroll);
//        mapViewContainer.add(mapView, BorderLayout.CENTER);
//
//        setMapState(MapState.ALL);
//        c.gridx = 0;
//        c.gridy = 2;
//        c.gridwidth = 2;
//        c.weighty = 1.0;
//        c.fill = GridBagConstraints.BOTH;
//        c.insets = new Insets(0, 0, 0, 0);
//
//        messagesPane = new MessagesTextArea();
//        JScrollPane scroll2 = new JScrollPane(messagesPane);
//        scroll2.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        scroll2.setPreferredSize(new Dimension(0, 0));
//        scroll2.setMinimumSize(new Dimension(0, 0));
//        leftPane.add(scroll2, c);
//
//        c.gridy = 3;
//        c.weighty = 0.0;
//        notificationPane = new NotificationPane(engine);
//        leftPane.add(notificationPane, c);
//
//        pack();
//        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//        setLocationRelativeTo(null);
//        InputMap inputMap = ((JComponent) getContentPane()).getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
//        inputMap.put(KeyStroke.getKeyStroke("ADD"), "zoomIn");
//        inputMap.put(KeyStroke.getKeyStroke("shift EQUALS"), "zoomIn");
//        inputMap.put(KeyStroke.getKeyStroke("SUBTRACT"), "zoomOut");
//        inputMap.put(KeyStroke.getKeyStroke("MINUS"), "zoomOut");
//        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "escape");
//
//        ActionMap actionMap = ((JComponent) getContentPane()).getActionMap();
//        actionMap.put("zoomIn", new AbstractAction() {
//            public void actionPerformed(ActionEvent evt) {
//                doZoom(1);
//            }
//        });
//        actionMap.put("zoomOut", new AbstractAction() {
//            public void actionPerformed(ActionEvent evt) {
//                doZoom(-1);
//            }
//        });
//        actionMap.put("escape", new AbstractAction() {
//            public void actionPerformed(ActionEvent evt) {
//                onEscapePressed();
//            }
//        });
//        MouseAdapter mouse = new MouseAdapter() {
//            public void mousePressed(MouseEvent ev) {
//                try {
//                    onToolDown(ev);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//
//            public void mouseReleased(MouseEvent ev) {
//                try {
//                    onToolUp(ev);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//
//            public void mouseDragged(MouseEvent ev) {
//                try {
//                    onToolDrag(ev);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//
//            public void mouseMoved(MouseEvent ev) {
//                try {
//                    onToolHover(ev);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//
//            public void mouseExited(MouseEvent ev) {
//                try {
//                    onToolExited(ev);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//
//            public void mouseWheelMoved(MouseWheelEvent evt) {
//                try {
//                    onMouseWheelMoved(evt);
//                } catch (Throwable e) {
//                    showErrorMessage(e);
//                }
//            }
//        };
//        drawingArea.addMouseListener(mouse);
//        drawingArea.addMouseMotionListener(mouse);
//        drawingArea.addMouseWheelListener(mouse);
//        addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent ev) {
//                closeWindow();
//            }
//
//            public void windowClosed(WindowEvent ev) {
//                onWindowClosed(ev);
//            }
//        });
//
        //Preferences prefs = Preferences.userNodeForPackage(MainWindow.class);
        //doSounds = prefs.getBoolean(SOUNDS_PREF, true);
        // start things up
        //mapView.setEngine(engine);
        engine.addListener(this);
        engine.addEarthquakeListener(this);
        reloadFunds();
        reloadOptions();
        startTimer();
        makeClean();
    }

    public void setEngine(Micropolis newEngine) {
        if (engine != null) { // old engine
            engine.removeListener(this);
            engine.removeEarthquakeListener(this);
        }

        engine = newEngine;

        if (engine != null) { // new engine
            engine.addListener(this);
            engine.addEarthquakeListener(this);
        }

        boolean timerEnabled = isTimerActive();
        if (timerEnabled) {
            stopTimer();
        }
        stopEarthquake();

        drawingArea.setEngine(engine);
        mapView.setEngine(engine);   //must change mapView after drawingArea
        //evaluationPane.setEngine(engine);
        demandInd.setEngine(engine);
        graphsPane.setEngine(engine);
        reloadFunds();
        reloadOptions();
//        notificationPane.setVisible(false);

        if (timerEnabled) {
            startTimer();
        }
    }

    private void selectTool(MicropolisTool newTool) {
        //toolBtns.get(newTool).setSelected(true);
        if (newTool == currentTool) {
            return;
        }

//        if (currentTool != null) {
//            toolBtns.get(currentTool).setSelected(false);
//        }
        currentTool = newTool;

        currentToolLbl.setText(
                MSG.containsKey("tool." + currentTool.name() + ".name")
                ? MSG.getString("tool." + currentTool.name() + ".name")
                : currentTool.name()
        );

        int cost = currentTool.getToolCost();
        currentToolCostLbl.setText(cost != 0 ? formatFunds(cost) : " ");
    }

    public void onNewCityClicked() {
        if (maybeSaveCity()) {
            doNewCity(false);
        }
    }

    public void doNewCity(boolean firstTime) {
        boolean timerEnabled = isTimerActive();
        if (timerEnabled) {
            stopTimer();
        }

        NewCityDialog newCityDialog = new NewCityDialog(this, !firstTime);
        Optional<ButtonType> btnType = newCityDialog.showAndWait();
        if (btnType.get() == ButtonType.OK) {
            engine = new Micropolis();
            new MapGenerator(engine).generateNewCity();
            setEngine(engine); // new Engine();

            //win.currentFile = file;
            //win.makeClean();
            engine.setGameLevel(newCityDialog.getSelectedGameLevel());
            engine.setFunds(GameLevel.getStartingFunds(engine.gameLevel));
        }

        if (timerEnabled) {
            startTimer();
        }
    }

    boolean maybeSaveCity() {
        if (needsSaved()) {
            boolean timerEnabled = isTimerActive();
            if (timerEnabled) {
                stopTimer();
            }

            try {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Save City Dialog");
                //alert.setHeaderText("Look, a Confirmation Dialog");
                alert.setContentText(MSG.getString("main.save_query"));
                ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
                ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    // ... user chose OK
                    if (!onSaveCityClicked()) {
                        // canceled save dialog
                        return false;
                    }
                } else {
                    // ... user chose CANCEL or closed the dialog
                    return false;
                }

//                int rv = JOptionPane.showConfirmDialog(
//                        this,
//                        MSG.getString("main.save_query"),
//                        PRODUCT_NAME,
//                        JOptionPane.YES_NO_CANCEL_OPTION,
//                        JOptionPane.WARNING_MESSAGE);
//                if (rv == JOptionPane.CANCEL_OPTION) {
//                    return false;
//                }
//
//                if (rv == JOptionPane.YES_OPTION) {
//                    if (!onSaveCityClicked()) {
//                        // canceled save dialog
//                        return false;
//                    }
//                }
            } finally {
                if (timerEnabled) {
                    startTimer();
                }
            }
        }
        return true;
    }

    boolean needsSaved() {
        if (dirty1) //player has built something since last save
        {
            return true;
        }

        if (!dirty2) //no simulator ticks since last save
        {
            return false;
        }

        // simulation time has passed since last save, but the player
        // hasn't done anything. Whether we need to prompt for save
        // will depend on how much real time has elapsed.
        // The threshold is 30 seconds.
        return (System.currentTimeMillis() - lastSavedTime > 30000);
    }

    private void makeMenu() {
        MenuBar menuBar = new MenuBar();

        Menu gameMenu = new Menu(MSG.getString("menu.game"));
        setupKeys(gameMenu, "menu.game");
        menuBar.getMenus().add(gameMenu);

        MenuItem menuItem;
        menuItem = new MenuItem(MSG.getString("menu.game.new"));
        setupKeys(menuItem, "menu.game.new");
        menuItem.setOnAction((t) -> {
            onNewCityClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onNewCityClicked();
//            }
//        }));
        gameMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.game.load"));
        setupKeys(menuItem, "menu.game.load");
        menuItem.setOnAction((t) -> {
            onLoadGameClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onLoadGameClicked();
//            }
//        }));
        gameMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.game.save"));
        setupKeys(menuItem, "menu.game.save");
        menuItem.setOnAction((t) -> {
            onSaveCityClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onSaveCityClicked();
//            }
//        }));
        gameMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.game.save_as"));
        setupKeys(menuItem, "menu.game.save_as");
        menuItem.setOnAction((t) -> {
            onSaveCityAsClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onSaveCityAsClicked();
//            }
//        }));
        gameMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.game.exit"));
        setupKeys(menuItem, "menu.game.exit");
        menuItem.setOnAction((t) -> {
            closeWindow();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                closeWindow();
//            }
//        }));
        gameMenu.getItems().add(menuItem);

        Menu optionsMenu = new Menu(MSG.getString("menu.options"));
        setupKeys(optionsMenu, "menu.options");
        menuBar.getMenus().add(optionsMenu);

        Menu levelMenu = new Menu(MSG.getString("menu.difficulty"));
        setupKeys(levelMenu, "menu.difficulty");
        optionsMenu.getItems().add(levelMenu);

        difficultyMenuItems = new HashMap<Integer, MenuItem>();
        for (int i = GameLevel.MIN_LEVEL; i <= GameLevel.MAX_LEVEL; i++) {
            final int level = i;
            menuItem = new RadioMenuItem(MSG.getString("menu.difficulty." + level));
            setupKeys(menuItem, "menu.difficulty." + level);
            menuItem.setOnAction((t) -> {
                onDifficultyClicked(level);
            });
//            menuItem.addActionListener(wrapActionListener(
//                    new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    onDifficultyClicked(level);
//                }
//            }));
            levelMenu.getItems().add(menuItem);
            difficultyMenuItems.put(level, menuItem);
        }

        autoBudgetMenuItem = new CheckMenuItem(MSG.getString("menu.options.auto_budget"));
        setupKeys(autoBudgetMenuItem, "menu.options.auto_budget");
        autoBudgetMenuItem.setOnAction((t) -> {
            onAutoBudgetClicked();
        });
//        autoBudgetMenuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onAutoBudgetClicked();
//            }
//        }));
        optionsMenu.getItems().add(autoBudgetMenuItem);

        autoBulldozeMenuItem = new CheckMenuItem(MSG.getString("menu.options.auto_bulldoze"));
        setupKeys(autoBulldozeMenuItem, "menu.options.auto_bulldoze");
        autoBulldozeMenuItem.setOnAction((t) -> {
            onAutoBulldozeClicked();
        });
//        autoBulldozeMenuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onAutoBulldozeClicked();
//            }
//        }));
        optionsMenu.getItems().add(autoBulldozeMenuItem);

        disastersMenuItem = new CheckMenuItem(MSG.getString("menu.options.disasters"));
        setupKeys(disastersMenuItem, "menu.options.disasters");
        disastersMenuItem.setOnAction((t) -> {
            onDisastersClicked();
        });
//        disastersMenuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onDisastersClicked();
//            }
//        }));
        optionsMenu.getItems().add(disastersMenuItem);

        soundsMenuItem = new CheckMenuItem(MSG.getString("menu.options.sound"));
        setupKeys(soundsMenuItem, "menu.options.sound");
        soundsMenuItem.setOnAction((t) -> {
            onSoundClicked();
        });
//        soundsMenuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onSoundClicked();
//            }
//        }));
        optionsMenu.getItems().add(soundsMenuItem);

        menuItem = new MenuItem(MSG.getString("menu.options.zoom_in"));
        setupKeys(menuItem, "menu.options.zoom_in");
        menuItem.setOnAction((t) -> {
            doZoom(1);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                doZoom(1);
//            }
//        }));
        optionsMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.options.zoom_out"));
        setupKeys(menuItem, "menu.options.zoom_out");
        menuItem.setOnAction((t) -> {
            doZoom(-1);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                doZoom(-1);
//            }
//        }));
        optionsMenu.getItems().add(menuItem);

        Menu disastersMenu = new Menu(MSG.getString("menu.disasters"));
        setupKeys(disastersMenu, "menu.disasters");
        menuBar.getMenus().add(disastersMenu);

        menuItem = new MenuItem(MSG.getString("menu.disasters.MONSTER"));
        setupKeys(menuItem, "menu.disasters.MONSTER");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.MONSTER);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.MONSTER);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.disasters.FIRE"));
        setupKeys(menuItem, "menu.disasters.FIRE");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.FIRE);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.FIRE);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.disasters.FLOOD"));
        setupKeys(menuItem, "menu.disasters.FLOOD");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.FLOOD);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.FLOOD);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.disasters.MELTDOWN"));
        setupKeys(menuItem, "menu.disasters.MELTDOWN");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.MELTDOWN);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.MELTDOWN);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.disasters.TORNADO"));
        setupKeys(menuItem, "menu.disasters.TORNADO");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.TORNADO);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.TORNADO);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.disasters.EARTHQUAKE"));
        setupKeys(menuItem, "menu.disasters.EARTHQUAKE");
        menuItem.setOnAction((t) -> {
            onInvokeDisasterClicked(Disaster.EARTHQUAKE);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onInvokeDisasterClicked(Disaster.EARTHQUAKE);
//            }
//        }));
        disastersMenu.getItems().add(menuItem);

        Menu priorityMenu = new Menu(MSG.getString("menu.speed"));
        setupKeys(priorityMenu, "menu.speed");
        menuBar.getMenus().add(priorityMenu);

        priorityMenuItems = new EnumMap<Speed, MenuItem>(Speed.class);
        menuItem = new RadioMenuItem(MSG.getString("menu.speed.SUPER_FAST"));
        setupKeys(menuItem, "menu.speed.SUPER_FAST");
        menuItem.setOnAction((t) -> {
            onPriorityClicked(Speed.SUPER_FAST);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onPriorityClicked(Speed.SUPER_FAST);
//            }
//        }));
        priorityMenu.getItems().add(menuItem);
        priorityMenuItems.put(Speed.SUPER_FAST, menuItem);

        menuItem = new RadioMenuItem(MSG.getString("menu.speed.FAST"));
        setupKeys(menuItem, "menu.speed.FAST");
        menuItem.setOnAction((t) -> {
            onPriorityClicked(Speed.FAST);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onPriorityClicked(Speed.FAST);
//            }
//        }));
        priorityMenu.getItems().add(menuItem);
        priorityMenuItems.put(Speed.FAST, menuItem);

        menuItem = new RadioMenuItem(MSG.getString("menu.speed.NORMAL"));
        setupKeys(menuItem, "menu.speed.NORMAL");
        menuItem.setOnAction((t) -> {
            onPriorityClicked(Speed.NORMAL);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onPriorityClicked(Speed.NORMAL);
//            }
//        }));
        priorityMenu.getItems().add(menuItem);
        priorityMenuItems.put(Speed.NORMAL, menuItem);

        menuItem = new RadioMenuItem(MSG.getString("menu.speed.SLOW"));
        setupKeys(menuItem, "menu.speed.SLOW");
        menuItem.setOnAction((t) -> {
            onPriorityClicked(Speed.SLOW);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onPriorityClicked(Speed.SLOW);
//            }
//        }));
        priorityMenu.getItems().add(menuItem);
        priorityMenuItems.put(Speed.SLOW, menuItem);

        menuItem = new RadioMenuItem(MSG.getString("menu.speed.PAUSED"));
        setupKeys(menuItem, "menu.speed.PAUSED");
        menuItem.setOnAction((t) -> {
            onPriorityClicked(Speed.PAUSED);
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onPriorityClicked(Speed.PAUSED);
//            }
//        }));
        priorityMenu.getItems().add(menuItem);
        priorityMenuItems.put(Speed.PAUSED, menuItem);

        Menu windowsMenu = new Menu(MSG.getString("menu.windows"));
        setupKeys(windowsMenu, "menu.windows");
        menuBar.getMenus().add(windowsMenu);

        menuItem = new MenuItem(MSG.getString("menu.windows.budget"));
        setupKeys(menuItem, "menu.windows.budget");
        menuItem.setOnAction((t) -> {
            onViewBudgetClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onViewBudgetClicked();
//            }
//        }));
        windowsMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.windows.evaluation"));
        setupKeys(menuItem, "menu.windows.evaluation");
        menuItem.setOnAction((t) -> {
            onViewEvaluationClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onViewEvaluationClicked();
//            }
//        }));
        windowsMenu.getItems().add(menuItem);

        menuItem = new MenuItem(MSG.getString("menu.windows.graph"));
        setupKeys(menuItem, "menu.windows.graph");
        menuItem.setOnAction((t) -> {
            onViewGraphClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onViewGraphClicked();
//            }
//        }));
        windowsMenu.getItems().add(menuItem);

        Menu helpMenu = new Menu(MSG.getString("menu.help"));
        setupKeys(helpMenu, "menu.help");
        menuBar.getMenus().add(helpMenu);

//        menuItem = new MenuItem(MSG.getString("menu.help.launch-translation-tool"));
//        setupKeys(menuItem, "menu.help.launch-translation-tool");
//        menuItem.setOnAction((t) -> {
//            onLaunchTranslationToolClicked();
//        });
////        menuItem.addActionListener(new ActionListener() {
////            public void actionPerformed(ActionEvent evt) {
////                onLaunchTranslationToolClicked();
////            }
////        });
//        helpMenu.getItems().add(menuItem);
        menuItem = new MenuItem(MSG.getString("menu.help.about"));
        setupKeys(menuItem, "menu.help.about");
        menuItem.setOnAction((t) -> {
            onAboutClicked();
        });
//        menuItem.addActionListener(wrapActionListener(
//                new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onAboutClicked();
//            }
//        }));
        helpMenu.getItems().add(menuItem);

        //setJMenuBar(menuBar);
        setTop(menuBar);
    }

    private void makeRightBar() {
        setRight(makeToolbar());
    }

    private void makeBottomBar() {
        Region spacer = new Region();
        HBox.getHgrow(spacer);

        HBox box = new HBox();
        box.setPadding(new Insets(6, 16, 6, 16));
        box.setSpacing(6);
        // Info
        // Speed Control
        // spacer
        box.getChildren().add(spacer);
        // info
        // RCI
        box.getChildren().add(demandInd);
        box.getChildren().add(messagesPane);
        box.getChildren().add(dateFundsPopPane);
        //box.getChildren().add(new OverlayMapView(engine));
//        box.getChildren().add(new MiniMap(engine));

        setBottom(box);
    }

    private Node makeToolbar() {
        //toolBtns = new EnumMap<>(MicropolisTool.class);

        GridPane toolGrid = new GridPane();
        toolGrid.setHgap(4);
        toolGrid.setVgap(4);
        toolGrid.setPadding(new Insets(12));
        // First column
        toolGrid.add(makeToolBtn(MicropolisTool.BULLDOZER), 0, 0);
        toolGrid.add(makeToolBtn(MicropolisTool.WIRE), 0, 1);
        toolGrid.add(makeToolBtn(MicropolisTool.PARK), 0, 2);
        toolGrid.add(makeToolBtn(MicropolisTool.COMMERCIAL), 0, 3);
        toolGrid.add(makeToolBtn(MicropolisTool.POLICE), 0, 4);
        toolGrid.add(makeToolBtn(MicropolisTool.STADIUM), 0, 5);
        toolGrid.add(makeToolBtn(MicropolisTool.SEAPORT), 0, 6);
        toolGrid.add(makeToolBtn(MicropolisTool.NUCLEAR), 0, 7);
        // Second column
        toolGrid.add(makeToolBtn(MicropolisTool.ROADS), 1, 0);
        toolGrid.add(makeToolBtn(MicropolisTool.RAIL), 1, 1);
        toolGrid.add(makeToolBtn(MicropolisTool.RESIDENTIAL), 1, 2);
        toolGrid.add(makeToolBtn(MicropolisTool.INDUSTRIAL), 1, 3);
        toolGrid.add(makeToolBtn(MicropolisTool.FIRE), 1, 4);
        toolGrid.add(makeToolBtn(MicropolisTool.POWERPLANT), 1, 5);
        toolGrid.add(makeToolBtn(MicropolisTool.AIRPORT), 1, 6);

        currentToolLbl = new Label(" ");
        currentToolCostLbl = new Label(" ");
        VBox currentToolInfoBox = new VBox(currentToolLbl, currentToolCostLbl);
        currentToolInfoBox.setAlignment(Pos.CENTER);
        currentToolInfoBox.setPadding(new Insets(2));
        currentToolInfoBox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(6), new BorderWidths(1))));
        VBox box = new VBox(toolGrid, currentToolInfoBox);
        box.setPadding(new Insets(4));
        box.setSpacing(6);

        //ToolBar toolBar = new ToolBar(MSG.getString("main.tools_caption"), ToolBar.VERTICAL);
        //toolBar.setFloatable(false);
        //toolBar.setRollover(false);
        //JPanel gridBox = new JPanel(new GridBagLayout());
        //toolBar.add(gridBox);
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = c.gridy = 0;
//        c.anchor = GridBagConstraints.NORTH;
//        c.insets = new Insets(8, 0, 0, 0);
//        currentToolLbl = new JLabel(" ");
//        gridBox.add(currentToolLbl, c);
//
//        c.gridy = 1;
//        c.insets = new Insets(0, 0, 12, 0);
//        currentToolCostLbl = new JLabel(" ");
//        gridBox.add(currentToolCostLbl, c);
//
//        c.gridy++;
//        c.fill = GridBagConstraints.NONE;
//        c.weightx = 1.0;
//        c.insets = new Insets(0, 0, 0, 0);
//        Box b0 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b0, c);
//
//        b0.add(makeToolBtn(MicropolisTool.BULLDOZER));
//        b0.add(makeToolBtn(MicropolisTool.WIRE));
//        b0.add(makeToolBtn(MicropolisTool.PARK));
//
//        c.gridy++;
//        Box b1 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b1, c);
//
//        b1.add(makeToolBtn(MicropolisTool.ROADS));
//        b1.add(makeToolBtn(MicropolisTool.RAIL));
//
//        c.gridy++;
//        Box b2 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b2, c);
//
//        b2.add(makeToolBtn(MicropolisTool.RESIDENTIAL));
//        b2.add(makeToolBtn(MicropolisTool.COMMERCIAL));
//        b2.add(makeToolBtn(MicropolisTool.INDUSTRIAL));
//
//        c.gridy++;
//        Box b3 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b3, c);
//
//        b3.add(makeToolBtn(MicropolisTool.FIRE));
//        b3.add(makeToolBtn(MicropolisTool.QUERY));
//        b3.add(makeToolBtn(MicropolisTool.POLICE));
//
//        c.gridy++;
//        Box b4 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b4, c);
//
//        b4.add(makeToolBtn(MicropolisTool.POWERPLANT));
//        b4.add(makeToolBtn(MicropolisTool.NUCLEAR));
//
//        c.gridy++;
//        Box b5 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b5, c);
//
//        b5.add(makeToolBtn(MicropolisTool.STADIUM));
//        b5.add(makeToolBtn(MicropolisTool.SEAPORT));
//
//        c.gridy++;
//        Box b6 = new Box(BoxLayout.X_AXIS);
//        gridBox.add(b6, c);
//
//        b6.add(makeToolBtn(MicropolisTool.AIRPORT));
//
//        // add glue to make all elements align toward top
//        c.gridy++;
//        c.weighty = 1.0;
//        gridBox.add(new Label(), c);
        return box;
    }

    private ToggleButton makeToolBtn(final MicropolisTool tool) {
        String iconName = MSG.containsKey("tool." + tool.name() + ".icon")
                ? MSG.getString("tool." + tool.name() + ".icon")
                : "/images/" + tool.name().toLowerCase() + ".png";
        String iconSelectedName = MSG.containsKey("tool." + tool.name() + ".selected_icon")
                ? MSG.getString("tool." + tool.name() + ".selected_icon")
                : iconName;
        String tipText = MSG.containsKey("tool." + tool.name() + ".tip")
                ? MSG.getString("tool." + tool.name() + ".tip")
                : tool.name();

        LOGGER.log(Level.FINEST, "Icon Load: " + "/buttons/" + iconSelectedName);
        Image img = new Image(MainPane.class.getResourceAsStream("/buttons/" + iconSelectedName));
        ImageView iv = new ImageView(img);
        iv.setFitHeight(32);
        iv.setPreserveRatio(true);
        ToggleButton btn = new ToggleButton(null, iv);
        btn.setUserData(tool);
        //btn.setIcon(new ImageIcon(MainPane.class.getResource(iconName)));
        //btn.setSelectedIcon(new ImageIcon(MainPane.class.getResource(iconSelectedName)));
        btn.setTooltip(new Tooltip(tipText));
        btn.setPadding(new Insets(0, 0, 0, 0));
        //btn.setBorderPainted(false);
        btn.setOnAction((t) -> {
            selectTool(tool);
        });
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                selectTool(tool);
//            }
//        });
        //toolBtns.put(tool, btn);
        btn.setToggleGroup(toolToggles);
        return btn;
    }

    private void setupKeys(Menu menu, String prefix) {
        if (MSG.containsKey(prefix + ".key")) {
            String mnemonic = MSG.getString(prefix + ".key");
            // ex with modifiers
            //menu.setAccelerator(new KeyCodeCombination(KeyCode.valueOf(mnemonic), KeyCombination.CONTROL_DOWN, KeyCombination.SHORTCUT_DOWN));
            menu.setAccelerator(new KeyCodeCombination(KeyCode.valueOf(mnemonic)));

//            menu.setMnemonic( // Swing/AWT
//                    KeyStroke.getKeyStroke(mnemonic).getKeyCode()
//            );
        }
    }

    private void setupKeys(MenuItem menuItem, String prefix) {
        if (MSG.containsKey(prefix + ".key")) {
            String mnemonic = MSG.getString(prefix + ".key");
            menuItem.setAccelerator(new KeyCodeCombination(KeyCode.valueOf(mnemonic)));
//            menuItem.setMnemonic(
//                    KeyStroke.getKeyStroke(mnemonic).getKeyCode()
//            );
        }
        if (MSG.containsKey(prefix + ".shortcut")) { // TODO: Test me
            String shortcut = MSG.getString(prefix + ".shortcut");
            if (shortcut.startsWith("ctrl")) {
                String[] split = shortcut.split("\\s+");
                menuItem.setAccelerator(new KeyCodeCombination(KeyCode.valueOf(split[1]), KeyCombination.CONTROL_DOWN));
            } else {
                menuItem.setAccelerator(new KeyCodeCombination(KeyCode.valueOf(shortcut)));
            }
//            menuItem.setAccelerator(
//                    KeyStroke.getKeyStroke(shortcut)
//            );
        }
    }

    private class EarthquakeStepper {

        int count = 0;

        void oneStep() {
            count = (count + 1) % MicropolisDrawingArea.SHAKE_STEPS;
            drawingArea.shake(count);
        }
    }

    EarthquakeStepper currentEarthquake;

    //implements EarthquakeListener
    @Override
    public void earthquakeStarted() {
        if (isTimerActive()) {
            stopTimer();
        }

        currentEarthquake = new EarthquakeStepper();
        currentEarthquake.oneStep();
        startTimer();
    }

    void stopEarthquake() {
        drawingArea.shake(0);
        currentEarthquake = null;
    }

    Timer simTimer;
    Timer shakeTimer;

    private void startTimer() {
        final Micropolis eng = engine;
        final int count = eng.simSpeed.simStepsPerUpdate;

        assert !isTimerActive();

        if (eng.simSpeed == Speed.PAUSED) {
            return;
        }

        if (currentEarthquake != null) {
            int interval = 3000 / MicropolisDrawingArea.SHAKE_STEPS;
            shakeTimer = new Timer();
            shakeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (currentEarthquake.count == 0) {
                        stopTimer();
                        currentEarthquake = null;
                        startTimer();
                    }
                }
            }, interval);

//            shakeTimer = new Timer(interval, new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    currentEarthquake.oneStep();
//                    if (currentEarthquake.count == 0) {
//                        stopTimer();
//                        currentEarthquake = null;
//                        startTimer();
//                    }
//                }
//            });
            //shakeTimer.start();
            return;
        }

//        ActionListener taskPerformer = new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                for (int i = 0; i < count; i++) {
//                    eng.animate();
//                    if (!eng.autoBudget && eng.isBudgetTime()) {
//                        showAutoBudget();
//                        return;
//                    }
//                }
//                updateDateLabel();
//                dirty2 = true;
//            }
//        };
        TimerTask tf = new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < count; i++) {
                    eng.animate();
                    if (!eng.autoBudget && eng.isBudgetTime()) {
                        showAutoBudget();
                        return;
                    }
                }
                updateDateLabel();
                dirty2 = true;
            }
        }; //taskPerformer = wrapActionListener(taskPerformer);

        assert simTimer == null;
        simTimer = new Timer();
        simTimer.schedule(tf, eng.simSpeed.animationDelay);
        //simTimer.start();
    }

    private void stopTimer() {
        assert isTimerActive();

        if (simTimer != null) {
            simTimer.cancel();
            simTimer = null;
        }
        if (shakeTimer != null) {
            shakeTimer.cancel();
            shakeTimer = null;
        }
    }

    boolean isTimerActive() {
        return simTimer != null || shakeTimer != null;
    }

    public void onWindowClosed(WindowEvent ev) {
        if (isTimerActive()) {
            stopTimer();
        }
    }

    private void onDifficultyClicked(int newDifficulty) {
        engine.setGameLevel(newDifficulty);
    }

    private void onPriorityClicked(Speed newSpeed) {
        if (isTimerActive()) {
            stopTimer();
        }

        engine.setSpeed(newSpeed);
        startTimer();
    }

    private void onInvokeDisasterClicked(Disaster disaster) {
        dirty1 = true;
        switch (disaster) {
            case FIRE:
                engine.makeFire();
                break;
            case FLOOD:
                engine.makeFlood();
                break;
            case MONSTER:
                engine.makeMonster();
                break;
            case MELTDOWN:
                if (!engine.makeMeltdown()) {
                    messagesPane.appendCityMessage(MicropolisMessage.NO_NUCLEAR_PLANTS);
                }
                break;
            case TORNADO:
                engine.makeTornado();
                break;
            case EARTHQUAKE:
                engine.makeEarthquake();
                break;
            default:
                assert false; //unknown disaster
        }
    }

    private void reloadFunds() {
        dateFundsPopPane.setFunds(formatFunds(engine.budget.totalFunds));
    }

    //implements Micropolis.Listener
    @Override
    public void cityMessage(MicropolisMessage m, CityLocation p) {
        messagesPane.appendCityMessage(m);

        if (m.useNotificationPane && p != null) {
            new NotificationAlert(engine, m, p.x, p.y).showAndWait();
            //notificationPane.showMessage(engine, m, p.x, p.y);
        }
    }

    //implements Micropolis.Listener
    @Override
    public void fundsChanged() {
        reloadFunds();
    }

    //implements Micropolis.Listener
    @Override
    public void optionsChanged() {
        reloadOptions();
    }

    private void reloadOptions() {
        autoBudgetMenuItem.setSelected(engine.autoBudget);
        autoBulldozeMenuItem.setSelected(engine.autoBulldoze);
        disastersMenuItem.setSelected(!engine.noDisasters);
        soundsMenuItem.setSelected(doSounds);
        for (Speed spd : priorityMenuItems.keySet()) {
            if (priorityMenuItems.get(spd) instanceof RadioMenuItem item) {
                item.setSelected(engine.simSpeed == spd);
            }
        }
        for (int i = GameLevel.MIN_LEVEL; i <= GameLevel.MAX_LEVEL; i++) {
            if (difficultyMenuItems.get(i) instanceof RadioMenuItem item) {
                item.setSelected(engine.gameLevel == i);
            }
        }
    }

    private void onAutoBudgetClicked() {
        dirty1 = true;
        engine.toggleAutoBudget();
    }

    private void onAutoBulldozeClicked() {
        dirty1 = true;
        engine.toggleAutoBulldoze();
    }

    private void onDisastersClicked() {
        dirty1 = true;
        engine.toggleDisasters();
    }

    private void onSoundClicked() {
        doSounds = !doSounds;
        AppProperties appProps = AppProperties.getInstance();
        appProps.put(SOUNDS_PREF, doSounds);
        //Preferences prefs = Preferences.userNodeForPackage(MainPane.class);
        //prefs.putBoolean(SOUNDS_PREF, doSounds);
        reloadOptions();
    }

    private void onAboutClicked() {
        String version = getClass().getPackage().getImplementationVersion();
        String versionStr = MessageFormat.format(MSG.getString("main.version_string"), version);
        versionStr = versionStr.replace("%java.version%", System.getProperty("java.version"));
        versionStr = versionStr.replace("%java.vendor%", System.getProperty("java.vendor"));

//        Label appNameLbl = new Label(versionStr);
//        Label appDetailsLbl = new Label(MSG.getString("main.about_text"));
//        JComponent[] inputs = new JComponent[]{appNameLbl, appDetailsLbl};
//        JOptionPane.showMessageDialog(this,
//                inputs,
//                strings.getString("main.about_caption"),
//                JOptionPane.PLAIN_MESSAGE,
//                appIcon);
        Label appNameLbl = new Label(versionStr);
        Label appDetailsLbl = new Label(MSG.getString("main.about_text"));
        VBox box = new VBox(appNameLbl, appDetailsLbl);

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setGraphic(null);
        alert.getDialogPane().setContent(box);
        //alert.setContentText("I have a great message for you!");

        alert.showAndWait();
    }

    final void makeClean() {
        dirty1 = false;
        dirty2 = false;
        lastSavedTime = System.currentTimeMillis();
        if (currentFile != null) {
            String fileName = currentFile.getName();
            if (fileName.endsWith("." + EXTENSION)) {
                fileName = fileName.substring(0, fileName.length() - 1 - EXTENSION.length());
            }

            Podunk.primaryStage.setTitle(MessageFormat.format(MSG.getString("main.caption_named_city"), fileName));
        } else {
            Podunk.primaryStage.setTitle(MSG.getString("main.caption_unnamed_city"));
        }
    }

    private void setMapLegend(MapState state) {
        String k = "legend_image." + state.name();
        //java.net.URL iconUrl = null;
        InputStream iconStream = null;
        if (MSG.containsKey(k)) {
            String iconName = MSG.getString(k);
            //iconUrl = MainWindow.class.getResource(iconName);
            iconStream = getClass().getResourceAsStream("/images/" + iconName);
        }
        if (iconStream != null) {
            mapLegendLbl.setGraphic(new ImageView(new Image(iconStream)));
        } else {
            mapLegendLbl.setGraphic(null);
        }
    }

//    private void onLaunchTranslationToolClicked() {
//        if (maybeSaveCity()) {
//            //dispose(); ???
//            TranslationTool tt = new TranslationTool();
//            tt.setVisible(true);
//        }
//    }
    private boolean onSaveCityClicked() {
        if (currentFile == null) {
            return onSaveCityAsClicked();
        }

        try {
            engine.save(currentFile);
            makeClean();
            return true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
//            JOptionPane.showMessageDialog(this, e, MSG.getString("main.error_caption"),
//                    JOptionPane.ERROR_MESSAGE);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Save Error");
            alert.setContentText(MSG.getString("main.error_caption"));

            alert.showAndWait();
            return false;
        }
    }

    private boolean onSaveCityAsClicked() {
        boolean timerEnabled = isTimerActive();
        if (timerEnabled) {
            stopTimer();
        }
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save City");
            LOGGER.log(Level.CONFIG, "Save City Clicked.");
            File file = fileChooser.showSaveDialog(getScene().getWindow());
            if (file != null) {
                try {
                    currentFile = file;
                    if (!currentFile.getName().endsWith("." + EXTENSION)) {
                        currentFile = new File(currentFile.getPath() + "." + EXTENSION);
                    }
                    engine.save(currentFile);
                    makeClean();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }

//            JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter filter1 = new FileNameExtensionFilter(MSG.getString("cty_file"), EXTENSION);
//            fc.setFileFilter(filter1);
//            int rv = fc.showSaveDialog(this);
//            if (rv == JFileChooser.APPROVE_OPTION) {
//                currentFile = fc.getSelectedFile();
//                if (!currentFile.getName().endsWith("." + EXTENSION)) {
//                    currentFile = new File(currentFile.getPath() + "." + EXTENSION);
//                }
//                engine.save(currentFile);
//                makeClean();
//                return true;
//            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
            LOGGER.log(Level.SEVERE, "Save As Error Occured", e);
//            JOptionPane.showMessageDialog(this, e, MSG.getString("main.error_caption"),
//                    JOptionPane.ERROR_MESSAGE);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Save As Error");
            alert.setContentText(MSG.getString("main.error_caption"));

            alert.showAndWait();
        } finally {
            if (timerEnabled) {
                startTimer();
            }
        }
        return false;
    }

    public void onLoadGameClicked() {
        // check if user wants to save their current city
        if (!maybeSaveCity()) {
            return;
        }

        boolean timerEnabled = isTimerActive();
        if (timerEnabled) {
            stopTimer();
        }

        try {
            assert !isTimerActive();

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load City");
            LOGGER.log(Level.CONFIG, "Load City Clicked.");
            File file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                //File file = fc.getSelectedFile();
                Micropolis newEngine = new Micropolis();
                newEngine.load(file);
                setEngine(newEngine);
                currentFile = file;
                makeClean();
            }

//            JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter filter1 = new FileNameExtensionFilter(MSG.getString("cty_file"), EXTENSION);
//            fc.setFileFilter(filter1);
//
//            int rv = fc.showOpenDialog(this);
//            if (rv == JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//                Micropolis newEngine = new Micropolis();
//                newEngine.load(file);
//                setEngine(newEngine);
//                currentFile = file;
//                makeClean();
//            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
//            JOptionPane.showMessageDialog(this, e, MSG.getString("main.error_caption"),
//                    JOptionPane.ERROR_MESSAGE);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Load City Error");
            alert.setContentText(MSG.getString("main.error_caption"));

            alert.showAndWait();
        } finally {
            if (timerEnabled) {
                startTimer();
            }
        }
    }

    @Override
    public void citySound(Sound sound, CityLocation loc) {
        if (!doSounds) {
            return;
        }

//        URL afile = sound.getAudioFile();
//        if (afile == null) {
//            return;
//        }
//
//        boolean isOnScreen = drawingAreaScroll.getViewport().getViewRect().contains(
//                drawingArea.getTileBounds(loc.x, loc.y)
//        );
//        if (sound == Sound.HONKHONK_LOW && !isOnScreen) {
//            return;
//        }
//
//        try {
//            Clip clip = AudioSystem.getClip();
//            clip.open(AudioSystem.getAudioInputStream(afile));
//            clip.start();
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
    }

    void onViewBudgetClicked() {
        dirty1 = true;
        showBudgetWindow(false);
    }

    void onViewEvaluationClicked() {
        //evaluationPane.setVisible(true);
        //evaluationPane.showAndWait();
        new EvaluationPane(engine).showAndWait();
    }

    void onViewGraphClicked() {
        graphsPane.show();
    }

    private void showAutoBudget() {
        if (toolStroke == null) {
            showBudgetWindow(true);
        } else {
            autoBudgetPending = true;
        }
    }

    private void showBudgetWindow(boolean isEndOfYear) {
        boolean timerEnabled = isTimerActive();
        if (timerEnabled) {
            stopTimer();
        }

        BudgetDialog dlg = new BudgetDialog(engine);
        dlg.showAndWait();
        //dlg.setModal(true);
        //dlg.setVisible(true);

        if (timerEnabled) {
            startTimer();
        }
    }

    private MenuItem makeMapStateMenuItem(String stringPrefix, final MapState state) {
        String caption = MSG.getString(stringPrefix);
        RadioMenuItem menuItem = new RadioMenuItem(caption);
        setupKeys(menuItem, stringPrefix);
//        menuItem.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                setMapState(state);
//            }
//        });
        menuItem.setOnAction((t) -> {
            setMapState(state);
        });
        mapStateMenuItems.put(state, menuItem);
        //menuItem.setToggleGroup(mapStateToggles);
        return menuItem;
    }

    private void setMapState(MapState state) {
        mapStateMenuItems.get(mapView.getMapState()).setSelected(false);
        mapStateMenuItems.get(state).setSelected(true);
        mapView.setMapState(state);
        setMapLegend(state);
    }

    private void onToolDown(MouseEvent ev) {
//        if (ev.getButton() == MouseEvent.BUTTON3) {
//            CityLocation loc = drawingArea.getCityLocation(ev.getX(), ev.getY());
//            doQueryTool(loc.x, loc.y);
//            return;
//        }
//
//        if (ev.getButton() != MouseEvent.BUTTON1) {
//            return;
//        }
//
//        if (currentTool == null) {
//            return;
//        }
//
//        CityLocation loc = drawingArea.getCityLocation(ev.getX(), ev.getY());
//        int x = loc.x;
//        int y = loc.y;
//
//        if (currentTool == MicropolisTool.QUERY) {
//            doQueryTool(x, y);
//            this.toolStroke = null;
//        } else {
//            this.toolStroke = currentTool.beginStroke(engine, x, y);
//            previewTool();
//        }
//
//        this.lastX = x;
//        this.lastY = y;
    }

    private void onEscapePressed() {
        // if currently dragging a tool...
        if (toolStroke != null) {
            // cancel the current mouse operation
            toolStroke = null;
            drawingArea.setToolPreview(null);
            drawingArea.setToolCursor(null);
        } else {
            // dismiss any alerts currently visible
            notificationPane.setVisible(false);
        }
    }

    private void onToolUp(MouseEvent ev) {
        if (toolStroke != null) {
            drawingArea.setToolPreview(null);

            CityLocation loc = toolStroke.getLocation();
            ToolResult tr = toolStroke.apply();
            showToolResult(loc, tr);
            toolStroke = null;
        }

        onToolHover(ev);

        if (autoBudgetPending) {
            autoBudgetPending = false;
            showBudgetWindow(true);
        }
    }

    void previewTool() {
        assert this.toolStroke != null;
        assert this.currentTool != null;

        drawingArea.setToolCursor(
                toolStroke.getBounds(),
                currentTool
        );
        drawingArea.setToolPreview(
                toolStroke.getPreview()
        );
    }

    private void onToolDrag(MouseEvent ev) {
//        if (currentTool == null) {
//            return;
//        }
//        if ((ev.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == 0) {
//            return;
//        }
//
//        CityLocation loc = drawingArea.getCityLocation(ev.getX(), ev.getY());
//        int x = loc.x;
//        int y = loc.y;
//        if (x == lastX && y == lastY) {
//            return;
//        }
//
//        if (toolStroke != null) {
//            toolStroke.dragTo(x, y);
//            previewTool();
//        } else if (currentTool == MicropolisTool.QUERY) {
//            doQueryTool(x, y);
//        }
//
//        lastX = x;
//        lastY = y;
    }

    private void onToolHover(MouseEvent ev) {
//        if (currentTool == null || currentTool == MicropolisTool.QUERY) {
//            drawingArea.setToolCursor(null);
//            return;
//        }
//
//        CityLocation loc = drawingArea.getCityLocation(ev.getX(), ev.getY());
//        int x = loc.x;
//        int y = loc.y;
//        int w = currentTool.getWidth();
//        int h = currentTool.getHeight();
//
//        if (w >= 3) {
//            x--;
//        }
//        if (h >= 3) {
//            y--;
//        }
//
//        drawingArea.setToolCursor(new CityRect(x, y, w, h), currentTool);
    }

    private void onToolExited(MouseEvent ev) {
        drawingArea.setToolCursor(null);
    }

    private void showToolResult(CityLocation loc, ToolResult result) {
        switch (result) {
            case SUCCESS:
                citySound(currentTool == MicropolisTool.BULLDOZER ? Sound.BULLDOZE : Sound.BUILD, loc);
                dirty1 = true;
                break;

            case NONE:
                break;
            case UH_OH:
                messagesPane.appendCityMessage(MicropolisMessage.BULLDOZE_FIRST);
                citySound(Sound.UHUH, loc);
                break;

            case INSUFFICIENT_FUNDS:
                messagesPane.appendCityMessage(MicropolisMessage.INSUFFICIENT_FUNDS);
                citySound(Sound.SORRY, loc);
                break;

            default:
                assert false;
        }
    }

    public String formatFunds(int funds) {
        return MessageFormat.format(
                MSG.getString("funds"), funds
        );
    }

    public String formatGameDate(int cityTime) {
        Calendar c = Calendar.getInstance();
        c.set(1900 + cityTime / 48,
                (cityTime % 48) / 4,
                (cityTime % 4) * 7 + 1
        );

        return MessageFormat.format(
                MSG.getString("citytime"),
                c.getTime()
        );
    }

    private void updateDateLabel() {
        dateFundsPopPane.setDate(formatGameDate(engine.cityTime));

        NumberFormat nf = NumberFormat.getInstance();
        dateFundsPopPane.setPop(nf.format(engine.getCityPopulation()));
    }

    void doQueryTool(int xpos, int ypos) {
        if (!engine.testBounds(xpos, ypos)) {
            return;
        }

        ZoneStatus z = engine.queryZoneStatus(xpos, ypos);
        new NotificationAlert(engine, z, xpos, ypos).showAndWait();
        //notificationPane.showZoneStatus(engine, xpos, ypos, z);
    }

    private void doZoom(int dir, Point2D mousePt) {
//        int oldZoom = drawingArea.getTileSize();
//        int newZoom = dir < 0 ? (oldZoom / 2) : (oldZoom * 2);
//        if (newZoom < 8) {
//            newZoom = 8;
//        }
//        if (newZoom > 32) {
//            newZoom = 32;
//        }
//
//        if (oldZoom != newZoom) {
//            // preserve effective mouse position in viewport when changing zoom level
//            double f = (double) newZoom / (double) oldZoom;
//            Point2D pos = drawingAreaScroll.getViewport().getViewPosition();
//            int newX = (int) Math.round(mousePt.x * f - (mousePt.x - pos.x));
//            int newY = (int) Math.round(mousePt.y * f - (mousePt.y - pos.y));
//            drawingArea.selectTileSize(newZoom);
//            drawingAreaScroll.validate();
//            drawingAreaScroll.getViewport().setViewPosition(new Point(newX, newY));
//        }
    }

    private void doZoom(int dir) {
//        Rectangle rect = drawingAreaScroll.getViewport().getViewRect();
//        doZoom(dir,
//                new Point2D(rect.x + rect.width / 2,
//                        rect.y + rect.height / 2
//                )
//        );
    }

//    private void onMouseWheelMoved(MouseWheelEvent evt) {
//        if (evt.getWheelRotation() < 0) {
//            doZoom(1, evt.getPoint());
//        } else {
//            doZoom(-1, evt.getPoint());
//        }
//    }
    //implements Micropolis.Listener
    @Override
    public void censusChanged() {
        dateFundsPopPane.setPop(String.valueOf(engine.getCityPopulation()));
    }

    @Override
    public void demandChanged() {
    }

    @Override
    public void evaluationChanged() {
    }

    public void pushProperties(AppProperties appProperties) {
        appProperties.setProperty(PANE_SIZE_W_PROP_KEY, String.valueOf(getWidth()));
        appProperties.setProperty(PANE_SIZE_H_PROP_KEY, String.valueOf(getHeight()));
        appProperties.setProperty(SOUNDS_PREF, doSounds ? "true" : "false");
    }

    public void pullProperties(AppProperties appProperties) {
        String sizeW = appProperties.getProperty(PANE_SIZE_W_PROP_KEY, "1280");
        String sizeH = appProperties.getProperty(PANE_SIZE_H_PROP_KEY, "960");
        setPrefSize(Double.parseDouble(sizeW), Double.parseDouble(sizeH));
        doSounds = appProperties.getProperty(SOUNDS_PREF, "true").equals("true");

    }

    void closeWindow() {
        if (maybeSaveCity()) {
            Platform.exit();
        }
    }

}

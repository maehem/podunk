// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.CityLocation;
import com.maehem.podunk.engine.Micropolis;
import com.maehem.podunk.engine.MicropolisMessage;
import com.maehem.podunk.engine.Sound;
import java.util.*;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


/*

BorderPane borderPane = new BorderPane();
Scene scene = new Scene(borderPane, 600, 600);
Stage stage = new Stage();
stage.setScene(scene);
stage.setTitle("Cool Window");
stage.show();

 */
public class GraphsPane extends Stage
        implements Micropolis.Listener {

    Micropolis engine;

    ToggleButton tenYearsBtn;
    ToggleButton onetwentyYearsBtn;
    ToggleGroup timeSpanGroup = new ToggleGroup();

    //GraphArea graphArea;
    final LineChart<Number, Number> lineChart;

    static enum TimePeriod {
        TEN_YEARS,
        ONETWENTY_YEARS;
    }

    static enum GraphData {
        RESPOP,
        COMPOP,
        INDPOP,
        MONEY,
        CRIME,
        POLLUTION;
    }
    EnumMap<GraphData, ToggleButton> dataBtns = new EnumMap<GraphData, ToggleButton>(GraphData.class);

    public final ResourceBundle MSG;
//    static final int LEFT_MARGIN = 4;
//    static final int RIGHT_MARGIN = 4;
//    static final int TOP_MARGIN = 2;
//    static final int BOTTOM_MARGIN = 2;
//    static final int LEGEND_PADDING = 6;

    private final NumberAxis xAxis = new NumberAxis();
    private final NumberAxis yAxis = new NumberAxis();

    public GraphsPane(Micropolis engine) {

        assert engine != null;
        this.engine = engine;
        engine.addListener(this);

        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.
        xAxis.setLabel("Year");

        lineChart = new LineChart<>(xAxis, yAxis);

        Scene scene = new Scene(contentPane(), 600, 600);
        setScene(scene);
        setTitle("Graphs");

        //getDialogPane().setContent(contentPane());
        setTimePeriod(TimePeriod.TEN_YEARS);
        dataBtns.get(GraphData.MONEY).setSelected(true);
        dataBtns.get(GraphData.POLLUTION).setSelected(true);
    }

    private BorderPane contentPane() {
        BorderPane bp = new BorderPane();

        GridPane tools = new GridPane();
        tenYearsBtn = new ToggleButton(MSG.getString("ten_years"));
        tenYearsBtn.setToggleGroup(timeSpanGroup);
        onetwentyYearsBtn = new ToggleButton(MSG.getString("onetwenty_years"));
        onetwentyYearsBtn.setToggleGroup(timeSpanGroup);

        tools.add(makeDataBtn(GraphData.RESPOP), 0, 0);
        tools.add(makeDataBtn(GraphData.COMPOP), 1, 0);
        tools.add(makeDataBtn(GraphData.INDPOP), 0, 1);
        tools.add(makeDataBtn(GraphData.MONEY), 1, 1);
        tools.add(makeDataBtn(GraphData.CRIME), 0, 2);
        tools.add(makeDataBtn(GraphData.POLLUTION), 1, 2);
        tools.add(tenYearsBtn, 0, 3);
        tools.add(onetwentyYearsBtn, 1, 3);

        bp.setLeft(tools);

//        JPanel toolsPane = new JPanel(new GridBagLayout());
//        b1.add(toolsPane, BorderLayout.WEST);
//
//        GridBagConstraints c = new GridBagConstraints();
//        c.gridx = c.gridy = 0;
//        c.gridwidth = 2;
//        c.fill = GridBagConstraints.BOTH;
//        c.insets = new Insets(1, 1, 1, 1);
//        tenYearsBtn = new JToggleButton(MSG.getString("ten_years"));
//        tenYearsBtn.setMargin(new Insets(0, 0, 0, 0));
//        tenYearsBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                setTimePeriod(TimePeriod.TEN_YEARS);
//            }
//        });
//        toolsPane.add(tenYearsBtn, c);
//
//        c.gridy++;
//        onetwentyYearsBtn = new JToggleButton(MSG.getString("onetwenty_years"));
//        onetwentyYearsBtn.setMargin(new Insets(0, 0, 0, 0));
//        onetwentyYearsBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                setTimePeriod(TimePeriod.ONETWENTY_YEARS);
//            }
//        });
//        toolsPane.add(onetwentyYearsBtn, c);
//        c.gridx = 0;
//        c.gridy = 2;
//        c.gridwidth = 1;
//        c.anchor = GridBagConstraints.NORTH;
//        c.weightx = 0.5;
//        toolsPane.add(makeDataBtn(GraphData.RESPOP), c);
//
//        c.gridy = 3;
//        toolsPane.add(makeDataBtn(GraphData.COMPOP), c);
//
//        c.gridy = 4;
//        toolsPane.add(makeDataBtn(GraphData.INDPOP), c);
//
//        c.gridx = 1;
//        c.gridy = 2;
//        toolsPane.add(makeDataBtn(GraphData.MONEY), c);
//
//        c.gridy = 3;
//        toolsPane.add(makeDataBtn(GraphData.CRIME), c);
//
//        c.gridy = 4;
//        toolsPane.add(makeDataBtn(GraphData.POLLUTION), c);
        //graphArea = new GraphArea();
        bp.setCenter(lineChart);

        //b1.add(graphArea, BorderLayout.CENTER);
        return bp;
    }

    public void setEngine(Micropolis newEngine) {
        if (engine != null) {  //old engine
            engine.removeListener(this);
        }
        engine = newEngine;
        if (engine != null) {  //new engine
            engine.addListener(this);
            //graphArea.repaint();
            updateGraph();
        }
    }

//    private void onDismissClicked() {
//        setVisible(false);
//    }
    //implements Micropolis.Listener
    public void cityMessage(MicropolisMessage message, CityLocation loc) {
    }

    public void citySound(Sound sound, CityLocation loc) {
    }

    public void demandChanged() {
    }

    public void evaluationChanged() {
    }

    public void fundsChanged() {
    }

    public void optionsChanged() {
    }

    //implements Micropolis.Listener
    public void censusChanged() {
        //graphArea.repaint();
        updateGraph();
    }

    private ToggleButton makeDataBtn(GraphData graph) {
        String icon1name = MSG.getString("graph_button." + graph.name());
        String icon2name = MSG.getString("graph_button." + graph.name() + ".selected");

        Image iconImg = new Image(getClass().getResourceAsStream("/images/" + icon1name));
        //ImageIcon icon2 = new ImageIcon(getClass().getResource("/" + icon2name));

        ImageView icon1 = new ImageView(iconImg);
        ToggleButton btn = new ToggleButton(null, icon1);
        //btn.setIcon(icon1);
        //btn.setSelectedIcon(icon2);
        //btn.setBorder(null);
        //btn.setPadding(new Insets(0, 0, 0, 0));

//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                graphArea.repaint();
//            }
//        });
        btn.setOnAction((t) -> {
            updateGraph();
        });
        dataBtns.put(graph, btn);
        return btn;
    }

    /**
     * Return the highest value of any recorded data element. For setting upper
     * bounds on a graph.
     *
     * @return largest recorded value
     */
    int getHistoryMax() {
        int max = 0;
        for (GraphData g : GraphData.values()) {
            for (int pos = 0; pos < 240; pos++) {
                max = Math.max(max, getHistoryValue(g, pos));
            }
        }
        return max;
    }

    int getHistoryValue(GraphData graph, int pos) {
        assert pos >= 0 && pos < 240;
        switch (graph) {
            case RESPOP:
                return engine.history.res[pos];
            case COMPOP:
                return engine.history.com[pos];
            case INDPOP:
                return engine.history.ind[pos];
            case MONEY:
                return engine.history.money[pos];
            case CRIME:
                return engine.history.crime[pos];
            case POLLUTION:
                return engine.history.pollution[pos];
            default:
                throw new Error("unexpected");
        }
    }

    final void setTimePeriod(TimePeriod period) {
        tenYearsBtn.setSelected(period == TimePeriod.TEN_YEARS);
        onetwentyYearsBtn.setSelected(period == TimePeriod.ONETWENTY_YEARS);
        //graphArea.repaint();
        updateGraph();
    }

    @SuppressWarnings("unchecked")
    private void updateGraph() {
        lineChart.getData().clear();
        boolean isOneTwenty = onetwentyYearsBtn.isSelected();

        int H = isOneTwenty ? 239 : 119;
        //final HashMap<GraphData, Path2D.Double> paths = new HashMap<GraphData, Path2D.Double>();
        //double scale = Math.max(256.0, getHistoryMax());
        for (GraphData gd : GraphData.values()) {
            if (dataBtns.get(gd).isSelected()) {

                XYChart.Series series1;// = new XYChart.Series<Integer, Integer>();
                // Labels are Years (int)
                series1 = new XYChart.Series<Integer, Integer>();
                series1.setName(gd.name());

                for (int i = 0; i < 120; i++) {
                    series1.getData().add(new XYChart.Data<>(i, getHistoryValue(gd, H - i)));

//                    double xp = leftEdge + i * x_interval;
//                    double yp = bottomEdge - getHistoryValue(gd, H - i) * (bottomEdge - topEdge) / scale;
//                    if (i == 0) {
//                        path.moveTo(xp, yp);
//                    } else {
//                        path.lineTo(xp, yp);
//                    }
                }
                //paths.put(gd, path);
                lineChart.getData().add(series1);
            }
        }
    }

//    class GraphArea extends Pane {
//
//        GraphArea() {
//            setBorder(BorderFactory.createLoweredBevelBorder());
//        }
//        @Override
//        public void paintComponent(Graphics gr1) {
//            Graphics2D gr = (Graphics2D) gr1;
//            FontMetrics fm = gr.getFontMetrics();
//
//            gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            gr.setColor(Color.WHITE);
//            gr.fill(gr.getClipBounds());
//
//            // determine length of longest label
//            int maxLabelWidth = 0;
//            for (GraphData gd : GraphData.values()) {
//                String labelStr = MSG.getString("graph_label." + gd.name());
//                int adv = fm.stringWidth(labelStr);
//                if (adv > maxLabelWidth) {
//                    maxLabelWidth = adv;
//                }
//            }
//
//            int leftEdge = getInsets().left + LEFT_MARGIN;
//            int topEdge = getInsets().top + TOP_MARGIN + fm.getHeight() * 2;
//            int bottomEdge = getHeight() - getInsets().bottom - getInsets().top - BOTTOM_MARGIN;
//            int rightEdge = getWidth() - getInsets().right - getInsets().left - RIGHT_MARGIN - maxLabelWidth - LEGEND_PADDING;
//
//            // draw graph lower, upper borders
//            gr.setColor(Color.BLACK);
//            gr.drawLine(leftEdge, topEdge, rightEdge, topEdge);
//            gr.drawLine(leftEdge, bottomEdge, rightEdge, bottomEdge);
//
//            // draw vertical bars and label the dates
//            boolean isOneTwenty = onetwentyYearsBtn.isSelected();
//            int unitPeriod = isOneTwenty ? 12 * Micropolis.CENSUSRATE : Micropolis.CENSUSRATE;
//            int hashPeriod = isOneTwenty ? 10 * unitPeriod : 12 * unitPeriod;
//            int startTime = ((engine.history.cityTime / unitPeriod) - 119) * unitPeriod;
//
//            double x_interval = (rightEdge - leftEdge) / 120.0;
//            for (int i = 0; i < 120; i++) {
//                int t = startTime + i * unitPeriod;  // t might be negative
//                if (t % hashPeriod == 0) {
//                    // year
//                    int year = 1900 + (t / (12 * Micropolis.CENSUSRATE));
//                    int numHashes = t / hashPeriod;
//                    int x = (int) Math.round(leftEdge + i * x_interval);
//                    int y = getInsets().top + TOP_MARGIN
//                            + (numHashes % 2 == 0 ? fm.getHeight() : 0)
//                            + fm.getAscent();
//                    gr.drawString(Integer.toString(year), x, y);
//                    gr.drawLine(x, topEdge, x, bottomEdge);
//                }
//            }
//
//            int H = isOneTwenty ? 239 : 119;
//            final HashMap<GraphData, Path2D.Double> paths = new HashMap<GraphData, Path2D.Double>();
//            double scale = Math.max(256.0, getHistoryMax());
//            for (GraphData gd : GraphData.values()) {
//                if (dataBtns.get(gd).isSelected()) {
//
//                    Path2D.Double path = new Path2D.Double();
//                    for (int i = 0; i < 120; i++) {
//                        double xp = leftEdge + i * x_interval;
//                        double yp = bottomEdge - getHistoryValue(gd, H - i) * (bottomEdge - topEdge) / scale;
//                        if (i == 0) {
//                            path.moveTo(xp, yp);
//                        } else {
//                            path.lineTo(xp, yp);
//                        }
//                    }
//                    paths.put(gd, path);
//                }
//            }
//
//            GraphData[] myGraphs = paths.keySet().toArray(new GraphData[0]);
//            Arrays.sort(myGraphs, new Comparator<GraphData>() {
//                public int compare(GraphData a, GraphData b) {
//                    double y0 = paths.get(a).getCurrentPoint().getY();
//                    double y1 = paths.get(b).getCurrentPoint().getY();
//                    return -Double.compare(y0, y1);
//                }
//            });
//
//            int lbottom = bottomEdge;
//            for (GraphData gd : myGraphs) {
//                String labelStr = MSG.getString("graph_label." + gd.name());
//                String colStr = MSG.getString("graph_color." + gd.name());
//                Color col = parseColor(colStr);
//                Path2D.Double path = paths.get(gd);
//
//                gr.setColor(col);
//                gr.setStroke(new BasicStroke(2));
//                gr.draw(path);
//
//                int x = rightEdge + LEGEND_PADDING;
//                int y = (int) Math.round(path.getCurrentPoint().getY() + fm.getAscent() / 2);
//                y = Math.min(lbottom, y);
//                lbottom = y - fm.getAscent();
//
//                gr.setColor(col);
//                gr.drawString(labelStr, x - 1, y);
//                gr.drawString(labelStr, x, y - 1);
//
//                gr.setColor(Color.BLACK);
//                gr.drawString(labelStr, x, y);
//            }
//        }
//    }
}

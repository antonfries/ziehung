package ziehung;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class Main extends Frame {

    private final Label[] zahlLbl; // Array aller Zahlen-Label
    private final Button btnStart;
    private final Button btnStop;
    private int zuWartendeZeit = 0;
    private final ArrayList<ZahlenGenerator> zahlenGeneratorListe = new ArrayList<>();

    public class ZahlenGenerator extends Thread {
        private final int nummer;
        private final int warteZeit;
        private boolean sollBeendetWerden;

        public void setSollBeendetWerden() {
            sollBeendetWerden = true;
        }

        ZahlenGenerator(int nummer, int warteZeit) {
            this.nummer = nummer;
            this.warteZeit = warteZeit;
        }

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    if (sollBeendetWerden) {
                        sleep(warteZeit);
                        interrupt();
                    }
                    int zufall = (int) (Math.random() * 10);
                    zahlLbl[nummer].setText(String.valueOf(zufall));
                    zahlLbl[nummer].repaint();
                    sleep(100);
                }
            } catch (InterruptedException ignored) {
                zahlLbl[nummer].setForeground(Color.BLUE);
                System.out.println("Ergebnis: " + zahlLbl[nummer].getText());
            }
        }
    }

    public Main() {
        super("Spiel 77");
        setLocationRelativeTo(null);
        zahlLbl = new Label[7];
        add(new Label("Gewinnzahlen:"), BorderLayout.NORTH);
        // Gewinnzahl-Labels anlegen
        Panel zahlen = new Panel(new GridLayout(1, 7));
        for (int i = 0; i < zahlLbl.length; i++) {
            Label lbl = new Label("0");
            lbl.setForeground(Color.red);
            lbl.setFont(new Font("Arial", Font.BOLD, 16));
            zahlLbl[i] = lbl;
            zahlen.add(lbl);
        }
        add(zahlen);
        // Start-/Stop-Buttons
        Panel buttons = new Panel(new GridLayout(1, 2));
        btnStart = new Button("Ziehung starten");
        buttons.add(btnStart);
        btnStop = new Button("Ziehung beenden");
        buttons.add(btnStop);
        btnStop.setEnabled(false);
        add(buttons, BorderLayout.SOUTH);
        pack();
        btnStart.addActionListener(e -> {
            zuWartendeZeit = 0;
            zahlenGeneratorListe.clear();
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            for (int i = 0; i < 7; i++) {
                int mindestZeit = (int) (Math.random() * 80) + 20;
                zuWartendeZeit += mindestZeit;
                zahlLbl[i].setForeground(Color.red);
                ZahlenGenerator zahlenGenerator = new ZahlenGenerator(i, zuWartendeZeit);
                zahlenGeneratorListe.add(zahlenGenerator);
                zahlenGenerator.start();
            }
        });
        btnStop.addActionListener(e -> {
            btnStop.setEnabled(false);
            for (ZahlenGenerator zahlenGenerator : zahlenGeneratorListe) {
                zahlenGenerator.setSollBeendetWerden();
            }
            try {
                Thread.sleep(zuWartendeZeit);
                btnStart.setEnabled(true);
            } catch (InterruptedException ignored) {
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                for (ZahlenGenerator zahlenGenerator : zahlenGeneratorListe) {
                    zahlenGenerator.interrupt();
                }
                dispose();
            }
        });
        setVisible(true);
    }

    public static void main(String[] args) {
        new Main();
    }
}

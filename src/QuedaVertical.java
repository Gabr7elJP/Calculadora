import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class QuedaVertical implements ActionListener {

    // Variáveis globais
    JFrame frame;

    JButton calcularBtn, resetBtn;

    JTextField v0Input, h0Input, HInput, hjInput;
    JTextField tempoSoloOutput, velocidadeFinalOutput, tempoJanelaOutput;
    JTextField tempoTopoOutput, alturaMaxOutput;

    JComboBox<String> direcaoBox;

    double v0, h0, H, hj;
    double a = -9.8;

    // Construtor
    QuedaVertical() {

        frame = new JFrame("Calculadora de Queda Vertical");
        frame.setSize(520, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // Inputs
        JPanel inputPanel = new JPanel(null);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Entradas",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        inputPanel.setBounds(20, 20, 460, 210);

        //Velocidade inicial
        JLabel v0Label = createLabel("Velocidade inicial (m/s):", 20, 30, 200, 25);
        v0Input = createTextField("", 250, 30, 100, 25, true);

        //Direção
        JLabel direcaoLabel = createLabel("Direção:", 20, 70, 200, 25);
        direcaoBox = new JComboBox<>(new String[]{"↑ Para cima", "↓ Para baixo"});
        direcaoBox.setBounds(250, 70, 150, 25);

        //Altura inicial
        JLabel h0Label = createLabel("Altura inicial (m):", 20, 110, 200, 25);
        h0Input = createTextField("", 250, 110, 100, 25, true);

        //Altura do prédio
        JLabel HLabel = createLabel("Altura do prédio (m):", 20, 150, 200, 25);
        HInput = createTextField("", 250, 150, 100, 25, true);

        //Altura da janela
        JLabel hjLabel = createLabel("Altura da janela (m):", 20, 180, 200, 25);
        hjInput = createTextField("", 250, 180, 100, 25, true);

        inputPanel.add(v0Label);
        inputPanel.add(v0Input);
        inputPanel.add(direcaoLabel);
        inputPanel.add(direcaoBox);
        inputPanel.add(h0Label);
        inputPanel.add(h0Input);
        inputPanel.add(HLabel);
        inputPanel.add(HInput);
        inputPanel.add(hjLabel);
        inputPanel.add(hjInput);

        // Outputs
        JPanel outputPanel = new JPanel(null);
        outputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                "Resultados",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        outputPanel.setBounds(20, 240, 460, 180);

        tempoSoloOutput = createOutput("Tempo de queda: ", 20, 30);
        velocidadeFinalOutput = createOutput("Velocidade final: ", 20, 60);
        tempoJanelaOutput = createOutput("Tempo até a janela: ", 20, 90);

        tempoTopoOutput = createOutput("Tempo até o topo: ", 20, 120);
        alturaMaxOutput = createOutput("Altura máxima: ", 20, 150);

        outputPanel.add(tempoSoloOutput);
        outputPanel.add(velocidadeFinalOutput);
        outputPanel.add(tempoJanelaOutput);
        outputPanel.add(tempoTopoOutput);
        outputPanel.add(alturaMaxOutput);

        // Botões
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        buttonPanel.setBounds(20, 430, 460, 60);

        // Botão calcular
        calcularBtn = new JButton("Calcular");
        calcularBtn.setBounds(80, 15, 120, 30);
        calcularBtn.addActionListener(this);

        // Botão reset
        resetBtn = new JButton("Reset");
        resetBtn.setBounds(260, 15, 120, 30);
        resetBtn.addActionListener(this);

        // Hover Botões
        calcularBtn.setBackground(Color.LIGHT_GRAY);
        calcularBtn.setOpaque(true);
        calcularBtn.setBorderPainted(false);

        calcularBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                calcularBtn.setBackground(new Color(100, 180, 255));
            }
            public void mouseExited(MouseEvent e) {
                calcularBtn.setBackground(Color.LIGHT_GRAY);
            }
        });

        resetBtn.setBackground(Color.LIGHT_GRAY);
        resetBtn.setOpaque(true);
        resetBtn.setBorderPainted(false);

        resetBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                resetBtn.setBackground(new Color(255, 120, 120));
            }
            public void mouseExited(MouseEvent e) {
                resetBtn.setBackground(Color.LIGHT_GRAY);
            }
        });

        buttonPanel.add(calcularBtn);
        buttonPanel.add(resetBtn);

        frame.add(inputPanel);
        frame.add(outputPanel);
        frame.add(buttonPanel);

        frame.setVisible(true);
    }

    // Main
    public static void main(String[] args) {
        new QuedaVertical();
    }

    // Funções dos botões
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == calcularBtn) {
            try {
                v0 = Double.parseDouble(v0Input.getText());
                h0 = Double.parseDouble(h0Input.getText());
                H = Double.parseDouble(HInput.getText());
                hj = Double.parseDouble(hjInput.getText());

                String direcao = direcaoBox.getSelectedItem().toString().trim().toLowerCase();

                // Restrições das variávies
                if (v0 < 0) {
                    errorMsg("Velocidade inicial deve ser positiva.");
                    return;
                }

                if (h0 < 0) {
                    errorMsg("Altura inicial inválida.");
                    return;
                }

                if (H < 0) {
                    errorMsg("Altura do prédio inválida.");
                    return;
                }

                if (h0 > H) {
                    errorMsg("Altura inicial não pode ser maior que o prédio.");
                    return;
                }

                if (hj < 0 || hj > H) {
                    errorMsg("Altura da janela deve estar entre 0 e H.");
                    return;
                }

                if (direcao.contains("baixo")) {
                    v0 = -v0;
                }

                // Tempo até o solo
                double tSolo = calcularTempoAteAltura(0);

                if (tSolo == -1) {
                    errorMsg("Erro no cálculo.");
                    return;
                }

                double vFinal = v0 + a * tSolo;

                // Tempo até a janela
                double tJanela = calcularTempoAteAltura(hj);

                tempoSoloOutput.setText("Tempo de queda: " + String.format("%.2f", tSolo) + " s");
                velocidadeFinalOutput.setText("Velocidade final: " + String.format("%.2f", vFinal) + " m/s");

                if (tJanela != -1) {
                    tempoJanelaOutput.setText("Tempo até a janela: " + String.format("%.2f", tJanela) + " s");
                } else {
                    tempoJanelaOutput.setText("Tempo até a janela: não atinge");
                }

                // Se for para cima
                if (direcao.contains("cima")) {
                    double tSubida = -v0 / a;
                    double hMax = h0 + v0 * tSubida + 0.5 * a * tSubida * tSubida;

                    tempoTopoOutput.setText("Tempo até o topo: " + String.format("%.2f", tSubida) + " s");
                    alturaMaxOutput.setText("Altura máxima: " + String.format("%.2f", hMax) + " m");
                } else {
                    tempoTopoOutput.setText("Tempo até o topo: N/A");
                    alturaMaxOutput.setText("Altura máxima: N/A");
                }

            } catch (NumberFormatException ex) {
                errorMsg("Digite apenas números válidos.");
            }
        }

        // Reset
        if (e.getSource() == resetBtn) {
            v0Input.setText("");
            h0Input.setText("");
            HInput.setText("");
            hjInput.setText("");

            tempoSoloOutput.setText("Tempo de queda: ");
            velocidadeFinalOutput.setText("Velocidade final: ");
            tempoJanelaOutput.setText("Tempo até a janela: ");
            tempoTopoOutput.setText("Tempo até o topo: ");
            alturaMaxOutput.setText("Altura máxima: ");
        }
    }

    // Bhaskara
    private double calcularTempoAteAltura(double yAlvo) {
        double A = 0.5 * a;
        double B = v0;
        double C = h0 - yAlvo;

        double delta = B * B - 4 * A * C;

        if (delta < 0) return -1;

        double t1 = (-B + Math.sqrt(delta)) / (2 * A);
        double t2 = (-B - Math.sqrt(delta)) / (2 * A);

        if (t1 >= 0 && t2 >= 0) {
            if (t1 == 0 || t2 == 0) return 0;
            return Math.max(t1, t2);
        }
        if (t1 >= 0) return t1;
        if (t2 >= 0) return t2;

        return -1;
    }

    private JTextField createTextField(String text, int x, int y, int w, int h, boolean edit) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, w, h);
        field.setEditable(edit);
        return field;
    }

    private JTextField createOutput(String text, int x, int y) {
        JTextField field = new JTextField(text);
        field.setBounds(x, y, 400, 25);
        field.setEditable(false);
        field.setBackground(new Color(230, 230, 230));
        field.setFont(new Font("Arial", Font.BOLD, 12));
        return field;
    }

    private JLabel createLabel(String text, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, w, h);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        return label;
    }

    private void errorMsg(String msg) {
        JOptionPane.showMessageDialog(frame, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }
}
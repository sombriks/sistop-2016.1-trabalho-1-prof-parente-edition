package br.com.edu.ifce.sistops;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AgenciaBancaria extends JPanel {

  private static final long     serialVersionUID = -7065575459579164850L;
  private List<ProcessoCaixa>   caixas           = new ArrayList<>();
  private List<ProcessoCliente> clientes         = new ArrayList<>();
  // cosmetic
  private String                nomes[]          = { "José", "Antônio", "Dorneles", "Atílio", "Fernando", "Bruce" };
  private String                sobrenomes[]     = { "Silva", "Resende", "Parente", "Antunes", "Tobias", "Wayne" };
  private String                senhas[]         = { "A", "B", "C", "D", "1", "2", "3", "4", "5", "6", "7", "X", "K" };

  private int                   x, y;

  public AgenciaBancaria() {

    setLayout(null);

    int numCaixas = 0;
    String s = JOptionPane.showInputDialog("Informe o número de caixas");
    try {
      numCaixas = Integer.parseInt(s);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Quantidade inválida, usando 2 caixas somente...");
      numCaixas = 2;
    }
    x = 50;
    y = 50;
    int l1 = nomes.length;
    int l2 = sobrenomes.length;
    while (numCaixas-- > 0) {
      String nome = nomes[rnd(l1)] + " " + sobrenomes[rnd(l2)];
      ProcessoCaixa pc = new ProcessoCaixa(this, nome, x, y);
      pc.start();
      caixas.add(pc);
      x += 60;
    }
    x = 50;
    y = 350;

    JButton addCliente = new JButton("Adicionar cliente");
    add(addCliente);
    addCliente.setBounds(440, 440, 170, 30);
    addCliente.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        int l = senhas.length;
        String senha = senhas[rnd(l)] + senhas[rnd(l)] + senhas[rnd(l)] + senhas[rnd(l)];
        long tempo = (long) (3000 + Math.random() * 15000);
        ProcessoCliente pc = new ProcessoCliente(AgenciaBancaria.this, senha, tempo, x, y);
        pc.start();
        clientes.add(pc);
        x += 60;
        if (x > 1200)
          x = 50;
      }
    });

    setSize(640, 480);
    setVisible(true);
  }

  private int rnd(int max) {
    return (int) Math.floor(Math.random() * max);
  }

  @Override
  protected void paintComponent(Graphics g) {
    g.clearRect(0, 0, 640, 480);
    Graphics2D g2 = (Graphics2D) g;
    for (ProcessoCaixa pc : caixas) {
      pc.paint(g2);
    }
    for (ProcessoCliente pc : clientes) {
      pc.paint(g2);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(640, 480);
  }

  public static void main(String[] args) {
    JFrame jf = new JFrame();
    jf.add(new AgenciaBancaria());
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.pack();
    jf.setVisible(true);

  }

}

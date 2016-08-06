package br.com.edu.ifce.sistops;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AgenciaBancaria extends JPanel {

  private static final long     serialVersionUID = -7065575459579164850L;
  private List<ProcessoCaixa>   caixas           = new ArrayList<>();
  private List<ProcessoCaixa>   caixasDesenho    = new ArrayList<>();
  private List<ProcessoCliente> clientes         = new ArrayList<>();
  private List<ProcessoCliente> clientesRemover  = new ArrayList<>();
  private JTextArea             jta              = new JTextArea();
  private Semaphore             mutex            = new Semaphore(1);
  private Semaphore             mtCaixas;
  // cosmetic
  private String                nomes[]          = { "José", "Antônio", "Dorneles", "Atílio", "Fernando", "Bruce" };
  private String                sobrenomes[]     = { "Silva", "Resende", "Parente", "Antunes", "Tobias", "Wayne" };
  private String                senhas[]         = { "A", "B", "C", "D", "1", "2", "3", "4", "5", "6", "7", "X", "K" };
  private int                   x, y;

  public AgenciaBancaria() throws Exception {

    setLayout(null);

    int numCaixas = 0;
    String s = JOptionPane.showInputDialog("Informe o número de caixas");
    try {
      numCaixas = Integer.parseInt(s);
    } catch (Exception e) {
      JOptionPane.showMessageDialog(this, "Quantidade inválida, usando 2 caixas somente...");
      numCaixas = 2;
    }
    mtCaixas = new Semaphore(numCaixas);
    x = 50;
    y = 50;
    int l1 = nomes.length;
    int l2 = sobrenomes.length;
    while (numCaixas-- > 0) {
      String nome = nomes[rnd(l1)] + " " + sobrenomes[rnd(l2)];
      ProcessoCaixa pc = new ProcessoCaixa(this, nome, x, y);
      pc.start();
      caixas.add(pc);
      x += 160;
    }
    caixasDesenho.addAll(caixas);
    x = 50;
    y = 250;

    JButton addCliente = new JButton("Adicionar cliente");
    add(addCliente);
    addCliente.setBounds(440, 440, 170, 30);
    addCliente.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          int l = senhas.length;
          String senha = senhas[rnd(l)] + senhas[rnd(l)] + senhas[rnd(l)] + senhas[rnd(l)];
          long tempo = (long) (3000 + Math.random() * 15000);
          ProcessoCliente pc = new ProcessoCliente(AgenciaBancaria.this, senha, tempo, x, y);
          clientes.add(pc);
          x += 160;
          if (x > 600) {
            x = 50;
            y += 100;
          }
          if (y > 500) {
            y = 250;
          }
          pc.start();
        } catch (Exception e1) {
          throw new RuntimeException(e1);
        }
      }
    });

    // logs
    JFrame jf = new JFrame();
    jf.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    JScrollPane scroll = new JScrollPane(jta);
    jf.add(scroll);
    jf.setSize(800, 480);

    JButton verLog = new JButton("Ver o Log de eventos");
    add(verLog);
    verLog.setBounds(140, 440, 230, 30);
    verLog.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        jf.setVisible(true);
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
    for (ProcessoCaixa pc : caixasDesenho) {
      pc.paint(g2);
    }
    clientes.removeAll(clientesRemover);
    clientesRemover.removeAll(clientesRemover);
    for (ProcessoCliente pc : clientes) {
      pc.paint(g2);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(640, 480);
  }

  public ProcessoCaixa getCaixaLivre(ProcessoCliente processoCliente) throws Exception {
    mutex.acquire();
    mtCaixas.acquire();
    ProcessoCaixa cx = caixas.remove(0);
    mutex.release();
    return cx;
  }

  public void encerraAtendimento(ProcessoCaixa cx, ProcessoCliente cli) throws Exception {
    clientesRemover.add(cli);
    caixas.add(cx);
    mtCaixas.release();
    log("Caixas livres na agência: " + caixas.size());
    log("Clientes ainda na agência: " + (clientes.size() - 1));
  }

  public static void main(String[] args) throws Exception {
    JFrame jf = new JFrame();
    jf.add(new AgenciaBancaria());
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.pack();
    jf.setResizable(false);
    jf.setVisible(true);
  }

  public void log(String line) throws Exception {
    line = "[" + new Timestamp(System.currentTimeMillis()).toString() + "]" + line;
    jta.append(line + "\r\n");
    if (jta.getRows() > 1000) {
      int end = jta.getLineEndOffset(0);
      jta.replaceRange("", 0, end);
    }

  }

}

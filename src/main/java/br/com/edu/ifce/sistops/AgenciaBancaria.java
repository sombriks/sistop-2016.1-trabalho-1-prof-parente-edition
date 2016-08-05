package br.com.edu.ifce.sistops;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class AgenciaBancaria extends JPanel {

  private static final long     serialVersionUID = -7065575459579164850L;
  private List<ProcessoCaixa>   caixas           = new ArrayList<>();
  private List<ProcessoCaixa>   caixasDesenho    = new ArrayList<>();
  private List<ProcessoCliente> clientes         = new ArrayList<>();
  private List<ProcessoCliente> clientesRemover  = new ArrayList<>();
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
      x += 120;
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
          x += 120;
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
//    System.out.println("paint");
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

  public void encerraAtendimento(ProcessoCaixa cx, ProcessoCliente cli) {
    clientesRemover.add(cli);
    caixas.add(cx);
    mtCaixas.release();
    System.out.println("Caixas livres na agência: " + caixas.size());
    System.out.println("Clientes na fila: "+clientes.size());
  }

  public static void main(String[] args) throws Exception {
    JFrame jf = new JFrame();
    jf.add(new AgenciaBancaria());
    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    jf.pack();
    jf.setResizable(false);
    jf.setVisible(true);
  }

}

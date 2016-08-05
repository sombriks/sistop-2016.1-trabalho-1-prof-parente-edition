package br.com.edu.ifce.sistops;

import java.awt.Graphics2D;
import java.util.concurrent.Semaphore;

import br.com.edu.ifce.sistops.base.BonecoBase;

/**
 * 
 * @author sombriks
 *
 */
public class ProcessoCaixa extends BonecoBase {

  private String          status      = "Criado", nme;
  private Semaphore       atendimento = new Semaphore(1);
  private ProcessoCliente cliente;
  private int             numAtendimentos;
  private long            t1, t2;

  public ProcessoCaixa(AgenciaBancaria agencia, String name, int x, int y) throws Exception {
    super(agencia, x, y);
    nme = name;
    setName("Caixa <" + name + ">");
    atendimento.acquire();
  }

  @Override
  public void paint(Graphics2D g2) {
    super.paint(g2);
    g2.drawString(nme, x, y - 15);
    g2.drawString(status + " " + "[" + (numAtendimentos) + "]", x, y);

  }

  public void atende(ProcessoCliente cliente) {
    System.out.println(getName() + " está atendendo o " + cliente.getName());
    status = "Atendendo";
    numAtendimentos++;
    this.cliente = cliente;
    atendimento.release();// começa a atender
    t1 = System.currentTimeMillis();
  }

  @Override
  protected void step() throws Exception {
    if (cliente == null) {
      status = "Aguardando";
      System.out.println(getName() + " aguarda o próximo cliente");
      atendimento.acquire();// dorme até ter cliente
    }
  }

  public void encerra(ProcessoCliente processoCliente) {
    agencia.encerraAtendimento(this, cliente);
    t2 = System.currentTimeMillis();
    String s = getName() + " encerrou o atendimento do cliente ";
    s += cliente.getName() + " após [" + (t2 - t1) / 1000 + "s]";
    System.out.println(s);
    agencia.repaint();
    status = "Aguardando";
    cliente = null;

  }

}
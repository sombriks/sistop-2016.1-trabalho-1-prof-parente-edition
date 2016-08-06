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
  private Semaphore       mutex       = new Semaphore(1);
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
    g2.drawString(nme + " #" + getId(), x, y - 15);
    g2.drawString(status + " " + "[" + (numAtendimentos) + "]", x, y);

  }

  public void atende(ProcessoCliente cliente) throws Exception {
    mutex.acquire();
    agencia.log(getName() + " está atendendo o " + cliente.getName());
    status = "Atendendo " + cliente.getSenha();
    numAtendimentos++;
    this.cliente = cliente;
    atendimento.release();// começa a atender
    t1 = System.currentTimeMillis();
  }

  @Override
  protected void step() throws Exception {
    if (cliente == null) {
      status = "Aguardando";
      agencia.log(getName() + " aguarda o próximo cliente");
      atendimento.acquire();// dorme até ter cliente
    }
  }

  public void encerra(ProcessoCliente processoCliente) throws Exception {
    t2 = System.currentTimeMillis();
    String s = getName() + " encerrou o atendimento do cliente ";
    s += processoCliente.getName() + " após [" + (t2 - t1) / 1000 + "s]";
    agencia.log(s);
    status = "Aguardando";
    agencia.encerraAtendimento(this, processoCliente);
    cliente = null;
    agencia.repaint();
    mutex.release();

  }

}
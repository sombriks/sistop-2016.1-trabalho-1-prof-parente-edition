package br.com.edu.ifce.sistops;

import java.awt.Graphics2D;
import java.util.concurrent.Semaphore;

import br.com.edu.ifce.sistops.base.BonecoBase;

/*
 * 
 */
public class ProcessoCliente extends BonecoBase {

  private String        status, nme;
  private ProcessoCaixa caixa;
  private long          tempo, tick2, nextTick2;
  private Semaphore     mutex = new Semaphore(1);

  public ProcessoCliente(AgenciaBancaria agencia, String name, long tempo, int x, int y) throws Exception {
    super(agencia, x, y);
    setName("Cliente <" + name + ">");
    nme = name;
    status = "Na fila";
    this.tempo = tempo;
  }

  @Override
  public void paint(Graphics2D g2) {
    super.paint(g2);
    g2.drawString(nme, x, y - 15);
    g2.drawString(status + "\n" + "[" + (tempo / 1000) + "s]", x, y);
  }

  protected void step() throws Exception {
    if (rodando && caixa == null) {
      System.out.println(getName() + " solicitou um caixa livre");
      caixa = agencia.getCaixaLivre(this);
      caixa.atende(this);
    } else {
      status = "Atendimento";
      if (nextTick2 == 0)
        nextTick2 = tempo + System.currentTimeMillis();
      tick2 = System.currentTimeMillis();
      // vendo se acabaram os boletos
      if (tick2 > nextTick2) {
        mutex.acquire();
        status = "Fim";
        if (caixa != null)
          caixa.encerra(this);
        caixa = null;
        rodando = false;
        mutex.release();
      }
    }
  }

  public ProcessoCaixa getCaixa() {
    return caixa;
  }
}

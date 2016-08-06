package br.com.edu.ifce.sistops;

import java.awt.Graphics2D;

import br.com.edu.ifce.sistops.base.BonecoBase;

/*
 * 
 */
public class ProcessoCliente extends BonecoBase {

  private String        status, senha;
  private ProcessoCaixa caixa;
  private long          tempo, tick2, nextTick2;

  public ProcessoCliente(AgenciaBancaria agencia, String name, long tempo, int x, int y) throws Exception {
    super(agencia, x, y);
    setName("Cliente <" + name + ">");
    senha = name;
    status = "Na fila";
    this.tempo = tempo;
  }

  public String getSenha() {
    return senha;
  }

  @Override
  public void paint(Graphics2D g2) {
    super.paint(g2);
    g2.drawString(senha, x, y - 15);
    g2.drawString(status + "\n" + "[" + (tempo / 1000) + "s]", x, y);
  }

  protected void step() throws Exception {
    if (rodando && caixa == null) {
      agencia.log(getName() + " solicitou um caixa livre");
      caixa = agencia.getCaixaLivre(this);
      caixa.atende(this);
    } else {
      status = "Atend. em #" + caixa.getId();
      if (nextTick2 == 0)
        nextTick2 = tempo + System.currentTimeMillis();
      tick2 = System.currentTimeMillis();
      // vendo se acabaram os boletos
      if (tick2 > nextTick2) {
        status = "Fim";
        if (caixa != null)
          caixa.encerra(this);
        caixa = null;
        rodando = false;
      }
    }
  }

  public ProcessoCaixa getCaixa() {
    return caixa;
  }
}

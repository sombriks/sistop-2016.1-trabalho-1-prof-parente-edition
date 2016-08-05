package br.com.edu.ifce.sistops;

import br.com.edu.ifce.sistops.base.BonecoBase;

/*
 * 
 */
public class ProcessoCliente extends BonecoBase {

  public ProcessoCliente(AgenciaBancaria agencia, String name, long tempo, int x, int y) {
    super(agencia, x, y);
    setName("Cliente <" + name + ">");
  }

}

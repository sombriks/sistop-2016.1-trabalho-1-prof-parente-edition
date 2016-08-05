package br.com.edu.ifce.sistops;

import br.com.edu.ifce.sistops.base.BonecoBase;

/**
 * 
 * @author sombriks
 *
 */
public class ProcessoCaixa extends BonecoBase {

  public ProcessoCaixa(AgenciaBancaria agencia, String name, int x, int y) {
    super(agencia, x, y);
    setName("Caixa <" + name + ">");
  }

}
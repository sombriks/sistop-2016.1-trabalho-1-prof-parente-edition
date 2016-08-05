package br.com.edu.ifce.sistops.base;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import br.com.edu.ifce.sistops.AgenciaBancaria;
import br.com.edu.ifce.sistops.assets.Loader;

public class BonecoBase extends Thread {

  private BufferedImage   sprite;
  private BufferedImage   ch0;
  private BufferedImage   ch1;
  private BufferedImage   ch2;
  private int             x, y, key = 0, animSpeed = 200;
  private boolean         rodando = true;
  private BufferedImage   frames[];
  private AgenciaBancaria agencia;
  private long            tick, nextTick;

  public BonecoBase(AgenciaBancaria agencia, int x, int y) {
    try {
      this.agencia = agencia;
      this.x = x;
      this.y = y;
      ch0 = Loader.INSTANCE.assetImg("ch0.png");
      ch1 = Loader.INSTANCE.assetImg("ch1.png");
      ch2 = Loader.INSTANCE.assetImg("ch2.png");
      frames = new BufferedImage[] { ch0, ch1, ch0, ch2};
      sprite = ch1;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void paint(Graphics2D g2) {
    g2.drawImage(sprite, x, y, null);
  }

  @Override
  public void run() {
    nextTick = System.currentTimeMillis() + animSpeed;
    while (rodando) {
      tick = System.currentTimeMillis();
      if (tick > nextTick) {
        nextTick();
        nextTick = tick + animSpeed;
      }
    }
  }

  private void nextTick() {
    sprite = frames[key++];
    agencia.repaint();
    if (key == frames.length)
      key = 0;
  }

}

/*
 * 
 * 
 * 'general':[ 'up': [ {'x': 0, 'y': 7, 'w': 32, 'h': 48 }, {'x': 36, 'y': 7,
 * 'w': 32, 'h': 48 }, {'x': 73, 'y': 7, 'w': 32, 'h': 48 } ],
 * 
 * 'right': [ {'x': 0, 'y': 55, 'w': 32, 'h': 48 }, {'x': 36, 'y': 55, 'w': 32,
 * 'h': 48 }, {'x': 73, 'y': 55, 'w': 32, 'h': 48 } ],
 * 
 * 'down': [ {'x': 0, 'y': 103, 'w': 32, 'h': 48 }, {'x': 36, 'y': 103, 'w': 32,
 * 'h': 48 }, {'x': 73, 'y': 103, 'w': 32, 'h': 48 } ],
 * 
 * 'left': [ {'x': 0, 'y': 151, 'w': 32, 'h': 48 }, {'x': 36, 'y': 151, 'w': 32,
 * 'h': 48 }, {'x': 73, 'y': 151, 'w': 32, 'h': 48 } ], ]
 */

package br.com.edu.ifce.sistops.base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import br.com.edu.ifce.sistops.AgenciaBancaria;
import br.com.edu.ifce.sistops.assets.Loader;

/*
 * classe pra desenhar o boneco
 */
public class BonecoBase extends Thread {

  private BufferedImage           sprite;
  private BufferedImage           ch0;
  private BufferedImage           ch1;
  private BufferedImage           ch2;
  protected int                   x, y;
  private int                     key     = 0, animSpeed = 200;
  protected boolean               rodando = true;
  private BufferedImage           frames[];
  protected final AgenciaBancaria agencia;
  private long                    tick, nextTick;

  public BonecoBase(AgenciaBancaria agencia, int x, int y) throws Exception {
    this.agencia = agencia;
    this.x = x;
    this.y = y;
    ch0 = Loader.INSTANCE.assetImg("ch0.png");
    ch1 = Loader.INSTANCE.assetImg("ch1.png");
    ch2 = Loader.INSTANCE.assetImg("ch2.png");
    frames = new BufferedImage[] { ch0, ch1, ch0, ch2 };
    sprite = ch1;
  }

  public void paint(Graphics2D g2) {
    g2.drawImage(sprite, x, y, null);
  }

  public boolean isRodando() {
    return rodando;
  }

  @Override
  public void run() {
    while (rodando) {
      try {
        // work on other things
        step();
        // animation smoothness -- thick
        nextTick();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void nextTick() {
    tick = System.currentTimeMillis();
    if (tick > nextTick) {
      sprite = frames[key++];
      agencia.repaint();
      if (key == frames.length)
        key = 0;
      nextTick = tick + animSpeed;
    }
  }

  protected void step() throws Exception {
    // does nothing
  };
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

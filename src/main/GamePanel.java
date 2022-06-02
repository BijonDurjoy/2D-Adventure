package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable
{
    //SCREEN SETTINGS
    final int originalTileSize = 16; // 16*16 tile
    final int scale = 3;

    final int tileSize = originalTileSize * scale; // 48*48 tile;
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; //768 pix
    final int screenHeight = tileSize * maxScreenRow; // 576 pix

    //FPS
    int FPS = 60;
    KeyHandler keyH = new KeyHandler();
    Thread gameThread;

    //set player's default position
    int playerX = 100;
    int playerY = 100;
    int playerSpreed= 4;

    public GamePanel()
    {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }

    public void startGameThread()
    {
        gameThread = new Thread(this);
        gameThread.start();
    }
    @Override
    public void run()
    {
        double drawInterval = 1000000000/FPS; //0.01666666 seconds
        double nextDrawTime = System.nanoTime() + drawInterval;
        while(gameThread != null)
        {

            update();
            repaint();

            try
            {
                double remainingTime = nextDrawTime -System.nanoTime();
                remainingTime = remainingTime /1000000;
                if(remainingTime <0)
                {
                    remainingTime = 0;
                }
                Thread.sleep((long) remainingTime);
                nextDrawTime += drawInterval;
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
    public void update()
    {
        if(keyH.upPressed == true)
        {
            playerY -= playerSpreed;
        }
        else if(keyH.downPressed ==true)
        {
            playerY += playerSpreed;
        }
        else if(keyH.leftPressed ==true)
        {
            playerX -= playerSpreed;
        }
        else if(keyH.rightPressed == true)
        {
            playerX += playerSpreed;
        }
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.white);
        g2.fillRect(playerX,playerY, tileSize, tileSize);
        g2.dispose();
    }
}

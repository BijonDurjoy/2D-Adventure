package main;

import entity.Entity;
import entity.Player;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable
{
    //SCREEN SETTINGS
    final int originalTileSize = 16; // 16*16 tile
    final int scale = 3;

    public final int tileSize = originalTileSize * scale; // 48*48 tile;
    public final int maxScreenCol = 16;
    public final int maxScreenRow = 12;
    public  final int screenWidth = tileSize * maxScreenCol; //768 pix
    public  final int screenHeight = tileSize * maxScreenRow; // 576 pix

    //WORLD SETTINGS
    public final int maxWorldCol = 50;
    public final int maxWorldRow = 50;


    //FPS
    int FPS = 60;
    TileManager tileM = new TileManager(this);
    KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    Thread gameThread;
    //ENTITY & OBJECT
    public Player player =new Player(this,keyH);
    public SuperObject obj[] = new SuperObject[10];
    public Entity npc[] = new Entity[10];

    //GAME STATE
    public int gameState;
    public final int playState = 1;
    public final int pauseState = 2;

    public GamePanel()
    {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.addKeyListener(keyH);
        this.setFocusable(true);
    }
    public void setupGame()
    {
        aSetter.setObject();
        aSetter.setNPC();
        playMusic(0);
        //stopMusic();
        gameState = playState;
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
        if (gameState == playState) {
            player.update();
            for(int i=0;i< npc.length;i++){
                if(npc[i]!=null){
                    npc[i].update();
                }
            }
        }
        if (gameState == pauseState) {
            //NOTHING
        }
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        //DEBUG
        long drawStart = 0;
        drawStart = System.nanoTime();
        if(keyH.checkDrawTime==true){
            drawStart = System.nanoTime();
        }
        //TILE
        tileM.draw(g2);

        //OBJECT
        for(int i=0; i<obj.length; i++)
        {
            if(obj[i] != null)
            {
                obj[i].draw(g2, this);
            }
        }
        //NPC
        for(int i=0;i<npc.length;i++){
            if(npc[i]!=null){
                npc[i].draw(g2);
            }
        }

        //PLAYER
        player.draw(g2);
        //UI
        ui.draw(g2);
        if(keyH.checkDrawTime==true){
            long drawEnd = System.nanoTime();
            long Passed = drawEnd - drawStart;
            g2.setColor(Color.white);
            g2.drawString("Draw Time: "+Passed,10,100);
            System.out.println(Passed);
        }

        g2.dispose();
    }
    public void playMusic(int i)
    {
        music.setFile(i);
        music.play();
        music.loop();
    }
    public void stopMusic()
    {
        music.stop();
    }
    public void playSE (int i)
    {
        se.setFile(i);
        se.play();
    }
}

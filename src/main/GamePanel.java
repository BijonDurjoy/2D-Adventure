package main;

import entity.Entity;
import entity.Player;
import tile.TileManager;
import tile_interactive.InteractiveTile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

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
    public KeyHandler keyH = new KeyHandler(this);
    Sound music = new Sound();
    Sound se = new Sound();
    public CollisionChecker cChecker = new CollisionChecker(this);
    public AssetSetter aSetter = new AssetSetter(this);
    public UI ui = new UI(this);
    public EventHandler eHandler = new EventHandler(this);
    Thread gameThread;

    //ENTITY & OBJECT
    public Player player =new Player(this,keyH);
    public Entity obj[] = new Entity[20];
    public Entity npc[] = new Entity[10];
    public Entity monster[] = new Entity[20];
    public InteractiveTile iTile[] = new InteractiveTile[50];
    public ArrayList<Entity> projectileList = new ArrayList<>();
    public ArrayList<Entity> particleList = new ArrayList<>();
    ArrayList<Entity> entityList = new ArrayList<>();

    //GAME STATE
    public int gameState;
    public final int titleState = 0;
    public final int playState = 1;
    public final int pauseState = 2;
    public final int dialogueState = 3;
    public final int characterState = 4;
    public final int gameFinishedState = 5;
    public final int gameOverState = 6;


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
        aSetter.setMonster();
        aSetter.setInteractiveTile();
        playMusic(0);
        //stopMusic();
        gameState = titleState;
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
            for (int i =0; i< monster.length; i++) {
                if (monster[i] !=  null) {
                    if(monster[i].alive == true && monster[i].dying == false){
                        monster[i].update();
                    }
                    if(monster[i].alive == false){
                        monster[i].checkDrop();
                        monster[i] = null;
                    }
                }
            }
            for (int i =0; i< projectileList.size(); i++) {
                if (projectileList.get(i) !=  null) {
                    if(projectileList.get(i).alive == true){
                        projectileList.get(i).update();
                    }
                    if(projectileList.get(i).alive == false){
                        projectileList.remove(i);
                    }
                }
            }
            for (int i =0; i< particleList.size(); i++) {
                if (particleList.get(i) !=  null) {
                    if(particleList.get(i).alive == true){
                        particleList.get(i).update();
                    }
                    if(particleList.get(i).alive == false){
                        particleList.remove(i);
                    }
                }
            }
            for(int i = 0;i<iTile.length;i++){
                if(iTile[i]!=null){
                    iTile[i].update();
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
        //Title State
        if(gameState == titleState){
            ui.draw(g2);
        }
        //Others
        else{
            //TILE
            tileM.draw(g2);
            //Interactive Tile
            for(int i = 0;i<iTile.length;i++){
                if(iTile[i]!=null){
                    iTile[i].draw(g2);
                }
            }
            //ADD ENTITY TO THE LIST
            entityList.add(player);
            for (int i =0; i<npc.length; i++) {
                if (npc[i] != null) {
                    entityList.add(npc[i]);
                }
            }
            for(int i =0; i<obj.length; i++) {
                if(obj[i] != null) {
                    entityList.add(obj[i]);
                }
            }
            for(int i =0; i<monster.length; i++) {
                if(monster[i] != null) {
                    entityList.add(monster[i]);
                }
            }
            for(int i =0; i<projectileList.size(); i++) {
                if(projectileList.get(i) != null) {
                    entityList.add(projectileList.get(i));
                }
            }
            for(int i =0; i<particleList.size(); i++) {
                if(particleList.get(i) != null) {
                    entityList.add(particleList.get(i));
                }
            }

            //SORT
            Collections.sort(entityList, new Comparator<Entity>()
            {
                @Override
                public int compare(Entity e1, Entity e2)
                {
                    int result = Integer.compare(e1.worldY, e2.worldY);
                    return result;
                }
            });
            //DRAW ENTITIES
            for(int i =0; i< entityList.size(); i++)
            {
                entityList.get(i).draw(g2);
            }

            //EMPTY ENTITY LIST
            entityList.clear();

            //UI
            ui.draw(g2);
        }

        //DEBUG
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

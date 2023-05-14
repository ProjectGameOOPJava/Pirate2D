package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import levels.Level;
import utilz.LoadSave;
import static utilz.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private BufferedImage[][] snailArr, boarArr, beeArr;
	private Level currentLevel;

	public EnemyManager(Playing playing) {
		this.playing = playing;
		loadEnemyImgs();

	}

	public void loadEnemies(Level level) {
		this.currentLevel = level;
	}

	public void update(int[][] lvlData) {
		boolean isAnyActive = false;
		for (Snail c : currentLevel.getSnails())
			if (c.isActive()) {
				c.update(lvlData, playing);
				isAnyActive = true;
			}
		
		for (Bee p : currentLevel.getBees())
			if (p.isActive()) {
				p.update(lvlData, playing);
				isAnyActive = true;
			}
		
		for (Boar s : currentLevel.getBoars())
			if (s.isActive()) {
				s.update(lvlData, playing);
				isAnyActive = true;
			}
		
		if(!isAnyActive)
			playing.setLevelCompleted(true);
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawSnails(g, xLvlOffset);
		drawBees(g, xLvlOffset);
		drawBoars(g, xLvlOffset);
	}

	private void drawSnails(Graphics g, int xLvlOffset) {
		for (Snail c : currentLevel.getSnails())
			if (c.isActive()) {
				g.drawImage(snailArr[c.getState()][c.getAniIndex()], 
						(int) c.getHitbox().x - xLvlOffset - SNAIL_DRAWOFFSET_X + c.flipX(), 
						(int) c.getHitbox().y - SNAIL_DRAWOFFSET_Y + (int) c.getPushDrawOffset(),
						SNAIL_WIDTH * c.flipW(), SNAIL_HEIGHT, null);
			c.drawHitbox(g, xLvlOffset);
		}
	}
		
	private void drawBees(Graphics g, int xLvlOffset) {
		for (Bee s : currentLevel.getBees())
			if (s.isActive()) {
				g.drawImage(beeArr[s.getState()][s.getAniIndex()], 
						(int) s.getHitbox().x - xLvlOffset - BEE_DRAWOFFSET_X + s.flipX(),
						(int) s.getHitbox().y - BEE_DRAWOFFSET_Y + (int) s.getPushDrawOffset(), BEE_WIDTH * s.flipW(), BEE_HEIGHT, null);
				s.drawHitbox(g, xLvlOffset);
				s.drawAttackBox(g, xLvlOffset);
			}
		}
		
	private void drawBoars(Graphics g, int xLvlOffset) {
		for (Boar p : currentLevel.getBoars())
			if (p.isActive()) {
				g.drawImage(boarArr[p.getState()][p.getAniIndex()], 
						(int) p.getHitbox().x - xLvlOffset - BOAR_DRAWOFFSET_X + p.flipX(),
						(int) p.getHitbox().y - BOAR_DRAWOFFSET_Y + (int) p.getPushDrawOffset(), BOAR_WIDTH * p.flipW(), BOAR_HEIGHT, null);
				p.drawHitbox(g, xLvlOffset);
			}
		}

	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for (Snail c : currentLevel.getSnails())
			if (c.isActive())
				if (c.getState() != HIT)
					if (attackBox.intersects(c.getHitbox())) {
						c.hurt(10);
						return;
				}
		for (Bee p : currentLevel.getBees())
			if (p.isActive()) {
				if (p.getState() == ATTACK && p.getAniIndex() >= 3)
					return;
				else {
					if (p.getState() != HIT)
						if (attackBox.intersects(p.getHitbox())) {
							p.hurt(20);
							return;
						}
				}
			}

		for (Boar s : currentLevel.getBoars())
			if (s.isActive()) {
				if (s.getState() != HIT)
					if (attackBox.intersects(s.getHitbox())) {
						s.hurt(20);
						return;
			
					}
			}
	}

	private void loadEnemyImgs() {
		snailArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.SNAIL_ATLAS), 8, 3, SNAIL_WIDTH_DEFAULT, SNAIL_HEIGHT_DEFAULT);
		beeArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.BEE_ATLAS), 4, 3, BEE_WIDTH_DEFAULT, BEE_HEIGHT_DEFAULT);
		boarArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.BOAR_ATLAS),6, 3, BOAR_WIDTH_DEFAULT, BOAR_HEIGHT_DEFAULT);
	}
	
	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}
	
	public void resetAllEnemies() {
		for (Snail c : currentLevel.getSnails())
			c.resetEnemy();
		for (Bee p : currentLevel.getBees())
			p.resetEnemy();
		for (Boar s : currentLevel.getBoars())
			s.resetEnemy();
	}
}
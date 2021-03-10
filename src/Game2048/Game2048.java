package Game2048;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
//import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;
 
public class Game2048 extends JPanel {
 
	// 0: start, 1: running, 2: won, 3: over 4: puzzle win
		private int gameState = 0;
		
		private Color gridColor = new Color(0xBBADA0);
	    private Color emptyColor = new Color(0xCDC1B4);
	    private Color startColor = new Color(0xFFEBCD);
	    
	    private boolean isCheatBoxOn = false;
	    private String cheatCode = "";
	    
	    
	    private final Color[] colorTable = {
	            new Color(0x701710), new Color(0xFFE4C3), new Color(0xfff4d3),
	            new Color(0xffdac3), new Color(0xe7b08e), new Color(0xe7bf8e),
	            new Color(0xffc4c3), new Color(0xE7948e), new Color(0xbe7e56),
	            new Color(0xbe5e56), new Color(0x9c3931), new Color(0x701710)};
	    
	    private final int goal = 2048;
	    private int score, highest, side = 4;
	    
	    private Random rand = new Random();
	    
	    private boolean canMoveChecking = false;
	    
	    private Tile[][] tiles;
	    private Piece[][] pieces;
	    
		private Font cheatFont = new Font("SansSerif", Font.BOLD, 37);
//		private Font testFont = new Font("SansSerif", Font.BOLD, 13);
		
		private BufferedImage img;
		private int imgW, imgH;
		private boolean isPuzzleOn = false;
 
    public Game2048() {
		this.setPreferredSize(new Dimension(900, 700));
		this.setBackground(new Color(0xf0e5d8));
		this.setFont(new Font("SansSerif", Font.BOLD, 48));
		this.setFocusable(true);
		
		this.addMouseListener(new MouseAdapter() {
			
			// 화면 클릭 시 startGame 메소드 호출. startGame 메소드는 게임이 진행 중이지 않을 때만 동작한다.
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				startGame();
				
				if (isPuzzleOn) {
					puzzlePressed(e);
					checkPuzzle();
				}
				repaint();
			}
		});
		
		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				
				if (!isCheatBoxOn) {
					switch (e.getKeyCode()) {
					case KeyEvent.VK_UP:
						if (!isPuzzleOn)
							moveUp();
						else {
							puzzleUp();
							checkPuzzle();
						}
						break;
					case KeyEvent.VK_DOWN:
						if (!isPuzzleOn)
							moveDown();
						else {
							puzzleDown();
							checkPuzzle();
						}
						break;
					case KeyEvent.VK_LEFT:
						if (!isPuzzleOn)
							moveLeft();
						else {
							puzzleLeft();
							checkPuzzle();
						}
						break;
					case KeyEvent.VK_RIGHT:
						if (!isPuzzleOn)
							moveRight();
						else {
							puzzleRight();
							checkPuzzle();
						}
						break;
					}
					if (gameState == 1 && e.getKeyCode() == KeyEvent.VK_ENTER)
						switchCheatBox();
				} else {
					if (e.getKeyCode() <= KeyEvent.VK_Z &&
							e.getKeyCode() >= KeyEvent.VK_0) {
						inputCheatCode(e.getKeyChar());
					} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						cheatEvent();
						switchCheatBox();
					} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE || 
							e.getKeyCode() == KeyEvent.VK_SPACE) {
						splCheatCode(e);
					}
				}
				repaint();
			}
			
		});
		
	}
 
    protected void puzzlePressed(MouseEvent e) {
		int col = (e.getX() - 210) / 120;
		int row = (e.getY() - 110) / 120;
		
		if (row > 0 && pieces[row-1][col].getNum() == side*side-1) {
			pieces[row-1][col].setNum(pieces[row][col].getNum());
			pieces[row][col].setNum(side*side-1);
		} else if (row < side-1 && pieces[row+1][col].getNum() == side*side-1) {
			pieces[row+1][col].setNum(pieces[row][col].getNum());
			pieces[row][col].setNum(side*side-1);
		} else if (col > 0 && pieces[row][col-1].getNum() == side*side-1) {
			pieces[row][col-1].setNum(pieces[row][col].getNum());
			pieces[row][col].setNum(side*side-1);
		} else if (col < side-1 && pieces[row][col+1].getNum() == side*side-1) {
			pieces[row][col+1].setNum(pieces[row][col].getNum());
			pieces[row][col].setNum(side*side-1);
		}
	}

	protected void puzzleUp() {
		boolean moved = false;
		
    	for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (pieces[i][j].getNum() == side*side-1) {
					if (i!=side-1) {
						pieces[i][j].setNum(pieces[i+1][j].getNum());
						pieces[i+1][j].setNum(side*side-1);
						moved = true;
						break;
					}
				}
			}
			if (moved)
				break;
		}
	}

	protected void puzzleDown() {
		for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (pieces[i][j].getNum() == side*side-1) {
					if (i!=0) {
						pieces[i][j].setNum(pieces[i-1][j].getNum());
						pieces[i-1][j].setNum(side*side-1);
						break;
					}
				}
			}
		}
		
	}

	protected void puzzleLeft() {
    	for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (pieces[i][j].getNum() == side*side-1) {
					if (j!=side-1) {
						pieces[i][j].setNum(pieces[i][j+1].getNum());
						pieces[i][j+1].setNum(side*side-1);
						break;
					}
				}
			}
		}
	}

	protected void puzzleRight() {
		for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (pieces[i][j].getNum() == side*side-1) {
					if (j!=0) {
						pieces[i][j].setNum(pieces[i][j-1].getNum());
						pieces[i][j-1].setNum(side*side-1);
						break;
					}
				}
			}
		}
	}

	protected void cheatEvent() {
    	final String win = "there is no cow level";
    	final String lose = "game over man";
    	final String score = "show me the money";
    	final String wall = "black sheep wall";
    	final String dble = "operation cwal";
    	final String hslide = "crabby but efficient";
    	final String vslide = "for vsrg lovers";
    	final String retro = "we no play 2048";
    	final String pwin = "piece breaker";
    	
    	if (cheatCode.equalsIgnoreCase(win)) {
    		if (isPuzzleOn == false)
    			gameState = 2;
    	}
    	else if (cheatCode.equalsIgnoreCase(lose)) {
    		if (isPuzzleOn == false)
    			gameState = 3;
    	}
    	else if (cheatCode.equalsIgnoreCase(score))
    		this.score += 10000;
    	else if (cheatCode.equalsIgnoreCase(wall))
    		blackSheepWall();
    	else if (cheatCode.equalsIgnoreCase(dble))
    		operationCwal();
    	else if (cheatCode.equalsIgnoreCase(hslide))
    		horizontalSlide();
    	else if (cheatCode.equalsIgnoreCase(vslide))
    		verticalSlide();
    	else if (cheatCode.equalsIgnoreCase(retro))
    		playPuzzle();
    	else if (cheatCode.equalsIgnoreCase(pwin)) {
    		if (isPuzzleOn == true) {
    			gameState = 4;
    			isPuzzleOn = false;
    		}
    	}
	}
	
	private void checkPuzzle() {
		int checker=0;
		boolean result=true;
		
		for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (checker++ != pieces[i][j].getNum())
					result = false;
			}
		}

		if (result) {
			gameState = 4;
			isPuzzleOn = false;
		}
	}

	private void playPuzzle() {
		String file = "";
		int f = rand.nextInt(32);
		
		switch (f) {
		case 0:
			file = "akatsuki1.png";
			break;
		case 1:
			file = "cosith_01.jpg";
			break;
		case 2:
			file = "crecent1.png";
			break;
		case 3:
			file = "est_01.jpg";
			break;
		case 4:
			file = "est_02.jpg";
			break;
		case 5:
			file = "ishioh2.png";
			break;
		case 6:
			file = "ishioh3.png";
			break;
		case 7:
			file = "ishioh7.png";
			break;
		case 8:
			file = "ishioh9.png";
			break;
		case 9:
			file = "ishiou_01.jpg";
			break;
		case 10:
			file = "karasawa_01.jpg";
			break;
		case 11:
			file = "keso_01.jpg";
			break;
		case 12:
			file = "kinako1.png";
			break;
		case 13:
			file = "left_01.jpg";
			break;
		case 14:
			file = "left1.png";
			break;
		case 15:
			file = "nekoko1.png";
			break;
		case 16:
			file = "poyon1.png";
			break;
		case 17:
			file = "reika2.png";
			break;
		case 18:
			file = "riutaso_01.jpg";
			break;
		case 19:
			file = "tokinin_01.jpg";
			break;
		case 20:
			file = "yamabuki1.png";
			break;
		case 21:
			file = "fether11.png";
			break;
		case 22:
			file = "fether3.png";
			break;
		case 23:
			file = "fether2.png";
			break;
		case 24:
			file = "fether7.png";
			break;
		case 25:
			file = "fether8.png";
			break;
		case 26:
			file = "maki2.png";
			break;
		case 27:
			file = "maki3.png";
			break;
		case 28:
			file = "fether_02.jpg";
			break;
		case 29:
			file = "fether_01.jpg";
			break;
		case 30:
			file = "fether_04.jpg";
			break;
		case 31:
			file = "ishio10.png";
			break;
		}
		
		try {
			img = ImageIO.read(getClass().getClassLoader().getResourceAsStream(file));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
//		ArrayList<Integer> sideTable = new ArrayList<>();
//		for (int i=0; i<side*side; i++) {
//			sideTable.add(i);
//		}
//		
//		pieces = new Piece[side][side];
//		
//		for (int i=0; i<side; i++) {
//			for (int j=0; j<side; j++) {
//				pieces[i][j] = new Piece();
//				int temp = rand.nextInt(sideTable.size());
//				pieces[i][j].setNum(sideTable.get(temp));
//				sideTable.remove(temp);
//			}
//		}
		
		int temp = 0;
		pieces = new Piece[side][side];
		
		for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				pieces[i][j] = new Piece();
				pieces[i][j].setNum(temp++);
			}
		}
		
		for (int i=0; i<500; i++) {
			int r = rand.nextInt(4);
			
			switch (r) {
			case 0:
				puzzleUp();
				break;
			case 1:
				puzzleDown();
				break;
			case 2:
				puzzleLeft();
				break;
			case 3:
				puzzleRight();
				break;
			}
		}
		
		imgH = img.getHeight();
		imgW = img.getWidth();
		
		isPuzzleOn = true;
	}

	private void verticalSlide() {
		Tile temp = null;
		for (int j=0; j<side; j++) {
			for (int i=0; i<side; i++) {
				if (i==0) {
					temp = tiles[side-1-i][side-1-j];
					tiles[side-1-i][side-1-j] = tiles[side-2-i][side-1-j];
					continue;
				} else if (i==side-1) {
					tiles[side-1-i][side-1-j] = temp;
					continue;
				}
				tiles[side-1-i][side-1-j] = tiles[side-2-i][side-1-j];
			}
		}
	}

	private void horizontalSlide() {
		Tile temp = null;
		for (int i=0; i<side; i++) {
			for (int j=0; j<side; j++) {
				if (j==0) {
					temp = tiles[side-1-i][side-1-j];
					tiles[side-1-i][side-1-j] = tiles[side-1-i][side-2-j];
					continue;
				} else if (j==side-1) {
					tiles[side-1-i][side-1-j] = temp;
					continue;
				}
				tiles[side-1-i][side-1-j] = tiles[side-1-i][side-2-j];
			}
		}
	}

	private void operationCwal() {
		for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.cwal();
	}

	private void blackSheepWall() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null && tile.getValue()<256)
                    tile.setValue(2);
    }

	protected void splCheatCode(KeyEvent e) {
		// TODO Auto-generated method stub
    	switch (e.getKeyCode()) {
    	case KeyEvent.VK_SPACE:
    		FontMetrics fm = this.getFontMetrics(cheatFont);
        	if (fm.stringWidth(this.cheatCode.concat(" ")) < 550)
    		this.cheatCode = this.cheatCode.concat(" ");
    		break;
    	case KeyEvent.VK_BACK_SPACE:
    		if (!this.cheatCode.equals(""))
    		this.cheatCode = this.cheatCode.substring(0, cheatCode.length()-1);
    		break;
    	}
	}

	protected void inputCheatCode(char key) {
    	String input = Character.toString(key);
    	FontMetrics fm = this.getFontMetrics(cheatFont);
    	if (fm.stringWidth(this.cheatCode.concat(input)) < 550)
    	this.cheatCode = this.cheatCode.concat(input);
		
	}

	protected void switchCheatBox() {
		// TODO Auto-generated method stub
		this.isCheatBoxOn = !this.isCheatBoxOn;
		this.cheatCode = "";
	}

	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2d);
    }
 
    void startGame() {
        if (gameState != 1) {
            score = 0;
            highest = 0;
            gameState = 1;
            tiles = new Tile[side][side];
            addRandomTile();
            addRandomTile();
        }
    }
 
    void drawGrid(Graphics2D g) {
		g.setColor(gridColor);
		g.fillRoundRect(200, 100, 499, 499, 15, 15);
		
		if (gameState == 1) {
			if (isPuzzleOn) {
				g.fillRoundRect(725, 474, 125, 125, 7, 7);
				g.drawImage(img, 730, 479, 730+115, 479+115, 0, 0, imgW, imgH, null);
				
			} else {
				drawScore(g);
			}
			
			for (int i=0; i<side; i++) {
				for (int j=0; j<side; j++) {
					if (isPuzzleOn) {
						drawPiece(g, i, j);
					} else {
						if (tiles[i][j] == null) {
							g.setColor(emptyColor);
							g.fillRoundRect(215 + j * 121, 115 + i * 121, 106, 106, 7, 7);
						} else {
							drawTile(g, i, j);
						}
					}
				}
			}
			if (isCheatBoxOn) {
				g.setColor(Color.black);
				g.fillRoundRect(100, 300, 700, 50, 7, 7);
				g.setColor(Color.white);
				g.setFont(cheatFont);
				g.drawString("cheat : "+this.cheatCode, 110, 340);
			}
		} else {
			g.setColor(startColor);
			g.fillRoundRect(215, 115, 469, 469, 7, 7);
			
			g.setColor(gridColor.darker());
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 128));
			g.drawString("2048", 310, 270);
			
			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			if (gameState == 4) {
				g.setColor(gridColor.darker());
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 64));
				g.drawString("Congratulations!", 195, 70);
				g.drawImage(img, 210, 110, 210+480, 110+480, 0, 0, imgW, imgH, null);
				g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
				g.setColor(gridColor);
				g.drawString("click to start a new game", 330, 650);
			} else {
				if (gameState == 3) {
					g.drawString("game over", 400, 350);
					drawResultScore(g);
				} else if (gameState == 2) {
					g.drawString("you won!", 405, 350);
					drawResultScore(g);
				}
				g.setColor(gridColor);
				g.drawString("click to start a new game", 330, 470);
				g.drawString("(use arrow keys to move tiles)", 310, 530);
			}
		}
	}
 
    private void drawScore(Graphics2D g) {
		g.fillRoundRect(250, 24, 399, 50, 7, 7);
		g.setColor(colorTable[0]);
		g.drawString("Score", 255, 66);
		FontMetrics fm = g.getFontMetrics();
		String s = String.valueOf(score);
		g.setColor(colorTable[1]);
		g.drawString(s, 514  - fm.stringWidth(s)/2, 66);
		
	}

	private void drawResultScore(Graphics2D g) {
    	FontMetrics fm = g.getFontMetrics();
    	String s = "score : " + String.valueOf(score);
    	g.drawString(s, 450 - fm.stringWidth(s) / 2, 390);
		
	}

	void drawTile(Graphics2D g, int r, int c) {
        int value = tiles[r][c].getValue();
 
        g.setColor(colorTable[(int) (Math.log(value) / Math.log(2)) + 1]);
        g.fillRoundRect(215 + c * 121, 115 + r * 121, 106, 106, 7, 7);
        String s = String.valueOf(value);
 
        g.setColor(value < 128 ? colorTable[0] : colorTable[1]);
 
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int dec = fm.getDescent();
 
        int x = 215 + c * 121 + (106 - fm.stringWidth(s)) / 2;
        int y = 115 + r * 121 + (asc + (106 - (asc + dec)) / 2);
 
        g.drawString(s, x, y);
    }
	
	void drawPiece(Graphics2D g, int r, int c) {
		int value = pieces[r][c].getNum();
		
		int pieceW = imgW / side;
		int pieceH = imgH / side;
		
		
		int sx = value % side * pieceW;
		int sy = value / side * pieceH;
		int dx = 210 + c * 120;
		int dy = 110 + r * 120;
				
		
//		String s = "value: " + value + "\n" + "x: " + r + "\n" + "y: " + c;
		
		
		if (value != side*side-1) {
			g.drawImage(img, dx, dy, dx+120, dy+120, sx, sy, sx+pieceW, sy+pieceH, null);
			
//			g.setColor(value < 128 ? colorTable[0] : colorTable[1]);
//	        g.setFont(testFont);
//	        g.drawString(s, dx, dy+13);
		}
		else {
			g.setColor(gridColor);
			g.fillRect(dx, dy, 120, 120);
			
//			g.setColor(value < 128 ? colorTable[0] : colorTable[1]);
//	        g.setFont(testFont);
//	        g.drawString(s, dx, dy+13);
		}
		
		
		
	}
 
 
    private void addRandomTile() {
    	int row, col, pos;
    	do {
    		pos = rand.nextInt(side * side);
    		row = pos / side;
    		col = pos % side;
    	} while (tiles[row][col] != null);
    	
    	int val = rand.nextInt(10) == 0 ? 4 : 2;
    	tiles[row][col] = new Tile(val);
    }
 
    private boolean move(int countFrom, int rInc, int cInc)	{
		boolean moved = false;
		
		for (int i = 0; i<side*side; i++) {
			int j = Math.abs(countFrom-i);
			
			int r = j / side;
			int c = j % side;
			
			if (tiles[r][c] == null) {
				continue;
			}
			
			int nextR = r + rInc;
			int nextC = c + cInc;
			
			while (nextR < side && nextC < side && nextR >= 0 && nextC >= 0) {
				
				Tile current = tiles[r][c];
				Tile next = tiles[nextR][nextC];
				
				if (next == null) {
					
					if (canMoveChecking) return true;
					
					tiles[nextR][nextC] = current;
					tiles[r][c] = null;
					r = nextR;
					c = nextC;
					nextR += rInc;
					nextC += cInc;
					moved = true;
					
				} else if (next.canMergeWith(current)) {
					
					if (canMoveChecking) return true;
					
					tiles[nextR][nextC].mergeWith(current);
					int val = tiles[nextR][nextC].getValue();
					
					if (val > highest) highest = val;
					score += val;
					tiles[r][c] = null;
					moved = true;
					break;
					
				} else break;
			}
		}
		
		if (moved) {
			if (highest < goal) {
				for (Tile[] a : tiles) {
					for (Tile b : a) {
						if (b != null)
						b.setMerged(false);
					}
				}
				addRandomTile();
				
				if (!canMove()) {
					gameState = 3;
				}
			} else {
				gameState = 2;
			}
		}
		
		return moved;

	}
 
    boolean moveUp() {
        return move(0, -1, 0);
    }
 
    boolean moveDown() {
        return move(side * side - 1, 1, 0);
    }
 
    boolean moveLeft() {
        return move(0, 0, -1);
    }
 
    boolean moveRight() {
        return move(side * side - 1, 0, 1);
    }
 
    void clearMerged() {
        for (Tile[] row : tiles)
            for (Tile tile : row)
                if (tile != null)
                    tile.setMerged(false);
    }
 
    boolean canMove() {
    	canMoveChecking = true;
        boolean hasMoves = moveUp() || moveDown() || moveLeft() || moveRight();
        canMoveChecking = false;
        return hasMoves;
    }
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame();
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setTitle("2048");
            f.setResizable(true);
            f.add(new Game2048(), BorderLayout.CENTER);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
 
class Tile {
    private boolean merged;
    private int value;
 
    Tile(int val) {
        value = val;
    }
 
    int getValue() {
        return value;
    }
    
    void setValue(int val) {
    	this.value = val;
    }
    
    void cwal() {
    	this.value *= 2;
    }
 
    void setMerged(boolean m) {
        merged = m;
    }
 
    boolean canMergeWith(Tile other) {
        return !merged && other != null && !other.merged && value == other.getValue();
    }
 
    int mergeWith(Tile other) {
        if (canMergeWith(other)) {
            value *= 2;
            merged = true;
            return value;
        }
        return -1;
    }
}

class Piece {
	private int num;
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
	
	
}
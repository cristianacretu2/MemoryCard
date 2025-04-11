
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;


public class MatchCards {

    class Card {

        String cardName;
        ImageIcon cardImageIcon;

        Card( String cardName, ImageIcon cardImageIcon ) {
            this.cardName = cardName;
            this.cardImageIcon = cardImageIcon;
        }

        public String toString() {
            return cardName;
        }
    }

    String[] cardList = { // contine numele imaginilor cartilor
            "1", "2", "3", "4", "5", "6"
    };

  //  int setsGuessed = 0; // variabla in care tinem minte seturile ghicite pt a putea termina jocul

    int rows = 4;
    int columns = 3;
    int cardWigth = 400; // pixels
    int cardHeight = 200;

    ArrayList<Card> cardSet; // a deck of cards
    ImageIcon cardBackImageIcon; // one of the 6 images

    // for the game window

    int boardWidth = columns * cardWigth + 30;
    int boardHeight = rows * cardHeight + 50;

    JFrame frame = new JFrame(" Match Cards ");
    JLabel textLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();
    JPanel restartGamePanel = new JPanel();
    JButton restartButton = new JButton();

    int errorCount = 0;
    ArrayList<JButton> board;

    Timer hideCardTimer;
    boolean gameReady = false;

    JButton card1Selected;
    JButton card2Selected;


    MatchCards() {

        setupCards();
        suffleCards();

       // frame.setVisible(true);
        frame.setLayout(new BorderLayout());
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        textLabel.setFont( new Font("Luminari", Font.PLAIN, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Errors: " + Integer.toString(errorCount));

        textPanel.setPreferredSize(new Dimension(boardWidth,30));
        textPanel.add(textLabel);
        frame.add(textPanel, BorderLayout.NORTH);

        // card game board
        board = new ArrayList<JButton>();
       // boardPanel.setLayout( new GridLayout(rows, columns));
        // incercam alta varianta pentru linia comentata

        GridLayout gridLayout = new GridLayout(rows, columns);
        gridLayout.setHgap(10);
        gridLayout.setVgap(10); // daca e nevoie marim spatiul
        boardPanel.setLayout(gridLayout);

        for ( int i = 0; i < cardSet.size(); i++ ) {
            JButton tile = new JButton();
            tile.setPreferredSize(new Dimension(cardWigth, cardHeight));
            tile.setOpaque(true);
            tile.setIcon(cardSet.get(i).cardImageIcon);
            tile.setFocusable(false);
            // for the 2 cards we chose
            tile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if ( !gameReady ) {
                        return;
                    }
                    JButton tile = (JButton) e.getSource();
                    if ( tile.getIcon() == cardBackImageIcon ) {
                        if ( card1Selected == null ) {
                            card1Selected = tile;
                            int index = board.indexOf(card1Selected);
                            card1Selected.setIcon( cardSet.get(index).cardImageIcon);
                        }
                        else if ( card2Selected == null ) {
                            card2Selected = tile;
                            int index = board.indexOf(card2Selected);
                            card2Selected.setIcon( cardSet.get(index).cardImageIcon);
                            // daca am ajuns aici inseamna ca avem 2 carti selectate

                            if ( card1Selected.getIcon() != card2Selected.getIcon() ) {
                                errorCount++;
                                textLabel.setText("Errors: " + Integer.toString(errorCount));
                                hideCardTimer.start();
                            }
                            else {
                                card1Selected = null;
                                card2Selected = null;
                            }

                        }
                    }
                }
            });
            board.add(tile);
            boardPanel.add(tile);
        }
        frame.add(boardPanel);

        // restart button

        restartButton.setFont(new Font("Luminari", Font.PLAIN, 20));
        restartButton.setText("Restart Game");
        restartButton.setPreferredSize( new Dimension(boardWidth,30));
        restartButton.setFocusable(false);
        restartButton.setEnabled(false);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ( !gameReady ) {
                    return;
                }
                    // new game settings
                gameReady = false;
                restartButton.setEnabled(false);
                card1Selected = null;
                card2Selected = null;
                suffleCards();
                // re assign buttons with new cards
                for ( int i = 0; i < board.size(); i++ ) {
                    board.get(i).setIcon(cardSet.get(i).cardImageIcon);
                }

                errorCount = 0;
                textLabel.setText("Error: " + Integer.toString(errorCount));
                hideCardTimer.start();
            }
        });
        restartGamePanel.add(restartButton);
        frame.add(restartGamePanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);

        // start game

        hideCardTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideCards();
            }
        });
        hideCardTimer.setRepeats(false);
        hideCardTimer.start();


    }

    void setupCards() {

        cardSet = new ArrayList<Card>();

        for( String cardName : cardList ) {
            // incarcam fiecare imagine
            Image cardImg = new ImageIcon( getClass().getResource("./images/" + cardName + ".png")).getImage();
            ImageIcon cardImageIcon = new ImageIcon(cardImg.getScaledInstance(cardWigth,cardHeight, Image.SCALE_SMOOTH)); // o scalam la dimensiunea noastra

            Card card = new Card(cardName, cardImageIcon);
            cardSet.add(card);
        }

        cardSet.addAll(cardSet); // le mai adaugam o data, adica dublam

        // load back card image
        Image cardBackImg = new ImageIcon( getClass().getResource("./images/back.png")).getImage();
        cardBackImageIcon = new ImageIcon(cardBackImg.getScaledInstance(cardWigth,cardHeight, Image.SCALE_SMOOTH));

    }

    void suffleCards() {

        System.out.println(cardSet);

        for ( int i = 0; i < cardSet.size(); i++ ) {
            int j = (int) (Math.random() * cardSet.size()); // un index ales random pentru inversare intre carti

            // le inversam
            Card temp = cardSet.get(i);
            cardSet.set(i, cardSet.get(j) );
            cardSet.set(j, temp);
        }

        System.out.println(cardSet);
    }

    void hideCards() {

        if ( gameReady && card1Selected != null && card2Selected != null ) {
            card1Selected.setIcon(cardBackImageIcon);
            card1Selected = null;
            card2Selected.setIcon(cardBackImageIcon);
            card2Selected = null;

        } else { // flip all cards
            for ( int i = 0; i < board.size(); i++ ) {
                board.get(i).setIcon(cardBackImageIcon);

            }
            gameReady = true;
            restartButton.setEnabled(true);

        }

    }




}

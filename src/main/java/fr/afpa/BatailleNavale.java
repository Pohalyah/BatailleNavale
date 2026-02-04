package fr.afpa;

import java.util.Random;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class BatailleNavale {

    public static void showGrille(String[][] grille) {
        AnsiConsole.systemInstall();
        for (int i = 0; i < grille.length; i++) {
            for (int j = 0; j < grille[i].length; j++) {
                String cellule = grille[i][j];
                if (i == 0 && j > 0)
                    System.out.print(Ansi.ansi().fg(Ansi.Color.WHITE).a("  " + cellule).reset());
                else if (j == 0 && i > 0)
                    System.out.print(Ansi.ansi().fg(Ansi.Color.YELLOW).a("  " + cellule).reset());
                else if (cellule.equals("~") || cellule.equals("O"))
                    System.out.print(Ansi.ansi().fg(Ansi.Color.BLUE).a("  " + cellule).reset());
                else if (cellule.equals("X"))
                    System.out.print(Ansi.ansi().fg(Ansi.Color.RED).a("  " + cellule).reset());
                else if (cellule.equals("#"))
                    System.out.print(Ansi.ansi().fg(Ansi.Color.GREEN).a("  " + cellule).reset());
                else
                    System.out.print("  " + cellule);
            }
            System.out.println();
        }
    }

    public static void initialiserGrilles(String[][] grilleVisible, String[][] grilleBateaux) {
        for (int i = 0; i < 11; i++)
            for (int j = 0; j < 11; j++) {
                if (i == 0 && j == 0) {
                    grilleVisible[i][j] = " ";
                    grilleBateaux[i][j] = " ";
                } else if (i == 0) {
                    grilleVisible[i][j] = String.valueOf((char) ('A' + j - 1));
                    grilleBateaux[i][j] = grilleVisible[i][j];
                } else if (j == 0) {
                    grilleVisible[i][j] = String.valueOf(i);
                    grilleBateaux[i][j] = grilleVisible[i][j];
                } else {
                    grilleVisible[i][j] = "~";
                    grilleBateaux[i][j] = "~";
                }
            }
    }

    public static String demanderCase(Scanner scanner) {
        while (true) {
            String saisie = scanner.nextLine().toUpperCase();
            if (saisie.length() >= 2 && saisie.length() <= 3 && saisie.charAt(0) >= 'A' && saisie.charAt(0) <= 'J') {
                try {
                    int ligne = Integer.parseInt(saisie.substring(1));
                    if (ligne >= 1 && ligne <= 10)
                        return saisie;
                } catch (Exception e) {
                }
            }
            System.out.println("Case invalide");
        }
    }

    public static boolean zoneLibre(String[][] grille, int ligne, int colonne) {
        for (int i = ligne - 1; i <= ligne + 1; i++)
            for (int j = colonne - 1; j <= colonne + 1; j++)
                if (i >= 1 && i <= 10 && j >= 1 && j <= 10)
                    if (grille[i][j].equals("B"))
                        return false;
        return true;
    }

    public static void placerBateaux(String[][] grille) {
        Random random = new Random();
        int[] taillesBateaux = { 5, 4, 3, 3, 2 };
        for (int taille : taillesBateaux) {
            boolean placementOk = false;
            while (!placementOk) {
                boolean horizontal = random.nextBoolean();
                int ligne = random.nextInt(10) + 1;
                int colonne = random.nextInt(10) + 1;
                placementOk = true;
                for (int k = 0; k < taille; k++) {
                    int nextline = horizontal ? ligne : ligne + k;
                    int nextColonne = horizontal ? colonne + k : colonne;
                    if (nextline > 10 || nextColonne > 10 || !zoneLibre(grille, nextline, nextColonne)) {
                        placementOk = false;
                        break;
                    }
                }
                if (placementOk)
                    for (int k = 0; k < taille; k++) {
                        int nl = horizontal ? ligne : ligne + k;
                        int nc = horizontal ? colonne + k : colonne;
                        grille[nl][nc] = "B";
                    }
            }
        }
    }

    public static boolean bateauRestant(String[][] grille, int ligne, int colonne, boolean[][] visite) {
        if (ligne < 1 || ligne > 10 || colonne < 1 || colonne > 10 || visite[ligne][colonne])
            return false;
        visite[ligne][colonne] = true;
        if (grille[ligne][colonne].equals("B"))
            return true;
        if (!grille[ligne][colonne].equals("X"))
            return false;
        return bateauRestant(grille, ligne + 1, colonne, visite) || bateauRestant(grille, ligne - 1, colonne, visite)
                || bateauRestant(grille, ligne, colonne + 1, visite)
                || bateauRestant(grille, ligne, colonne - 1, visite);
    }

    public static boolean resteBateau(String[][] grille, int ligne, int colonne) {
        boolean[][] visite = new boolean[11][11];
        return bateauRestant(grille, ligne, colonne, visite);
    }

    public static void colorerCoule(String[][] grilleVisible, int ligne, int colonne) {
        if (ligne < 1 || ligne > 10 || colonne < 1 || colonne > 10)
            return;
        if (!grilleVisible[ligne][colonne].equals("X"))
            return;
        grilleVisible[ligne][colonne] = "#";
        colorerCoule(grilleVisible, ligne + 1, colonne);
        colorerCoule(grilleVisible, ligne - 1, colonne);
        colorerCoule(grilleVisible, ligne, colonne + 1);
        colorerCoule(grilleVisible, ligne, colonne - 1);
    }

    public static boolean jouerTourJoueur(Scanner scanner, String nomJoueur, String[][] grilleVisible,
            String[][] grilleBateaux) {
        System.out.println(nomJoueur);
        showGrille(grilleVisible);
        String caseChoisie = demanderCase(scanner);
        int ligne = Integer.parseInt(caseChoisie.substring(1));
        int colonne = caseChoisie.charAt(0) - 'A' + 1;
        if (!grilleVisible[ligne][colonne].equals("~"))
            return true;
        if (grilleBateaux[ligne][colonne].equals("B")) {
            grilleBateaux[ligne][colonne] = "X";
            grilleVisible[ligne][colonne] = "X";
            if (!resteBateau(grilleBateaux, ligne, colonne)) {
                colorerCoule(grilleVisible, ligne, colonne);
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(nomJoueur + " : Coulé !").reset());
            } else
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(nomJoueur + " : Touché !").reset());
            return true;
        } else {
            grilleVisible[ligne][colonne] = "O";
            System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a(nomJoueur + " : À l'eau !").reset());
            return false;
        }
    }

    static int lastHitRow = -1;
    static int lastHitCol = -1;
    static List<int[]> targets = new ArrayList<>();

    public static boolean jouerTourOrdinateur(String nom, String[][] grilleVisible, String[][] grilleBateaux) {
        int ligne = -1;
        int colonne = -1;

        if (!targets.isEmpty()) {
            int[] nextTarget = targets.remove(0);
            ligne = nextTarget[0];
            colonne = nextTarget[1];
        } else {
            do {
                ligne = (int) (Math.random() * 10) + 1;
                colonne = (int) (Math.random() * 10) + 1;
            } while (!grilleVisible[ligne][colonne].equals("~"));
        }

        if (grilleBateaux[ligne][colonne].equals("B")) {
            grilleBateaux[ligne][colonne] = "X";
            grilleVisible[ligne][colonne] = "X";
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(nom + " : Touché !").reset());

            if (!resteBateau(grilleBateaux, ligne, colonne)) {
                colorerCoule(grilleVisible, ligne, colonne);
                lastHitRow = -1;
                lastHitCol = -1;
                targets.clear();
                System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a(nom + " : Coulé !").reset());
            } else {
                lastHitRow = ligne;
                lastHitCol = colonne;
                targets.clear();
                if (ligne > 1 && grilleVisible[ligne - 1][colonne].equals("~"))
                    targets.add(new int[] { ligne - 1, colonne });
                if (ligne < 10 && grilleVisible[ligne + 1][colonne].equals("~"))
                    targets.add(new int[] { ligne + 1, colonne });
                if (colonne > 1 && grilleVisible[ligne][colonne - 1].equals("~"))
                    targets.add(new int[] { ligne, colonne - 1 });
                if (colonne < 10 && grilleVisible[ligne][colonne + 1].equals("~"))
                    targets.add(new int[] { ligne, colonne + 1 });
            }
            return true;
        } else {
            grilleVisible[ligne][colonne] = "O";
            System.out.println(Ansi.ansi().fg(Ansi.Color.BLUE).a(nom + " : À l'eau !").reset());
            return false;
        }
    }

    public static boolean aGagne(String[][] grilleBateaux) {
        for (int i = 1; i <= 10; i++)
            for (int j = 1; j <= 10; j++)
                if (grilleBateaux[i][j].equals("B"))
                    return false;
        return true;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("1 : PvP");
        System.out.println("2 : PvC");
        int mode = Integer.parseInt(scanner.nextLine());
        String[][] grilleVisibleJ1 = new String[11][11];
        String[][] grilleBateauxJ1 = new String[11][11];
        String[][] grilleVisibleJ2 = new String[11][11];
        String[][] grilleBateauxJ2 = new String[11][11];
        initialiserGrilles(grilleVisibleJ1, grilleBateauxJ1);
        initialiserGrilles(grilleVisibleJ2, grilleBateauxJ2);
        placerBateaux(grilleBateauxJ1);
        placerBateaux(grilleBateauxJ2);
        int tirsRestants = 70;
        boolean tourJoueur1 = true;

        while (true) {
            boolean rejouer;
            if (tourJoueur1) {
                if (mode == 2) {
                    System.out.println("Tirs restants : " + tirsRestants);
                    if (tirsRestants == 0) {
                        System.out.println("Plus de munitions, défaite !");
                        break;
                    }
                    tirsRestants--;
                    do {
                        rejouer = jouerTourJoueur(scanner, "Joueur", grilleVisibleJ2, grilleBateauxJ2);
                        if (aGagne(grilleBateauxJ2))
                            break;
                    } while (rejouer && !aGagne(grilleBateauxJ2));
                    if (aGagne(grilleBateauxJ2))
                        break;
                } else {
                    do {
                        rejouer = jouerTourJoueur(scanner, "Joueur 1", grilleVisibleJ2, grilleBateauxJ2);
                        if (aGagne(grilleBateauxJ2))
                            break;
                    } while (rejouer && !aGagne(grilleBateauxJ2));
                    if (aGagne(grilleBateauxJ2))
                        break;
                }
            } else {
                if (mode == 1) {
                    do {
                        rejouer = jouerTourJoueur(scanner, "Joueur 2", grilleVisibleJ1, grilleBateauxJ1);
                        if (aGagne(grilleBateauxJ1))
                            break;
                    } while (rejouer && !aGagne(grilleBateauxJ1));
                    if (aGagne(grilleBateauxJ1))
                        break;
                } else {
                    do {
                        rejouer = jouerTourOrdinateur("Ordinateur", grilleVisibleJ1, grilleBateauxJ1);
                        if (aGagne(grilleBateauxJ1))
                            break;
                    } while (rejouer && !aGagne(grilleBateauxJ1));
                    if (aGagne(grilleBateauxJ1))
                        break;
                }
            }
            tourJoueur1 = !tourJoueur1;
        }

        System.out.println("Partie terminée");
    }
}

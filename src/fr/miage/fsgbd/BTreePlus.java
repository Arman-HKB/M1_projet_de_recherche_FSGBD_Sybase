package fr.miage.fsgbd;

import javax.swing.tree.DefaultMutableTreeNode;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Galli Gregory, Mopolo Moke Gabriel
 * @param <Type>
 */
public class BTreePlus<Type> implements java.io.Serializable {
    private Noeud<Type> racine;
    /*  Arraylist qui contiendra la liste de toutes les clefs
        Utilisé plus tard pour récupérer aléatoirement 100 clefs pour les recherches sans avoir à directement modifier l'ArrayList keys de Noeud.java
    */
    private ArrayList<Type> listKeys = new ArrayList<>();

    public BTreePlus(int u, Executable e) {
        racine = new Noeud<Type>(u, e, null);
    }

    public void afficheArbre() {
        racine.afficheNoeud(true, 0);
    }

    /**
     * Méthode récursive permettant de récupérer tous les noeuds
     *
     * @return DefaultMutableTreeNode
     */
    public DefaultMutableTreeNode bArbreToJTree() {
        return bArbreToJTree(racine);
    }

    private DefaultMutableTreeNode bArbreToJTree(Noeud<Type> root) {
        StringBuilder txt = new StringBuilder();
        for (Type key : root.keys)
            txt.append(key.toString()).append(" ");

        DefaultMutableTreeNode racine2 = new DefaultMutableTreeNode(txt.toString(), true);
        for (Noeud<Type> fil : root.fils)
            racine2.add(bArbreToJTree(fil));

        return racine2;
    }


    public boolean addValeur(Type valeur) {
        System.out.println("Ajout de la valeur : " + valeur.toString());
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }

    // On surcharge addValeur
    public boolean addValeur(Type valeur, Integer ligne) {
        System.out.println("Ajout de la valeur : " + valeur.toString());
        if (racine.contient(valeur) == null) {
            Noeud<Type> newRacine = racine.addValeur(valeur, ligne);
            this.listKeys.add(valeur);
            if (racine != newRacine)
                racine = newRacine;
            return true;
        }
        return false;
    }

    public void removeValeur(Type valeur) {
        System.out.println("Retrait de la valeur : " + valeur.toString());
        if (racine.contient(valeur) != null) {
            Noeud<Type> newRacine = racine.removeValeur(valeur, false);
            if (racine != newRacine)
                racine = newRacine;
        }
    }

    // On lit le fichier ligne par ligne, et on compare le premier élément que chaque ligne à la valeur recherchée
    public Integer rechercheSequentielleDepuisFichier(Integer value) {
        try (BufferedReader reader = new BufferedReader(new FileReader(GUI.file))) {
            String ligne;
            Integer ligneN = 0;
            while ((ligne = reader.readLine()) != null) {
                ligneN++;
                if (Integer.parseInt(ligne.substring(0, ligne.indexOf(";"))) == value) {
                    return ligneN;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    // On utilise la fonction get() pour récupérer la clé associée à la valeur recherchée
    public Integer rechercheDepuisIndex(Integer value) {
        return racine.choixNoeudAjout((Type) value).pointeurs.get(value);
    }

    // Recherche de 100 clefs choisies aléatoirement
    public void lancerRecherche() {
        if (this.listKeys.size() == 0) {
            System.out.println("Le fichier n'est pas chargé");
        } else {
            long debut, fin, tempsIndex, tempsMinIndex, tempsMaxIndex, tempsMoyIndex, tempsSeq, tempsMinSeq, tempsMaxSeq, tempsMoySeq;
            tempsMinIndex = Long.MAX_VALUE;
            tempsMaxIndex = Long.MIN_VALUE;
            tempsMoyIndex = 0;
            tempsMinSeq = Long.MAX_VALUE;
            tempsMaxSeq = Long.MIN_VALUE;
            tempsMoySeq = 0;

            // Liste aléatoire de 100 clefs
            ArrayList<Type> list = new ArrayList<>();
            Collections.shuffle(listKeys);
            for(int i = 0; i < 100; i ++){
                list.add(listKeys.get(i));
            }

            // Recherche des 100 clefs
            for (Type key : list) {
                // Depuis l'index
                debut = System.nanoTime();
                rechercheDepuisIndex((Integer) key);
                fin = System.nanoTime();
                tempsIndex = fin - debut;

                if (tempsIndex < tempsMinIndex) {
                    tempsMinIndex = tempsIndex;
                }
                if (tempsIndex > tempsMaxIndex) {
                    tempsMaxIndex = tempsIndex;
                }
                tempsMoyIndex += tempsIndex;

                // Sequentielle depuis un fichier
                debut = System.nanoTime();
                rechercheSequentielleDepuisFichier((Integer) key);
                fin = System.nanoTime();
                tempsSeq = fin - debut;

                if (tempsSeq < tempsMinSeq) {
                    tempsMinSeq = tempsSeq;
                }
                if (tempsSeq > tempsMaxSeq) {
                    tempsMaxSeq = tempsSeq;
                }
                tempsMoySeq += tempsSeq;
            }

            tempsMoyIndex = tempsMoyIndex/100;
            tempsMoySeq = tempsMoySeq/100;

            System.out.println("\nTemps min de la recherche depuis l'index : " + tempsMinIndex + " ns");
            System.out.println("Temps max de la recherche depuis l'index : " + tempsMaxIndex + " ns");
            System.out.println("Temps moyen de la recherche depuis l'index : " + tempsMoyIndex + " ns\n");
            System.out.println("Temps min de la recherche sequentielle depuis un fichier : " + tempsMinSeq + " ns");
            System.out.println("Temps max de la recherche sequentielle depuis un fichier : " + tempsMaxSeq + " ns");
            System.out.println("Temps moyen de la recherche sequentielle depuis un fichier : " + tempsMoySeq + " ns");
        }
    }
}
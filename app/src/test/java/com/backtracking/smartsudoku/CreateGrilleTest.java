package com.backtracking.smartsudoku;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

import com.backtracking.smartsudoku.models.Grille;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

// Execute the tests in a predictable order
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CreateGrilleTest {
    private static final Integer TAILLE_GRILLE = 3;
    private Grille grille = new Grille(TAILLE_GRILLE);
    @Test
    @Before
    public void T1_creationGrille(){
        assertNotNull(grille);

    }

    @Test
    public void T2_checkRegions(){
        assertEquals(grille.getRegions().size(), Math.pow(TAILLE_GRILLE, 2), 0.0);
    }

}

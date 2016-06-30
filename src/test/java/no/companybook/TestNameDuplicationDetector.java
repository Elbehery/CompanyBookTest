package no.companybook;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mustafa on 30.06.16.
 */
public class TestNameDuplicationDetector {

    private NameDuplicationDetector nameDuplicationDetector;
    private List<String> nullNames;
    private List<String> emptyNames;
    private List<String> combinationNames;


    @Before
    public void setUp() {

        nameDuplicationDetector = new NameDuplicationDetector();

        nullNames = null;
        emptyNames = new LinkedList<>();

        combinationNames = new LinkedList<>();
        combinationNames.add("Bill Gates");
        combinationNames.add("Gates Bill");
        combinationNames.add("Bill Henry Gates");
        combinationNames.add("William Gates");
        combinationNames.add("Walter Gates");
        combinationNames.add("William Henry Gatez");

    }

    @Test(expected = NullPointerException.class)
    public void testComputeLevenshteinDistanceNullException() {

        String sourceStringOne = null;
        String targetStringOne = "test";

        nameDuplicationDetector.computeLevenshteinDistance(sourceStringOne, targetStringOne);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testComputeLevenshteinDistanceIllegalArgumentException() {

        String sourceStringOne = "";
        String targetStringOne = "test";

        nameDuplicationDetector.computeLevenshteinDistance(sourceStringOne, targetStringOne);
    }

    @Test
    public void testComputeLevenshteinDistance() {

        String sourceStringOne = "test";
        String targetStringOne = "test";

        int distance = nameDuplicationDetector.computeLevenshteinDistance(sourceStringOne, targetStringOne);
        Assert.assertEquals("Levenshtein Distance is not correct", 0, distance);

        String sourceStringTwo = "test";
        String targetStringTwo = "tent";

        distance = nameDuplicationDetector.computeLevenshteinDistance(sourceStringTwo, targetStringTwo);
        Assert.assertEquals("Levenshtein Distance is not correct", 1, distance);

        String sourceStringThree = "GUMBO";
        String targetStringThree = "GAMBOL";

        distance = nameDuplicationDetector.computeLevenshteinDistance(sourceStringThree, targetStringThree);
        Assert.assertEquals("Levenshtein Distance is not correct", 2, distance);
    }

    @Test(expected = NullPointerException.class)
    public void testGetLastNameOnlyNullException() {

        String fullName = null;
        nameDuplicationDetector.getLastNameOnly(fullName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLastNameOnlyIllegalArgumentException() {

        String fullName = "";
        nameDuplicationDetector.getLastNameOnly(fullName);
    }

    @Test
    public void testGetLastNameOnly() {

        String fullName = "Mustafa Elbehery";
        String lastName = nameDuplicationDetector.getLastNameOnly(fullName);
        Assert.assertEquals("getLastName method has incorrect behavior", "Elbehery", lastName);

        fullName = "           Mustafa Elsayed Elbehery";
        lastName = nameDuplicationDetector.getLastNameOnly(fullName);
        Assert.assertEquals("getLastName method has incorrect behavior", "Elbehery", lastName);

        fullName = "Mustafa Elsayed Elbehery              ";
        lastName = nameDuplicationDetector.getLastNameOnly(fullName);
        Assert.assertEquals("getLastName method has incorrect behavior", "Elbehery", lastName);

        fullName = "MustafaElbehery";
        lastName = nameDuplicationDetector.getLastNameOnly(fullName);
        Assert.assertEquals("getLastName method has incorrect behavior", "MustafaElbehery", lastName);
    }


    @Test(expected = NullPointerException.class)
    public void testFlipOrderOfNamePartsNullException() {

        String fullName = null;
        nameDuplicationDetector.flipOrderOfNameParts(fullName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFlipOrderOfNamePartsIllegalArgumentException() {

        String fullName = "";
        nameDuplicationDetector.flipOrderOfNameParts(fullName);

        fullName = "Mustafa Elsayed Elbehery              ";
        nameDuplicationDetector.flipOrderOfNameParts(fullName);

    }


    @Test
    public void testFlipOrderOfNameParts() {

        String fullName = "Mustafa Elbehery";
        String flippedName = nameDuplicationDetector.flipOrderOfNameParts(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "Elbehery Mustafa", flippedName);


        fullName = "Bill Gates ";
        flippedName = nameDuplicationDetector.flipOrderOfNameParts(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "Gates Bill", flippedName);

        fullName = "MustafaElbehery";
        flippedName = nameDuplicationDetector.flipOrderOfNameParts(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "MustafaElbehery", flippedName);
    }


    @Test(expected = NullPointerException.class)
    public void testSortStringNullException() {

        String fullName = null;
        nameDuplicationDetector.sortString(fullName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSortStringIllegalArgumentException() {

        String fullName = "";
        nameDuplicationDetector.sortString(fullName);

    }


    @Test(expected = NullPointerException.class)
    public void testEliminateMiddleNamesNullException() {

        String fullName = null;
        nameDuplicationDetector.eliminateMiddleNames(fullName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEliminateMiddleNamesIllegalArgumentException() {

        String fullName = "";
        nameDuplicationDetector.eliminateMiddleNames(fullName);

    }


    @Test
    public void testEliminateMiddleNames() {

        String fullName = "Bill Henry Gates ";
        String firstLastNameOnly = nameDuplicationDetector.eliminateMiddleNames(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "Bill Gates", firstLastNameOnly);

        fullName = "Mustafa Elbehery";
        firstLastNameOnly = nameDuplicationDetector.eliminateMiddleNames(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "Mustafa Elbehery", firstLastNameOnly);

        fullName = "Mustafa Elsayed Elbehery";
        firstLastNameOnly = nameDuplicationDetector.eliminateMiddleNames(fullName);
        Assert.assertEquals("flipOrderOfNameParts method has incorrect behavior", "Mustafa Elbehery", firstLastNameOnly);
    }


    @Test(expected = NullPointerException.class)
    public void testCheckDuplicatesNullException() {

        nameDuplicationDetector.checkDuplicates(nullNames);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCheckDuplicatesIllegalArgumentException() {

        nameDuplicationDetector.checkDuplicates(emptyNames);

    }

    @Test
    public void testCheckDuplicates() {

        List<Pair<String, String>> result = nameDuplicationDetector.checkDuplicates(combinationNames);
        List<String> foundDuplicates = new ArrayList<>(result.size());

        for (Pair<String, String> pair : result) {

            Assert.assertEquals("The indentified Key Name is incorrect, it must be 'Bill Gates' ", "Bill Gates", pair.getR());
            foundDuplicates.add(pair.getL());
        }


        Assert.assertTrue("Missing expected duplicate",foundDuplicates.contains("Gates Bill"));
        Assert.assertTrue("Missing expected duplicate",foundDuplicates.contains("Bill Henry Gates"));
        Assert.assertTrue("Missing expected duplicate", foundDuplicates.contains("William Gates"));
        Assert.assertTrue("Missing expected duplicate", foundDuplicates.contains("William Henry Gatez"));

        Assert.assertFalse("Missing expected duplicate", foundDuplicates.contains("Walter Gates"));

    }


}

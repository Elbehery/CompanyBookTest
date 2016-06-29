package no.companybook;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class NameDuplicationDetector {

    private static final ClassLoader loader = NameDuplicationDetector.class.getClassLoader();
    private Map<String, List<String>> firstNamesVariationsLookup;


    public NameDuplicationDetector() {

        // initialize the hash table only once during object construction, to prevent further manipulation.
        initializeFirstNameVariationLookupMap();

    }

    public List<Pair<String, String>> checkDuplicates(List<String> inputNames) {

        if (inputNames.size() < 1) {
            throw new IllegalArgumentException("Input names are empty");
        }

        // initialize the required data structures.
        Map<String, String> namesMap = new HashMap<>();
        List<Pair<String, String>> listOfDuplicates = new LinkedList<>();

        // identify the duplicates
        for (String originalName : inputNames) {

            // preprocess the name.
            String firstLastNameOnly = eliminateMiddleNames(originalName);
            boolean flag = true;
            String match = "";

            // case : Identical.
            String sortedName = sortString(firstLastNameOnly);
            if (namesMap.containsKey(sortedName)) {

                flag = false;
                listOfDuplicates.add(new Pair<String, String>(originalName, namesMap.get(sortedName)));

            }

            // case : change of name parts.
            sortedName = sortString(flipOrderOfNameParts(firstLastNameOnly));
            if (flag && namesMap.containsKey(sortedName)) {

                flag = false;
                listOfDuplicates.add(new Pair<String, String>(originalName, namesMap.get(sortedName)));

            }

            // case : variation of first name && misspelling last name.
            if (flag && (match = checkDuplicatesByCombinations(firstLastNameOnly, namesMap)) != null) {

                flag = false;
                listOfDuplicates.add(new Pair<String, String>(originalName, match));
                match = "";

            }


            // case : a new name.
            if (flag) {
                namesMap.put(sortString(originalName), originalName);
            }
        }


        return listOfDuplicates;
    }

    public String checkDuplicatesByCombinations(String firstLastNameOnly, Map<String, String> namesMap) {

        String[] firstLastNameSplits = firstLastNameOnly.split("\\s");
        String firstName = firstLastNameSplits[0];
        String lastName = firstLastNameSplits[1];

        List<String> allFirstNameVariants = this.firstNamesVariationsLookup.get(firstName);
        List<String> allLastNameVariants = getAllLastNameWithinDistanceOne(lastName, namesMap);
        StringBuilder candidateName = new StringBuilder();
        String sortedName = "";

        // check the existence of all the possible combinations.

        if (allFirstNameVariants != null && allFirstNameVariants.size() > 0 && allLastNameVariants != null && allLastNameVariants.size() > 0) {
            for (String fName : allFirstNameVariants) {

                for (String lName : allLastNameVariants) {

                    candidateName.append(fName).append(" ").append(lName);
                    sortedName = sortString(candidateName.toString());
                    if (namesMap.containsKey(sortedName)) {
                        return namesMap.get(sortedName);
                    }

                    candidateName.delete(0, candidateName.length());
                }
            }
        } else if (allFirstNameVariants != null && allFirstNameVariants.size() > 0) {

            for (String fName : allFirstNameVariants) {

                candidateName.append(fName).append(" ").append(lastName);
                sortedName = sortString(candidateName.toString());
                if (namesMap.containsKey(sortedName)) {
                    return namesMap.get(sortedName);
                }

                candidateName.delete(0, candidateName.length());
            }

        } else if (allLastNameVariants != null && allLastNameVariants.size() > 0) {

            for (String lName : allLastNameVariants) {

                candidateName.append(firstName).append(" ").append(lName);
                sortedName = sortString(candidateName.toString());
                if (namesMap.containsKey(sortedName)) {
                    return namesMap.get(sortedName);
                }

                candidateName.delete(0, candidateName.length());
            }

        }


        return null;
    }

    private void initializeFirstNameVariationLookupMap() {

        List<String> data = readFirstNamesDataFile();
        this.firstNamesVariationsLookup = new HashMap<>();

        for (String line : data) {

            // preprocess the data into a reasonable structure for faster lookup.
            String[] keyValueSplits = line.split("-");
            String key = keyValueSplits[0].trim();
            String value = keyValueSplits[1].trim();

            //TODO: I have used this initialization way in the beginning, but the variant names contains a white space prefix which affects the hashing value. Instead of invoking String.trim() upon each comparison,
            // TODO: I initialized the hash table with the trimmed variants, to avoid creating many objects during the computation. I think this may increase the JVM garbage collection.
            //List<String> valuesList = new LinkedList<>(Arrays.asList(value.split(",")));

            List<String> valuesList = new LinkedList<>();
            for (String stringValue : value.split(",")) {
                valuesList.add(stringValue.trim());
            }


            if (firstNamesVariationsLookup.containsKey(key)) {
                firstNamesVariationsLookup.get(key).addAll(valuesList);
            } else {
                firstNamesVariationsLookup.put(key, valuesList);
            }
        }
    }

    private List<String> readFirstNamesDataFile() {

        List<String> lines = null;
        try {

            lines = FileUtils.readLines(new File(loader.getResource("data/Firstnames.txt").getFile()));

        } catch (IOException e) {

            System.err.println("Reading First Names file causes error due to " + e.getCause());
        }

        return lines;
    }

    private String eliminateMiddleNames(String fullName) {

        // trim the input, and split to identify only the first and last names.
        String[] arrayOfNames = fullName.trim().split("\\s");

        // sanity check to avoid irrelevant computation.
        if (arrayOfNames.length < 3) {
            return fullName;
        }

        // using StringBuilder is more optimal here, to avoid overwhelming the JVM with garbage objects.
        // StringBuilder; unlike StringBuffer, does not use locking which affects the performance.
        StringBuilder firstLastName = new StringBuilder();
        firstLastName.append(arrayOfNames[0]).append(" ").append(arrayOfNames[arrayOfNames.length - 1]);

        return firstLastName.toString();
    }

    private String sortString(String unsorted) {

        char[] unsortedArray = unsorted.toLowerCase().toCharArray();
        Arrays.sort(unsortedArray);

        return new String(unsortedArray);
    }

    private String flipOrderOfNameParts(String fullName) {

        String[] firstLastNameSplits = fullName.split("\\s");

        if (firstLastNameSplits.length == 1) {
            return fullName;
        }

        String firstName = firstLastNameSplits[0];
        String lastName = firstLastNameSplits[1];

        return lastName + " " + firstName;

    }

    private List<String> getAllLastNameWithinDistanceOne(String sourceLastName, Map<String, String> nameMap) {

        List<String> allIdentifiedLastNamesWithinDistanceOne = new LinkedList<>();

        for (Map.Entry<String, String> target : nameMap.entrySet()) {

            String targetLastName = getLastNameOnly(target.getValue());
            int distance = computeLevenshteinDistance(sourceLastName, targetLastName);
            if (distance < 2) {
                allIdentifiedLastNamesWithinDistanceOne.add(targetLastName);
            }
        }

        return allIdentifiedLastNamesWithinDistanceOne;
    }

    private String getLastNameOnly(String fullName) {

        String[] firstLastNameSplits = fullName.split("\\s");

        if (firstLastNameSplits.length == 1) {
            return fullName;
        }

        String firstName = firstLastNameSplits[0];
        String lastName = firstLastNameSplits[1];

        return lastName;

    }

    private int computeLevenshteinDistance(String source, String target) {
        int[][] distance = new int[source.length() + 1][target.length() + 1];

        for (int i = 0; i <= source.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= target.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= source.length(); i++)
            for (int j = 1; j <= target.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((source.charAt(i - 1) == target.charAt(j - 1)) ? 0 : 1));

        return distance[source.length()][target.length()];
    }

    private int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    public static void main(String[] args) {


        List<String> names = new LinkedList<>();
        names.add("Bill Gates");
        names.add("Gates Bill");
        names.add("Bill Henry Gates");
        names.add("William Gates");
        names.add("Walter Gates");
        names.add("William Henry Gatez");

/*
        names.add("Eric Rudder");
        names.add("Eric D Rudder");
        names.add("Ted Turner");
        names.add("Rudder Eric");
        names.add("Mustafa Elbehery");
        names.add("Turner Ted");
        names.add("Joshua Rosenkranz");
        names.add(" Rosenkranz E Joshua");
        names.add("Eric Rudder");
        names.add("Joshua Rosenkranz");
        names.add("Elbehery Mustafa");
        names.add("Elbehery elsayed Mustafa");

        Collections.shuffle(names);
        */
        NameDuplicationDetector nameDuplicationDetector = new NameDuplicationDetector();
        List<Pair<String, String>> result = nameDuplicationDetector.checkDuplicates(names);

        for (Pair pair : result) {
            System.out.println(pair.getL() + "\t" + pair.getR());
        }


    }

}
